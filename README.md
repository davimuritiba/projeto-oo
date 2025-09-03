# Projeto OO - Sistema de Rede Social

Sistema completo de rede social desenvolvido em Java seguindo os princípios da Programação Orientada a Objetos (POO) e o padrão MVC (Model-View-Controller).

## Funcionalidades Implementadas

- **Sistema de Usuários**: Cadastro, login, edição e gerenciamento
- **Sistema de Posts**: Texto, imagem e vídeo com sistema de curtidas
- **Sistema de Amizades**: Solicitações, aceitação e gerenciamento
- **Sistema de Grupos**: Criação, participação e moderação
- **Sistema de Eventos**: Criação e gerenciamento de eventos
- **Sistema de Mensagens**: Chat direto entre usuários
- **Sistema de Notificações**: Alertas para interações
- **Feed de Posts**: Visualização de posts dos amigos
- **Interface Gráfica**: Interface Swing moderna e intuitiva

## Estrutura do Projeto

```
projeto-oo/
├── src/
│   ├── Main.java                    # Classe principal
│   ├── controller/                  # Controladores (Lógica de negócio)
│   │   ├── UserController.java      # Gerenciamento de usuários
│   │   ├── PostController.java      # Gerenciamento de posts
│   │   ├── FriendController.java    # Gerenciamento de amizades
│   │   ├── FriendRequestController.java # Solicitações de amizade
│   │   ├── MessageController.java   # Mensagens diretas
│   │   ├── GroupController.java     # Gerenciamento de grupos
│   │   ├── GroupChatController.java # Chat de grupos
│   │   ├── EventController.java     # Gerenciamento de eventos
│   │   ├── NotificationController.java # Sistema de notificações
│   │   └── FeedController.java      # Feed de posts
│   ├── model/                       # Modelos (Entidades)
│   │   ├── User.java               # Modelo de usuário
│   │   ├── Post.java               # Classe abstrata de posts
│   │   ├── TextPost.java           # Posts de texto
│   │   ├── ImagePost.java          # Posts de imagem
│   │   ├── VideoPost.java          # Posts de vídeo
│   │   ├── Message.java            # Mensagens diretas
│   │   ├── FriendRequest.java      # Solicitações de amizade
│   │   ├── Group.java              # Grupos
│   │   ├── Event.java              # Eventos
│   │   ├── Notification.java       # Notificações
│   │   ├── Content.java            # Classe abstrata para conteúdo
│   │   ├── MemberEntity.java       # Classe abstrata para entidades com membros
│   │   └── Privacy.java            # Enum de privacidade
│   └── view/                        # Interface (Apresentação)
│       └── SocialAppSwing.java     # Interface gráfica Swing
├── bin/                            # Arquivos compilados (.class)
├── Makefile                        # Script de compilação
└── README.md                       # Este arquivo
```

## Como Compilar e Executar

### Pré-requisitos
- Java 8 ou superior
- Terminal/Console

### 1. Compilar o Projeto

#### Opção 1: Compilação Simples (Recomendada)
```bash
javac -d bin src\model\*.java src\controller\*.java src\view\*.java src\Main.java
```

#### Opção 2: Compilação com Makefile
```bash
make compile
```

#### Opção 3: Compilação Manual (se houver problemas)
```bash
# Compilar modelos primeiro
javac -d bin src/model/Privacy.java
javac -d bin src/model/User.java
javac -d bin src/model/Content.java
javac -d bin src/model/Post.java
javac -d bin src/model/TextPost.java
javac -d bin src/model/ImagePost.java
javac -d bin src/model/VideoPost.java
javac -d bin src/model/Message.java
javac -d bin src/model/FriendRequest.java
javac -d bin src/model/MemberEntity.java
javac -d bin src/model/Group.java
javac -d bin src/model/Event.java
javac -d bin src/model/Notification.java

# Compilar controladores
javac -cp bin -d bin src/controller/*.java

# Compilar interface
javac -cp bin -d bin src/view/SocialAppSwing.java

# Compilar classe principal
javac -cp bin -d bin src/Main.java
```

### 2. Executar o Sistema

```bash
java -cp bin Main
```

### 3. Limpar Arquivos Compilados (Opcional)

```bash
# Usando Makefile
make clean

# Ou manualmente
rmdir /s bin
mkdir bin
```

## Funcionalidades Detalhadas

### Sistema de Usuários
- **Cadastro**: Criação de novos usuários com validação
- **Login**: Autenticação por email e senha
- **Perfil**: Edição de dados pessoais
- **Privacidade**: Configuração de perfil público/privado

### Sistema de Posts
- **Posts de Texto**: Criação de posts com texto simples
- **Posts de Imagem**: Compartilhamento de imagens com descrição
- **Posts de Vídeo**: Compartilhamento de vídeos com duração
- **Sistema de Curtidas**: Interação com posts de outros usuários
- **Feed**: Visualização de posts dos amigos

### Sistema de Amizades
- **Solicitações**: Envio e recebimento de pedidos de amizade
- **Aceitação/Recusa**: Gerenciamento de solicitações pendentes
- **Lista de Amigos**: Visualização de conexões
- **Remoção**: Desfazer amizades

### Sistema de Grupos
- **Criação**: Criação de grupos públicos/privados
- **Participação**: Entrada em grupos públicos
- **Moderação**: Sistema de moderadores e proprietários
- **Chat**: Mensagens dentro dos grupos
- **Transferência**: Transferência de propriedade

### Sistema de Eventos
- **Criação**: Criação de eventos com data/hora
- **Participação**: Inscrição em eventos
- **Gerenciamento**: Edição e exclusão de eventos
- **Filtros**: Eventos por período (hoje, amanhã, semana)

### Sistema de Mensagens
- **Chat Direto**: Mensagens privadas entre usuários
- **Status de Leitura**: Controle de mensagens lidas/não lidas
- **Histórico**: Visualização de conversas

### Sistema de Notificações
- **Alertas**: Notificações para interações
- **Tipos**: Solicitações, mensagens, curtidas, convites
- **Gerenciamento**: Marcar como lida, excluir

## Paradigmas da POO Implementados

### 1. Encapsulamento
- Atributos privados com getters/setters
- Controle de acesso aos dados
- Validação de entrada

### 2. Herança
- `Content` (classe abstrata) → `Post` e `Message`
- `Post` (classe abstrata) → `TextPost`, `ImagePost`, `VideoPost`
- `MemberEntity` (classe abstrata) → `Group` e `Event`

### 3. Polimorfismo
- Métodos abstratos implementados diferentemente
- `getContentType()`, `getFormattedContent()` com comportamentos específicos
- Tratamento uniforme de diferentes tipos de conteúdo

### 4. Abstração
- Classes abstratas `Content`, `Post`, `MemberEntity`
- Métodos abstratos que definem contratos
- Ocultação de complexidade de implementação

## Arquitetura MVC

### Model (Modelo)
- **Entidades**: User, Post, Message, Group, Event, etc.
- **Lógica de dados**: Validações e regras de negócio
- **Estado**: Armazenamento em memória

### View (Visão)
- **Interface Gráfica**: SocialAppSwing.java
- **Apresentação**: Formulários, listas, botões
- **Interação**: Eventos de usuário

### Controller (Controlador)
- **Lógica de Negócio**: Controllers para cada entidade
- **Coordenação**: Comunicação entre Model e View
- **Validação**: Regras de negócio e permissões

## Características Técnicas

- **Interface Gráfica**: Swing com tema escuro
- **Validação**: Entrada de dados e regras de negócio
- **Sistema de Sessão**: Usuário logado e contexto
- **CRUD Completo**: Operações de criação, leitura, atualização e exclusão
- **Tratamento de Erros**: Validação e mensagens de erro
- **Identificação Única**: UUIDs para todas as entidades
- **Armazenamento**: Dados em memória (não persistente)

## Requisitos do Sistema

- **Java**: Versão 8 ou superior
- **Sistema Operacional**: Windows, Linux ou macOS
- **Memória**: Mínimo 512MB RAM
- **Interface**: Suporte a interface gráfica

## Limitações Atuais

- **Persistência**: Dados não são salvos entre execuções
- **Rede**: Sistema local, sem comunicação de rede
- **Mídia**: URLs de imagens/vídeos, não upload de arquivos
- **Escalabilidade**: Limitado pela memória disponível

## Desenvolvimento Futuro

- **Banco de Dados**: Implementação de persistência
- **Upload de Arquivos**: Sistema de upload de mídia
- **API REST**: Interface para aplicações web
- **Notificações Push**: Sistema de notificações em tempo real
- **Busca Avançada**: Filtros e pesquisa de conteúdo
