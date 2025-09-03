package view;

import controller.UserController;
import controller.PostController;
import controller.FriendController;
import controller.MessageController;
import controller.FriendRequestController;
import controller.GroupController;
import controller.GroupChatController;
import controller.EventController;
import controller.FeedController;
import controller.NotificationController;
import model.User;
import model.Post;
import model.TextPost;
import model.ImagePost;
import model.VideoPost;
import model.Privacy;
import model.Message;
import model.FriendRequest;
import model.Group;
import model.GroupMessage;
import model.Event;
import model.Notification;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class SocialAppSwing {
    private UserController userController;
    private PostController postController;
    private FriendController friendController;
    private MessageController messageController;
    private FriendRequestController friendRequestController;
    private GroupController groupController;
    private GroupChatController groupChatController;
    private EventController eventController;
    private FeedController feedService;
    private NotificationController notificationController;
    
    private User currentUser;
    private JFrame mainFrame;
    private JTabbedPane tabbedPane;
    private JList<String> userList;
    private JList<String> postList;
    private DefaultListModel<String> userListModel;
    private DefaultListModel<String> postListModel;
    private JList<String> feedList;
    private JList<String> filteredFeedList;
    private JList<String> friendPostsList;
    private JComboBox<String> friendsCombo;

    private static final String FONT_FAMILY = getAvailableFont();
    private static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 24);
    private static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.BOLD, 16);
    private static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.PLAIN, 14);

    private static String getAvailableFont() {
        String[] fontNames = {"Segoe UI", "Arial", "SansSerif", "Dialog"};
        for (String fontName : fontNames) {
            Font font = new Font(fontName, Font.PLAIN, 12);
            if (font.getFamily().equals(fontName)) {
                return fontName;
            }
        }
        return Font.SANS_SERIF; // Fallback padrão
    }

    private static final Color DARK_BG = new Color(18, 18, 18);
    private static final Color DARKER_BG = new Color(12, 12, 12);
    private static final Color CARD_BG = new Color(30, 30, 30);
    private static final Color ACCENT_COLOR = new Color(147, 112, 219);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(147, 112, 219);
    private static final Color BORDER_COLOR = new Color(60, 60, 60);

    private JComboBox<User> friendsUserCombo;
    private JComboBox<User> messagesRecipientCombo;

    private JList<String> notificationList;
    private DefaultListModel<String> notificationListModel;
    private JLabel notificationBadge;
    
    public SocialAppSwing() {

        try {

            Font systemFont = new Font("Dialog", Font.PLAIN, 14);
            UIManager.put("TextField.font", systemFont);
            UIManager.put("PasswordField.font", systemFont);
            UIManager.put("TextArea.font", systemFont);
            UIManager.put("ComboBox.font", systemFont);

            UIManager.put("TextField.background", DARKER_BG);
            UIManager.put("TextField.foreground", TEXT_PRIMARY);
            UIManager.put("TextField.caretForeground", TEXT_PRIMARY);
            UIManager.put("PasswordField.background", DARKER_BG);
            UIManager.put("PasswordField.foreground", TEXT_PRIMARY);
            UIManager.put("PasswordField.caretForeground", TEXT_PRIMARY);

            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            
        } catch (Exception e) {

            System.err.println("Erro ao configurar fontes: " + e.getMessage());
        }
        
        userController = new UserController();
        notificationController = new NotificationController(userController);
        friendRequestController = new FriendRequestController(userController);
        friendRequestController.setNotificationController(notificationController);
        friendController = new FriendController(friendRequestController);
        postController = new PostController();
        postController.setNotificationController(notificationController);
        postController.setUserController(userController);
        messageController = new MessageController(friendController, userController);
        messageController.setNotificationController(notificationController);
        groupController = new GroupController(userController);
        groupChatController = new GroupChatController(groupController, userController);
        eventController = new EventController(userController);
        feedService = new FeedController(postController, friendController, userController);
        
        addSampleUsers();
        addSampleGroups();
        addSampleEvents();
        createAndShowGUI();
    }
    
    private void addSampleUsers() {
        User joao = userController.createUser("João Silva", "joao@email.com", "123456", Privacy.PUBLIC);
        User maria = userController.createUser("Maria Santos", "maria@email.com", "654321", Privacy.PRIVATE);
        User pedro = userController.createUser("Pedro Costa", "pedro@email.com", "abcdef", Privacy.PUBLIC);

        friendRequestController.sendFriendRequest(joao.getId(), maria.getId());
        friendRequestController.sendFriendRequest(joao.getId(), pedro.getId());

        friendController.acceptRequest(maria.getId(), joao.getId());
        friendController.acceptRequest(pedro.getId(), joao.getId());

        postController.createTextPost(joao.getId(), "Acabei de fazer um passeio incrível de bicicleta pela cidade! 🚴‍♂️");
        postController.createTextPost(maria.getId(), "Dia lindo para pedalar! Quem mais gosta de mountain bike? 🏔️");
        postController.createImagePost(pedro.getId(), "https://example.com/bike.jpg", "Minha nova bicicleta! Estou muito feliz com a compra.");
        postController.createTextPost(joao.getId(), "Dicas para iniciantes em ciclismo: sempre use capacete e mantenha a bicicleta em bom estado!");
        postController.createVideoPost(maria.getId(), "https://example.com/video.mp4", "Tutorial de manutenção básica da bicicleta", 300);
        postController.createTextPost(pedro.getId(), "Participando da minha primeira corrida de bicicleta no próximo fim de semana! 🏁");

        List<Post> allPosts = postController.getAllPosts();
        if (allPosts.size() >= 3) {
            postController.likePost(allPosts.get(0).getId(), maria.getId());
            postController.likePost(allPosts.get(0).getId(), pedro.getId());
            postController.likePost(allPosts.get(1).getId(), joao.getId());
            postController.likePost(allPosts.get(2).getId(), joao.getId());
            postController.likePost(allPosts.get(2).getId(), maria.getId());
        }
    }
    
    private void addSampleGroups() {

        List<User> users = userController.getAllUsers();
        if (users.size() >= 3) {
            User joao = users.get(0); // João Silva
            User maria = users.get(1); // Maria Santos
            User pedro = users.get(2); // Pedro Costa
            
            Group mtbGroup = groupController.createGroup("MTB Enthusiasts", "Grupo para amantes de mountain bike", joao.getId(), Privacy.PUBLIC);
            Group urbanGroup = groupController.createGroup("Ciclistas Urbanos", "Ciclistas que preferem a cidade", joao.getId(), Privacy.PRIVATE);
            Group toursGroup = groupController.createGroup("Bike Tours", "Organização de passeios de bicicleta", maria.getId(), Privacy.PUBLIC);

            groupController.addMember(mtbGroup.getId(), maria.getId(), joao.getId());
            groupController.addMember(mtbGroup.getId(), pedro.getId(), joao.getId());
            groupController.addMember(urbanGroup.getId(), maria.getId(), joao.getId());
            groupController.addMember(toursGroup.getId(), joao.getId(), maria.getId());
            groupController.addMember(toursGroup.getId(), pedro.getId(), maria.getId());

            groupChatController.sendMessage(mtbGroup.getId(), joao.getId(), "Olá pessoal! Bem-vindos ao grupo de MTB!");
            groupChatController.sendMessage(mtbGroup.getId(), maria.getId(), "Oi! Adoro mountain bike! Alguém quer fazer um passeio no fim de semana?");
            groupChatController.sendMessage(mtbGroup.getId(), pedro.getId(), "Eu topo! Qual trilha vocês sugerem?");
            groupChatController.sendMessage(mtbGroup.getId(), joao.getId(), "Tem uma trilha incrível na Serra da Cantareira!");
            
            groupChatController.sendMessage(toursGroup.getId(), maria.getId(), "Vamos organizar um passeio pela cidade?");
            groupChatController.sendMessage(toursGroup.getId(), joao.getId(), "Ótima ideia! Qual rota vocês preferem?");
        }
    }
    
    private void addSampleEvents() {

        List<User> users = userController.getAllUsers();
        if (users.size() >= 3) {
            User joao = users.get(0); // João Silva
            User maria = users.get(1); // Maria Santos
            User pedro = users.get(2); // Pedro Costa

            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
            LocalDateTime nextWeek = LocalDateTime.now().plusDays(7).withHour(10).withMinute(0);
            LocalDateTime nextMonth = LocalDateTime.now().plusDays(30).withHour(16).withMinute(30);
            
            eventController.createEvent("Passeio de MTB na Serra", "Passeio de mountain bike na Serra da Cantareira", tomorrow, joao.getId());
            eventController.createEvent("Workshop de Ciclismo", "Workshop sobre técnicas de ciclismo urbano", nextWeek, maria.getId());
            eventController.createEvent("Corrida de Bicicleta", "Corrida beneficente de bicicleta pela cidade", nextMonth, pedro.getId());
        }
    }
    
    private void refreshAllComboBoxes() {
        if (friendsUserCombo != null) {
            refreshComboBoxData(friendsUserCombo);
        }
        if (messagesRecipientCombo != null) {
            refreshComboBoxData(messagesRecipientCombo);
        }
    }
    
    private void refreshComboBoxData(JComboBox<User> comboBox) {
        comboBox.removeAllItems();
        List<User> users = userController.getAllUsers();
        for (User user : users) {
            comboBox.addItem(user);
        }
    }
    
    private void createAndShowGUI() {
        mainFrame = new JFrame("🌙 Rede Social MTB - Dark Mode");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 700);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.getContentPane().setBackground(DARK_BG);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(DARK_BG);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setFont(FONT_SUBTITLE);

        tabbedPane.addTab("🔐 Login", createLoginPanel());
        tabbedPane.addTab("👥 Usuários", createUsersPanel());
        tabbedPane.addTab("📰 Posts", createPostsPanel());
        tabbedPane.addTab("🤝 Amizades", createFriendsPanel());
        tabbedPane.addTab("💬 Mensagens", createMessagesPanel());
        tabbedPane.addTab("👥 Grupos", createGroupsPanel());
        tabbedPane.addTab("📅 Eventos", createEventsPanel());
        tabbedPane.addTab("📱 Feed", createFeedPanel());
        tabbedPane.addTab("🔔 Notificações", createNotificationsPanel());
        
        mainFrame.add(tabbedPane);
        mainFrame.setVisible(true);
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JLabel titleLabel = new JLabel(" Rede Social PS");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel loginForm = new JPanel(new GridLayout(4, 2, 8, 8));
        loginForm.setMaximumSize(new Dimension(350, 180));
        loginForm.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginForm.setBackground(CARD_BG);
        loginForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JTextField emailField = new JTextField();
        emailField.setFont(new Font("Dialog", Font.PLAIN, 14)); // Fonte do sistema
        emailField.setBackground(DARKER_BG);
        emailField.setForeground(TEXT_PRIMARY);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        emailField.setCaretColor(TEXT_PRIMARY);
        emailField.setOpaque(true);
        emailField.setEditable(true);
        
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 14)); // Fonte do sistema
        passwordField.setBackground(DARKER_BG);
        passwordField.setForeground(TEXT_PRIMARY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        passwordField.setCaretColor(TEXT_PRIMARY);
        passwordField.setOpaque(true);
        passwordField.setEditable(true);
        
        JButton loginButton = new JButton("🔐 Entrar");
        loginButton.setFont(FONT_BUTTON);
        loginButton.setBackground(ACCENT_COLOR);
        loginButton.setForeground(TEXT_PRIMARY);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setOpaque(true);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        
        JLabel emailLabel = new JLabel("📧 Email:");
        emailLabel.setFont(FONT_BODY);
        emailLabel.setForeground(TEXT_SECONDARY);
        loginForm.add(emailLabel);
        loginForm.add(emailField);
        
        JLabel passwordLabel = new JLabel("🔒 Senha:");
        passwordLabel.setFont(FONT_BODY);
        passwordLabel.setForeground(TEXT_SECONDARY);
        loginForm.add(passwordLabel);
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

        JPanel createForm = new JPanel(new GridLayout(6, 2, 12, 12));
        createForm.setMaximumSize(new Dimension(400, 250));
        createForm.setAlignmentX(Component.CENTER_ALIGNMENT);
        createForm.setBackground(CARD_BG);
        createForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel createTitleLabel = new JLabel("✨ Criar nova conta:");
        createTitleLabel.setFont(FONT_SUBTITLE);
        createTitleLabel.setForeground(TEXT_PRIMARY);
        
        JLabel nameLabel = new JLabel("👤 Nome:");
        nameLabel.setFont(FONT_BODY);
        nameLabel.setForeground(TEXT_SECONDARY);
        
        JLabel newEmailLabel = new JLabel("📧 Email:");
        newEmailLabel.setFont(FONT_BODY);
        newEmailLabel.setForeground(TEXT_SECONDARY);
        
        JLabel newPasswordLabel = new JLabel("🔒 Senha:");
        newPasswordLabel.setFont(FONT_BODY);
        newPasswordLabel.setForeground(TEXT_SECONDARY);
        
        JLabel privacyLabel = new JLabel("🔐 Privacidade:");
        privacyLabel.setFont(FONT_BODY);
        privacyLabel.setForeground(TEXT_SECONDARY);

        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Dialog", Font.PLAIN, 14)); // Fonte do sistema
        nameField.setBackground(DARKER_BG);
        nameField.setForeground(TEXT_PRIMARY);
        nameField.setPreferredSize(new Dimension(250, 35));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        nameField.setCaretColor(TEXT_PRIMARY);
        nameField.setOpaque(true);
        nameField.setEditable(true);
        
        JTextField newEmailField = new JTextField(20);
        newEmailField.setFont(new Font("Dialog", Font.PLAIN, 14)); // Fonte do sistema
        newEmailField.setBackground(DARKER_BG);
        newEmailField.setForeground(TEXT_PRIMARY);
        newEmailField.setPreferredSize(new Dimension(250, 35));
        newEmailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        newEmailField.setCaretColor(TEXT_PRIMARY);
        newEmailField.setOpaque(true);
        newEmailField.setEditable(true);
        
        JPasswordField newPasswordField = new JPasswordField(20);
        newPasswordField.setFont(new Font("Dialog", Font.PLAIN, 14)); // Fonte do sistema
        newPasswordField.setBackground(DARKER_BG);
        newPasswordField.setForeground(TEXT_PRIMARY);
        newPasswordField.setPreferredSize(new Dimension(250, 35));
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        newPasswordField.setCaretColor(TEXT_PRIMARY);
        newPasswordField.setOpaque(true);
        newPasswordField.setEditable(true);
        
        JComboBox<Privacy> privacyCombo = new JComboBox<>(Privacy.values());
        privacyCombo.setFont(FONT_BODY);
        privacyCombo.setBackground(DARKER_BG);
        privacyCombo.setForeground(TEXT_PRIMARY);
        privacyCombo.setPreferredSize(new Dimension(250, 35));
        
        JButton createButton = new JButton(" Criar conta");
        createButton.setFont(FONT_BUTTON);
        createButton.setBackground(ACCENT_COLOR);
        createButton.setForeground(TEXT_PRIMARY);
        createButton.setBorderPainted(false);
        createButton.setFocusPainted(false);
        createButton.setOpaque(true);
        createButton.setPreferredSize(new Dimension(200, 40));
        createButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        
        createForm.add(createTitleLabel);
        createForm.add(new JLabel(""));
        createForm.add(nameLabel);
        createForm.add(nameField);
        createForm.add(newEmailLabel);
        createForm.add(newEmailField);
        createForm.add(newPasswordLabel);
        createForm.add(newPasswordField);
        createForm.add(privacyLabel);
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
        panel.setBackground(DARK_BG);

        JLabel titleLabel = new JLabel("👥 Lista de Usuários");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setFont(FONT_BODY);
        userList.setBackground(DARKER_BG);
        userList.setForeground(TEXT_PRIMARY);
        userList.setSelectionBackground(ACCENT_COLOR);
        userList.setSelectionForeground(TEXT_PRIMARY);
        userList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setBackground(DARKER_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JButton refreshButton = new JButton("🔄 Atualizar");
        refreshButton.setFont(FONT_BUTTON);
        refreshButton.setBackground(ACCENT_COLOR);
        refreshButton.setForeground(TEXT_PRIMARY);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setOpaque(true);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        refreshButton.addActionListener(e -> refreshUsersList());
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        refreshUsersList();
        return panel;
    }
    
    private JPanel createPostsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JPanel createPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        createPanel.setBackground(CARD_BG);
        createPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel contentLabel = new JLabel("📝 Conteúdo:");
        contentLabel.setFont(FONT_BODY);
        contentLabel.setForeground(TEXT_SECONDARY);
        
        JLabel typeLabel = new JLabel("🏷️ Tipo:");
        typeLabel.setFont(FONT_BODY);
        typeLabel.setForeground(TEXT_SECONDARY);
        
        JTextArea contentArea = new JTextArea(3, 20);
        contentArea.setFont(FONT_BODY);
        contentArea.setBackground(DARKER_BG);
        contentArea.setForeground(TEXT_PRIMARY);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBackground(DARKER_BG);
        contentScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        contentScroll.setPreferredSize(new Dimension(250, 80));
        contentScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"TEXT", "IMAGE", "VIDEO"});
        typeCombo.setFont(FONT_BODY);
        typeCombo.setBackground(DARKER_BG);
        typeCombo.setForeground(TEXT_PRIMARY);
        
        JButton createButton = new JButton(" Criar Post");
        createButton.setFont(FONT_BUTTON);
        createButton.setBackground(ACCENT_COLOR);
        createButton.setForeground(TEXT_PRIMARY);
        createButton.setBorderPainted(false);
        createButton.setFocusPainted(false);
        createButton.setOpaque(true);
        createButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        
        createPanel.add(contentLabel);
        createPanel.add(contentScroll);
        createPanel.add(typeLabel);
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

        postListModel = new DefaultListModel<>();
        postList = new JList<>(postListModel);
        postList.setFont(FONT_BODY);
        postList.setBackground(DARKER_BG);
        postList.setForeground(TEXT_PRIMARY);
        postList.setSelectionBackground(ACCENT_COLOR);
        postList.setSelectionForeground(TEXT_PRIMARY);
        postList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JScrollPane postScroll = new JScrollPane(postList);
        postScroll.setBackground(DARKER_BG);
        postScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JButton refreshPostsButton = new JButton(" Atualizar Posts");
        refreshPostsButton.setFont(FONT_BUTTON);
        refreshPostsButton.setBackground(ACCENT_COLOR);
        refreshPostsButton.setForeground(TEXT_PRIMARY);
        refreshPostsButton.setBorderPainted(false);
        refreshPostsButton.setFocusPainted(false);
        refreshPostsButton.setOpaque(true);
        refreshPostsButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        refreshPostsButton.addActionListener(e -> refreshPostsList());
        
        panel.add(createPanel, BorderLayout.NORTH);
        panel.add(postScroll, BorderLayout.CENTER);
        panel.add(refreshPostsButton, BorderLayout.SOUTH);
        
        refreshPostsList();
        return panel;
    }
    
    private JComboBox<User> createUserComboBox() {
        List<User> users = userController.getAllUsers();
        JComboBox<User> comboBox = new JComboBox<>(users.toArray(new User[0]));

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof User) {
                    User user = (User) value;
                    setText(user.getName() + " (" + user.getEmail() + ")");
                }
                return this;
            }
        });
        
        return comboBox;
    }
    
    private JPanel createFriendsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JTabbedPane friendsTabbedPane = new JTabbedPane();
        friendsTabbedPane.setBackground(DARK_BG);
        friendsTabbedPane.setForeground(TEXT_PRIMARY);

        JPanel sendRequestPanel = createSendRequestPanel();
        friendsTabbedPane.addTab("📨 Enviar", sendRequestPanel);

        JPanel receivedRequestsPanel = createReceivedRequestsPanel();
        friendsTabbedPane.addTab("📥 Recebidas", receivedRequestsPanel);

        JPanel sentRequestsPanel = createSentRequestsPanel();
        friendsTabbedPane.addTab("📤 Enviadas", sentRequestsPanel);

        JPanel friendsListPanel = createFriendsListPanel();
        friendsTabbedPane.addTab("👥 Amigos", friendsListPanel);
        
        panel.add(friendsTabbedPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createSendRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JPanel requestPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        requestPanel.setBackground(CARD_BG);
        requestPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel userLabel = new JLabel("👤 Selecione usuário:");
        userLabel.setFont(FONT_BODY);
        userLabel.setForeground(TEXT_SECONDARY);
        
        friendsUserCombo = createUserComboBox();
        JButton sendButton = new JButton("📨 Enviar Solicitação");
        sendButton.setFont(FONT_BUTTON);
        sendButton.setBackground(ACCENT_COLOR);
        sendButton.setForeground(TEXT_PRIMARY);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setOpaque(true);
        sendButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        
        requestPanel.add(userLabel);
        requestPanel.add(friendsUserCombo);
        requestPanel.add(new JLabel(""));
        requestPanel.add(sendButton);
        
        sendButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(mainFrame, "Você precisa estar logado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User target = (User) friendsUserCombo.getSelectedItem();
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
                JOptionPane.showMessageDialog(mainFrame, "Solicitação enviada para " + target.getName() + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                refreshAllLists();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Não foi possível enviar a solicitação!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(requestPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createReceivedRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JLabel titleLabel = new JLabel("📥 Solicitações Recebidas");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JList<String> receivedList = new JList<>();
        receivedList.setFont(FONT_BODY);
        receivedList.setBackground(DARKER_BG);
        receivedList.setForeground(TEXT_PRIMARY);
        receivedList.setSelectionBackground(ACCENT_COLOR);
        receivedList.setSelectionForeground(TEXT_PRIMARY);
        receivedList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JScrollPane scrollPane = new JScrollPane(receivedList);
        scrollPane.setBackground(DARKER_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(DARK_BG);
        
        JButton acceptButton = new JButton("✅ Aceitar");
        acceptButton.setFont(FONT_BUTTON);
        acceptButton.setBackground(new Color(0, 150, 0));
        acceptButton.setForeground(TEXT_PRIMARY);
        acceptButton.setBorderPainted(false);
        acceptButton.setFocusPainted(false);
        acceptButton.setOpaque(true);
        
        JButton rejectButton = new JButton("❌ Rejeitar");
        rejectButton.setFont(FONT_BUTTON);
        rejectButton.setBackground(new Color(200, 0, 0));
        rejectButton.setForeground(TEXT_PRIMARY);
        rejectButton.setBorderPainted(false);
        rejectButton.setFocusPainted(false);
        rejectButton.setOpaque(true);
        
        JButton refreshButton = new JButton("🔄 Atualizar");
        refreshButton.setFont(FONT_BUTTON);
        refreshButton.setBackground(ACCENT_COLOR);
        refreshButton.setForeground(TEXT_PRIMARY);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setOpaque(true);
        
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(refreshButton);

        acceptButton.addActionListener(e -> {
            if (currentUser != null) {
                String selected = receivedList.getSelectedValue();
                if (selected != null && !selected.equals("Nenhuma solicitação pendente")) {

                    String senderName = selected.substring(4, selected.indexOf(" ("));

                    User sender = findUserByName(senderName);
                    if (sender != null) {

                        boolean success = friendController.acceptRequest(currentUser.getId(), sender.getId());
                        if (success) {
                            JOptionPane.showMessageDialog(mainFrame, "Solicitação aceita! Agora vocês são amigos!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                            refreshReceivedRequestsList(receivedList);
                            refreshAllLists();
                        } else {
                            JOptionPane.showMessageDialog(mainFrame, "Erro ao aceitar solicitação!", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Selecione uma solicitação válida!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        rejectButton.addActionListener(e -> {
            if (currentUser != null) {
                String selected = receivedList.getSelectedValue();
                if (selected != null && !selected.equals("Nenhuma solicitação pendente")) {

                    String senderName = selected.substring(4, selected.indexOf(" ("));

                    User sender = findUserByName(senderName);
                    if (sender != null) {

                        boolean success = friendController.declineRequest(currentUser.getId(), sender.getId());
                        if (success) {
                            JOptionPane.showMessageDialog(mainFrame, "Solicitação rejeitada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                            refreshReceivedRequestsList(receivedList);
                        } else {
                            JOptionPane.showMessageDialog(mainFrame, "Erro ao rejeitar solicitação!", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Selecione uma solicitação válida!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        refreshButton.addActionListener(e -> {
            if (currentUser != null) {
                refreshReceivedRequestsList(receivedList);
            }
        });
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        if (currentUser != null) {
            refreshReceivedRequestsList(receivedList);
        }
        
        return panel;
    }
    
    private JPanel createSentRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JLabel titleLabel = new JLabel("📤 Solicitações Enviadas");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JList<String> sentList = new JList<>();
        sentList.setFont(FONT_BODY);
        sentList.setBackground(DARKER_BG);
        sentList.setForeground(TEXT_PRIMARY);
        sentList.setSelectionBackground(ACCENT_COLOR);
        sentList.setSelectionForeground(TEXT_PRIMARY);
        sentList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JScrollPane scrollPane = new JScrollPane(sentList);
        scrollPane.setBackground(DARKER_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JButton refreshButton = new JButton("🔄 Atualizar");
        refreshButton.setFont(FONT_BUTTON);
        refreshButton.setBackground(ACCENT_COLOR);
        refreshButton.setForeground(TEXT_PRIMARY);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setOpaque(true);
        refreshButton.addActionListener(e -> {
            if (currentUser != null) {
                refreshSentRequestsList(sentList);
            }
        });
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        if (currentUser != null) {
            refreshSentRequestsList(sentList);
        }
        
        return panel;
    }
    
    private JPanel createFriendsListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JLabel titleLabel = new JLabel("👥 Lista de Amigos");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JList<String> friendsList = new JList<>();
        friendsList.setFont(FONT_BODY);
        friendsList.setBackground(DARKER_BG);
        friendsList.setForeground(TEXT_PRIMARY);
        friendsList.setSelectionBackground(ACCENT_COLOR);
        friendsList.setSelectionForeground(TEXT_PRIMARY);
        friendsList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JScrollPane scrollPane = new JScrollPane(friendsList);
        scrollPane.setBackground(DARKER_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JButton refreshFriendsButton = new JButton("🔄 Atualizar Amigos");
        refreshFriendsButton.setFont(FONT_BUTTON);
        refreshFriendsButton.setBackground(ACCENT_COLOR);
        refreshFriendsButton.setForeground(TEXT_PRIMARY);
        refreshFriendsButton.setBorderPainted(false);
        refreshFriendsButton.setFocusPainted(false);
        refreshFriendsButton.setOpaque(true);
        refreshFriendsButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        refreshFriendsButton.addActionListener(e -> {
            if (currentUser != null) {
                refreshFriendsList(friendsList);
            }
        });
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshFriendsButton, BorderLayout.SOUTH);

        if (currentUser != null) {
            refreshFriendsList(friendsList);
        }
        
        return panel;
    }
    
    private void refreshReceivedRequestsList(JList<String> list) {
        if (currentUser == null) return;
        
        DefaultListModel<String> model = new DefaultListModel<>();
        List<FriendRequest> pendingRequests = friendRequestController.getPendingRequestsReceived(currentUser.getId());
        
        if (pendingRequests.isEmpty()) {
            model.addElement("Nenhuma solicitação pendente");
        } else {
            for (FriendRequest request : pendingRequests) {
                User sender = userController.getUserById(request.getSenderId());
                if (sender != null) {
                    String display = String.format("De: %s (%s) - %s", 
                        sender.getName(), 
                        sender.getEmail(), 
                        request.getRelativeDate());
                    model.addElement(display);
                }
            }
        }
        
        list.setModel(model);
    }
    
    private void refreshSentRequestsList(JList<String> list) {
        if (currentUser == null) return;
        
        DefaultListModel<String> model = new DefaultListModel<>();
        List<FriendRequest> sentRequests = friendRequestController.getRequestsSent(currentUser.getId());
        
        if (sentRequests.isEmpty()) {
            model.addElement("Nenhuma solicitação enviada");
        } else {
            for (FriendRequest request : sentRequests) {
                User receiver = userController.getUserById(request.getReceiverId());
                if (receiver != null) {
                    String status = request.getStatus() == FriendRequest.RequestStatus.PENDING ? "⏳" : 
                                  request.getStatus() == FriendRequest.RequestStatus.ACCEPTED ? "✅" : "❌";
                    String display = String.format("%s Para: %s (%s) - %s", 
                        status,
                        receiver.getName(), 
                        receiver.getEmail(), 
                        request.getRelativeDate());
                    model.addElement(display);
                }
            }
        }
        
        list.setModel(model);
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

            String displayContent = post.getDisplayContent();

            if (displayContent.length() > 100) {
                displayContent = displayContent.substring(0, 100) + "...";
            }
            
            String display = String.format("[%s] %s | Likes: %d | %s", 
                post.getPostType(),
                displayContent,
                post.getLikeCount(),
                post.getFormattedDate());
                
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
    
    private JPanel createFeedPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JTabbedPane feedTabbedPane = new JTabbedPane();
        feedTabbedPane.setBackground(DARK_BG);
        feedTabbedPane.setForeground(TEXT_PRIMARY);

        JPanel mainFeedPanel = createMainFeedPanel();
        feedTabbedPane.addTab("📱 Feed Principal", mainFeedPanel);

        JPanel feedByTypePanel = createFeedByTypePanel();
        feedTabbedPane.addTab("🔍 Por Tipo", feedByTypePanel);

        JPanel feedByFriendPanel = createFeedByFriendPanel();
        feedTabbedPane.addTab("👤 Por Amigo", feedByFriendPanel);

        JPanel feedStatsPanel = createFeedStatsPanel();
        feedTabbedPane.addTab("📊 Estatísticas", feedStatsPanel);

        panel.add(feedTabbedPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createMainFeedPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BG);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(DARK_BG);
        controlPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JButton refreshButton = new JButton("🔄 Atualizar Feed");
        refreshButton.setFont(FONT_BUTTON);
        refreshButton.setBackground(ACCENT_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setOpaque(true);
        refreshButton.addActionListener(e -> refreshMainFeed());

        JButton mostLikedButton = new JButton("❤️ Mais Curtidos");
        mostLikedButton.setFont(FONT_BUTTON);
        mostLikedButton.setBackground(ACCENT_COLOR);
        mostLikedButton.setForeground(Color.WHITE);
        mostLikedButton.setOpaque(true);
        mostLikedButton.addActionListener(e -> showMostLikedFeed());

        JButton todayButton = new JButton("📅 Hoje");
        todayButton.setFont(FONT_BUTTON);
        todayButton.setBackground(ACCENT_COLOR);
        todayButton.setForeground(Color.WHITE);
        todayButton.setOpaque(true);
        todayButton.addActionListener(e -> showTodayFeed());

        JButton thisWeekButton = new JButton("📆 Esta Semana");
        thisWeekButton.setFont(FONT_BUTTON);
        thisWeekButton.setBackground(ACCENT_COLOR);
        thisWeekButton.setForeground(Color.WHITE);
        thisWeekButton.setOpaque(true);
        thisWeekButton.addActionListener(e -> showThisWeekFeed());

        controlPanel.add(refreshButton);
        controlPanel.add(mostLikedButton);
        controlPanel.add(todayButton);
        controlPanel.add(thisWeekButton);

        JList<String> feedList = new JList<>();
        feedList.setBackground(DARK_BG);
        feedList.setForeground(TEXT_PRIMARY);
        feedList.setFont(FONT_BODY);
        feedList.setSelectionBackground(ACCENT_COLOR);
        feedList.setSelectionForeground(Color.WHITE);
        feedList.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(feedList);
        scrollPane.setBackground(DARK_BG);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBackground(DARK_BG);
        actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton likeButton = new JButton("❤️ Curtir");
        likeButton.setFont(FONT_BUTTON);
        likeButton.setBackground(ACCENT_COLOR);
        likeButton.setForeground(Color.WHITE);
        likeButton.setOpaque(true);
        likeButton.addActionListener(e -> likeSelectedPost(feedList));

        JButton viewDetailsButton = new JButton("👁️ Ver Detalhes");
        viewDetailsButton.setFont(FONT_BUTTON);
        viewDetailsButton.setBackground(ACCENT_COLOR);
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setOpaque(true);
        viewDetailsButton.addActionListener(e -> viewPostDetails(feedList));

        actionPanel.add(likeButton);
        actionPanel.add(viewDetailsButton);

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        this.feedList = feedList;

        return panel;
    }
    
    private JPanel createFeedByTypePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BG);

        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.setBackground(DARK_BG);
        typePanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel typeLabel = new JLabel("Tipo de Post:");
        typeLabel.setForeground(TEXT_PRIMARY);
        typeLabel.setFont(FONT_BODY);

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"TEXT", "IMAGE", "VIDEO"});
        typeCombo.setBackground(DARK_BG);
        typeCombo.setForeground(TEXT_PRIMARY);
        typeCombo.setFont(FONT_BODY);

        JButton filterButton = new JButton("🔍 Filtrar");
        filterButton.setFont(FONT_BUTTON);
        filterButton.setBackground(ACCENT_COLOR);
        filterButton.setForeground(Color.WHITE);
        filterButton.setOpaque(true);
        filterButton.addActionListener(e -> filterFeedByType(typeCombo.getSelectedItem().toString()));

        typePanel.add(typeLabel);
        typePanel.add(typeCombo);
        typePanel.add(filterButton);

        JList<String> filteredList = new JList<>();
        filteredList.setBackground(DARK_BG);
        filteredList.setForeground(TEXT_PRIMARY);
        filteredList.setFont(FONT_BODY);
        filteredList.setSelectionBackground(ACCENT_COLOR);
        filteredList.setSelectionForeground(Color.WHITE);
        filteredList.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(filteredList);
        scrollPane.setBackground(DARK_BG);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

        panel.add(typePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        this.filteredFeedList = filteredList;

        return panel;
    }
    
    private JPanel createFeedByFriendPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BG);

        JPanel friendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        friendPanel.setBackground(DARK_BG);
        friendPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel friendLabel = new JLabel("Amigo:");
        friendLabel.setForeground(TEXT_PRIMARY);
        friendLabel.setFont(FONT_BODY);

        JComboBox<String> friendCombo = new JComboBox<>();
        friendCombo.setBackground(DARK_BG);
        friendCombo.setForeground(TEXT_PRIMARY);
        friendCombo.setFont(FONT_BODY);

        JButton refreshFriendsButton = new JButton("🔄 Atualizar");
        refreshFriendsButton.setFont(FONT_BUTTON);
        refreshFriendsButton.setBackground(ACCENT_COLOR);
        refreshFriendsButton.setForeground(Color.WHITE);
        refreshFriendsButton.setOpaque(true);
        refreshFriendsButton.addActionListener(e -> refreshFriendsCombo(friendCombo));

        JButton filterFriendButton = new JButton("🔍 Ver Posts");
        filterFriendButton.setFont(FONT_BUTTON);
        filterFriendButton.setBackground(ACCENT_COLOR);
        filterFriendButton.setForeground(Color.WHITE);
        filterFriendButton.setOpaque(true);
        filterFriendButton.addActionListener(e -> filterFeedByFriend(friendCombo.getSelectedItem().toString()));

        friendPanel.add(friendLabel);
        friendPanel.add(friendCombo);
        friendPanel.add(refreshFriendsButton);
        friendPanel.add(filterFriendButton);

        JList<String> friendPostsList = new JList<>();
        friendPostsList.setBackground(DARK_BG);
        friendPostsList.setForeground(TEXT_PRIMARY);
        friendPostsList.setFont(FONT_BODY);
        friendPostsList.setSelectionBackground(ACCENT_COLOR);
        friendPostsList.setSelectionForeground(Color.WHITE);
        friendPostsList.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(friendPostsList);
        scrollPane.setBackground(DARK_BG);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

        panel.add(friendPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        this.friendPostsList = friendPostsList;
        this.friendsCombo = friendCombo;

        return panel;
    }
    
    private JPanel createFeedStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BG);

        JTextArea statsArea = new JTextArea();
        statsArea.setBackground(DARK_BG);
        statsArea.setForeground(TEXT_PRIMARY);
        statsArea.setFont(FONT_BODY);
        statsArea.setEditable(false);
        statsArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        statsArea.setLineWrap(true);
        statsArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(statsArea);
        scrollPane.setBackground(DARK_BG);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(DARK_BG);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JButton refreshStatsButton = new JButton("🔄 Atualizar Estatísticas");
        refreshStatsButton.setFont(FONT_BUTTON);
        refreshStatsButton.setBackground(ACCENT_COLOR);
        refreshStatsButton.setForeground(Color.WHITE);
        refreshStatsButton.setOpaque(true);
        refreshStatsButton.addActionListener(e -> {
            if (currentUser != null) {
                String stats = feedService.getFeedStats(currentUser.getId());
                statsArea.setText(stats);
            } else {
                statsArea.setText("Faça login para ver as estatísticas do seu feed.");
            }
        });

        buttonPanel.add(refreshStatsButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private void refreshAllLists() {
        refreshUsersList();
        refreshPostsList();
        refreshAllComboBoxes();

        if (currentUser != null && tabbedPane.getSelectedIndex() == 3) { // Aba de amizades

            refreshAllFriendsLists();
        }

        if (currentUser != null && tabbedPane.getSelectedIndex() == 5) { // Aba de grupos

            SwingUtilities.invokeLater(() -> {
                mainFrame.revalidate();
                mainFrame.repaint();
            });
        }

        if (currentUser != null && tabbedPane.getSelectedIndex() == 5) { // Aba de grupos (inclui chat)

            SwingUtilities.invokeLater(() -> {
                mainFrame.revalidate();
                mainFrame.repaint();
            });
        }

        if (currentUser != null && tabbedPane.getSelectedIndex() == 6) { // Aba de eventos

            SwingUtilities.invokeLater(() -> {
                mainFrame.revalidate();
                mainFrame.repaint();
            });
        }
    }

    private void refreshAllFriendsLists() {
        if (currentUser != null) {


            SwingUtilities.invokeLater(() -> {

                mainFrame.revalidate();
                mainFrame.repaint();
            });
        }
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
    
    private JPanel createGroupsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JTabbedPane groupsTabbedPane = new JTabbedPane();
        groupsTabbedPane.setBackground(DARK_BG);
        groupsTabbedPane.setForeground(TEXT_PRIMARY);

        JPanel createGroupPanel = createCreateGroupPanel();
        groupsTabbedPane.addTab("✨ Criar", createGroupPanel);

        JPanel manageGroupPanel = createManageGroupPanel();
        groupsTabbedPane.addTab("⚙️ Gerenciar", manageGroupPanel);

        JPanel exploreGroupPanel = createExploreGroupPanel();
        groupsTabbedPane.addTab("🔍 Explorar", exploreGroupPanel);

        JPanel myGroupsPanel = createMyGroupsPanel();
        groupsTabbedPane.addTab("👥 Meus Grupos", myGroupsPanel);

        JPanel groupChatPanel = createGroupChatPanel();
        groupsTabbedPane.addTab("💬 Chat", groupChatPanel);
        
        panel.add(groupsTabbedPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createCreateGroupPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JLabel titleLabel = new JLabel("✨ Criar Novo Grupo");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel nameLabel = new JLabel("🏷️ Nome:");
        nameLabel.setFont(FONT_BODY);
        nameLabel.setForeground(TEXT_SECONDARY);
        
        JTextField nameField = new JTextField(20);
        nameField.setFont(FONT_BODY);
        nameField.setBackground(DARKER_BG);
        nameField.setForeground(TEXT_PRIMARY);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JLabel descLabel = new JLabel("📝 Descrição:");
        descLabel.setFont(FONT_BODY);
        descLabel.setForeground(TEXT_SECONDARY);
        
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setFont(FONT_BODY);
        descArea.setBackground(DARKER_BG);
        descArea.setForeground(TEXT_PRIMARY);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBackground(DARKER_BG);
        descScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        descScroll.setPreferredSize(new Dimension(250, 80));
        
        JLabel privacyLabel = new JLabel("🔐 Privacidade:");
        privacyLabel.setFont(FONT_BODY);
        privacyLabel.setForeground(TEXT_SECONDARY);
        
        JComboBox<Privacy> privacyCombo = new JComboBox<>(Privacy.values());
        privacyCombo.setFont(FONT_BODY);
        privacyCombo.setBackground(DARKER_BG);
        privacyCombo.setForeground(TEXT_PRIMARY);
        
        JButton createButton = new JButton("✨ Criar Grupo");
        createButton.setFont(FONT_BUTTON);
        createButton.setBackground(ACCENT_COLOR);
        createButton.setForeground(TEXT_PRIMARY);
        createButton.setBorderPainted(false);
        createButton.setFocusPainted(false);
        createButton.setOpaque(true);
        createButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(descLabel);
        formPanel.add(descScroll);
        formPanel.add(privacyLabel);
        formPanel.add(privacyCombo);
        formPanel.add(new JLabel(""));
        formPanel.add(createButton);
        
        createButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(mainFrame, "Você precisa estar logado para criar grupos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String name = nameField.getText().trim();
            String description = descArea.getText().trim();
            Privacy privacy = (Privacy) privacyCombo.getSelectedItem();
            
            if (name.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Nome e descrição são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Group newGroup = groupController.createGroup(name, description, currentUser.getId(), privacy);
                JOptionPane.showMessageDialog(mainFrame, "Grupo '" + newGroup.getName() + "' criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
                nameField.setText("");
                descArea.setText("");
                refreshAllLists();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Erro ao criar grupo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createManageGroupPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JLabel titleLabel = new JLabel("⚙️ Gerenciar Grupos");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JList<String> manageList = new JList<>();
        manageList.setFont(FONT_BODY);
        manageList.setBackground(DARKER_BG);
        manageList.setForeground(TEXT_PRIMARY);
        manageList.setSelectionBackground(ACCENT_COLOR);
        manageList.setSelectionForeground(TEXT_PRIMARY);
        manageList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JScrollPane scrollPane = new JScrollPane(manageList);
        scrollPane.setBackground(DARKER_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(DARK_BG);
        
        JButton editButton = new JButton("✏️ Editar");
        editButton.setFont(FONT_BUTTON);
        editButton.setBackground(ACCENT_COLOR);
        editButton.setForeground(TEXT_PRIMARY);
        editButton.setBorderPainted(false);
        editButton.setFocusPainted(false);
        editButton.setOpaque(true);
        
        JButton deleteButton = new JButton("🗑️ Deletar");
        deleteButton.setFont(FONT_BUTTON);
        deleteButton.setBackground(new Color(200, 0, 0));
        deleteButton.setForeground(TEXT_PRIMARY);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setOpaque(true);
        
        JButton refreshButton = new JButton("🔄 Atualizar");
        refreshButton.setFont(FONT_BUTTON);
        refreshButton.setBackground(ACCENT_COLOR);
        refreshButton.setForeground(TEXT_PRIMARY);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setOpaque(true);
        
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        editButton.addActionListener(e -> {
            if (currentUser != null) {
                String selected = manageList.getSelectedValue();
                if (selected != null && !selected.equals("Nenhum grupo para gerenciar")) {
                    showEditGroupDialog(selected);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Selecione um grupo para editar!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        deleteButton.addActionListener(e -> {
            if (currentUser != null) {
                String selected = manageList.getSelectedValue();
                if (selected != null && !selected.equals("Nenhum grupo para gerenciar")) {
                    showDeleteGroupDialog(selected);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Selecione um grupo para deletar!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        refreshButton.addActionListener(e -> {
            if (currentUser != null) {
                refreshManageGroupsList(manageList);
            }
        });
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        if (currentUser != null) {
            refreshManageGroupsList(manageList);
        }
        
        return panel;
    }
    
    private JPanel createExploreGroupPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JLabel titleLabel = new JLabel("🔍 Explorar Grupos");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBackground(DARK_BG);
        
        JTextField searchField = new JTextField(20);
        searchField.setFont(FONT_BODY);
        searchField.setBackground(DARKER_BG);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JButton searchButton = new JButton("🔍 Buscar");
        searchButton.setFont(FONT_BUTTON);
        searchButton.setBackground(ACCENT_COLOR);
        searchButton.setForeground(TEXT_PRIMARY);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setOpaque(true);
        
        searchPanel.add(new JLabel("🔍 Buscar grupos:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JList<String> exploreList = new JList<>();
        exploreList.setFont(FONT_BODY);
        exploreList.setBackground(DARKER_BG);
        exploreList.setForeground(TEXT_PRIMARY);
        exploreList.setSelectionBackground(ACCENT_COLOR);
        exploreList.setSelectionForeground(TEXT_PRIMARY);
        exploreList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JScrollPane scrollPane = new JScrollPane(exploreList);
        scrollPane.setBackground(DARKER_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBackground(DARK_BG);
        
        JButton joinButton = new JButton("➕ Entrar");
        joinButton.setFont(FONT_BUTTON);
        joinButton.setBackground(new Color(0, 150, 0));
        joinButton.setForeground(TEXT_PRIMARY);
        joinButton.setBorderPainted(false);
        joinButton.setFocusPainted(false);
        joinButton.setOpaque(true);
        
        JButton viewButton = new JButton("👁️ Ver Detalhes");
        viewButton.setFont(FONT_BUTTON);
        viewButton.setBackground(ACCENT_COLOR);
        viewButton.setForeground(TEXT_PRIMARY);
        viewButton.setBorderPainted(false);
        viewButton.setFocusPainted(false);
        viewButton.setOpaque(true);
        
        actionPanel.add(joinButton);
        actionPanel.add(viewButton);

        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                refreshExploreGroupsList(exploreList, searchTerm);
            } else {
                refreshExploreGroupsList(exploreList, null);
            }
        });
        
        joinButton.addActionListener(e -> {
            if (currentUser != null) {
                String selected = exploreList.getSelectedValue();
                if (selected != null && !selected.equals("Nenhum grupo encontrado")) {
                    showJoinGroupDialog(selected);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Selecione um grupo para entrar!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        viewButton.addActionListener(e -> {
            String selected = exploreList.getSelectedValue();
            if (selected != null && !selected.equals("Nenhum grupo encontrado")) {
                showGroupDetailsDialog(selected);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Selecione um grupo para ver detalhes!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(DARK_BG);
        
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(searchPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(actionPanel);
        
        panel.add(mainPanel, BorderLayout.CENTER);

        refreshExploreGroupsList(exploreList, null);
        
        return panel;
    }
    
    private JPanel createMyGroupsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JLabel titleLabel = new JLabel("👥 Meus Grupos");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JList<String> myGroupsList = new JList<>();
        myGroupsList.setFont(FONT_BODY);
        myGroupsList.setBackground(DARKER_BG);
        myGroupsList.setForeground(TEXT_PRIMARY);
        myGroupsList.setSelectionBackground(ACCENT_COLOR);
        myGroupsList.setSelectionForeground(TEXT_PRIMARY);
        myGroupsList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JScrollPane scrollPane = new JScrollPane(myGroupsList);
        scrollPane.setBackground(DARKER_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(DARK_BG);
        
        JButton leaveButton = new JButton("🚪 Sair");
        leaveButton.setFont(FONT_BUTTON);
        leaveButton.setBackground(new Color(200, 100, 0));
        leaveButton.setForeground(TEXT_PRIMARY);
        leaveButton.setBorderPainted(false);
        leaveButton.setFocusPainted(false);
        leaveButton.setOpaque(true);
        
        JButton refreshButton = new JButton("🔄 Atualizar");
        refreshButton.setFont(FONT_BUTTON);
        refreshButton.setBackground(ACCENT_COLOR);
        refreshButton.setForeground(TEXT_PRIMARY);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setOpaque(true);
        
        buttonPanel.add(leaveButton);
        buttonPanel.add(refreshButton);

        leaveButton.addActionListener(e -> {
            if (currentUser != null) {
                String selected = myGroupsList.getSelectedValue();
                if (selected != null && !selected.equals("Você não participa de nenhum grupo")) {
                    showLeaveGroupDialog(selected);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Selecione um grupo para sair!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        refreshButton.addActionListener(e -> {
            if (currentUser != null) {
                refreshMyGroupsList(myGroupsList);
            }
        });
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        if (currentUser != null) {
            refreshMyGroupsList(myGroupsList);
        }
        
        return panel;
    }
    
    private JPanel createGroupChatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JLabel titleLabel = new JLabel("💬 Chat dos Grupos");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BG);

        JPanel groupSelectionPanel = new JPanel(new FlowLayout());
        groupSelectionPanel.setBackground(CARD_BG);
        groupSelectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel groupLabel = new JLabel("👥 Selecione um grupo:");
        groupLabel.setFont(FONT_BODY);
        groupLabel.setForeground(TEXT_SECONDARY);
        
        JComboBox<Group> groupCombo = new JComboBox<>();
        groupCombo.setFont(FONT_BODY);
        groupCombo.setBackground(DARKER_BG);
        groupCombo.setForeground(TEXT_PRIMARY);
        groupCombo.setPreferredSize(new Dimension(300, 35));

        groupCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Group) {
                    Group group = (Group) value;
                    setText(group.getName() + " (" + group.getDescription() + ")");
                }
                return this;
            }
        });
        
        JButton refreshGroupsButton = new JButton("🔄 Atualizar");
        refreshGroupsButton.setFont(FONT_BUTTON);
        refreshGroupsButton.setBackground(ACCENT_COLOR);
        refreshGroupsButton.setForeground(TEXT_PRIMARY);
        refreshGroupsButton.setBorderPainted(false);
        refreshGroupsButton.setFocusPainted(false);
        refreshGroupsButton.setOpaque(true);
        
        groupSelectionPanel.add(groupLabel);
        groupSelectionPanel.add(groupCombo);
        groupSelectionPanel.add(refreshGroupsButton);

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(CARD_BG);
        chatPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JTextArea chatArea = new JTextArea();
        chatArea.setFont(FONT_BODY);
        chatArea.setBackground(DARKER_BG);
        chatArea.setForeground(TEXT_PRIMARY);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBackground(DARKER_BG);
        chatScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        chatScroll.setPreferredSize(new Dimension(600, 300));

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(CARD_BG);
        
        JTextField messageField = new JTextField();
        messageField.setFont(FONT_BODY);
        messageField.setBackground(DARKER_BG);
        messageField.setForeground(TEXT_PRIMARY);
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JButton sendButton = new JButton("📨 Enviar");
        sendButton.setFont(FONT_BUTTON);
        sendButton.setBackground(ACCENT_COLOR);
        sendButton.setForeground(TEXT_PRIMARY);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setOpaque(true);
        sendButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        chatPanel.add(messagePanel, BorderLayout.SOUTH);

        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBackground(DARK_BG);
        
        JButton clearChatButton = new JButton("🧹 Limpar Chat");
        clearChatButton.setFont(FONT_BUTTON);
        clearChatButton.setBackground(new Color(200, 100, 0));
        clearChatButton.setForeground(TEXT_PRIMARY);
        clearChatButton.setBorderPainted(false);
        clearChatButton.setFocusPainted(false);
        clearChatButton.setOpaque(true);
        
        JButton searchButton = new JButton("🔍 Buscar");
        searchButton.setFont(FONT_BUTTON);
        searchButton.setBackground(ACCENT_COLOR);
        searchButton.setForeground(TEXT_PRIMARY);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setOpaque(true);
        
        JButton statsButton = new JButton("📊 Estatísticas");
        statsButton.setFont(FONT_BUTTON);
        statsButton.setBackground(ACCENT_COLOR);
        statsButton.setForeground(TEXT_PRIMARY);
        statsButton.setBorderPainted(false);
        statsButton.setFocusPainted(false);
        statsButton.setOpaque(true);
        
        actionPanel.add(clearChatButton);
        actionPanel.add(searchButton);
        actionPanel.add(statsButton);

        refreshGroupsButton.addActionListener(e -> {
            if (currentUser != null) {
                refreshGroupComboBox(groupCombo);
            }
        });
        
        sendButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(mainFrame, "Você precisa estar logado para enviar mensagens!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Group selectedGroup = (Group) groupCombo.getSelectedItem();
            String messageContent = messageField.getText().trim();
            
            if (selectedGroup == null) {
                JOptionPane.showMessageDialog(mainFrame, "Selecione um grupo primeiro!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (messageContent.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "A mensagem não pode estar vazia!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                GroupMessage sentMessage = groupChatController.sendMessage(selectedGroup.getId(), currentUser.getId(), messageContent);
                JOptionPane.showMessageDialog(mainFrame, "Mensagem enviada para o grupo '" + selectedGroup.getName() + "'!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
                messageField.setText("");
                refreshGroupChat(chatArea, selectedGroup.getId());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Erro ao enviar mensagem: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        clearChatButton.addActionListener(e -> {
            Group selectedGroup = (Group) groupCombo.getSelectedItem();
            if (selectedGroup != null) {
                showClearChatDialog(selectedGroup.getId(), chatArea);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Selecione um grupo primeiro!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        searchButton.addActionListener(e -> {
            Group selectedGroup = (Group) groupCombo.getSelectedItem();
            if (selectedGroup != null) {
                showSearchMessagesDialog(selectedGroup.getId());
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Selecione um grupo primeiro!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        statsButton.addActionListener(e -> {
            Group selectedGroup = (Group) groupCombo.getSelectedItem();
            if (selectedGroup != null) {
                String stats = groupChatController.getGroupChatStats(selectedGroup.getId());
                JOptionPane.showMessageDialog(mainFrame, stats, "Estatísticas do Chat: " + selectedGroup.getName(), JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Selecione um grupo primeiro!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        groupCombo.addActionListener(e -> {
            Group selectedGroup = (Group) groupCombo.getSelectedItem();
            if (selectedGroup != null) {
                refreshGroupChat(chatArea, selectedGroup.getId());
            }
        });

        mainPanel.add(groupSelectionPanel, BorderLayout.NORTH);
        mainPanel.add(chatPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(mainPanel, BorderLayout.CENTER);

        if (currentUser != null) {
            refreshGroupComboBox(groupCombo);
        }
        
        return panel;
    }
    
    private JPanel createEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JTabbedPane eventsTabbedPane = new JTabbedPane();
        eventsTabbedPane.setBackground(DARK_BG);
        eventsTabbedPane.setForeground(TEXT_PRIMARY);

        JPanel createEventPanel = createCreateEventPanel();
        eventsTabbedPane.addTab("✨ Criar", createEventPanel);

        JPanel manageEventPanel = createManageEventPanel();
        eventsTabbedPane.addTab("⚙️ Gerenciar", manageEventPanel);

        JPanel exploreEventPanel = createExploreEventPanel();
        eventsTabbedPane.addTab("🔍 Explorar", exploreEventPanel);

        JPanel myEventsPanel = createMyEventsPanel();
        eventsTabbedPane.addTab("📅 Meus Eventos", myEventsPanel);
        
        panel.add(eventsTabbedPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createCreateEventPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JLabel titleLabel = new JLabel("✨ Criar Novo Evento");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel nameLabel = new JLabel("🏷️ Nome:");
        nameLabel.setFont(FONT_BODY);
        nameLabel.setForeground(TEXT_SECONDARY);
        
        JTextField nameField = new JTextField(20);
        nameField.setFont(FONT_BODY);
        nameField.setBackground(DARKER_BG);
        nameField.setForeground(TEXT_PRIMARY);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JLabel descLabel = new JLabel("📝 Descrição:");
        descLabel.setFont(FONT_BODY);
        descLabel.setForeground(TEXT_SECONDARY);
        
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setFont(FONT_BODY);
        descArea.setBackground(DARKER_BG);
        descArea.setForeground(TEXT_PRIMARY);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBackground(DARKER_BG);
        descScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        descScroll.setPreferredSize(new Dimension(250, 80));
        
        JLabel dateLabel = new JLabel("📅 Data:");
        dateLabel.setFont(FONT_BODY);
        dateLabel.setForeground(TEXT_SECONDARY);
        
        JTextField dateField = new JTextField();
        dateField.setFont(FONT_BODY);
        dateField.setBackground(DARKER_BG);
        dateField.setForeground(TEXT_PRIMARY);
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        dateField.setToolTipText("Formato: dd/MM/yyyy (ex: 25/12/2024)");
        
        JLabel timeLabel = new JLabel("🕐 Horário:");
        timeLabel.setFont(FONT_BODY);
        timeLabel.setForeground(TEXT_SECONDARY);
        
        JTextField timeField = new JTextField();
        timeField.setFont(FONT_BODY);
        timeField.setBackground(DARKER_BG);
        timeField.setForeground(TEXT_PRIMARY);
        timeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        timeField.setToolTipText("Formato: HH:mm (ex: 14:30)");
        
        JButton createButton = new JButton("✨ Criar Evento");
        createButton.setFont(FONT_BUTTON);
        createButton.setBackground(ACCENT_COLOR);
        createButton.setForeground(TEXT_PRIMARY);
        createButton.setBorderPainted(false);
        createButton.setFocusPainted(false);
        createButton.setOpaque(true);
        createButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(descLabel);
        formPanel.add(descScroll);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(timeLabel);
        formPanel.add(timeField);
        formPanel.add(new JLabel(""));
        formPanel.add(createButton);
        
        createButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(mainFrame, "Você precisa estar logado para criar eventos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String name = nameField.getText().trim();
            String description = descArea.getText().trim();
            String dateStr = dateField.getText().trim();
            String timeStr = timeField.getText().trim();
            
            if (name.isEmpty() || description.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Todos os campos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {

                LocalDateTime eventDateTime = parseDateTime(dateStr, timeStr);
                
                Event newEvent = eventController.createEvent(name, description, eventDateTime, currentUser.getId());
                JOptionPane.showMessageDialog(mainFrame, "Evento '" + newEvent.getName() + "' criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
                nameField.setText("");
                descArea.setText("");
                dateField.setText("");
                timeField.setText("");
                refreshAllLists();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Erro ao criar evento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createManageEventPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JLabel titleLabel = new JLabel("⚙️ Gerenciar Eventos");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JList<String> manageList = new JList<>();
        manageList.setFont(FONT_BODY);
        manageList.setBackground(DARKER_BG);
        manageList.setForeground(TEXT_PRIMARY);
        manageList.setSelectionBackground(ACCENT_COLOR);
        manageList.setSelectionForeground(TEXT_PRIMARY);
        manageList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JScrollPane scrollPane = new JScrollPane(manageList);
        scrollPane.setBackground(DARKER_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(DARK_BG);
        
        JButton editButton = new JButton("✏️ Editar");
        editButton.setFont(FONT_BUTTON);
        editButton.setBackground(ACCENT_COLOR);
        editButton.setForeground(TEXT_PRIMARY);
        editButton.setBorderPainted(false);
        editButton.setFocusPainted(false);
        editButton.setOpaque(true);
        
        JButton deleteButton = new JButton("🗑️ Deletar");
        deleteButton.setFont(FONT_BUTTON);
        deleteButton.setBackground(new Color(200, 0, 0));
        deleteButton.setForeground(TEXT_PRIMARY);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setOpaque(true);
        
        JButton refreshButton = new JButton("🔄 Atualizar");
        refreshButton.setFont(FONT_BUTTON);
        refreshButton.setBackground(ACCENT_COLOR);
        refreshButton.setForeground(TEXT_PRIMARY);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setOpaque(true);
        
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        editButton.addActionListener(e -> {
            if (currentUser != null) {
                String selected = manageList.getSelectedValue();
                if (selected != null && !selected.equals("Nenhum evento para gerenciar")) {
                    showEditEventDialog(selected);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Selecione um evento para editar!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        deleteButton.addActionListener(e -> {
            if (currentUser != null) {
                String selected = manageList.getSelectedValue();
                if (selected != null && !selected.equals("Nenhum evento para gerenciar")) {
                    showDeleteEventDialog(selected);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Selecione um evento para deletar!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        refreshButton.addActionListener(e -> {
            if (currentUser != null) {
                refreshManageEventsList(manageList);
            }
        });
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        if (currentUser != null) {
            refreshManageEventsList(manageList);
        }
        
        return panel;
    }
    
    private JPanel createExploreEventPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JLabel titleLabel = new JLabel("🔍 Explorar Eventos");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JPanel filterPanel = new JPanel(new FlowLayout());
        filterPanel.setBackground(CARD_BG);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel filterLabel = new JLabel("🔍 Filtrar:");
        filterLabel.setFont(FONT_BODY);
        filterLabel.setForeground(TEXT_SECONDARY);
        
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"Todos", "Futuros", "Hoje", "Amanhã", "Esta Semana"});
        filterCombo.setFont(FONT_BODY);
        filterCombo.setBackground(DARKER_BG);
        filterCombo.setForeground(TEXT_PRIMARY);
        
        JTextField searchField = new JTextField(20);
        searchField.setFont(FONT_BODY);
        searchField.setBackground(DARKER_BG);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JButton searchButton = new JButton("🔍 Buscar");
        searchButton.setFont(FONT_BUTTON);
        searchButton.setBackground(ACCENT_COLOR);
        searchButton.setForeground(TEXT_PRIMARY);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setOpaque(true);
        
        filterPanel.add(filterLabel);
        filterPanel.add(filterCombo);
        filterPanel.add(new JLabel("📝 Buscar:"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);

        JList<String> exploreList = new JList<>();
        exploreList.setFont(FONT_BODY);
        exploreList.setBackground(DARKER_BG);
        exploreList.setForeground(TEXT_PRIMARY);
        exploreList.setSelectionBackground(ACCENT_COLOR);
        exploreList.setSelectionForeground(TEXT_PRIMARY);
        exploreList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JScrollPane scrollPane = new JScrollPane(exploreList);
        scrollPane.setBackground(DARKER_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBackground(DARK_BG);
        
        JButton viewButton = new JButton("👁️ Ver Detalhes");
        viewButton.setFont(FONT_BUTTON);
        viewButton.setBackground(ACCENT_COLOR);
        viewButton.setForeground(TEXT_PRIMARY);
        viewButton.setBorderPainted(false);
        viewButton.setFocusPainted(false);
        viewButton.setOpaque(true);
        
        actionPanel.add(viewButton);

        searchButton.addActionListener(e -> {
            String filter = (String) filterCombo.getSelectedItem();
            String searchTerm = searchField.getText().trim();
            refreshExploreEventsList(exploreList, filter, searchTerm);
        });
        
        filterCombo.addActionListener(e -> {
            String filter = (String) filterCombo.getSelectedItem();
            String searchTerm = searchField.getText().trim();
            refreshExploreEventsList(exploreList, filter, searchTerm);
        });
        
        viewButton.addActionListener(e -> {
            String selected = exploreList.getSelectedValue();
            if (selected != null && !selected.equals("Nenhum evento encontrado")) {
                showEventDetailsDialog(selected);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Selecione um evento para ver detalhes!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(filterPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        refreshExploreEventsList(exploreList, "Futuros", "");
        
        return panel;
    }
    
    private JPanel createMyEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JLabel titleLabel = new JLabel("📅 Meus Eventos");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JList<String> myEventsList = new JList<>();
        myEventsList.setFont(FONT_BODY);
        myEventsList.setBackground(DARKER_BG);
        myEventsList.setForeground(TEXT_PRIMARY);
        myEventsList.setSelectionBackground(ACCENT_COLOR);
        myEventsList.setSelectionForeground(TEXT_PRIMARY);
        myEventsList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JScrollPane scrollPane = new JScrollPane(myEventsList);
        scrollPane.setBackground(DARKER_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(DARK_BG);
        
        JButton viewButton = new JButton("👁️ Ver Detalhes");
        viewButton.setFont(FONT_BUTTON);
        viewButton.setBackground(ACCENT_COLOR);
        viewButton.setForeground(TEXT_PRIMARY);
        viewButton.setBorderPainted(false);
        viewButton.setFocusPainted(false);
        viewButton.setOpaque(true);
        
        JButton refreshButton = new JButton("🔄 Atualizar");
        refreshButton.setFont(FONT_BUTTON);
        refreshButton.setBackground(ACCENT_COLOR);
        refreshButton.setForeground(TEXT_PRIMARY);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setOpaque(true);
        
        buttonPanel.add(viewButton);
        buttonPanel.add(refreshButton);

        viewButton.addActionListener(e -> {
            String selected = myEventsList.getSelectedValue();
            if (selected != null && !selected.equals("Você não criou nenhum evento")) {
                showEventDetailsDialog(selected);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Selecione um evento para ver detalhes!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        refreshButton.addActionListener(e -> {
            if (currentUser != null) {
                refreshMyEventsList(myEventsList);
            }
        });
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        if (currentUser != null) {
            refreshMyEventsList(myEventsList);
        }
        
        return panel;
    }
    
    private JPanel createMessagesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JPanel sendMessagePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        sendMessagePanel.setBackground(CARD_BG);
        sendMessagePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel recipientLabel = new JLabel("👤 Para:");
        recipientLabel.setFont(FONT_BODY);
        recipientLabel.setForeground(TEXT_SECONDARY);
        
        JLabel messageLabel = new JLabel("💬 Mensagem:");
        messageLabel.setFont(FONT_BODY);
        messageLabel.setForeground(TEXT_SECONDARY);

        messagesRecipientCombo = createUserComboBox();
        JTextArea messageArea = new JTextArea(3, 20);
        messageArea.setFont(FONT_BODY);
        messageArea.setBackground(DARKER_BG);
        messageArea.setForeground(TEXT_PRIMARY);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JScrollPane messageScroll = new JScrollPane(messageArea);
        messageScroll.setBackground(DARKER_BG);
        messageScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        messageScroll.setPreferredSize(new Dimension(250, 80));
        messageScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JButton sendButton = new JButton("📨 Enviar Mensagem");
        sendButton.setFont(FONT_BUTTON);
        sendButton.setBackground(ACCENT_COLOR);
        sendButton.setForeground(TEXT_PRIMARY);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setOpaque(true);
        sendButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        
        sendMessagePanel.add(recipientLabel);
        sendMessagePanel.add(messagesRecipientCombo);
        sendMessagePanel.add(messageLabel);
        sendMessagePanel.add(messageScroll);
        sendMessagePanel.add(new JLabel(""));
        sendMessagePanel.add(sendButton);
        
        sendButton.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(mainFrame, "Você precisa estar logado para enviar mensagens!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User recipient = (User) messagesRecipientCombo.getSelectedItem();
            String messageContent = messageArea.getText().trim();
            
            if (recipient == null) {
                JOptionPane.showMessageDialog(mainFrame, "Selecione um destinatário!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (messageContent.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "A mensagem não pode estar vazia!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (recipient.getId().equals(currentUser.getId())) {
                JOptionPane.showMessageDialog(mainFrame, "Você não pode enviar mensagem para si mesmo!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Message sentMessage = messageController.sendMessage(currentUser.getId(), recipient.getId(), messageContent);
                JOptionPane.showMessageDialog(mainFrame, "Mensagem enviada para " + recipient.getName() + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
                messageArea.setText("");
                refreshMessagesList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Erro ao enviar mensagem: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JTabbedPane messagesTabbedPane = new JTabbedPane();

        JPanel receivedPanel = new JPanel(new BorderLayout());
        JList<String> receivedList = new JList<>();
        JScrollPane receivedScroll = new JScrollPane(receivedList);
        JButton markAsReadButton = new JButton("✅ Marcar como Lida");
        
        markAsReadButton.addActionListener(e -> {
            if (currentUser != null) {

                String selected = receivedList.getSelectedValue();
                if (selected != null) {

                    JOptionPane.showMessageDialog(mainFrame, "Mensagem marcada como lida!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    refreshMessagesList();
                }
            }
        });
        
        receivedPanel.add(receivedScroll, BorderLayout.CENTER);
        receivedPanel.add(markAsReadButton, BorderLayout.SOUTH);

        JPanel sentPanel = new JPanel(new BorderLayout());
        JList<String> sentList = new JList<>();
        JScrollPane sentScroll = new JScrollPane(sentList);
        JButton deleteButton = new JButton("🗑️ Deletar Mensagem");
        
        deleteButton.addActionListener(e -> {
            if (currentUser != null) {
                String selected = sentList.getSelectedValue();
                if (selected != null) {

                    JOptionPane.showMessageDialog(mainFrame, "Mensagem deletada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    refreshMessagesList();
                }
            }
        });
        
        sentPanel.add(sentScroll, BorderLayout.CENTER);
        sentPanel.add(deleteButton, BorderLayout.SOUTH);
        
        messagesTabbedPane.addTab("📥 Recebidas", receivedPanel);
        messagesTabbedPane.addTab("📤 Enviadas", sentPanel);

        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("🔄 Atualizar");
        JButton clearButton = new JButton("🧹 Limpar Todas");
        
        refreshButton.addActionListener(e -> refreshMessagesList());
        clearButton.addActionListener(e -> {
            if (currentUser != null) {
                int confirm = JOptionPane.showConfirmDialog(mainFrame, 
                    "Tem certeza que deseja limpar todas as mensagens?", 
                    "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    messageController.clearAllMessages();
                    refreshMessagesList();
                    JOptionPane.showMessageDialog(mainFrame, "Todas as mensagens foram removidas!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        controlPanel.add(refreshButton);
        controlPanel.add(clearButton);

        panel.add(sendMessagePanel, BorderLayout.NORTH);
        panel.add(messagesTabbedPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        refreshMessagesList();
        
        return panel;
    }

    private void refreshGroupComboBox(JComboBox<Group> comboBox) {
        if (currentUser == null) return;
        
        comboBox.removeAllItems();
        List<Group> myGroups = groupController.getGroupsByMember(currentUser.getId());
        
        for (Group group : myGroups) {
            comboBox.addItem(group);
        }
        
        if (myGroups.isEmpty()) {
            comboBox.addItem(null); // Adicionar item vazio se não houver grupos
        }
    }
    
    private void refreshGroupChat(JTextArea chatArea, UUID groupId) {
        if (groupId == null) {
            chatArea.setText("Selecione um grupo para ver o chat.");
            return;
        }
        
        List<GroupMessage> messages = groupChatController.getGroupMessages(groupId);
        if (messages.isEmpty()) {
            chatArea.setText("Nenhuma mensagem neste grupo ainda.\nSeja o primeiro a enviar uma mensagem!");
            return;
        }
        
        StringBuilder chatContent = new StringBuilder();
        for (GroupMessage message : messages) {
            User sender = userController.getUserById(message.getSenderId());
            String senderName = sender != null ? sender.getName() : "Usuário desconhecido";
            
            chatContent.append(String.format("[%s] %s: %s\n", 
                message.getRelativeDate(), 
                senderName, 
                message.getContent()));
        }
        
        chatArea.setText(chatContent.toString());

        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    private void showClearChatDialog(UUID groupId, JTextArea chatArea) {
        int confirm = JOptionPane.showConfirmDialog(mainFrame, 
            "Tem certeza que deseja limpar todo o chat deste grupo?\nEsta ação não pode ser desfeita!", 
            "Confirmar Limpeza", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = groupChatController.clearGroupChat(groupId, currentUser.getId());
            
            if (success) {
                JOptionPane.showMessageDialog(mainFrame, "Chat limpo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                refreshGroupChat(chatArea, groupId);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Você não tem permissão para limpar este chat!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showSearchMessagesDialog(UUID groupId) {
        String searchTerm = JOptionPane.showInputDialog(mainFrame, 
            "Digite o termo para buscar nas mensagens:", 
            "Buscar Mensagens", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            List<GroupMessage> foundMessages = groupChatController.searchMessages(groupId, searchTerm);
            
            if (foundMessages.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Nenhuma mensagem encontrada com o termo: " + searchTerm, 
                    "Busca", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder results = new StringBuilder();
                results.append("Mensagens encontradas:\n\n");
                
                for (GroupMessage message : foundMessages) {
                    User sender = userController.getUserById(message.getSenderId());
                    String senderName = sender != null ? sender.getName() : "Usuário desconhecido";
                    
                    results.append(String.format("[%s] %s: %s\n", 
                        message.getRelativeDate(), 
                        senderName, 
                        message.getContent()));
                }
                
                JOptionPane.showMessageDialog(mainFrame, 
                    results.toString(), 
                    "Resultados da Busca", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private LocalDateTime parseDateTime(String dateStr, String timeStr) {
        try {

            String[] dateParts = dateStr.split("/");
            if (dateParts.length != 3) {
                throw new IllegalArgumentException("Formato de data inválido. Use dd/MM/yyyy");
            }
            
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);

            String[] timeParts = timeStr.split(":");
            if (timeParts.length != 2) {
                throw new IllegalArgumentException("Formato de horário inválido. Use HH:mm");
            }
            
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            
            return LocalDateTime.of(year, month, day, hour, minute);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Data ou horário contém caracteres inválidos");
        }
    }
    
    private void refreshManageEventsList(JList<String> list) {
        if (currentUser == null) return;
        
        DefaultListModel<String> model = new DefaultListModel<>();
        List<Event> myEvents = eventController.getEventsByCreator(currentUser.getId());
        
        if (myEvents.isEmpty()) {
            model.addElement("Nenhum evento para gerenciar");
        } else {
            for (Event event : myEvents) {
                String status = event.isPast() ? "🔴 Passado" : "🟢 Futuro";
                String display = String.format("%s - %s (%s) - %s", 
                    event.getName(), 
                    event.getDescription(), 
                    event.getRelativeEventDate(),
                    status);
                model.addElement(display);
            }
        }
        
        list.setModel(model);
    }
    
    private void refreshExploreEventsList(JList<String> list, String filter, String searchTerm) {
        DefaultListModel<String> model = new DefaultListModel<>();
        List<Event> eventsToShow = new ArrayList<>();

        switch (filter) {
            case "Futuros":
                eventsToShow = eventController.getUpcomingEvents();
                break;
            case "Hoje":
                eventsToShow = eventController.getTodayEvents();
                break;
            case "Amanhã":
                eventsToShow = eventController.getTomorrowEvents();
                break;
            case "Esta Semana":
                eventsToShow = eventController.getThisWeekEvents();
                break;
            default: // "Todos"
                eventsToShow = eventController.getAllEvents();
                break;
        }

        if (!searchTerm.isEmpty()) {
            eventsToShow = eventsToShow.stream()
                .filter(event -> event.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                               event.getDescription().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (eventsToShow.isEmpty()) {
            model.addElement("Nenhum evento encontrado");
        } else {
            for (Event event : eventsToShow) {
                User creator = userController.getUserById(event.getCreatorId());
                String creatorName = creator != null ? creator.getName() : "Usuário desconhecido";
                String status = event.isPast() ? "🔴 Passado" : "🟢 Futuro";
                
                String display = String.format("%s - %s (%s) - Criado por: %s - %s", 
                    event.getName(), 
                    event.getDescription(), 
                    event.getRelativeEventDate(),
                    creatorName,
                    status);
                model.addElement(display);
            }
        }
        
        list.setModel(model);
    }
    
    private void refreshMyEventsList(JList<String> list) {
        if (currentUser == null) return;
        
        DefaultListModel<String> model = new DefaultListModel<>();
        List<Event> myEvents = eventController.getEventsByCreator(currentUser.getId());
        
        if (myEvents.isEmpty()) {
            model.addElement("Você não criou nenhum evento");
        } else {
            for (Event event : myEvents) {
                String status = event.isPast() ? "🔴 Passado" : "🟢 Futuro";
                String display = String.format("%s - %s (%s) - %s", 
                    event.getName(), 
                    event.getDescription(), 
                    event.getRelativeEventDate(),
                    status);
                model.addElement(display);
            }
        }
        
        list.setModel(model);
    }
    
    private void showEditEventDialog(String eventDisplay) {

        String eventName = eventDisplay.substring(0, eventDisplay.indexOf(" - "));

        List<Event> events = eventController.getEventsByName(eventName);
        if (events.isEmpty()) return;
        
        Event event = events.get(0);

        JDialog dialog = new JDialog(mainFrame, "Editar Evento: " + event.getName(), true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(DARK_BG);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JTextField nameField = new JTextField(event.getName());
        JTextArea descArea = new JTextArea(event.getDescription());
        JTextField dateField = new JTextField(event.getEventDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        JTextField timeField = new JTextField(event.getEventDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        
        formPanel.add(new JLabel("Nome:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Descrição:"));
        formPanel.add(new JScrollPane(descArea));
        formPanel.add(new JLabel("Data (dd/MM/yyyy):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Horário (HH:mm):"));
        formPanel.add(timeField);
        
        JButton saveButton = new JButton("Salvar");
        JButton cancelButton = new JButton("Cancelar");
        
        saveButton.addActionListener(e -> {
            try {
                LocalDateTime newDateTime = parseDateTime(dateField.getText().trim(), timeField.getText().trim());
                
                boolean success = eventController.editEvent(
                    event.getId(), 
                    currentUser.getId(), 
                    nameField.getText().trim(), 
                    descArea.getText().trim(), 
                    newDateTime
                );
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Evento atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshAllLists();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erro ao atualizar evento!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showDeleteEventDialog(String eventDisplay) {
        String eventName = eventDisplay.substring(0, eventDisplay.indexOf(" - "));
        
        int confirm = JOptionPane.showConfirmDialog(mainFrame, 
            "Tem certeza que deseja deletar o evento '" + eventName + "'?\nEsta ação não pode ser desfeita!", 
            "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            List<Event> events = eventController.getEventsByName(eventName);
            if (!events.isEmpty()) {
                Event event = events.get(0);
                boolean success = eventController.deleteEvent(event.getId(), currentUser.getId());
                
                if (success) {
                    JOptionPane.showMessageDialog(mainFrame, "Evento deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    refreshAllLists();
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Erro ao deletar evento!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void showEventDetailsDialog(String eventDisplay) {
        String eventName = eventDisplay.substring(0, eventDisplay.indexOf(" - "));
        
        List<Event> events = eventController.getEventsByName(eventName);
        if (events.isEmpty()) return;
        
        Event event = events.get(0);
        
        String stats = eventController.getEventStats(event.getId());
        JOptionPane.showMessageDialog(mainFrame, stats, "Detalhes do Evento: " + event.getName(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshManageGroupsList(JList<String> list) {
        if (currentUser == null) return;
        
        DefaultListModel<String> model = new DefaultListModel<>();
        List<Group> manageableGroups = new ArrayList<>();

        List<Group> allGroups = groupController.getAllGroups();
        for (Group group : allGroups) {
            if (groupController.canModify(group.getId(), currentUser.getId())) {
                manageableGroups.add(group);
            }
        }
        
        if (manageableGroups.isEmpty()) {
            model.addElement("Nenhum grupo para gerenciar");
        } else {
            for (Group group : manageableGroups) {
                String role = groupController.isOwner(group.getId(), currentUser.getId()) ? "👑 Proprietário" : "⚙️ Moderador";
                String display = String.format("%s - %s (%s) - %s", 
                    group.getName(), 
                    group.getDescription(), 
                    role,
                    group.getPrivacy());
                model.addElement(display);
            }
        }
        
        list.setModel(model);
    }
    
    private void refreshExploreGroupsList(JList<String> list, String searchTerm) {
        DefaultListModel<String> model = new DefaultListModel<>();
        List<Group> groupsToShow;
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            groupsToShow = groupController.getGroupsByName(searchTerm);
        } else {
            groupsToShow = groupController.getPublicGroups();
        }
        
        if (groupsToShow.isEmpty()) {
            model.addElement("Nenhum grupo encontrado");
        } else {
            for (Group group : groupsToShow) {
                String display = String.format("%s - %s (%s) - %d membros", 
                    group.getName(), 
                    group.getDescription(), 
                    group.getPrivacy(),
                    group.getMemberCount());
                model.addElement(display);
            }
        }
        
        list.setModel(model);
    }
    
    private void refreshMyGroupsList(JList<String> list) {
        if (currentUser == null) return;
        
        DefaultListModel<String> model = new DefaultListModel<>();
        List<Group> myGroups = groupController.getGroupsByMember(currentUser.getId());
        
        if (myGroups.isEmpty()) {
            model.addElement("Você não participa de nenhum grupo");
        } else {
            for (Group group : myGroups) {
                String role;
                if (groupController.isOwner(group.getId(), currentUser.getId())) {
                    role = "👑 Proprietário";
                } else if (groupController.isModerator(group.getId(), currentUser.getId())) {
                    role = "⚙️ Moderador";
                } else {
                    role = "👤 Membro";
                }
                
                String display = String.format("%s - %s (%s) - %s", 
                    group.getName(), 
                    group.getDescription(), 
                    role,
                    group.getPrivacy());
                model.addElement(display);
            }
        }
        
        list.setModel(model);
    }
    
    private void showEditGroupDialog(String groupDisplay) {

        String groupName = groupDisplay.substring(0, groupDisplay.indexOf(" - "));

        List<Group> groups = groupController.getGroupsByName(groupName);
        if (groups.isEmpty()) return;
        
        Group group = groups.get(0);

        JDialog dialog = new JDialog(mainFrame, "Editar Grupo: " + group.getName(), true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(DARK_BG);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JTextField nameField = new JTextField(group.getName());
        JTextArea descArea = new JTextArea(group.getDescription());
        JComboBox<Privacy> privacyCombo = new JComboBox<>(Privacy.values());
        privacyCombo.setSelectedItem(group.getPrivacy());
        
        formPanel.add(new JLabel("Nome:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Descrição:"));
        formPanel.add(new JScrollPane(descArea));
        formPanel.add(new JLabel("Privacidade:"));
        formPanel.add(privacyCombo);
        
        JButton saveButton = new JButton("Salvar");
        JButton cancelButton = new JButton("Cancelar");
        
        saveButton.addActionListener(e -> {
            try {
                boolean success = groupController.editGroup(
                    group.getId(), 
                    currentUser.getId(), 
                    nameField.getText().trim(), 
                    descArea.getText().trim(), 
                    (Privacy) privacyCombo.getSelectedItem()
                );
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Grupo atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshAllLists();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erro ao atualizar grupo!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showDeleteGroupDialog(String groupDisplay) {
        String groupName = groupDisplay.substring(0, groupDisplay.indexOf(" - "));
        
        int confirm = JOptionPane.showConfirmDialog(mainFrame, 
            "Tem certeza que deseja deletar o grupo '" + groupName + "'?\nEsta ação não pode ser desfeita!", 
            "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            List<Group> groups = groupController.getGroupsByName(groupName);
            if (!groups.isEmpty()) {
                Group group = groups.get(0);
                boolean success = groupController.deleteGroup(group.getId(), currentUser.getId());
                
                if (success) {
                    JOptionPane.showMessageDialog(mainFrame, "Grupo deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    refreshAllLists();
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Erro ao deletar grupo!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void showJoinGroupDialog(String groupDisplay) {
        String groupName = groupDisplay.substring(0, groupDisplay.indexOf(" - "));
        
        List<Group> groups = groupController.getGroupsByName(groupName);
        if (groups.isEmpty()) return;
        
        Group group = groups.get(0);

        if (groupController.isMember(group.getId(), currentUser.getId())) {
            JOptionPane.showMessageDialog(mainFrame, "Você já é membro deste grupo!", "Informação", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (group.getPrivacy() == Privacy.PRIVATE) {
            JOptionPane.showMessageDialog(mainFrame, "Este grupo é privado. Entre em contato com o proprietário para solicitar entrada.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        boolean success = groupController.joinGroup(group.getId(), currentUser.getId());
        
        if (success) {
            JOptionPane.showMessageDialog(mainFrame, "Você entrou no grupo '" + group.getName() + "'!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            refreshAllLists();
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Erro ao entrar no grupo!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showGroupDetailsDialog(String groupDisplay) {
        String groupName = groupDisplay.substring(0, groupDisplay.indexOf(" - "));
        
        List<Group> groups = groupController.getGroupsByName(groupName);
        if (groups.isEmpty()) return;
        
        Group group = groups.get(0);
        
        String stats = groupController.getGroupStats(group.getId());
        JOptionPane.showMessageDialog(mainFrame, stats, "Detalhes do Grupo: " + group.getName(), JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showLeaveGroupDialog(String groupDisplay) {
        String groupName = groupDisplay.substring(0, groupDisplay.indexOf(" - "));
        
        int confirm = JOptionPane.showConfirmDialog(mainFrame, 
            "Tem certeza que deseja sair do grupo '" + groupName + "'?", 
            "Confirmar Saída", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            List<Group> groups = groupController.getGroupsByName(groupName);
            if (!groups.isEmpty()) {
                Group group = groups.get(0);
                boolean success = groupController.leaveGroup(group.getId(), currentUser.getId());
                
                if (success) {
                    JOptionPane.showMessageDialog(mainFrame, "Você saiu do grupo '" + group.getName() + "'!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    refreshAllLists();
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Erro ao sair do grupo!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void refreshMessagesList() {
        if (currentUser == null) return;

        List<Message> receivedMessages = messageController.getMessagesReceived(currentUser.getId());
        DefaultListModel<String> receivedModel = new DefaultListModel<>();
        
        if (receivedMessages.isEmpty()) {
            receivedModel.addElement("Nenhuma mensagem recebida");
        } else {
            for (Message msg : receivedMessages) {
                User sender = findUserById(msg.getSenderId());
                String senderName = sender != null ? sender.getName() : "Usuário desconhecido";
                String status = msg.isRead() ? "✅" : "🔴";
                String display = String.format("%s %s: %s (%s)", 
                    status, senderName, msg.getTruncatedContent(50), msg.getRelativeDate());
                receivedModel.addElement(display);
            }
        }

        List<Message> sentMessages = messageController.getMessagesSent(currentUser.getId());
        DefaultListModel<String> sentModel = new DefaultListModel<>();
        
        if (sentMessages.isEmpty()) {
            sentModel.setElementAt("Nenhuma mensagem enviada", 0);
        } else {
            for (Message msg : sentMessages) {
                User receiver = findUserById(msg.getReceiverId());
                String receiverName = receiver != null ? receiver.getName() : "Usuário desconhecido";
                String display = String.format("Para %s: %s (%s)", 
                    receiverName, msg.getTruncatedContent(50), msg.getRelativeDate());
                sentModel.addElement(display);
            }
        }


    }
    
    private User findUserById(UUID userId) {
        List<User> users = userController.getAllUsers();
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }
    
    private User findUserByName(String name) {
        List<User> users = userController.getAllUsers();
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }

    private void refreshMainFeed() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(mainFrame, "Faça login para ver o feed.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<FeedController.FeedPost> feedPosts = feedService.getFriendsFeedWithAuthorInfo(currentUser.getId());
        DefaultListModel<String> model = new DefaultListModel<>();
        
        if (feedPosts.isEmpty()) {
            model.addElement("Nenhum post encontrado no seu feed.");
            model.addElement("Adicione alguns amigos para ver seus posts!");
        } else {
            for (FeedController.FeedPost feedPost : feedPosts) {
                model.addElement(feedPost.getDisplayText());
            }
        }
        
        if (feedList != null) {
            feedList.setModel(model);
        }
    }
    
    private void showMostLikedFeed() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(mainFrame, "Faça login para ver o feed.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Post> mostLikedPosts = feedService.getFriendsFeedMostLiked(currentUser.getId());
        DefaultListModel<String> model = new DefaultListModel<>();
        
        if (mostLikedPosts.isEmpty()) {
            model.addElement("Nenhum post encontrado no seu feed.");
        } else {
            for (Post post : mostLikedPosts) {
                User author = findUserById(post.getUserId());
                String authorName = author != null ? author.getName() : "Usuário desconhecido";
                String display = String.format("[%s] %s: %s (❤️ %d)", 
                    post.getFormattedDate(), authorName, post.getDisplayContent(), post.getLikeCount());
                model.addElement(display);
            }
        }
        
        if (feedList != null) {
            feedList.setModel(model);
        }
    }
    
    private void showTodayFeed() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(mainFrame, "Faça login para ver o feed.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Post> todayPosts = feedService.getFriendsFeedToday(currentUser.getId());
        DefaultListModel<String> model = new DefaultListModel<>();
        
        if (todayPosts.isEmpty()) {
            model.addElement("Nenhum post de hoje no seu feed.");
        } else {
            for (Post post : todayPosts) {
                User author = findUserById(post.getUserId());
                String authorName = author != null ? author.getName() : "Usuário desconhecido";
                String display = String.format("[%s] %s: %s", 
                    post.getFormattedDate(), authorName, post.getDisplayContent());
                model.addElement(display);
            }
        }
        
        if (feedList != null) {
            feedList.setModel(model);
        }
    }
    
    private void showThisWeekFeed() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(mainFrame, "Faça login para ver o feed.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Post> weekPosts = feedService.getFriendsFeedThisWeek(currentUser.getId());
        DefaultListModel<String> model = new DefaultListModel<>();
        
        if (weekPosts.isEmpty()) {
            model.addElement("Nenhum post desta semana no seu feed.");
        } else {
            for (Post post : weekPosts) {
                User author = findUserById(post.getUserId());
                String authorName = author != null ? author.getName() : "Usuário desconhecido";
                String display = String.format("[%s] %s: %s", 
                    post.getFormattedDate(), authorName, post.getDisplayContent());
                model.addElement(display);
            }
        }
        
        if (feedList != null) {
            feedList.setModel(model);
        }
    }
    
    private void filterFeedByType(String postType) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(mainFrame, "Faça login para ver o feed.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Post> filteredPosts = feedService.getFriendsFeedByType(currentUser.getId(), postType);
        DefaultListModel<String> model = new DefaultListModel<>();
        
        if (filteredPosts.isEmpty()) {
            model.addElement("Nenhum post do tipo " + postType + " encontrado no seu feed.");
        } else {
            for (Post post : filteredPosts) {
                User author = findUserById(post.getUserId());
                String authorName = author != null ? author.getName() : "Usuário desconhecido";
                String display = String.format("[%s] %s: %s", 
                    post.getFormattedDate(), authorName, post.getDisplayContent());
                model.addElement(display);
            }
        }
        
        if (filteredFeedList != null) {
            filteredFeedList.setModel(model);
        }
    }
    
    private void refreshFriendsCombo(JComboBox<String> combo) {
        if (currentUser == null) {
            combo.removeAllItems();
            return;
        }
        
        combo.removeAllItems();
        List<UUID> friends = new ArrayList<>(friendController.getFriends(currentUser.getId()));
        
        for (UUID friendId : friends) {
            User friend = findUserById(friendId);
            if (friend != null) {
                combo.addItem(friend.getName());
            }
        }
        
        if (combo.getItemCount() == 0) {
            combo.addItem("Nenhum amigo encontrado");
        }
    }
    
    private void filterFeedByFriend(String friendName) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(mainFrame, "Faça login para ver o feed.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (friendName.equals("Nenhum amigo encontrado")) {
            return;
        }
        
        User friend = findUserByName(friendName);
        if (friend == null) {
            JOptionPane.showMessageDialog(mainFrame, "Amigo não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Post> friendPosts = feedService.getFriendsFeedFromFriend(currentUser.getId(), friend.getId());
        DefaultListModel<String> model = new DefaultListModel<>();
        
        if (friendPosts.isEmpty()) {
            model.addElement("Nenhum post encontrado de " + friendName + ".");
        } else {
            for (Post post : friendPosts) {
                String display = String.format("[%s] %s", 
                    post.getFormattedDate(), post.getDisplayContent());
                model.addElement(display);
            }
        }
        
        if (friendPostsList != null) {
            friendPostsList.setModel(model);
        }
    }
    
    private void likeSelectedPost(JList<String> list) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(mainFrame, "Faça login para curtir posts.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selected = list.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(mainFrame, "Selecione um post para curtir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }


        try {
            String content = selected.substring(selected.indexOf(": ") + 2);

            List<Post> allPosts = postController.getAllPosts();
            for (Post post : allPosts) {
                if (post.getDisplayContent().equals(content)) {
                    boolean success = postController.likePost(post.getId(), currentUser.getId());
                    if (success) {
                        JOptionPane.showMessageDialog(mainFrame, "Post curtido!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        refreshMainFeed(); // Atualizar o feed
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "Erro ao curtir o post.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                    return;
                }
            }
            
            JOptionPane.showMessageDialog(mainFrame, "Post não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, "Erro ao processar o post selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewPostDetails(JList<String> list) {
        String selected = list.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(mainFrame, "Selecione um post para ver os detalhes.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String content = selected.substring(selected.indexOf(": ") + 2);

            List<Post> allPosts = postController.getAllPosts();
            for (Post post : allPosts) {
                if (post.getDisplayContent().equals(content)) {
                    User author = findUserById(post.getUserId());
                    String authorName = author != null ? author.getName() : "Usuário desconhecido";

                    if (post instanceof ImagePost || post instanceof VideoPost) {
                        showMediaPost(post);
                    } else {

                        String details = String.format(
                            "📝 Detalhes do Post\n\n" +
                            "👤 Autor: %s\n" +
                            "📅 Data: %s\n" +
                            "🏷️ Tipo: %s\n" +
                            "❤️ Curtidas: %d\n" +
                            "📄 Conteúdo:\n%s",
                            authorName, post.getFormattedDate(), post.getPostType(), 
                            post.getLikeCount(), post.getDisplayContent()
                        );
                        
                        JOptionPane.showMessageDialog(mainFrame, details, "Detalhes do Post", JOptionPane.INFORMATION_MESSAGE);
                    }
                    return;
                }
            }
            
            JOptionPane.showMessageDialog(mainFrame, "Post não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, "Erro ao processar o post selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMediaPost(Post post) {
        if (post == null) return;
        
        User author = userController.getUserById(post.getUserId());
        String authorName = author != null ? author.getName() : "Usuário Desconhecido";
        
        JDialog mediaDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(mainFrame), "Visualizar Mídia", true);
        mediaDialog.setSize(600, 500);
        mediaDialog.setLocationRelativeTo(mainFrame);
        mediaDialog.getContentPane().setBackground(DARK_BG);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DARK_BG);
        
        JLabel authorLabel = new JLabel("👤 " + authorName);
        authorLabel.setFont(FONT_SUBTITLE);
        authorLabel.setForeground(TEXT_PRIMARY);
        
        JLabel dateLabel = new JLabel("📅 " + post.getFormattedDate());
        dateLabel.setFont(FONT_BODY);
        dateLabel.setForeground(TEXT_SECONDARY);
        
        JLabel likesLabel = new JLabel("❤️ " + post.getLikeCount() + " curtidas");
        likesLabel.setFont(FONT_BODY);
        likesLabel.setForeground(TEXT_SECONDARY);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(DARK_BG);
        infoPanel.add(authorLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(dateLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(likesLabel);
        
        headerPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel mediaPanel = new JPanel(new BorderLayout());
        mediaPanel.setBackground(DARK_BG);
        mediaPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        if (post instanceof ImagePost) {
            ImagePost imagePost = (ImagePost) post;
            showImageInDialog(mediaPanel, imagePost);
        } else if (post instanceof VideoPost) {
            VideoPost videoPost = (VideoPost) post;
            showVideoInDialog(mediaPanel, videoPost);
        }

        JTextArea descriptionArea = new JTextArea(3, 40);
        descriptionArea.setFont(FONT_BODY);
        descriptionArea.setBackground(DARKER_BG);
        descriptionArea.setForeground(TEXT_PRIMARY);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        if (post instanceof ImagePost) {
            descriptionArea.setText("📝 " + ((ImagePost) post).getDescription());
        } else if (post instanceof VideoPost) {
            descriptionArea.setText("📝 " + ((VideoPost) post).getDescription());
        }
        
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBackground(DARK_BG);
        descriptionScroll.setBorder(null);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(DARK_BG);
        
        JButton likeButton = new JButton("❤️ Curtir");
        likeButton.setFont(FONT_BUTTON);
        likeButton.setBackground(ACCENT_COLOR);
        likeButton.setForeground(TEXT_PRIMARY);
        likeButton.setFocusPainted(false);
        likeButton.setOpaque(true);
        likeButton.setBorderPainted(false);
        likeButton.addActionListener(e -> {
            if (currentUser != null) {
                postController.likePost(post.getId(), currentUser.getId());
                likesLabel.setText("❤️ " + post.getLikeCount() + " curtidas");
                JOptionPane.showMessageDialog(mediaDialog, "Post curtido! ❤️", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mediaDialog, "Você precisa estar logado para curtir posts.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton closeButton = new JButton("❌ Fechar");
        closeButton.setFont(FONT_BUTTON);
        closeButton.setBackground(BORDER_COLOR);
        closeButton.setForeground(TEXT_PRIMARY);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(e -> mediaDialog.dispose());
        
        buttonPanel.add(likeButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(closeButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(mediaPanel, BorderLayout.CENTER);
        mainPanel.add(descriptionScroll, BorderLayout.SOUTH);
        
        mediaDialog.add(mainPanel, BorderLayout.CENTER);
        mediaDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        mediaDialog.setVisible(true);
    }
    
    private void showImageInDialog(JPanel mediaPanel, ImagePost imagePost) {
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setFont(FONT_BODY);
        imageLabel.setForeground(TEXT_SECONDARY);
        
        try {

            String imageUrl = imagePost.getImageUrl();

            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                URL url = new URL(imageUrl);
                ImageIcon imageIcon = new ImageIcon(url);
                if (imageIcon.getIconWidth() > 0) {

                    Image image = imageIcon.getImage();
                    Image scaledImage = image.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(scaledImage);
                    imageLabel.setIcon(imageIcon);
                } else {
                    imageLabel.setText("🖼️ Imagem não encontrada\nURL: " + imageUrl);
                }
            } else {

                File imageFile = new File(imageUrl);
                if (imageFile.exists()) {
                    ImageIcon imageIcon = new ImageIcon(imageUrl);
                    Image image = imageIcon.getImage();
                    Image scaledImage = image.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(scaledImage);
                    imageLabel.setIcon(imageIcon);
                } else {
                    imageLabel.setText("🖼️ Arquivo não encontrado\nCaminho: " + imageUrl);
                }
            }
        } catch (Exception e) {
            imageLabel.setText("🖼️ Erro ao carregar imagem\nURL: " + imagePost.getImageUrl() + "\nErro: " + e.getMessage());
        }
        
        mediaPanel.add(imageLabel, BorderLayout.CENTER);
    }
    
    private void showVideoInDialog(JPanel mediaPanel, VideoPost videoPost) {
        JPanel videoPanel = new JPanel(new BorderLayout());
        videoPanel.setBackground(DARK_BG);

        JLabel videoIcon = new JLabel("🎥", SwingConstants.CENTER);
        videoIcon.setFont(new Font("Arial", Font.PLAIN, 64));
        videoIcon.setForeground(ACCENT_COLOR);

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(DARK_BG);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        JLabel urlLabel = new JLabel("🔗 " + videoPost.getVideoUrl());
        urlLabel.setFont(FONT_BODY);
        urlLabel.setForeground(TEXT_PRIMARY);
        urlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel durationLabel = new JLabel("⏱️ Duração: " + formatDuration(videoPost.getDuration()));
        durationLabel.setFont(FONT_BODY);
        durationLabel.setForeground(TEXT_SECONDARY);
        durationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel noteLabel = new JLabel("💡 Clique no link para abrir o vídeo");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        noteLabel.setForeground(TEXT_SECONDARY);
        noteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(urlLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(durationLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(noteLabel);
        infoPanel.add(Box.createVerticalStrut(20));

        JButton openVideoButton = new JButton("🎬 Abrir Vídeo");
        openVideoButton.setFont(FONT_BUTTON);
        openVideoButton.setBackground(ACCENT_COLOR);
        openVideoButton.setForeground(TEXT_PRIMARY);
        openVideoButton.setFocusPainted(false);
        openVideoButton.setOpaque(true);
        openVideoButton.setBorderPainted(false);
        openVideoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        openVideoButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new java.net.URI(videoPost.getVideoUrl()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Erro ao abrir o vídeo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        infoPanel.add(openVideoButton);
        
        videoPanel.add(videoIcon, BorderLayout.CENTER);
        videoPanel.add(infoPanel, BorderLayout.SOUTH);
        
        mediaPanel.add(videoPanel, BorderLayout.CENTER);
    }
    
    private String formatDuration(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    private JPanel createNotificationsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("🔔 Notificações");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(DARK_BG);
        
        JButton refreshButton = new JButton("🔄 Atualizar");
        refreshButton.setFont(FONT_BUTTON);
        refreshButton.setBackground(ACCENT_COLOR);
        refreshButton.setForeground(TEXT_PRIMARY);
        refreshButton.setFocusPainted(false);
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(false);
        refreshButton.addActionListener(e -> refreshNotifications());
        
        JButton markAllReadButton = new JButton("✅ Marcar todas como lidas");
        markAllReadButton.setFont(FONT_BUTTON);
        markAllReadButton.setBackground(BORDER_COLOR);
        markAllReadButton.setForeground(TEXT_PRIMARY);
        markAllReadButton.setFocusPainted(false);
        markAllReadButton.setOpaque(true);
        markAllReadButton.setBorderPainted(false);
        markAllReadButton.addActionListener(e -> markAllNotificationsAsRead());
        
        JButton clearAllButton = new JButton("🗑️ Limpar todas");
        clearAllButton.setFont(FONT_BUTTON);
        clearAllButton.setBackground(new Color(220, 20, 60));
        clearAllButton.setForeground(TEXT_PRIMARY);
        clearAllButton.setFocusPainted(false);
        clearAllButton.setOpaque(true);
        clearAllButton.setBorderPainted(false);
        clearAllButton.addActionListener(e -> clearAllNotifications());
        
        controlPanel.add(refreshButton);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(markAllReadButton);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(clearAllButton);

        notificationListModel = new DefaultListModel<>();
        notificationList = new JList<>(notificationListModel);
        notificationList.setFont(FONT_BODY);
        notificationList.setBackground(DARKER_BG);
        notificationList.setForeground(TEXT_PRIMARY);
        notificationList.setSelectionBackground(ACCENT_COLOR);
        notificationList.setSelectionForeground(TEXT_PRIMARY);
        notificationList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        notificationList.setCellRenderer(new NotificationListCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(notificationList);
        scrollPane.setBackground(DARK_BG);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBackground(DARK_BG);
        
        JButton markReadButton = new JButton("✅ Marcar como lida");
        markReadButton.setFont(FONT_BUTTON);
        markReadButton.setBackground(ACCENT_COLOR);
        markReadButton.setForeground(TEXT_PRIMARY);
        markReadButton.setFocusPainted(false);
        markReadButton.setOpaque(true);
        markReadButton.setBorderPainted(false);
        markReadButton.addActionListener(e -> markSelectedNotificationAsRead());
        
        JButton deleteButton = new JButton("🗑️ Deletar");
        deleteButton.setFont(FONT_BUTTON);
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(TEXT_PRIMARY);
        deleteButton.setFocusPainted(false);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.addActionListener(e -> deleteSelectedNotification());
        
        actionPanel.add(markReadButton);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(deleteButton);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(DARK_BG);
        
        JLabel statsLabel = new JLabel();
        statsLabel.setFont(FONT_BODY);
        statsLabel.setForeground(TEXT_SECONDARY);
        statsPanel.add(statsLabel);

        updateNotificationStats(statsLabel);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        mainPanel.add(statsPanel, BorderLayout.SOUTH);

        refreshNotifications();
        
        return mainPanel;
    }

    private class NotificationListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof String) {
                String notificationText = (String) value;

                if (notificationText.startsWith("🔔")) {
                    setForeground(TEXT_PRIMARY); // Não lida - cor mais vibrante
                    setFont(new Font("Arial", Font.BOLD, 14));
                } else {
                    setForeground(TEXT_SECONDARY); // Lida - cor mais suave
                    setFont(new Font("Arial", Font.PLAIN, 14));
                }
                
                setText(notificationText);
            }
            
            setBackground(isSelected ? ACCENT_COLOR : DARKER_BG);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            return this;
        }
    }

    private void refreshNotifications() {
        if (currentUser == null) {
            notificationListModel.clear();
            notificationListModel.addElement("🔐 Faça login para ver suas notificações");
            return;
        }
        
        notificationListModel.clear();
        List<Notification> notifications = notificationController.getNotificationsByUser(currentUser.getId());
        
        if (notifications.isEmpty()) {
            notificationListModel.addElement("📭 Nenhuma notificação encontrada");
        } else {
            for (Notification notification : notifications) {
                notificationListModel.addElement(notification.getDisplayText());
            }
        }
        
        updateNotificationBadge();
    }
    
    private void markSelectedNotificationAsRead() {
        if (currentUser == null) return;
        
        String selected = notificationList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(mainFrame, "Selecione uma notificação para marcar como lida.", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Notification> notifications = notificationController.getNotificationsByUser(currentUser.getId());
        int selectedIndex = notificationList.getSelectedIndex();
        
        if (selectedIndex >= 0 && selectedIndex < notifications.size()) {
            Notification notification = notifications.get(selectedIndex);
            notificationController.markAsRead(notification.getId());
            refreshNotifications();
            updateNotificationBadge();
        }
    }
    
    private void markAllNotificationsAsRead() {
        if (currentUser == null) return;
        
        int result = JOptionPane.showConfirmDialog(mainFrame, 
            "Deseja marcar todas as notificações como lidas?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            notificationController.markAllAsRead(currentUser.getId());
            refreshNotifications();
            updateNotificationBadge();
        }
    }
    
    private void deleteSelectedNotification() {
        if (currentUser == null) return;
        
        String selected = notificationList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(mainFrame, "Selecione uma notificação para deletar.", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(mainFrame, 
            "Deseja deletar esta notificação?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            List<Notification> notifications = notificationController.getNotificationsByUser(currentUser.getId());
            int selectedIndex = notificationList.getSelectedIndex();
            
            if (selectedIndex >= 0 && selectedIndex < notifications.size()) {
                Notification notification = notifications.get(selectedIndex);
                notificationController.deleteNotification(notification.getId());
                refreshNotifications();
                updateNotificationBadge();
            }
        }
    }
    
    private void clearAllNotifications() {
        if (currentUser == null) return;
        
        int result = JOptionPane.showConfirmDialog(mainFrame, 
            "Deseja deletar TODAS as notificações? Esta ação não pode ser desfeita.", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            notificationController.deleteAllNotifications(currentUser.getId());
            refreshNotifications();
            updateNotificationBadge();
        }
    }
    
    private void updateNotificationStats(JLabel statsLabel) {
        if (currentUser == null) {
            statsLabel.setText("🔐 Faça login para ver estatísticas");
            return;
        }
        
        Map<String, Integer> stats = notificationController.getNotificationStats(currentUser.getId());
        String statsText = String.format("📊 Total: %d | 🔔 Não lidas: %d | 👥 Amizades: %d | 💬 Mensagens: %d | ❤️ Curtidas: %d",
            stats.getOrDefault("total", 0),
            stats.getOrDefault("unread", 0),
            stats.getOrDefault("friend_request", 0),
            stats.getOrDefault("message", 0),
            stats.getOrDefault("post_like", 0)
        );
        
        statsLabel.setText(statsText);
    }
    
    private void updateNotificationBadge() {
        if (currentUser == null) {
            if (notificationBadge != null) {
                notificationBadge.setText("");
            }
            return;
        }
        
        int unreadCount = notificationController.getUnreadCount(currentUser.getId());
        if (notificationBadge != null) {
            if (unreadCount > 0) {
                notificationBadge.setText("🔔 " + unreadCount);
                notificationBadge.setForeground(new Color(255, 69, 0)); // Cor laranja para chamar atenção
            } else {
                notificationBadge.setText("");
            }
        }
    }
}
