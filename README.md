# Password Validation API

Essa aplicação foi desenvolvida para expor uma API web capaz de validar se uma senha atende a critérios de segurança definidos.  

Além da validação, optei por enriquecer a solução com **persistência, cache e mensageria**, demonstrando conhecimentos em arquitetura backend moderna.

---

## Arquitetura

A aplicação segue princípios de **microserviços** e **separação de responsabilidades**:

- **Controller** → Responsável por expor os endpoints HTTP  
- **Service** → Contém as regras de negócio da aplicação  
- **Model** → Entidades de domínio da aplicação  
- **DTO** → Objetos de transferência de dados para entrada da API  
- **Validator** → Isola a lógica de validação da senha  
- **Repository** → Comunicação com o banco de dados  
- **Message/Producer** → Publicação de eventos assíncronos  
- **Config** → Centraliza configurações (beans)
- **Exceptions** → Camada de tratamento de erros, padronizando respostas HTTP  

---

## Principais decisões de design

- ### Uso de DTO
Optei por utilizar DTOs de entrada para desacoplar o contrato da API da camada de persistência. Essa decisão evita a exposição direta das entidades do banco, 
reduz o acoplamento entre a API, o modelo de dados e facilita futuras versões da API sem impactar a estrutura interna do sistema.

- ### Validação isolada da Service
A validação foi colocada em uma classe específica (`PasswordValidator`) para respeitar o princípio da responsabilidade única. Com isso,
a Service fica responsável apenas por orquestrar o fluxo, enquanto o Validator concentra todas as regras de validação. Essa abordagem também facilita a criação
de testes unitários isolados, garantindo maior confiabilidade e manutenção mais simples.

- ### Banco de dados
Apesar do exercício solicitar apenas um boolean como resposta, decidi criar um endpoint para persistir o hash da senha. Essa escolha demonstra integração com banco
relacional, modelagem de entidade e uso de JPA/Hibernate, permitindo auditoria e rastreabilidade das validações realizadas pelo sistema.

Para a API Spring Boot, escolhi MySQL, por ser um banco relacional consolidado e com forte suporte no ecossistema Spring através do Spring Data JPA. No entanto,
a solução poderia ser facilmente adaptada para DynamoDB em cenários que demandem alta escalabilidade ou arquiteturas serverless na AWS.

- ### Hash de senha
O hash das senhas foi implementado com o algoritmo BCrypt, amplamente reconhecido e utilizado para proteção de dados sensíveis. No Spring Boot,
utilizei o `PasswordEncoder` do Spring Security configurado como Bean, aproveitando a injeção de dependência e evitando acoplamento com implementações concretas.

- ### Redis (Cache)
O Redis foi utilizado para evitar validações repetidas da mesma senha em um curto intervalo de tempo, demonstrando a 
redução de processamento desnecessário, uso de cache distribuído com o ontrole de expiração automática via TTL.

> Observação: O cache é temporário e não substitui o banco de dados.

- ### Kafka (Mensageria)
Eventos são publicados, no tópico `password.validated` utilizando Kafka, após o processamento da senha para simular integração com outros sistemas. Demonstrando
arquitetura orientada a eventos, desacoplamento entre serviços e a simulação de processamento assíncrono. 

- ### Docker Compose
Os serviços (banco, cache e mensageria) foram orquestrados via **Docker Compose**, facilitando a execução do projeto sem necessidade de instalações locais.

---

## Testes Unitários

O projeto inclui testes unitários para validar a aplicação, alcançando 81% de cobertura de classes e 
90% de cobertura de linhas, conforme verificado pelo Run Coverage do IntelliJ.


<img width="500" height="500" alt="coberturadeteste" src="https://github.com/user-attachments/assets/3f1a25dd-dbb3-4e4b-90bd-bb198391be9f" />


Os testes foram implementados com JUnit 5 e Mockito, permitindo mockar dependências e garantir que cada componente seja testado isoladamente.
Além disso, os endpoints HTTP foram testados sem a necessidade de subir a aplicação completa, tornando os testes mais rápidos e confiáveis.

---

## Execução do Projeto

### Pré-requisitos
- Docker instalado na máquina
- Java 17+ 
- Git (opcional, se for clonar do repositório)

### Clonar o repositório
`git clone https://github.com/IsabelaTeixeira/password-validation-service.git`

### Rodar o Docker Compose
No terminal, dentro do projeto.

Subir os conteiners, rodar o comando:

`docker compose up -d`

Para verificar se os conteiners estão rodando corretamente:

`docker ps`

Imagem de exemplo:

<img width="807" height="702" alt="docker-compose" src="https://github.com/user-attachments/assets/14d0b989-7a01-400e-b431-1ffa691be1f2" />

### Rodar a Aplicação

- Abra o projeto na sua IDE (IntelliJ, Eclipse, VS Code)
- Execute a classe principal PasswordValidationServiceApplication
- A aplicação irá se conectar automaticamente aos serviços do Docker

Assim, a aplicação estará disponível em `http://localhost:8080`.

### Testar a API

- **Validar senha:**

`POST http://localhost:8080/password/validate`

```bash
{
  "password": "ExemploVaLidO!"
}
```

**Retorno:** `200 OK` 

Senha válida: `true` 

Senha inválida: `false`

- **Salvar o hash da senha:**

`POST http://localhost:8080/password/hash`

```bash
{
  "password": "ExemploVaLidO!"
}
```

**Retorno:**

Senha válida:

`200 OK` com o hash gerado

Senha inválida: 
```bash 
422 UNPROCESSABLE ENTITY
{
	"error": "Password does not meet security rules",
	"timestamp": "2026-03-17T17:07:36.775483",
	"status": 422
}
```

## Testar via terminal os serviços utilizados no docker-compose

### MYSQL 

Rode os seguintes comandos: 

`docker exec -it mysql mysql -u root -p`

`USE passworddb;` 

`SELECT * FROM passwords;`

Imagem de exemplo:

<img width="502" height="500" alt="mysql" src="https://github.com/user-attachments/assets/757e30f8-7edb-4f2f-b367-490f6f7ae914" />

### KAFKA 

Rode os seguintes comandos: 

`docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list`

```bash
docker exec -it kafka kafka-console-consumer  
--bootstrap-server localhost:9092  
--topic password-validated  
--from-beginning
```

Imagem de exemplo:

<img width="500" height="452" alt="kafka" src="https://github.com/user-attachments/assets/a69c13d5-eeb2-47a7-a39f-e7bc526542cc" />

### REDIS

Rode os seguintes comandos: 

`docker exec -it redis redis-cli`

`keys * ` 

Imagem de exemplo:

<img width="502" height="242" alt="redis" src="https://github.com/user-attachments/assets/0ca39583-bc73-4ef8-bd06-b065bea3f893" />

---

## Para parar os serviços
Para desligar o conteiner, utilize o comando:

`docker compose down -v`
