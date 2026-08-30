DOCUMENTAÇÃO TÉCNICA
API REST — Gerenciamento de Destinos de Viagem
Desafio 1 — Desenvolvimento de Sistemas Web
UniSENAI — ADS — 2026/2
Agosto de 2026
 
1. Visão Geral do Problema
Uma agência de viagens em processo de modernização digital necessita de uma API REST para centralizar o gerenciamento de seus destinos turísticos e possibilitar integrações com aplicativos parceiros, plataformas de turismo e sistemas futuros.
Atualmente a empresa opera com um site institucional e um sistema interno de reservas desconectados. A ausência de uma API padronizada impede a integração com parceiros externos e limita a escalabilidade dos serviços digitais.
1.1 Funcionalidades Requeridas
●	Cadastrar destinos de viagem com nome, localização, descrição e atividades
●	Listar todos os destinos disponíveis
●	Pesquisar destinos por nome ou localização
●	Visualizar detalhes de um destino específico
●	Atualizar informações de um destino existente
●	Registrar avaliações e recalcular automaticamente a média
●	Excluir destinos do sistema

2. Arquitetura Proposta
A aplicação adota a Arquitetura em Camadas (Layered Architecture), padrão amplamente utilizado em projetos Spring Boot e reconhecido pelo mercado como referência para APIs RESTful de pequeno e médio porte.
2.1 Diagrama de Camadas
┌─────────────────────────────────────┐
│         Cliente (HTTP/Browser)      │
└──────────────────┬──────────────────┘
                   │ requisição HTTP
┌──────────────────▼──────────────────┐
│   Controller  (camada de apresentação)│  ← recebe e devolve HTTP
│   DestinoController                  │
└──────────────────┬──────────────────┘
                   │ delega
┌──────────────────▼──────────────────┐
│   Service  (regras de negócio)       │  ← toda lógica fica aqui
│   DestinoService                     │
└──────────────────┬──────────────────┘
                   │ lê/escreve
┌──────────────────▼──────────────────┐
│   Model + Repositório em Memória     │  ← entidade + ConcurrentHashMap
│   Destino, DTOs                      │
└─────────────────────────────────────┘

2.2 Responsabilidade de Cada Camada
Controller: Recebe as requisições HTTP, valida os dados de entrada e delega ao Service. Não contém lógica de negócio — apenas traduz HTTP para chamadas de serviço e formata as respostas.
Service: Contém toda a lógica de negócio: cálculo de médias de avaliação, validação de regras, operações sobre o repositório. É a camada mais importante da aplicação.
Model/Entity: Define a estrutura dos dados (Destino) e os DTOs (DestinoRequest, AvaliacaoRequest). Separa a entidade interna dos dados que chegam pela API.
Exception: Centraliza o tratamento de erros. O GlobalExceptionHandler intercepta exceções e devolve respostas JSON padronizadas com status HTTP corretos.

2.3 Justificativa da Arquitetura
A arquitetura em camadas foi escolhida pelos seguintes motivos:
●	Separação de responsabilidades: cada camada tem uma função específica, facilitando testes e manutenção
●	Evolução independente: trocar o armazenamento em memória por um banco de dados (JPA/Hibernate) não exige alterar o Controller
●	Testabilidade: a DestinoService pode ser testada unitariamente sem subir o servidor HTTP
●	Padrão de mercado: é o modelo ensinado e exigido em projetos reais com Spring Boot

3. Estrutura do Projeto
viagens-api/
├── pom.xml                                      ← dependências e build
├── README.md                                    ← documentação resumida
└── src/
    ├── main/
    │   ├── java/com/agencia/viagens/
    │   │   ├── ViagensApiApplication.java       ← ponto de entrada
    │   │   ├── controller/
    │   │   │   └── DestinoController.java       ← endpoints HTTP
    │   │   ├── service/
    │   │   │   └── DestinoService.java          ← lógica de negócio
    │   │   ├── model/
    │   │   │   ├── Destino.java                 ← entidade principal
    │   │   │   ├── DestinoRequest.java          ← DTO de entrada
    │   │   │   └── AvaliacaoRequest.java        ← DTO de avaliação
    │   │   └── exception/
    │   │       ├── DestinoNotFoundException.java
    │   │       └── GlobalExceptionHandler.java  ← erros centralizados
    │   └── resources/
    │       ├── static/index.html                ← interface web
    │       └── application.properties
    └── test/
        └── DestinoServiceTest.java              ← testes unitários

4. Tecnologias Utilizadas

Tecnologia	Papel	Justificativa
Java 8	Linguagem principal	Amplamente adotado no mercado; LTS com suporte garantido; robusto para back-end.
Spring Boot 2.7	Framework principal	Reduz configuração manual; servidor Tomcat embutido; ideal para APIs REST.
Spring Web (MVC)	Camada HTTP	Mapeamento de rotas via anotações (@RestController, @GetMapping, etc.).
Spring Validation	Validação de dados	Bean Validation (JSR-380); valida DTOs de entrada sem código manual.
Maven	Gerenciador de build	Gerencia dependências e ciclo de build de forma declarativa (pom.xml).
ConcurrentHashMap	Armazenamento em memória	Thread-safe para ambiente web; permite múltiplas requisições simultâneas.
JUnit 5	Testes unitários	Garante que cada funcionalidade se comporta conforme esperado.

O Java 8 foi mantido como versão alvo para garantir compatibilidade com o ambiente disponível durante o desenvolvimento. O Spring Boot 2.7 é a última versão da linha 2.x e suporta Java 8, sendo amplamente documentada e estável para projetos acadêmicos e profissionais.

5. Endpoints da API
Base URL: http://localhost:8080

Método	Rota	Descrição	Corpo	Retorno
GET	/destinos	Lista todos os destinos	—	200 OK
GET	/destinos?busca={termo}	Pesquisa por nome ou localização	—	200 OK
GET	/destinos/{id}	Retorna um destino específico	—	200 OK
POST	/destinos	Cadastra novo destino	JSON	201 Created
PUT	/destinos/{id}	Atualiza destino completo	JSON	200 OK
PATCH	/destinos/{id}/avaliacao	Registra avaliação e recalcula média	JSON	200 OK
DELETE	/destinos/{id}	Remove o destino	—	204 No Content

5.1 Justificativa dos Métodos HTTP
GET: Recuperação de dados sem alteração de estado. Idempotente — pode ser chamado múltiplas vezes sem efeitos colaterais.
POST: Criação de novo recurso. Retorna 201 Created com o recurso criado no corpo da resposta.
PUT: Atualização completa de um recurso existente. Todos os campos são substituídos pelos enviados no corpo.
PATCH: Atualização parcial — modifica apenas a avaliação sem alterar os demais dados do destino.
DELETE: Remoção do recurso. Retorna 204 No Content confirmando o sucesso sem corpo de resposta.

5.2 Corpo das Requisições
POST /destinos e PUT /destinos/{id}:
{
  "nome": "Lisboa",
  "localizacao": "Lisboa, Portugal",
  "descricao": "Capital histórica de Portugal.",
  "atividades": ["Torre de Belém", "Alfama", "Mosteiro dos Jerônimos"]
}

PATCH /destinos/{id}/avaliacao:
{
  "nota": 4.5
}

5.3 Formato de Resposta de Erro
{
  "timestamp": "2026-08-30T14:32:00",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Destino com ID 99 não encontrado."
}

6. Funcionalidade de Avaliação
A média de avaliações é calculada de forma incremental, utilizando a fórmula de média acumulada. Isso evita armazenar o histórico completo de notas e garante eficiência em memória:
nova_media = media_anterior + (nota - media_anterior) / total_avaliacoes
Esta abordagem é matematicamente equivalente à média aritmética simples, mas requer apenas dois valores armazenados (totalAvaliacoes e mediaAvaliacoes), sem necessidade de uma lista de notas.

7. Como Executar o Projeto
7.1 Pré-requisitos
●	Java 8 ou superior (JDK — não JRE)
●	Eclipse IDE ou outra IDE com suporte a Maven
●	Maven 3.8+ (incluído no Eclipse/NetBeans)

7.2 Execução pelo Eclipse
1. Abra o Eclipse e vá em File → Import → Maven → Existing Maven Projects
2. Selecione a pasta viagens-api e clique em Finish
3. Clique com botão direito no projeto → Run As → Java Application
4. Selecione ViagensApiApplication e confirme
5. Aguarde a mensagem: Started ViagensApiApplication in X.XXX seconds
6. Acesse http://localhost:8080 no navegador

7.3 Execução pelo Terminal (Maven)
cd viagens-api
mvn spring-boot:run

7.4 Dados de Exemplo
A aplicação inicia com 3 destinos pré-cadastrados para facilitar os testes:
●	ID 1 — Paris (Paris, França) — média 4.75
●	ID 2 — Rio de Janeiro (Rio de Janeiro, Brasil) — média 4.8
●	ID 3 — Kyoto (Kyoto, Japão) — média 4.9

8. Exemplos de Teste (curl)
Listar todos os destinos
curl http://localhost:8080/destinos

Pesquisar por termo
curl "http://localhost:8080/destinos?busca=Paris"

Cadastrar destino
curl -X POST http://localhost:8080/destinos \
  -H "Content-Type: application/json" \
  -d '{"nome":"Lisboa","localizacao":"Lisboa, Portugal","descricao":"Capital histórica.","atividades":["Torre de Belém"]}'

Avaliar destino
curl -X PATCH http://localhost:8080/destinos/1/avaliacao \
  -H "Content-Type: application/json" \
  -d '{"nota": 4.8}'

Excluir destino
curl -X DELETE http://localhost:8080/destinos/2

Alternativamente, todos os endpoints podem ser testados pela interface web disponível em http://localhost:8080, que oferece formulários visuais para cada operação.

9. Considerações Finais
A API desenvolvida atende a todos os requisitos propostos pelo Desafio 1, implementando os sete endpoints solicitados com os métodos HTTP corretos, organização em três camadas distintas e armazenamento em memória thread-safe.
A arquitetura escolhida foi planejada com foco em manutenibilidade e evolução futura: a substituição do repositório em memória por um banco de dados relacional (via Spring Data JPA) pode ser feita sem alterar as camadas de Controller ou Service, apenas adicionando a dependência e criando um Repository.
A interface web incluída no projeto facilita a demonstração e os testes de todos os endpoints sem necessidade de ferramentas externas como Postman ou curl.
