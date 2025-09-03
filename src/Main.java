import view.SocialAppSwing;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Rede Social...\n");

        SwingUtilities.invokeLater(() -> {
            new SocialAppSwing();
        });
    }
}
