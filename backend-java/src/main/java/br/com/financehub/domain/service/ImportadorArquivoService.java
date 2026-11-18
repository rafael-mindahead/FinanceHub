package br.com.financehub.domain.service;

import br.com.financehub.domain.exception.FinanceHubException;
import br.com.financehub.domain.model.Categoria;
import br.com.financehub.domain.model.Despesa;
import br.com.financehub.domain.model.Lancamento;
import br.com.financehub.domain.model.Receita;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço para leitura e escrita de lançamentos em arquivos CSV ou TXT.
 * Atende ao requisito 13 do PjBL (Leitura de arquivo CSV ou TXT).
 *
 * Formato suportado por linha (separador ';' ou ','):
 * TIPO;DESCRICAO;VALOR;DATA;CATEGORIA
 * Exemplo:
 * RECEITA;Salario Mensal;3500.00;2026-11-01;Trabalho
 * DESPESA;Supermercado;450.75;2026-11-05;Alimentacao
 */
public class ImportadorArquivoService {

    public ArrayList<Lancamento> importarDeArquivo(
        Path caminhoArquivo,
        GerenciadorLancamentos gerenciadorLancamentos,
        GerenciadorCategorias gerenciadorCategorias
    ) throws FinanceHubException {
        if (caminhoArquivo == null || !Files.exists(caminhoArquivo)) {
            throw new FinanceHubException("ARQUIVO_INEXISTENTE", "O arquivo informado nao existe: " + caminhoArquivo);
        }

        try (BufferedReader reader = Files.newBufferedReader(caminhoArquivo, StandardCharsets.UTF_8)) {
            return processarBufferedReader(reader, gerenciadorLancamentos, gerenciadorCategorias);
        } catch (IOException e) {
            throw new FinanceHubException("ERRO_LEITURA_ARQUIVO", "Falha ao ler arquivo CSV/TXT: " + e.getMessage(), e);
        }
    }

    public ArrayList<Lancamento> importarDeConteudo(
        String conteudoCsvOuTxt,
        GerenciadorLancamentos gerenciadorLancamentos,
        GerenciadorCategorias gerenciadorCategorias
    ) throws FinanceHubException {
        if (conteudoCsvOuTxt == null || conteudoCsvOuTxt.isBlank()) {
            throw new FinanceHubException("CONTEUDO_VAZIO", "O conteudo CSV/TXT para importacao esta vazio.");
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(conteudoCsvOuTxt))) {
            return processarBufferedReader(reader, gerenciadorLancamentos, gerenciadorCategorias);
        } catch (IOException e) {
            throw new FinanceHubException("ERRO_LEITURA_CONTEUDO", "Falha ao processar conteudo CSV/TXT: " + e.getMessage(), e);
        }
    }

    private ArrayList<Lancamento> processarBufferedReader(
        BufferedReader reader,
        GerenciadorLancamentos gerenciadorLancamentos,
        GerenciadorCategorias gerenciadorCategorias
    ) throws IOException, FinanceHubException {
        ArrayList<Lancamento> importados = new ArrayList<>();
        String linha;
        int numeroLinha = 0;

        while ((linha = reader.readLine()) != null) {
            numeroLinha++;
            String linhaLimpa = linha.trim();
            if (numeroLinha == 1 && linhaLimpa.startsWith("\uFEFF")) linhaLimpa = linhaLimpa.substring(1).trim();

            if (linhaLimpa.isEmpty() || linhaLimpa.startsWith("#")) {
                continue;
            }

            // Ignora cabeçalho na primeira linha útil, inclusive após BOM/linhas vazias.
            if (importados.isEmpty()
                && linhaLimpa.split("[;,]", 2)[0].replace(String.valueOf((char) 34), "").trim().equalsIgnoreCase("TIPO")) {
                continue;
            }

            Lancamento lancamento = interpretarLinha(linhaLimpa, numeroLinha, gerenciadorCategorias);
            if (gerenciadorLancamentos != null) {
                gerenciadorLancamentos.cadastrar(lancamento);
            }
            importados.add(lancamento);
        }

        return importados;
    }

    public Lancamento interpretarLinha(
        String linha,
        int numeroLinha,
        GerenciadorCategorias gerenciadorCategorias
    ) throws FinanceHubException {
        List<String> partes = separarCamposCsv(linha, numeroLinha);
        if (partes.size() < 4 || partes.size() > 5) {
            throw new FinanceHubException("LINHA_INVALIDA",
                "Linha " + numeroLinha + " invalida. Esperado: TIPO;DESCRICAO;VALOR;DATA[;CATEGORIA]");
        }

        String tipo = partes.get(0).trim().toUpperCase();
        String descricao = partes.get(1).trim();
        String valorTexto = partes.get(2).trim();
        String dataTexto = partes.get(3).trim();
        String nomeCategoria = partes.size() == 5 && !partes.get(4).isBlank()
            ? partes.get(4).trim() : "Geral";

        BigDecimal valor;
        try {
            valor = new BigDecimal(valorTexto);
        } catch (NumberFormatException e) {
            throw new FinanceHubException("VALOR_INVALIDO", "Valor numerico invalido na linha " + numeroLinha + ": " + valorTexto, e);
        }

        LocalDate data;
        try {
            data = LocalDate.parse(dataTexto);
        } catch (DateTimeParseException e) {
            throw new FinanceHubException("DATA_INVALIDA", "Data invalida na linha " + numeroLinha + " (use YYYY-MM-DD): " + dataTexto, e);
        }

        Categoria categoria = resolverOuCriarCategoria(nomeCategoria, gerenciadorCategorias);
        Lancamento lancamento;
        if ("RECEITA".equals(tipo)) {
            lancamento = new Receita(null, descricao, valor, data, categoria, 1L);
        } else if ("DESPESA".equals(tipo)) {
            lancamento = new Despesa(null, descricao, valor, data, categoria, 1L);
        } else {
            throw new FinanceHubException("TIPO_INVALIDO", "Tipo invalido na linha " + numeroLinha + ": " + tipo + " (use RECEITA ou DESPESA)");
        }

        GerenciadorLancamentos.validarLancamento(lancamento);
        if (categoria != null) {
            categoria.adicionarLancamento(lancamento);
        }
        return lancamento;
    }

    /**
     * Suporta campos entre aspas, delimitadores dentro de campos e aspas escapadas ("").
     * O delimitador pode ser ponto e virgula ou virgula.
     */
    private List<String> separarCamposCsv(String linha, int numeroLinha) throws FinanceHubException {
        boolean aspas = false;
        char delimitador = ';';
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '"') {
                if (aspas && i + 1 < linha.length() && linha.charAt(i + 1) == '"') i++;
                else aspas = !aspas;
            } else if (!aspas && (c == ';' || c == ',')) {
                delimitador = c;
                break;
            }
        }

        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        aspas = false;
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '"') {
                if (aspas && i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
                    campo.append('"');
                    i++;
                } else {
                    aspas = !aspas;
                }
            } else if (c == delimitador && !aspas) {
                campos.add(campo.toString());
                campo.setLength(0);
            } else {
                campo.append(c);
            }
        }
        if (aspas) {
            throw new FinanceHubException("CSV_MALFORMADO", "Aspas nao fechadas na linha " + numeroLinha + ".");
        }
        campos.add(campo.toString());
        return campos;
    }

    private Categoria resolverOuCriarCategoria(String nomeCategoria, GerenciadorCategorias gerenciadorCategorias) {
        if (nomeCategoria == null || nomeCategoria.isBlank()) {
            return null;
        }
        if (gerenciadorCategorias == null) {
            return new Categoria(1L, nomeCategoria);
        }
        for (Categoria c : gerenciadorCategorias.listar()) {
            if (c.getNome().equalsIgnoreCase(nomeCategoria)) {
                return c;
            }
        }
        long novoId = gerenciadorCategorias.listar().size() + 1L;
        while (true) {
            try {
                gerenciadorCategorias.buscarPorId(novoId);
                novoId++;
            } catch (Exception ignored) {
                break;
            }
        }
        Categoria nova = new Categoria(novoId, nomeCategoria);
        gerenciadorCategorias.cadastrar(nova);
        return nova;
    }

    private String escaparCampoCsv(String valor) {
        if (valor == null) return "";
        if (valor.indexOf(';') >= 0 || valor.indexOf(',') >= 0 || valor.indexOf((char) 34) >= 0
            || valor.indexOf((char) 10) >= 0 || valor.indexOf((char) 13) >= 0) {
            String aspas = String.valueOf((char) 34);
            return aspas + valor.replace(aspas, aspas + aspas) + aspas;
        }
        return valor;
    }

    public void exportarParaArquivo(Path caminhoArquivo, List<Lancamento> lancamentos) throws FinanceHubException {
        if (caminhoArquivo == null) {
            throw new FinanceHubException("CAMINHO_INVALIDO", "O caminho do arquivo para exportacao nao pode ser nulo.");
        }
        try (BufferedWriter writer = Files.newBufferedWriter(caminhoArquivo, StandardCharsets.UTF_8)) {
            writer.write("TIPO;DESCRICAO;VALOR;DATA;CATEGORIA");
            writer.newLine();
            if (lancamentos != null) {
                for (Lancamento l : lancamentos) {
                    String nomeCat = (l.getCategoria() != null && l.getCategoria().getNome() != null)
                        ? l.getCategoria().getNome()
                        : "Geral";
                    writer.write(String.join(";",
                        escaparCampoCsv(l.getTipo()),
                        escaparCampoCsv(l.getDescricao()),
                        escaparCampoCsv(l.getValor().toPlainString()),
                        escaparCampoCsv(l.getData().toString()),
                        escaparCampoCsv(nomeCat)
                    ));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new FinanceHubException("ERRO_ESCRITA_ARQUIVO", "Falha ao exportar arquivo CSV/TXT: " + e.getMessage(), e);
        }
    }
}

