import model.Privacy;
import model.User;
import controller.UserController;
import view.UserWebView;
import view.Menu;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Iniciando Sistema de Gerenciamento de Usuários...\n");
        
        // Criar instâncias
        UserController userController = new UserController();
        UserWebView userView = new UserWebView();
        Menu menu = new Menu(userController, userView);
        
        // Iniciar o menu interativo
        menu.start();
    }
}
