package view;

import controller.UserController;
import controller.PostController;
import controller.FriendController;
import model.User;
import model.Post;
import model.Privacy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.UUID;

public class SocialAppSwing {
    private UserController userController;
    private PostController postController;
    private FriendController friendController;
    
    private User currentUser;
    private JFrame mainFrame;
    private JTabbedPane tabbedPane;
    private JList<String> userList;
    private JList<String> postList;
    private DefaultListModel<String> userListModel;
    private DefaultListModel<String> postListModel;
    
    public SocialAppSwing() {
        userController = new UserController();
        postController = new PostController();
        friendController = new FriendController();
        
        addSampleUsers();
        createAndShowGUI();
    }
    
    private void addSampleUsers() {
        userController.createUser("João Silva", "joao@email.com", "123456", Privacy.PUBLIC);
        userController.createUser("Maria Santos", "maria@email.com", "654321", Privacy.PRIVATE);
        userController.createUser("Pedro Costa", "pedro@email.com", "abcdef", Privacy.PUBLIC);
    }
    
    private void createAndShowGUI() {
        mainFrame = new JFrame("Social Networking App");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        
        // Criar abas
        tabbedPane.addTab("🔐 Login", createLoginPanel());
        tabbedPane.addTab("👥 Usuários", createUsersPanel());
        tabbedPane.addTab("📰 Posts", createPostsPanel());
        tabbedPane.addTab("🤝 Amizades", createFriendsPanel());
        
        mainFrame.add(tabbedPane);
        mainFrame.setVisible(true);
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Social Networking App");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Formulário de login
        JPanel loginForm = new JPanel(new GridLayout(4, 2, 10, 10));
        loginForm.setMaximumSize(new Dimension(300, 150));
        loginForm.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Entrar");
        
        loginForm.add(new JLabel("Email:"));
        loginForm.add(emailField);
        loginForm.add(new JLabel("Senha:"));
        loginForm.add(passwordField);
        loginForm.add(new JLabel(""));
        loginForm.add(loginButton);
        
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            
            User user = findUserByCredentials(email, password);
            if (user != null) {
                currentUser = user;
                JOptionPane.showMessageDialog(mainFrame, "Login realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                tabbedPane.setSelectedIndex(1);
                refreshAllLists();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Email ou senha incorretos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Formulário de criação
        JPanel createForm = new JPanel(new GridLayout(6, 2, 10, 10));
        createForm.setMaximumSize(new Dimension(300, 200));
        createForm.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextField nameField = new JTextField();
        JTextField newEmailField = new JTextField();
        JPasswordField newPasswordField = new JPasswordField();
        JComboBox<Privacy> privacyCombo = new JComboBox<>(Privacy.values());
        JButton createButton = new JButton("Criar conta");
        
        createForm.add(new JLabel("Criar nova conta:"));
        createForm.add(new JLabel(""));
        createForm.add(new JLabel("Nome:"));
        createForm.add(nameField);
        createForm.add(new JLabel("Email:"));
        createForm.add(newEmailField);
        createForm.add(new JLabel("Senha:"));
        createForm.add(newPasswordField);
        createForm.add(new JLabel("Privacidade:"));
        createForm.add(privacyCombo);
        createForm.add(new JLabel(""));
        createForm.add(createButton);
        
        createButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = newEmailField.getText();
            String password = new String(newPasswordField.getPassword());
            Privacy privacy = (Privacy) privacyCombo.getSelectedItem();
            
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Todos os campos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            userController.createUser(name, email, password, privacy);
            JOptionPane.showMessageDialog(mainFrame, "Usuário criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            nameField.setText("");
            newEmailField.setText("");
            newPasswordField.setText("");
            refreshAllLists();
        });
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(loginForm);
        panel.add(Box.createVerticalStrut(20));
        panel.add(createForm);
        
        return panel;
    }
    
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane scrollPane = new JScrollPane(userList);
        
        JButton refreshButton = new JButton("🔄 Atualizar");
        refreshButton.addActionListener(e -> refreshUsersList());
        
        panel.add(new JLabel("Lista de Usuários:", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        refreshUsersList();
        return panel;
    }
    
    private JPanel createPostsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Painel superior para criar posts
        JPanel createPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        JTextArea contentArea = new JTextArea(3, 20);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"TEXT", "IMAGE", "VIDEO"});
        JButton createButton = new JButton("📝 Criar Post");
        
        createPanel.add(new JLabel("Conteúdo:"));
        createPanel.add(contentScroll);
        createPanel.add(new JLabel("Tipo:"));
        createPanel.add(typeCombo);
        createPanel.add(new JLabel(""));
        createPanel.add(createButton);
        
        createButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(mainFrame, "Você precisa estar logado para criar posts!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String content = contentArea.getText();
            String type = (String) typeCombo.getSelectedItem();
            
            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "O conteúdo não pode estar vazio!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            postController.createPost(currentUser.getId(), content, type);
            JOptionPane.showMessageDialog(mainFrame, "Post criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            contentArea.setText("");
            refreshPostsList();
        });
        
        // Lista de posts
        postListModel = new DefaultListModel<>();
        postList = new JList<>(postListModel);
        JScrollPane postScroll = new JScrollPane(postList);
        
        JButton refreshPostsButton = new JButton("🔄 Atualizar Posts");
        refreshPostsButton.addActionListener(e -> refreshPostsList());
        
        panel.add(createPanel, BorderLayout.NORTH);
        panel.add(postScroll, BorderLayout.CENTER);
        panel.add(refreshPostsButton, BorderLayout.SOUTH);
        
        refreshPostsList();
        return panel;
    }
    
    private JPanel createFriendsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Painel superior para enviar solicitações
        JPanel requestPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        List<User> users = userController.getAllUsers();
        JComboBox<User> userCombo = new JComboBox<>(users.toArray(new User[0]));
        JButton sendButton = new JButton("📨 Enviar Solicitação");
        
        requestPanel.add(new JLabel("Selecione usuário:"));
        requestPanel.add(userCombo);
        requestPanel.add(new JLabel(""));
        requestPanel.add(sendButton);
        
        sendButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(mainFrame, "Você precisa estar logado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User target = (User) userCombo.getSelectedItem();
            if (target == null) {
                JOptionPane.showMessageDialog(mainFrame, "Selecione um usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (target.getId().equals(currentUser.getId())) {
                JOptionPane.showMessageDialog(mainFrame, "Você não pode enviar solicitação para si mesmo!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = friendController.sendFriendRequest(currentUser.getId(), target.getId());
            if (success) {
                JOptionPane.showMessageDialog(mainFrame, "Solicitação enviada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Não foi possível enviar a solicitação!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Lista de amigos
        JList<String> friendsList = new JList<>();
        JScrollPane friendsScroll = new JScrollPane(friendsList);
        
        JButton refreshFriendsButton = new JButton("🔄 Atualizar Amigos");
        refreshFriendsButton.addActionListener(e -> {
            if (currentUser != null) {
                refreshFriendsList(friendsList);
            }
        });
        
        panel.add(requestPanel, BorderLayout.NORTH);
        panel.add(friendsScroll, BorderLayout.CENTER);
        panel.add(refreshFriendsButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void refreshUsersList() {
        userListModel.clear();
        List<User> users = userController.getAllUsers();
        
        for (User user : users) {
            String display = String.format("%s (%s) - %s", 
                user.getName(), 
                user.getEmail(), 
                user.getPrivacy());
            userListModel.addElement(display);
        }
    }
    
    private void refreshPostsList() {
        postListModel.clear();
        List<Post> posts = postController.getAllPosts();
        
        for (Post post : posts) {
            String display = String.format("ID: %s | Tipo: %s | Curtidas: %d | Conteúdo: %s", 
                post.getId().toString().substring(0, 8) + "...",
                post.getPostType(),
                post.getLikeCount(),
                post.getContent().length() > 50 ? 
                    post.getContent().substring(0, 50) + "..." : 
                    post.getContent());
            postListModel.addElement(display);
        }
    }
    
    private void refreshFriendsList(JList<String> friendsList) {
        DefaultListModel<String> model = new DefaultListModel<>();
        
        if (currentUser != null) {
            var friendIds = friendController.getFriends(currentUser.getId());
            List<User> allUsers = userController.getAllUsers();
            
            for (User user : allUsers) {
                if (friendIds.contains(user.getId())) {
                    model.addElement(user.getName() + " (" + user.getEmail() + ")");
                }
            }
            
            if (model.isEmpty()) {
                model.addElement("Você ainda não tem amigos.");
            }
        }
        
        friendsList.setModel(model);
    }
    
    private void refreshAllLists() {
        refreshUsersList();
        refreshPostsList();
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
}
