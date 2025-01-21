# reading-flow-back-end
A API Reading Flow serve como backend para o aplicativo ulk Reading Flow, fornecendo funcionalidades robustas para a gestão de livros virtuais e o progresso de leitura. Esta API foi projetada para integrar-se perfeitamente com o frontend Angular, permitindo uma comunicação suave e o tratamento de dados eficiente.

Esta API foi desenvolvida utilizando o framework Spring, promovendo a segurança, facilidade de escalabilidade da aplicação e integração com bancos de dados MySQL.

## Visão Geral do Projeto
- **Descrição**: Este sistema permite a visualização de gráficos interativos, registro, consulta, atualização e remoção de registros de usuários, livros, solicitações de livros e leituras. Atua com o armazenamento dos arquivos por intermédio da API Google Drive, envio de Emails e emissão de Relatórios formato ".pdf".

![Captura de Tela](https://github.com/villson-junior/ulk-reading-flow-frontend/blob/master/screenshots/1-%20Login.png)
![Captura de Tela](https://github.com/villson-junior/ulk-reading-flow-frontend/blob/master/screenshots/2%20-%20Dashboard.png)
![Captura de Tela](https://github.com/villson-junior/ulk-reading-flow-frontend/blob/master/screenshots/3%20-%20Leituras.png)
![Captura de Tela](https://github.com/villson-junior/ulk-reading-flow-frontend/blob/master/screenshots/4%20-%20Biblioteca.png)
![Captura de Tela](https://github.com/villson-junior/ulk-reading-flow-frontend/blob/master/screenshots/5%20-%20G%C3%AAneros.png)
![Captura de Tela](https://github.com/villson-junior/ulk-reading-flow-frontend/blob/master/screenshots/6%20-%20Conta.png)

- [Repositório Frontend](https://github.com/villson-junior/ulk-reading-flow-frontend)
- [Repositório Backend](https://github.com/villson-junior/ulk-reading-flow-backend)

## Tecnologias Utilizadas

- **Spring Boot:** O núcleo da aplicação, fornecendo suporte para REST API, validação, JPA e segurança.
- **Spring Boot Starter Actuator:** Utilizado para monitoramento e gerenciamento da aplicação em tempo de execução.
- **Spring Boot Starter Data JPA:** Facilita a interação com o banco de dados usando JPA.
- **Spring Boot Starter Web:** Oferece suporte para construir APIs RESTful e aplicações web.
- **Spring Boot Starter Security:** Implementa mecanismos de autenticação e autorização seguros.
- **Spring Boot Starter Validation:** Usado para validar dados enviados e recebidos na aplicação.
- **Spring Boot Starter Thymeleaf:** Suporte para renderização de templates HTML no lado do servidor.
- **Flyway:** Ferramenta para versionamento e migração de banco de dados.
- **MySQL Connector:** Conector para interagir com o banco de dados MySQL em tempo de execução.
- **PDF Processing (OpenPDF, iTextPDF, Flying Saucer):** Geração e manipulação de arquivos PDF.
- **Jackson Datatype JSR310:** Manipulação de tipos de dados de data e hora.
- **Google OAuth e Google Drive API:** Integração para autenticação e gerenciamento de arquivos no Google Drive.
- **Gson:** Biblioteca para manipulação de JSON.
- **JSON Web Token (JWT):** Implementação de autenticação baseada em tokens.
- **Bcrypt:** Hashing de senhas para garantir segurança.
- **Jakarta Mail:** Ferramenta para envio de emails na aplicação.
- **Lombok:** Reduz o código boilerplate, como getters, setters e construtores.
- **Spring Boot DevTools:** Auxilia no desenvolvimento ao recarregar a aplicação automaticamente após mudanças.
- **Spring Boot Starter Test e Spring Security Test:** Fornece suporte para testes unitários e de integração.
- **Apache Commons BeanUtils:** Utilitário para trabalhar com beans Java.

## Configurações de Ambiente

- **Java Version**: 21
- **Database**: MySQL 8
- **IDE**: Eclipse, IntelliJ ou outro suporte ao Spring Boot

## Configuração do Banco de Dados
- Atualize o arquivo "application.properties" com suas credenciais do MySQL:
```
  spring.datasource.url=jdbc:mysql://localhost:3306/reading_flow
  spring.datasource.username=<seu-usuario>
  spring.datasource.password=<sua-senha>
```


## Dependências do Projeto

O `pom.xml` contém todas as dependências necessárias para o funcionamento da aplicação, incluindo as dependências para
validação, banco de dados, documentação da API e testes.

## Construção do Projeto

```console
  mvn clean install
```

## Como Executar

1. Certifique-se de ter o Java 21 instalado.
2. Clone o repositório do projeto.
3. Configure o banco de dados MySQL conforme necessário.
4. Execute a aplicação utilizando o Maven:

```console
   mvn spring-boot:run
```