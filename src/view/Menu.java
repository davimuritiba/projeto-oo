package view;

import controller.UserController;
import controller.PostController;
import controller.FriendController;
import model.Post;
import model.Privacy;
import model.User;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private UserController userController;
    private PostController postController;
    private FriendController friendController;
    private Scanner scanner;
    private User currentUser;
    
    public Menu(UserController userController, PostController postController, FriendController friendController) {
        this.userController = userController;
        this.postController = postController;
        this.friendController = friendController;
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
        System.out.println("6. ℹ️  Informações do sistema");
        System.out.println("7. 🔓 Trocar usuário");
        System.out.println("8. 📰 Menu de Posts");
        System.out.println("9. 🤝 Menu de Amizades");
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
                systemInfo();
                break;
            case 7:
                currentUser = null;
                System.out.println("🔓 Usuário deslogado. Retornando ao menu de acesso.");
                break;
            case 8:
                displayPostMenu();
                break;
            case 9:
                displayFriendMenu();
                break;
            case 0:
                return false;
            default:
                System.out.println("❌ Opção inválida! Tente novamente.");
        }
        
        return true;
    }

    private void displayPostMenu() {
        boolean inPosts = true;
        while (inPosts) {
            System.out.println("\n📰 MENU DE POSTS:");
            System.out.println("1. ➕ Criar post");
            System.out.println("2. 📄 Listar meus posts");
            System.out.println("3. 🌍 Listar todos os posts");
            System.out.println("4. 👍 Curtir post");
            System.out.println("5. 👎 Descurtir post");
            System.out.println("6. ✏️ Editar post");
            System.out.println("7. 🗑️ Excluir post");
            System.out.println("0. ↩️ Voltar");

            int choice = getIntInput("Escolha uma opção: ");
            switch (choice) {
                case 1:
                    createPostFlow();
                    break;
                case 2:
                    listMyPosts();
                    break;
                case 3:
                    listAllPosts();
                    break;
                case 4:
                    likePostFlow();
                    break;
                case 5:
                    unlikePostFlow();
                    break;
                case 6:
                    editPostFlow();
                    break;
                case 7:
                    deletePostFlow();
                    break;
                case 0:
                    inPosts = false;
                    break;
                default:
                    System.out.println("❌ Opção inválida! Tente novamente.");
            }
        }
    }

    private void displayFriendMenu() {
        if (ensureLoggedIn() == false) return;
        boolean inFriends = true;
        while (inFriends) {
            System.out.println("\n🤝 MENU DE AMIZADES:");
            System.out.println("1. ➕ Enviar solicitação");
            System.out.println("2. ✅ Aceitar solicitação");
            System.out.println("3. ❌ Recusar solicitação");
            System.out.println("4. 👥 Listar amigos");
            System.out.println("5. 📥 Solicitações recebidas");
            System.out.println("6. 📤 Solicitações enviadas");
            System.out.println("7. 🗑️ Remover amigo");
            System.out.println("0. ↩️ Voltar");

            int choice = getIntInput("Escolha uma opção: ");
            switch (choice) {
                case 1:
                    sendFriendRequestFlow();
                    break;
                case 2:
                    acceptFriendRequestFlow();
                    break;
                case 3:
                    declineFriendRequestFlow();
                    break;
                case 4:
                    listFriendsFlow();
                    break;
                case 5:
                    listReceivedRequestsFlow();
                    break;
                case 6:
                    listSentRequestsFlow();
                    break;
                case 7:
                    removeFriendFlow();
                    break;
                case 0:
                    inFriends = false;
                    break;
                default:
                    System.out.println("❌ Opção inválida! Tente novamente.");
            }
        }
    }

    private void sendFriendRequestFlow() {
        System.out.print("ID do usuário alvo (primeiros 8 caracteres): ");
        String idPart = scanner.nextLine();
        User target = findUserById(idPart);
        if (target == null) { System.out.println("❌ Usuário não encontrado."); return; }
        boolean ok = friendController.sendFriendRequest(currentUser.getId(), target.getId());
        System.out.println(ok ? "📨 Solicitação enviada." : "⚠️ Não foi possível enviar a solicitação.");
    }

    private void acceptFriendRequestFlow() {
        System.out.print("ID do solicitante (primeiros 8 caracteres): ");
        String idPart = scanner.nextLine();
        User requester = findUserById(idPart);
        if (requester == null) { System.out.println("❌ Usuário não encontrado."); return; }
        boolean ok = friendController.acceptRequest(currentUser.getId(), requester.getId());
        System.out.println(ok ? "✅ Solicitação aceita." : "⚠️ Nenhuma solicitação pendente deste usuário.");
    }

    private void declineFriendRequestFlow() {
        System.out.print("ID do solicitante (primeiros 8 caracteres): ");
        String idPart = scanner.nextLine();
        User requester = findUserById(idPart);
        if (requester == null) { System.out.println("❌ Usuário não encontrado."); return; }
        boolean ok = friendController.declineRequest(currentUser.getId(), requester.getId());
        System.out.println(ok ? "❌ Solicitação recusada." : "⚠️ Nenhuma solicitação pendente deste usuário.");
    }

    private void listFriendsFlow() {
        var ids = friendController.getFriends(currentUser.getId());
        if (ids.isEmpty()) { System.out.println("👥 Você ainda não tem amigos."); return; }
        System.out.println("\n👥 SEUS AMIGOS:");
        for (User u : userController.getAllUsers()) {
            if (ids.contains(u.getId())) {
                System.out.println("- " + u.getName() + " (" + u.getEmail() + ")");
            }
        }
    }

    private void listReceivedRequestsFlow() {
        var ids = friendController.getPendingReceived(currentUser.getId());
        if (ids.isEmpty()) { System.out.println("📥 Nenhuma solicitação recebida."); return; }
        System.out.println("\n📥 SOLICITAÇÕES RECEBIDAS:");
        for (User u : userController.getAllUsers()) {
            if (ids.contains(u.getId())) {
                System.out.println("- " + u.getName() + " (" + u.getEmail() + ")");
            }
        }
    }

    private void listSentRequestsFlow() {
        var ids = friendController.getPendingSent(currentUser.getId());
        if (ids.isEmpty()) { System.out.println("📤 Nenhuma solicitação enviada."); return; }
        System.out.println("\n📤 SOLICITAÇÕES ENVIADAS:");
        for (User u : userController.getAllUsers()) {
            if (ids.contains(u.getId())) {
                System.out.println("- " + u.getName() + " (" + u.getEmail() + ")");
            }
        }
    }

    private void removeFriendFlow() {
        System.out.print("ID do amigo (primeiros 8 caracteres): ");
        String idPart = scanner.nextLine();
        User friend = findUserById(idPart);
        if (friend == null) { System.out.println("❌ Usuário não encontrado."); return; }
        boolean ok = friendController.removeFriend(currentUser.getId(), friend.getId());
        System.out.println(ok ? "🗑️ Amizade removida." : "⚠️ Vocês não são amigos.");
    }

    private void createPostFlow() {
        if (ensureLoggedIn() == false) return;
        System.out.println("\n➕ CRIAR POST");
        System.out.print("Conteúdo: ");
        String content = scanner.nextLine();
        System.out.print("Tipo (TEXT/IMAGE/VIDEO): ");
        String postType = scanner.nextLine();
        Post post = postController.createPost(currentUser.getId(), content, postType);
        System.out.println("✅ Post criado! ID: " + post.getId());
    }

    private void listMyPosts() {
        if (ensureLoggedIn() == false) return;
        System.out.println("\n📄 MEUS POSTS");
        List<Post> posts = postController.getPostsByUser(currentUser.getId());
        printPosts(posts);
    }

    private void listAllPosts() {
        System.out.println("\n🌍 TODOS OS POSTS");
        List<Post> posts = postController.getAllPosts();
        printPosts(posts);
    }

    private void likePostFlow() {
        if (ensureLoggedIn() == false) return;
        System.out.print("ID do post (primeiros 8 caracteres): ");
        String idPart = scanner.nextLine();
        Post post = findPostByIdPrefix(idPart);
        if (post == null) {
            System.out.println("❌ Post não encontrado.");
            return;
        }
        boolean ok = postController.likePost(post.getId(), currentUser.getId());
        System.out.println(ok ? "👍 Like adicionado." : "⚠️ Você já curtiu este post.");
    }

    private void unlikePostFlow() {
        if (ensureLoggedIn() == false) return;
        System.out.print("ID do post (primeiros 8 caracteres): ");
        String idPart = scanner.nextLine();
        Post post = findPostByIdPrefix(idPart);
        if (post == null) {
            System.out.println("❌ Post não encontrado.");
            return;
        }
        boolean ok = postController.unlikePost(post.getId(), currentUser.getId());
        System.out.println(ok ? "👎 Like removido." : "⚠️ Você não tinha curtido este post.");
    }

    private void editPostFlow() {
        if (ensureLoggedIn() == false) return;
        System.out.print("ID do post (primeiros 8 caracteres): ");
        String idPart = scanner.nextLine();
        Post post = findPostByIdPrefix(idPart);
        if (post == null) {
            System.out.println("❌ Post não encontrado.");
            return;
        }
        if (!post.getUserId().equals(currentUser.getId())) {
            System.out.println("❌ Você só pode editar seus próprios posts.");
            return;
        }
        System.out.print("Novo conteúdo: ");
        String newContent = scanner.nextLine();
        System.out.print("Novo tipo (TEXT/IMAGE/VIDEO): ");
        String newType = scanner.nextLine();
        boolean ok = postController.editPost(post.getId(), newContent, newType);
        System.out.println(ok ? "✅ Post editado." : "❌ Erro ao editar post.");
    }

    private void deletePostFlow() {
        if (ensureLoggedIn() == false) return;
        System.out.print("ID do post (primeiros 8 caracteres): ");
        String idPart = scanner.nextLine();
        Post post = findPostByIdPrefix(idPart);
        if (post == null) {
            System.out.println("❌ Post não encontrado.");
            return;
        }
        if (!post.getUserId().equals(currentUser.getId())) {
            System.out.println("❌ Você só pode excluir seus próprios posts.");
            return;
        }
        boolean ok = postController.deletePost(post.getId());
        System.out.println(ok ? "✅ Post excluído." : "❌ Erro ao excluir post.");
    }

    private boolean ensureLoggedIn() {
        if (currentUser == null) {
            System.out.println("🔒 Você precisa estar logado para realizar esta ação.");
            return false;
        }
        return true;
    }

    private Post findPostByIdPrefix(String idPrefix) {
        List<Post> all = postController.getAllPosts();
        for (Post p : all) {
            if (p.getId().toString().startsWith(idPrefix)) {
                return p;
            }
        }
        return null;
    }

    private void printPosts(List<Post> posts) {
        if (posts == null || posts.isEmpty()) {
            System.out.println("📭 Nenhum post encontrado.");
            return;
        }
        System.out.println("-".repeat(80));
        for (Post p : posts) {
            System.out.println("ID: " + p.getId());
            System.out.println("Autor: " + p.getUserId());
            System.out.println("Tipo: " + p.getPostType());
            System.out.println("Curtidas: " + p.getLikeCount());
            System.out.println("Conteúdo: " + p.getContent());
            System.out.println("-".repeat(80));
        }
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
