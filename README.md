# Projeto OO - Sistema de Rede Social

Classes pensadas inicialmente para o projeto OO - Rede social:
- Classe User e seus serviços
- Classe Post e seus serviços
- Classe Mensagem e seus serviços
- Classe Pedido de Amizade e seus serviços
- Classe Grupo e seus serviços
- Classe Evento e seus serviços
- Classe Notificação e seus serviços

## 🏗️ Estrutura do Projeto

```
projeto-oo/
├── src/
│   ├── Main.java              # Classe principal
│   ├── controller/
│   │   ├── UserController.java # Controlador de usuários
│   │   ├── PostController.java # Controlador de posts
│   │   └── FriendController.java # Controlador de amizades
│   ├── model/
│   │   ├── User.java          # Modelo de usuário
│   │   ├── Post.java          # Modelo de post
│   │   └── Privacy.java       # Enum de privacidade
│   └── view/
│       ├── SocialAppSwing.java # Interface gráfica Swing
│       └── Menu.java          # Menu interativo console
├── bin/                       # Arquivos compilados
├── Makefile                   # Script de compilação
└── README.md                  # Este arquivo
```

## 🚀 Como Executar

### 1. Compilar o Projeto

```bash
# Usando Makefile (Windows)
make compile

# Ou compilar manualmente
javac -d bin src/model/Privacy.java
javac -d bin src/model/User.java
javac -d bin src/model/Post.java
javac -d bin src/controller/UserController.java
javac -d bin src/controller/PostController.java
javac -d bin src/controller/FriendController.java
javac -d bin src/view/Menu.java
javac -d bin src/view/SocialAppSwing.java
javac -cp bin -d bin src/Main.java
```

### 2. Executar o Sistema

```bash
# Interface gráfica (padrão)
java -cp bin Main

# Interface console
java -cp bin Main --console
```

## 🎯 Funcionalidades

### Interface Gráfica (Swing)
- **🔐 Login**: Sistema de autenticação
- **👥 Usuários**: Gerenciamento de usuários
- **📰 Posts**: Criação e visualização de posts
- **🤝 Amizades**: Sistema de amizades

### Interface Console
- **1. Criar novo usuário**: Cadastra um novo usuário
- **2. Entrar com usuário existente**: Login com email e senha
- **3. Listar todos os usuários**: Mostra usuários cadastrados
- **4. Editar usuário**: Modifica dados de usuário
- **5. Excluir usuário**: Remove usuário do sistema
- **6. Buscar usuário**: Procura por nome ou email
- **7. Informações do sistema**: Estatísticas
- **8. Trocar usuário**: Logout
- **0. Sair**: Encerra o sistema

## 🔐 Sistema de Login

- O sistema começa com 3 usuários de exemplo
- Para acessar as funcionalidades, é necessário fazer login
- Ao criar um usuário, ele é automaticamente logado
- É possível trocar de usuário a qualquer momento

## 📊 Usuários de Exemplo

O sistema é inicializado com os seguintes usuários:

1. **João Silva** - joao@email.com - 123456 - Público
2. **Maria Santos** - maria@email.com - 654321 - Privado  
3. **Pedro Costa** - pedro@email.com - abcdef - Público

## 🎨 Características

- **Interface dupla**: Swing (GUI) + Console
- **Interface amigável** com emojis e formatação
- **Validação de entrada** para números e dados
- **Sistema de sessão** com usuário logado
- **CRUD completo** para gerenciamento de usuários
- **Arquitetura MVC** bem estruturada
- **Tratamento de erros** robusto
- **Sistema de posts** e amizades

## 💻 Requisitos

- Java 8 ou superior
- Terminal/Console compatível com UTF-8 (para emojis)

## 🔧 Desenvolvimento

Este projeto demonstra:
- Padrão MVC (Model-View-Controller)
- Programação orientada a objetos
- Interface de usuário (Swing + Console)
- Gerenciamento de dados em memória
- Sistema de autenticação simples
- Sistema de rede social básico

## 📝 Notas

- Os dados são armazenados apenas em memória (não persistem entre execuções)
- O sistema usa UUIDs para identificação única dos usuários
- A privacidade pode ser PÚBLICA ou PRIVADA
- Todos os campos são obrigatórios ao criar usuários
- Suporte a posts e sistema de amizades
