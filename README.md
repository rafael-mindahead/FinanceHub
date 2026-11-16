# FinanceHub — Gerenciador Financeiro Pessoal

Projeto acadêmico de gerenciamento financeiro pessoal, desenvolvido em Java 21 para o PjBL de Programação Orientada a Objetos.

## Funcionalidades presentes no backend
- Domínio POO: Usuario, Categoria, Lancamento (abstrata), Receita, Despesa e Relatorio.
- Serviços para gerenciamento de categorias e lançamentos, cálculo de saldo e relatórios por período.
- Interface desktop Java Swing.
- API HTTP com o HttpServer nativo do JDK, com rotas versionadas em /api/v1.
- Persistência JDBC para MySQL e repositório local em arquivo.
- Importação e exportação de lançamentos CSV/TXT.
- Consulta de cotação de moedas pela AwesomeAPI.
- Testes automatizados com JUnit 5.

## Modos de execução
- Em um computador com interface gráfica, FinanceHubApplication inicia a aplicação Swing.
- Em ambiente headless, FinanceHubApplication inicia a API HTTP. A porta padrão é 8080 e pode ser alterada pela variável PORT.
- A API oferece health check, categorias, lançamentos, relatórios, importação e exportação.

## Compilar e testar
Na pasta raiz do repositório, execute:

1. cd backend-java
2. mvn clean test
3. mvn clean package
4. java -jar target/financehub-core-0.1.0-SNAPSHOT.jar

Para iniciar a API HTTP em um computador com ambiente gráfico, execute o JAR com a propriedade -Djava.awt.headless=true.

## MySQL local
O compose.yaml inicia o serviço MySQL. Copie .env.example para .env e execute docker compose up -d mysql.
O Docker Compose lê o arquivo .env automaticamente. Para que a aplicação Java local também receba essas variáveis, carregue-as no terminal antes de iniciar o JAR (por exemplo, com set -a && source .env && set +a no macOS/Linux).
Se a conexão MySQL não estiver disponível, a API utiliza a persistência local em arquivo.

## Arquitetura e documentação
- docs/arquitetura.md: arquitetura implementada e componentes planejados.
- docs/api/openapi.yaml: contrato HTTP.
- docs/diagrams/diagramas-pjbl.md: diagramas do domínio e fluxo.
- docs/requisitos-pjbl.md: checklist dos 17 critérios extraídos da rubrica da disciplina. Marque os itens somente após demonstrar e testar cada requisito.

## Componentes ainda planejados
Os diretórios api-php, web, mobile/android e mobile/ios permanecem como estrutura de expansão. O fluxo PHP → Java e os clientes Web/mobile não fazem parte desta implementação integrada.

## Observação de segurança
A API atual é uma implementação acadêmica/local. Antes de disponibilizá-la publicamente, é necessário implementar autenticação e autorização, restringir CORS e o endereço de bind do servidor, limitar o tamanho das requisições e revisar o tratamento de falhas de persistência.

## Contribuição
- Rafael Alves — @rafael-mindahead
- Rodrigo Amaral — @istampz (contribuição original do backend ampliado, interface Swing e serviços importados nesta integração).