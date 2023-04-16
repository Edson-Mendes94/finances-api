<h1 align="center"> Finances API </h1>

![Badge Concluído](https://img.shields.io/static/v1?label=Status&message=Concluído&color=success&style=for-the-badge)
![Badge Java](https://img.shields.io/static/v1?label=Java&message=17&color=orange&style=for-the-badge&logo=java)
![Badge Spring](https://img.shields.io/static/v1?label=Springboot&message=v2.6.3&color=brightgreen&style=for-the-badge&logo=spring)
![Badge Maven](https://img.shields.io/static/v1?label=Maven&message=v3.8.4&color=critical&style=for-the-badge&logo=apache+maven)
![Badge JUnit5](https://img.shields.io/static/v1?label=JUnit5&message=v5.8.2&color=green&style=for-the-badge&logo=junit5)
![Badge JUnit5](https://img.shields.io/static/v1?label=PostgreSQL&message=v14.4&color=blue&style=for-the-badge&logo=PostgreSQL)

## :book: Resumo do projeto
Finances API é um projeto de uma REST API para controle de orçamento familiar que visa auxiliar o usuário a gerenciar suas receitas e despesas de forma clara e segura.

O projeto foi proposto pela Alura no Challenge Backend 2ª edição.
## :hammer: Funcionalidades
- `Autenticação de usuário`
  - `Cadastrar usuário`: Cadastro de usuários através de um **POST /api/auth/signup** com as informações de nome, email, senha e confirmação de senha.
  - `Logar usuário`: Login de usuários através de um **POST /api/auth/signin** com as informações de autenticação do usuário (email e senha), 
  devolve um token JWT do tipo Bearer, que deve ser enviado no header authorization em cada nova requisição para as outras funcionalidades.</br>

- `Receitas`
  - `Cadastrar receita`: Cadastro de receitas através de um **POST /api/incomes** com as informações da receita a ser salva (descrição, data e valor).
  - `Buscar receitas`: Busca as receitas do usuário através de um **GET /api/incomes**, opcional buscar por descrição.
  - `Buscar receita por id`: Busca a receita do usuário por id através de um **GET /api/incomes**{id}*.
  - `Buscar receitas por ano e mês`: Busca as receitas do usuário em um dado ano e mês, através de um **GET /api/incomes**{ano}/{mês}*.
  - `Atualizar receita`: Atualizar uma receita do usuário através de um **PUT /api/incomes**{id}*.
  - `Deletar receita`: Deletar uma receita do usuário através de um **DELETE /api/incomes**{id}*</br>

- `Despesas`
  - `Cadastrar despesa`: Cadastro de despesas através de um **POST /api/expenses** com as informações da despesa a ser salva (descrição, data, valor e categoria), o campo categoria é opcional, as categorias até o momemto são: ALIMENTACAO, SAUDE, MORADIA, TRANSPORTE, EDUCACAO, LAZER, IMPREVISTOS, OUTRAS.
  - `Buscar despesas`: Busca as despesas do usuário através de um **GET /api/expenses**, opcional buscar por descrição.
  - `Buscar despesa por id`: Busca a despesa do usuário por id através de um **GET para /api/expenses/{valor do id}**.
  - `Buscar despesas por ano e mês`: Busca as despesas do usuário em um dado ano e mês, através de um **GET /api/expenses/{ano}/{mês}**.
  - `Atualizar despesa`: Atualizar uma despesa do usuário através de um **PUT /api/expenses/{id}**.
  - `Deletar despesa`: Deletar uma despesa do usuário através de um **DELETE /api/expenses/{id}**</br>

- `Categorias`
  - `Buscar categorias`: Busca de categorias através de um **GET /api/categories**.</br>

- `Resumo`
  - `Resumo de mês`: Através de um **GET /api/summaries/{ano}/{mês}** o usuário tem acesso ao total de receitas e despesas de um dado ano e mês, 
  assim como o saldo do mês e o total de despesas por categoria.</br>

- `Usuário`
  - `Atualizar senha`: Através de um **POST /api/users/password** o usuário pode alterar sua senha.
  - `Buscar usuários`: Através de um **GET /api/users** o *admin* pode buscar todos os usuários cadastrados, usuários comuns não possuem permissão de uso dessa funcionalidade.
  - `Deletar usuário`: Através de um **DELETE /api/users/{id}** o admin pode deletar um usuário por *id*.</br>

## :toolbox: Tecnologias
- `Intellij`
- `Java 17`
- `Spring Boot, Spring MVC, Spring Data JPA, Spring Security`
- `PostregSQL 14.4`
- `Maven`
- `Bean Validation`
- `OpenAPI 3 (Swagger)`
- `JWT`
- `Postman`
- `JUnit 5`
- `Mockito`
- `Testes de Unidade`
- `Testes de Integração`
- `Docker`
- `Flyway (Migration)`
- `Heroku`

## :hammer_and_wrench: Deploy
O deploy da aplicação foi realizado no *Heroku*, você pode testar/brincar/usar [aqui](https://apifinances.herokuapp.com/swagger-ui.html)

OBS: As aplicações que usam conta gratuita do heroku *adormecem* se ficarem inativas, então pode ser que a primeira requisição demore um pouco (até uns 60 segundos), apenas seja paciente :wink:.

## :card_file_box: Documentação
Documentação feita com Springdoc openapi, gerando [Swagger UI](https://apifinances.herokuapp.com/swagger-ui.html) e [Api docs](https://apifinances.herokuapp.com/api-docs)

## :gear: Atualizações futuras
- [x] Troca do banco de dados H2 para outro (MySQL, PostgreSQL, ou outro)
- [ ] Mais relatórios, tipo relatório anual, ou por categoria em dados mês e ano.
- [ ] Adicionar algum recurso para monitorar e gerenciar a aplicação, tipo o Actuator
- [x] Endpoint para listar todas as categorias.
- [x] Remoção da regra de negócio que **não permitia** receitas/despesas com mesmo **DESCRIÇÃO** no mesmo **MÊS** e **ANO**.
- [ ] Atualizar versão do Spring boot (2.7.* ou 3.0.*)