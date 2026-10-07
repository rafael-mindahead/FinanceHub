# FinanceHub

Plataforma de gestão financeira pessoal.

## Ambiente de desenvolvimento

- Java 21
- Docker Desktop
- Maven Wrapper incluído em backend/

## Banco de dados

Copie .env.example para .env e configure as credenciais.

Inicie o MySQL:

    docker compose up -d --wait mysql

Confira o serviço:

    docker compose ps

Conexão local: localhost:3307.
O banco utiliza um volume Docker para persistência.

## Executar o backend localmente

Com o MySQL iniciado, execute dentro de backend/:

    ./mvnw spring-boot:run -Dspring-boot.run.profiles=local

O perfil local lê o arquivo .env da raiz.

Verifique a aplicação e a conexão com o banco:

    curl -i http://localhost:8080/actuator/health

Resultado esperado: HTTP 200 e componente db com status UP.

As migrations Flyway serão mantidas em
backend/src/main/resources/db/migration.