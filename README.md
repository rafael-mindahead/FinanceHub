# FinanceHub

Gerenciador financeiro pessoal desenvolvido para o PjBL de Programação Orientada a Objetos.

## Escopo
Controle de receitas, despesas, categorias, histórico e relatórios pessoais. **Não** inclui contas bancárias, PIX ou transferências.

## Arquitetura planejada
- **backend-java/** — monólito Java 21 com POO, regras de negócio, HTTP `HttpServer` (JDK) e persistência MySQL via JDBC.
- **api-php/** — camada HTTP intermediária (gateway/BFF) para web, Android e iOS, sem regras de negócio.
- **web/** — interface web, consumindo a API PHP.
- **mobile/android/** — futuro cliente Kotlin.
- **mobile/ios/** — futuro cliente Swift.
- **infra/** — infraestrutura local, banco de dados e planejamento Azure.
- **docs/** — contrato HTTP, arquitetura, checklist dos requisitos e diagramas.
- **scripts/** — automações auxiliares futuras.

Fluxo: clientes → API PHP → monólito Java → MySQL.

## Situação desta revisão
Esta revisão **substitui a estrutura antiga baseada em Spring Boot por um esqueleto de diretórios**. As regras de negócio, rotas, interface, importação e aplicativos **ainda não estão implementados**.

## Desenvolvimento inicial
1. Copiar `.env.example` para `.env` e alterar os segredos locais.
2. Usar `docker compose up -d mysql` para iniciar **apenas** o MySQL local.
3. Implementar primeiro as classes Java e testes POO; depois o acesso JDBC e `HttpServer`.
4. Integrar o gateway PHP, criar a interface web e adicionar CSV/TXT.
5. Validar os requisitos em `docs/requisitos-pjbl.md`.
6. Deixar a publicação no Microsoft Azure para a etapa final, durante a apresentação.

## Branches
- `main`: base estável e integração.
- `rafael-develop`: desenvolvimento pessoal.

Veja `docs/arquitetura.md` para as decisões técnicas.