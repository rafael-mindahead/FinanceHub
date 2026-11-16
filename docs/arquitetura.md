# Arquitetura do FinanceHub

## Componentes implementados

### Aplicação Java 21
- FinanceHubApplication seleciona o modo de execução conforme o ambiente.
- Com interface gráfica disponível, inicia FinanceHubSwingFrame (Java Swing).
- Em ambiente headless, inicia FinanceHubHttpServer usando o HttpServer nativo do JDK.

### Domínio e serviços
- Lancamento é a classe abstrata base.
- Receita e Despesa especializam Lancamento e implementam o comportamento financeiro.
- Usuario mantém associação com lançamentos; Categoria classifica lançamentos.
- GerenciadorCategorias e GerenciadorLancamentos concentram operações de domínio.
- Relatorio consolida receitas, despesas e saldo.
- FinanceHubException representa erros de domínio.

### Persistência e integrações
- Repositórios JDBC acessam o MySQL.
- PersistenciaArquivoRepository mantém uma alternativa de persistência local.
- ImportadorArquivoService importa e exporta CSV/TXT.
- CotacaoMoedaService consulta a AwesomeAPI para obter cotações.

## Fluxos atuais

Aplicação desktop:

FinanceHubSwingFrame → serviços de domínio → modelos → repositórios de persistência.

API HTTP:

Cliente HTTP → FinanceHubHttpServer → serviços de domínio → repositórios de persistência.

Rotas implementadas sob /api/v1: health, categorias, lancamentos, relatorios, importacao e exportacao.

## Componentes planejados

api-php, web, mobile/android e mobile/ios continuam como estruturas planejadas. O gateway PHP e os clientes Web/mobile ainda não estão integrados ao backend atual.

## Contrato de dados
- JSON em UTF-8.
- Datas em formato ISO-8601 (YYYY-MM-DD).
- Valores monetários representados no domínio por BigDecimal; a API também aceita/exibe valores em centavos inteiros.
- API versionada em /api/v1.

## Segurança e operação

A API atual não possui autenticação/autorização, permite CORS com origem * e cria o servidor sem restringir explicitamente o endereço de bind. Portanto, deve permanecer em ambiente local/de demonstração até que esses pontos sejam corrigidos. Também é necessário limitar o tamanho do corpo das requisições e tornar explícitos os erros de persistência.

## Ordem sugerida de evolução
1. Executar e corrigir a suíte JUnit após a integração.
2. Corrigir as regras de exclusão de categorias e validação de tipo de lançamento.
3. Adicionar testes de integração JDBC/MySQL.
4. Endurecer a segurança da API.
5. Evoluir o gateway PHP e os clientes Web/mobile, se permanecerem no escopo.