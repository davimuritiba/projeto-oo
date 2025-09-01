import controller.UserController;
import controller.PostController;
import controller.FriendController;
import view.Menu;
import view.SocialAppSwing;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Iniciando Sistema de Rede Social...\n");
        
        // Verificar se quer usar interface gráfica ou console
        if (args.length > 0 && args[0].equals("--console")) {
            // Modo console
            UserController userController = new UserController();
            PostController postController = new PostController();
            FriendController friendController = new FriendController();
            Menu menu = new Menu(userController, postController, friendController);
            menu.start();
        } else {
            // Modo interface gráfica (padrão)
            SwingUtilities.invokeLater(() -> {
                new SocialAppSwing();
            });
        }
    }
}
