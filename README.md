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