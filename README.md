# Meeting Room Booking API

API REST em Java com Spring Boot para gerenciamento de **salas de reunião**, **usuários** e **reservas**, com validações de regra de negócio e prevenção de conflitos de horário.

> Projeto de estudo focado em backend, boas práticas com Spring e modelagem de domínio.

---

## ✨ Funcionalidades

- **Usuários**
  - Cadastro de usuários
  - Atualização parcial com `PATCH`
  - Busca de usuário por nome
  - Remoção de usuários

- **Salas de reunião**
  - Cadastro de salas com código, nome, capacidade e status (`ACTIVE` / `INACTIVE`)
  - Busca de sala pelo código
  - Remoção de sala
  - Status padrão `ACTIVE` quando não informado

- **Reservas**
  - Criação de reserva vinculada a **usuário** e **sala**
  - Validação de intervalo de tempo (`start < end`)
  - Sala precisa estar ativa (`ACTIVE`)
  - Prevenção de **conflitos de horário** usando regra de intervalo `[start, end)`
  - Listagem de todas as reservas cadastradas

---

## 🧱 Stack Tecnológica

- **Linguagem:** Java
- **Framework:** Spring Boot
  - Spring Web
  - Spring Data JPA
  - Bean Validation (Jakarta Validation)
- **Banco:** configurável via `docker-compose` ou local
- **Build:** Gradle (Kotlin DSL)
- **Outros:**
  - Lombok
  - Testes automatizados (service/controller)

---

## 🏛 Arquitetura da Aplicação

O projeto segue uma arquitetura em camadas:

- `controller` → expõe os endpoints REST
- `service` → contém a regra de negócio
- `repository` → abstrai o acesso ao banco
- `entity` → modelos JPA que representam as tabelas
- `dtos` → objetos para dados de entrada/saída
- `mapper` → conversão entre entity e DTO
- `exceptions` → exceções de domínio (ex.: sala inativa, usuário não encontrado)

---

## 🌐 Endpoints principais

### 👤 Usuários (`/users`)
- `POST /users` → cria usuário
- `PATCH /users/{id}` → atualização parcial
- `GET /users?name={nome}` → busca por nome
- `DELETE /users/{id}` → remove usuário

---

### 🏢 Salas de reunião (`/meeting-rooms`)
- `POST /meeting-rooms` → cria sala
- `GET /meeting-rooms/{code}` → busca sala por código
- `DELETE /meeting-rooms/{code}` → remove sala

---

### 📅 Reservas (`/bookings`)
- `POST /bookings` → cria reserva com as regras:
  - `start` deve ser anterior a `end`
  - sala deve estar `ACTIVE`
  - não pode haver sobreposição de horários
- `GET /bookings` → lista reservas

---

## ✅ Regras de Negócio Importantes

- **Validação de horário**
  - `start` < `end`, caso contrário retorna `400 BAD_REQUEST`

- **Sala ativa**
  - se `status == INACTIVE`, lança `InactiveMeetingRoomException`

- **Conflito de horário**
  - o repositório usa `existsOverlap(...)` para impedir reservas que se sobreponham
  - faixa considerada: `[start, end)`

---

## 🚀 Como rodar o projeto

### ✔️ Pré-requisitos
- Java (versão definida no `build.gradle.kts`)
- Git
- **Opcional:** Docker

---

### ▶️ Rodando localmente
```bash
git clone https://github.com/biancapasch/meeting-room-booking-2.git
cd meeting-room-booking-2

./gradlew bootRun
```

A API estará acessível via:

```
http://localhost:8080
```

---

### 🐳 Rodando com Docker (opcional)

Se quiser subir com Docker Compose:

```bash
docker-compose up --build
```

---

## 🧪 Testes

Para rodar os testes automatizados:

```bash
./gradlew test
```

---

## ✍️ Próximos passos (melhorias sugeridas)

- Paginação e filtros nas listagens
- Documentação com Swagger/OpenAPI
- Autenticação e autorização (Spring Security)
- Deployment em ambiente cloud
- Mais testes de integração

---

## 📄 Licença

Projeto de estudo. Uso livre para fins educacionais.
