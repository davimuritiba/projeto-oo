# Sistema de Gerenciamento de Usuários - Menu Interativo

Este projeto implementa um sistema de gerenciamento de usuários usando o padrão MVC (Model-View-Controller) com um menu interativo no console.

## 🏗️ Estrutura do Projeto

```
projeto-oo/
├── src/
│   ├── Main.java              # Classe principal
│   ├── controller/
│   │   └── UserController.java # Controlador de usuários
│   ├── model/
│   │   ├── User.java          # Modelo de usuário
│   │   └── Privacy.java       # Enum de privacidade
│   └── view/
│       ├── UserWebView.java   # Geração de HTML
│       └── Menu.java          # Menu interativo
├── bin/                       # Arquivos compilados
└── README.md                  # Este arquivo
```

## 🚀 Como Executar

### 1. Compilar o Projeto

```bash
# Compilar todas as classes
javac -cp bin -d bin src\Main.java

# Ou compilar arquivo por arquivo (se necessário)
javac -d bin src\model\Privacy.java
javac -d bin src\model\User.java
javac -d bin src\controller\UserController.java
javac -d bin src\view\UserWebView.java
javac -d bin src\view\Menu.java
javac -cp bin -d bin src\Main.java
```

### 2. Executar o Sistema

```bash
java -cp bin Main
```

## 🎯 Funcionalidades

### Menu de Acesso
- **1. Criar novo usuário**: Cadastra um novo usuário no sistema
- **2. Entrar com usuário existente**: Faz login com email e senha
- **0. Sair**: Encerra o sistema

### Menu Principal (após login)
- **1. Criar novo usuário**: Adiciona outro usuário
- **2. Listar todos os usuários**: Mostra todos os usuários cadastrados
- **3. Editar usuário**: Modifica dados de um usuário existente
- **4. Excluir usuário**: Remove um usuário do sistema
- **5. Buscar usuário**: Procura usuários por nome ou email
- **6. Gerar arquivos HTML**: Cria arquivos HTML para visualização web
- **7. Informações do sistema**: Mostra estatísticas e informações
- **8. Trocar usuário**: Faz logout e retorna ao menu de acesso
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

## 🌐 Geração de HTML

A opção 6 gera três arquivos HTML:
- `formulario.html`: Formulário para cadastro de usuários
- `lista_usuarios.html`: Lista de todos os usuários
- `sucesso.html`: Mensagem de confirmação

## 🎨 Características

- **Interface amigável** com emojis e formatação
- **Validação de entrada** para números e dados
- **Sistema de sessão** com usuário logado
- **CRUD completo** para gerenciamento de usuários
- **Arquitetura MVC** bem estruturada
- **Tratamento de erros** robusto

## 💻 Requisitos

- Java 8 ou superior
- Terminal/Console compatível com UTF-8 (para emojis)

## 🔧 Desenvolvimento

Este projeto demonstra:
- Padrão MVC (Model-View-Controller)
- Programação orientada a objetos
- Interface de usuário no console
- Gerenciamento de dados em memória
- Geração de conteúdo HTML
- Sistema de autenticação simples

## 📝 Notas

- Os dados são armazenados apenas em memória (não persistem entre execuções)
- O sistema usa UUIDs para identificação única dos usuários
- A privacidade pode ser PÚBLICA ou PRIVADA
- Todos os campos são obrigatórios ao criar usuários

