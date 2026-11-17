# Sistema de Gerenciamento do Lar

## Descrição do Projeto
O **Sistema de Gerenciamento do Lar** é uma aplicação focada em modernizar e facilitar o controle dos suprimentos presentes em uma residência. Historicamente, a gestão de itens domésticos (como despensa, produtos de limpeza e higiene) é feita de forma manual ou desorganizada, o que frequentemente resulta em desperdício de produtos vencidos, compras duplicadas ou a falta de itens essenciais no dia a dia. 

Para solucionar esse problema, este projeto propõe uma plataforma baseada em uma API REST desenvolvida em **Spring Boot (Java 25)** com persistência em banco de dados NoSQL **MongoDB**. O sistema permite organizar os produtos por categorias, acompanhar rigorosamente suas quantidades e manter a gestão domiciliar sempre atualizada e inteligente.

## Stack Tecnológica Real
* **Linguagem:** Java 25
* **Framework:** Spring Boot (Spring Data MongoDB, Spring Web / WebMVC)
* **Banco de Dados:** MongoDB
* **Testes e Qualidade:** JUnit 5, Mockito

## Objetivo da Solução
O principal objetivo desta solução é otimizar o controle de suprimentos da casa, promovendo economia de tempo e de recursos financeiros. Através do sistema, é possível:
- **Cadastrar e Monitorar:** O usuário pode cadastrar produtos informando a categoria a qual pertencem e a quantidade exata disponível no momento.
- **Controle de Consumo:** Conforme os itens são utilizados, suas quantidades podem ser atualizadas no sistema, garantindo um espelho fiel da realidade da residência.
- **Automação de Reposição por Demanda:** O grande diferencial logístico do projeto é que, através do endpoint de estoque baixo, a adição automática de itens à lista de compras ocorre **por demanda**, acionada via parâmetro (`adicionarNaLista=true` e `userId`) quando os itens atingem um limite crítico. Isso oferece ao usuário controle prático sobre o que precisa ser adquirido, evitando idas desnecessárias ao supermercado.

## Configuração do Ambiente e Execução

### 1. Banco de Dados (MongoDB)
Certifique-se de que o seu servidor MongoDB está em execução localmente:
- **Host / Porta:** `localhost:27017`
- **Base de Dados Padrão:** `home_inventory`

### 2. Executando a Aplicação
A partir da raiz do módulo da API (`Aplication-Api`), você pode executar o projeto utilizando o Maven Wrapper (`mvnw`):

* **No Linux / macOS:**
  ```bash
  ./mvnw spring-boot:run
  ```
* **No Windows (Prompt de Comando / PowerShell):**
  ```cmd
  mvnw.cmd spring-boot:run
  ```
A aplicação iniciará na porta padrão `8080` (ou na porta configurada no seu `application.properties`).

## Integrantes da Equipe
- Amanda Ellen Lopes Lima (ellyhtts)
- Adriana dos Santos Pereira (adriiana-dev)
- Diná Borges Azevedo (dinaazvd)
- Maria Fabiana Silva Martins (Fabiana-mfsm)
