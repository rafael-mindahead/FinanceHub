# Fluxograma do FinanceHub

Este diagrama representa o comportamento implementado no backend Java 21. Os caminhos de execução desktop e HTTP compartilham os serviços e o domínio; a persistência usa MySQL quando disponível e arquivo local como alternativa.

```mermaid
flowchart TD
    A([Início]) --> B[FinanceHubApplication]
    B --> C{Ambiente possui interface gráfica?}

    C -- Sim --> D[FinanceHubSwingFrame]
    C -- Não / headless --> E[FinanceHubHttpServer]
    E --> F[Servidor HTTP na porta PORT ou 8080]
    F --> G{Rota /api/v1}
    G --> H[Health check]
    G --> I[Categorias]
    G --> J[Lançamentos]
    G --> K[Relatórios]
    G --> L[Importação CSV/TXT]
    G --> M[Exportação CSV]

    D --> N[Interação do usuário]
    N --> O[Gerenciadores de domínio]
    I --> O
    J --> O
    K --> O
    L --> P[ImportadorArquivoService]
    P --> O
    M --> Q[Leitura dos lançamentos]
    Q --> R[Gerar conteúdo CSV UTF-8]

    O --> S[Validação e regras de negócio]
    S --> T[Modelos do domínio]
    T --> U[Polimorfismo: Lancamento]
    U --> V[Receita / Despesa]
    V --> W[Atualizar dados em memória]

    B --> X[Inicializar persistência]
    X --> Y{MySQL disponível?}
    Y -- Sim --> Z[Repositórios JDBC]
    Y -- Não --> AA[PersistenciaArquivoRepository]
    Z --> AB[Carregar categorias e lançamentos]
    AA --> AB
    AB --> AC{Existem dados?}
    AC -- Não --> AD[Semear categorias e lançamentos de demonstração]
    AC -- Sim --> AE[Disponibilizar dados carregados]
    AD --> AE
    AE --> D
    AE --> E

    W --> AF{MySQL ativo?}
    AF -- Sim --> AG[Persistir via JDBC]
    AF -- Não --> AH[Persistir em arquivo local]
    AG --> AI[Sincronizar arquivo local]
    AH --> AI
    AI --> AJ([Resposta / atualização da interface])

    H --> AJ
    R --> AJ
```

## Leitura do fluxo

1. **Inicialização:** `FinanceHubApplication` escolhe Swing quando existe ambiente gráfico ou a API HTTP em modo headless.
2. **Persistência:** a aplicação tenta conectar ao MySQL. Se a conexão não estiver disponível, recupera os dados do arquivo local. Se não houver dados, cria registros de demonstração.
3. **Regras de negócio:** as ações passam pelos gerenciadores, que trabalham com os modelos `Categoria`, `Lancamento`, `Receita` e `Despesa`.
4. **API:** o servidor Java expõe health check, categorias, lançamentos, relatórios, importação e exportação sob `/api/v1`.
5. **Arquivos:** `ImportadorArquivoService` interpreta CSV/TXT; a exportação gera CSV em UTF-8.
6. **Persistência das alterações:** os dados são atualizados nos gerenciadores e sincronizados com o mecanismo de persistência ativo e com o arquivo local.

> O fluxo representa a implementação Java atual. Gateway PHP e clientes Web/mobile não integram o caminho de execução documentado.
