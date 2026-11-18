package br.com.financehub.api.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FinanceHubHttpServerTest {

    @Test
    public void testarEndpointsCategoriasLancamentosERelatorios(@TempDir Path tempDir) throws Exception {
        Path arquivoTemp = tempDir.resolve("teste-http.db");
        FinanceHubHttpServer server = new FinanceHubHttpServer(0, arquivoTemp);
        server.iniciar();

        try {
            int porta = server.getPorta();
            HttpClient client = HttpClient.newHttpClient();
            String baseUrl = "http://localhost:" + porta + "/api/v1";

            // 1. Health check
            HttpResponse<String> health = client.send(
                HttpRequest.newBuilder(URI.create(baseUrl + "/health")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            assertEquals(200, health.statusCode());
            assertTrue(health.body().contains("\"status\":\"UP\""));

            // 2. Criar nova categoria
            HttpResponse<String> novaCat = client.send(
                HttpRequest.newBuilder(URI.create(baseUrl + "/categorias"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"nome\":\"Investimentos\"}"))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            assertEquals(201, novaCat.statusCode());
            assertTrue(novaCat.body().contains("Investimentos"));

            // 3. Criar novo lançamento em centavos
            HttpResponse<String> novoLanc = client.send(
                HttpRequest.newBuilder(URI.create(baseUrl + "/lancamentos"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                        "{\"tipo\":\"RECEITA\",\"descricao\":\"Dividendos\",\"valorCentavos\":15000,\"data\":\"2026-11-08\",\"categoriaId\":1}"
                    ))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            assertEquals(201, novoLanc.statusCode());
            assertTrue(novoLanc.body().contains("Dividendos"));

            // 4. Consultar relatório
            HttpResponse<String> relatorio = client.send(
                HttpRequest.newBuilder(URI.create(baseUrl + "/relatorios")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            assertEquals(200, relatorio.statusCode());
            assertTrue(relatorio.body().contains("saldoTotalCentavos"));

            // 5. Importar CSV pelo gateway HTTP Java
            HttpResponse<String> importacao = client.send(
                HttpRequest.newBuilder(URI.create(baseUrl + "/importacao"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                        "{\\"csv\\":\\"TIPO;DESCRICAO;VALOR;DATA;CATEGORIA\\\\nDESPESA;\\\\\\"Conta de luz; novembro\\\\\\";95.40;2026-11-12;Moradia\\"}"
                    ))
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            assertEquals(201, importacao.statusCode());
            assertTrue(importacao.body().contains("\\"quantidadeImportada\\":1"));
            assertTrue(importacao.body().contains("Conta de luz; novembro"));

            // 6. Exportar CSV pelo gateway HTTP Java
            HttpResponse<String> exportacao = client.send(
                HttpRequest.newBuilder(URI.create(baseUrl + "/exportacao")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
            );
            assertEquals(200, exportacao.statusCode());
            assertTrue(exportacao.headers().firstValue("Content-Type").orElse("").contains("text/csv"));
            assertTrue(exportacao.body().contains("TIPO;DESCRICAO;VALOR;DATA;CATEGORIA"));
        } finally {
            server.parar();
        }
    }
}

