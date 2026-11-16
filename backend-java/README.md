# Backend Java

Backend FinanceHub em Java 21, Maven e JUnit 5, sem Spring Boot.

## Modos de execução
- Aplicação desktop: Java Swing.
- API: HttpServer nativo do JDK em ambiente headless, porta padrão 8080 (variável PORT).

## Pacotes
- api/http: rotas HTTP.
- api/dto: serialização e utilitários JSON.
- app: inicialização e demonstração de POO.
- config: configuração JDBC.
- domain/model: entidades, herança e relatórios.
- domain/service: regras de negócio, importação/exportação e cotação.
- domain/exception: exceções de domínio.
- persistence: contratos e persistência em arquivo/JDBC.
- persistence/jdbc: repositórios MySQL.
- ui: interface desktop Swing.

## Comandos
- Testes: mvn clean test
- Build: mvn clean package
- Execução desktop: java -jar target/financehub-core-0.1.0-SNAPSHOT.jar
- Execução API em modo headless: java -Djava.awt.headless=true -jar target/financehub-core-0.1.0-SNAPSHOT.jar

Configure as variáveis MYSQL_HOST, MYSQL_PORT, MYSQL_DATABASE, MYSQL_USER e MYSQL_PASSWORD para conectar ao MySQL. Sem conexão MySQL, a API pode utilizar o repositório local em arquivo.