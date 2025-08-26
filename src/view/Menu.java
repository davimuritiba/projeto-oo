package view;

import controller.UserController;
import model.Privacy;
import model.User;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Menu {
    private UserController userController;
    private UserWebView userView;
    private Scanner scanner;
    private User currentUser;
    
    public Menu(UserController userController, UserWebView userView) {
        this.userController = userController;
        this.userView = userView;
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
    }
    
    public void start() {
        System.out.println("🚀 Sistema de Gerenciamento de Usuários - Menu Interativo");
        System.out.println("=" .repeat(60));
        
        // Adicionar alguns usuários de exemplo
        addSampleUsers();
        
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = displayLoginMenu();
            } else {
                running = displayMainMenu();
            }
        }
        
        scanner.close();
        System.out.println("\n👋 Encerrando o sistema. Até logo!");
    }
    
    private boolean displayLoginMenu() {
        System.out.println("\n🔐 MENU DE ACESSO:");
        System.out.println("1. ➕ Criar novo usuário");
        System.out.println("2. 🔑 Entrar com usuário existente");
        System.out.println("0. 🚪 Sair");
        
        int choice = getIntInput("Escolha uma opção: ");
        
        switch (choice) {
            case 1:
                createUser();
                return true;
            case 2:
                loginUser();
                return true;
            case 0:
                return false;
            default:
                System.out.println("❌ Opção inválida! Tente novamente.");
                return true;
        }
    }
    
    private boolean displayMainMenu() {
        System.out.println("\n👤 Usuário logado: " + currentUser.getName() + " (" + currentUser.getEmail() + ")");
        System.out.println("📋 MENU PRINCIPAL:");
        System.out.println("1. ➕ Criar novo usuário");
        System.out.println("2. 👥 Listar todos os usuários");
        System.out.println("3. ✏️  Editar usuário");
        System.out.println("4. 🗑️  Excluir usuário");
        System.out.println("5. 🔍 Buscar usuário");
        System.out.println("6. 🌐 Gerar arquivos HTML");
        System.out.println("7. ℹ️  Informações do sistema");
        System.out.println("8. 🔓 Trocar usuário");
        System.out.println("0. 🚪 Sair");
        
        int choice = getIntInput("Escolha uma opção: ");
        
        switch (choice) {
            case 1:
                createUser();
                break;
            case 2:
                listUsers();
                break;
            case 3:
                editUser();
                break;
            case 4:
                deleteUser();
                break;
            case 5:
                searchUser();
                break;
            case 6:
                generateHTML();
                break;
            case 7:
                systemInfo();
                break;
            case 8:
                currentUser = null;
                System.out.println("🔓 Usuário deslogado. Retornando ao menu de acesso.");
                break;
            case 0:
                return false;
            default:
                System.out.println("❌ Opção inválida! Tente novamente.");
        }
        
        return true;
    }
    
    private void addSampleUsers() {
        System.out.println("📝 Adicionando usuários de exemplo...");
        userController.createUser("João Silva", "joao@email.com", "123456", Privacy.PUBLIC);
        userController.createUser("Maria Santos", "maria@email.com", "654321", Privacy.PRIVATE);
        userController.createUser("Pedro Costa", "pedro@email.com", "abcdef", Privacy.PUBLIC);
        System.out.println("✅ 3 usuários de exemplo criados!\n");
    }
    
    private void createUser() {
        System.out.println("\n➕ CRIAR NOVO USUÁRIO");
        System.out.println("-" .repeat(30));
        
        System.out.print("Nome: ");
        String name = scanner.nextLine();
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Senha: ");
        String password = scanner.nextLine();
        
        System.out.println("Privacidade:");
        System.out.println("1. Público");
        System.out.println("2. Privado");
        int privacyChoice = getIntInput("Escolha (1 ou 2): ");
        
        Privacy privacy = (privacyChoice == 1) ? Privacy.PUBLIC : Privacy.PRIVATE;
        
        User newUser = userController.createUser(name, email, password, privacy);
        System.out.println("\n✅ Usuário criado com sucesso!");
        System.out.println("ID: " + newUser.getId());
        System.out.println("Nome: " + newUser.getName());
        System.out.println("Email: " + newUser.getEmail());
        System.out.println("Privacidade: " + newUser.getPrivacy());
        
        // Se não há usuário logado, logar automaticamente
        if (currentUser == null) {
            currentUser = newUser;
            System.out.println("🔑 Usuário logado automaticamente!");
        }
    }
    
    private void loginUser() {
        System.out.println("\n🔑 ENTRAR COM USUÁRIO EXISTENTE");
        System.out.println("-" .repeat(40));
        
        if (userController.getAllUsers().isEmpty()) {
            System.out.println("📭 Nenhum usuário cadastrado no sistema.");
            return;
        }
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Senha: ");
        String password = scanner.nextLine();
        
        User user = findUserByCredentials(email, password);
        if (user != null) {
            currentUser = user;
            System.out.println("✅ Login realizado com sucesso! Bem-vindo, " + user.getName() + "!");
        } else {
            System.out.println("❌ Email ou senha incorretos!");
        }
    }
    
    private void listUsers() {
        System.out.println("\n👥 LISTA DE USUÁRIOS");
        System.out.println("-" .repeat(50));
        
        List<User> users = userController.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("📭 Nenhum usuário encontrado.");
            return;
        }
        
        System.out.printf("%-36s %-20s %-25s %-10s%n", "ID", "Nome", "Email", "Privacidade");
        System.out.println("-" .repeat(100));
        
        for (User user : users) {
            System.out.printf("%-36s %-20s %-25s %-10s%n", 
                user.getId().toString().substring(0, 8) + "...",
                user.getName(),
                user.getEmail(),
                user.getPrivacy());
        }
        
        System.out.println("\n📊 Total de usuários: " + users.size());
    }
    
    private void editUser() {
        System.out.println("\n✏️ EDITAR USUÁRIO");
        System.out.println("-" .repeat(30));
        
        if (userController.getAllUsers().isEmpty()) {
            System.out.println("📭 Nenhum usuário para editar.");
            return;
        }
        
        listUsers();
        System.out.print("\nDigite o ID do usuário (primeiros 8 caracteres): ");
        String idInput = scanner.nextLine();
        
        User userToEdit = findUserById(idInput);
        if (userToEdit == null) {
            System.out.println("❌ Usuário não encontrado!");
            return;
        }
        
        System.out.println("\n📝 Editando usuário: " + userToEdit.getName());
        System.out.println("Deixe em branco para manter o valor atual");
        
        System.out.print("Novo nome [" + userToEdit.getName() + "]: ");
        String newName = scanner.nextLine();
        if (newName.trim().isEmpty()) newName = userToEdit.getName();
        
        System.out.print("Novo email [" + userToEdit.getEmail() + "]: ");
        String newEmail = scanner.nextLine();
        if (newEmail.trim().isEmpty()) newEmail = userToEdit.getEmail();
        
        System.out.print("Nova senha [" + userToEdit.getPassword() + "]: ");
        String newPassword = scanner.nextLine();
        if (newPassword.trim().isEmpty()) newPassword = userToEdit.getPassword();
        
        System.out.println("Nova privacidade:");
        System.out.println("1. Público");
        System.out.println("2. Privado");
        int privacyChoice = getIntInput("Escolha (1 ou 2) [" + (userToEdit.getPrivacy() == Privacy.PUBLIC ? "1" : "2") + "]: ");
        
        Privacy newPrivacy = (privacyChoice == 1) ? Privacy.PUBLIC : Privacy.PRIVATE;
        
        boolean success = userController.editUser(userToEdit.getId(), newName, newEmail, newPassword, newPrivacy);
        if (success) {
            System.out.println("✅ Usuário editado com sucesso!");
            
            // Se o usuário editado é o usuário logado, atualizar a referência
            if (currentUser != null && currentUser.getId().equals(userToEdit.getId())) {
                currentUser = userToEdit;
            }
        } else {
            System.out.println("❌ Erro ao editar usuário!");
        }
    }
    
    private void deleteUser() {
        System.out.println("\n🗑️ EXCLUIR USUÁRIO");
        System.out.println("-" .repeat(30));
        
        if (userController.getAllUsers().isEmpty()) {
            System.out.println("📭 Nenhum usuário para excluir.");
            return;
        }
        
        listUsers();
        System.out.print("\nDigite o ID do usuário (primeiros 8 caracteres): ");
        String idInput = scanner.nextLine();
        
        User userToDelete = findUserById(idInput);
        if (userToDelete == null) {
            System.out.println("❌ Usuário não encontrado!");
            return;
        }
        
        System.out.println("\n⚠️  ATENÇÃO: Esta ação não pode ser desfeita!");
        System.out.println("Usuário a ser excluído: " + userToDelete.getName() + " (" + userToDelete.getEmail() + ")");
        System.out.print("Confirma a exclusão? (s/N): ");
        String confirm = scanner.nextLine();
        
        if (confirm.toLowerCase().equals("s") || confirm.toLowerCase().equals("sim")) {
            boolean success = userController.deleteUser(userToDelete.getId());
            if (success) {
                System.out.println("✅ Usuário excluído com sucesso!");
                
                // Se o usuário excluído é o usuário logado, deslogar
                if (currentUser != null && currentUser.getId().equals(userToDelete.getId())) {
                    currentUser = null;
                    System.out.println("🔓 Você foi deslogado pois seu usuário foi excluído.");
                }
            } else {
                System.out.println("❌ Erro ao excluir usuário!");
            }
        } else {
            System.out.println("❌ Exclusão cancelada.");
        }
    }
    
    private void searchUser() {
        System.out.println("\n🔍 BUSCAR USUÁRIO");
        System.out.println("-" .repeat(30));
        
        System.out.println("1. Buscar por nome");
        System.out.println("2. Buscar por email");
        int searchChoice = getIntInput("Escolha o tipo de busca: ");
        
        System.out.print("Digite o termo de busca: ");
        String searchTerm = scanner.nextLine();
        
        List<User> users = userController.getAllUsers();
        List<User> results = new java.util.ArrayList<>();
        
        for (User user : users) {
            if (searchChoice == 1 && user.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                results.add(user);
            } else if (searchChoice == 2 && user.getEmail().toLowerCase().contains(searchTerm.toLowerCase())) {
                results.add(user);
            }
        }
        
        if (results.isEmpty()) {
            System.out.println("🔍 Nenhum usuário encontrado para: " + searchTerm);
        } else {
            System.out.println("\n🔍 RESULTADOS DA BUSCA:");
            System.out.println("-" .repeat(50));
            for (User user : results) {
                System.out.println("ID: " + user.getId());
                System.out.println("Nome: " + user.getName());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Privacidade: " + user.getPrivacy());
                System.out.println("-" .repeat(30));
            }
            System.out.println("📊 Total de resultados: " + results.size());
        }
    }
    
    private void generateHTML() {
        System.out.println("\n🌐 GERAR ARQUIVOS HTML");
        System.out.println("-" .repeat(30));
        
        try {
            List<User> users = userController.getAllUsers();
            
            // Gerar formulário HTML
            String formHTML = userView.generateUserForm();
            java.nio.file.Files.write(java.nio.file.Path.of("formulario.html"), formHTML.getBytes());
            System.out.println("✅ formulario.html gerado");
            
            // Gerar lista HTML
            String listHTML = userView.generateUserList(users);
            java.nio.file.Files.write(java.nio.file.Path.of("lista_usuarios.html"), listHTML.getBytes());
            System.out.println("✅ lista_usuarios.html gerado");
            
            // Gerar mensagem de sucesso
            String successHTML = userView.generateSuccessMessage("Usuário criado com sucesso!");
            java.nio.file.Files.write(java.nio.file.Path.of("sucesso.html"), successHTML.getBytes());
            System.out.println("✅ sucesso.html gerado");
            
            System.out.println("\n🌐 Arquivos HTML gerados com sucesso!");
            System.out.println("📁 Abra os arquivos no seu navegador para visualizar.");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao gerar arquivos HTML: " + e.getMessage());
        }
    }
    
    private void systemInfo() {
        System.out.println("\nℹ️  INFORMAÇÕES DO SISTEMA");
        System.out.println("-" .repeat(30));
        
        List<User> users = userController.getAllUsers();
        int publicUsers = 0, privateUsers = 0;
        
        for (User user : users) {
            if (user.getPrivacy() == Privacy.PUBLIC) {
                publicUsers++;
            } else {
                privateUsers++;
            }
        }
        
        System.out.println("👥 Total de usuários: " + users.size());
        System.out.println("🌍 Usuários públicos: " + publicUsers);
        System.out.println("🔒 Usuários privados: " + privateUsers);
        System.out.println("📊 Arquitetura: MVC (Model-View-Controller)");
        System.out.println("💻 Linguagem: Java");
        System.out.println("🎯 Padrão: CRUD completo");
        
        if (currentUser != null) {
            System.out.println("\n👤 USUÁRIO ATUAL:");
            System.out.println("Nome: " + currentUser.getName());
            System.out.println("Email: " + currentUser.getEmail());
            System.out.println("Privacidade: " + currentUser.getPrivacy());
        }
    }
    
    private User findUserById(String idInput) {
        List<User> users = userController.getAllUsers();
        for (User user : users) {
            if (user.getId().toString().startsWith(idInput)) {
                return user;
            }
        }
        return null;
    }
    
    private User findUserByCredentials(String email, String password) {
        List<User> users = userController.getAllUsers();
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor, digite um número válido!");
            }
        }
    }
}
