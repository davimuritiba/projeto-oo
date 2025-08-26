package view;

import model.User;
import java.util.List;

public class UserWebView {
    
    public String generateUserForm() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Sistema de Usuários</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .form-container { max-width: 500px; margin: 0 auto; }
                    .form-group { margin-bottom: 15px; }
                    label { display: block; margin-bottom: 5px; font-weight: bold; }
                    input, select { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
                    button { background: #007bff; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }
                    button:hover { background: #0056b3; }
                    .user-list { margin-top: 30px; }
                    .user-item { background: #f8f9fa; padding: 10px; margin: 5px 0; border-radius: 4px; }
                </style>
            </head>
            <body>
                <div class="form-container">
                    <h1>📝 Criar Novo Usuário</h1>
                    <form action="/users" method="POST">
                        <div class="form-group">
                            <label for="name">Nome:</label>
                            <input type="text" id="name" name="name" placeholder="Digite o nome" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="email">Email:</label>
                            <input type="email" id="email" name="email" placeholder="Digite o email" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="password">Senha:</label>
                            <input type="password" id="password" name="password" placeholder="Digite a senha" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="privacy">Privacidade:</label>
                            <select id="privacy" name="privacy">
                                <option value="PUBLIC">🌍 Público</option>
                                <option value="PRIVATE">🔒 Privado</option>
                            </select>
                        </div>
                        
                        <button type="submit">✅ Criar Usuário</button>
                    </form>
                </div>
            </body>
            </html>
            """;
    }
    
    public String generateUserList(List<User> users) {
        StringBuilder html = new StringBuilder();
        html.append("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>Lista de Usuários</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .container { max-width: 800px; margin: 0 auto; }
                    .user-item { background: #f8f9fa; padding: 15px; margin: 10px 0; border-radius: 8px; border-left: 4px solid #007bff; }
                    .user-name { font-size: 18px; font-weight: bold; color: #333; }
                    .user-email { color: #666; margin: 5px 0; }
                    .user-privacy { display: inline-block; padding: 3px 8px; border-radius: 12px; font-size: 12px; }
                    .privacy-public { background: #d4edda; color: #155724; }
                    .privacy-private { background: #f8d7da; color: #721c24; }
                    .back-btn { background: #6c757d; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px; display: inline-block; margin-bottom: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <a href="/" class="back-btn">← Voltar</a>
                    <h1>👥 Lista de Usuários</h1>
            """);
        
        if (users.isEmpty()) {
            html.append("<p>Nenhum usuário cadastrado ainda.</p>");
        } else {
            for (User user : users) {
                String privacyClass = user.getPrivacy().toString().equals("PUBLIC") ? "privacy-public" : "privacy-private";
                String privacyIcon = user.getPrivacy().toString().equals("PUBLIC") ? "🌍" : "🔒";
                
                html.append(String.format("""
                    <div class="user-item">
                        <div class="user-name">%s</div>
                        <div class="user-email">📧 %s</div>
                        <div class="user-privacy %s">%s %s</div>
                    </div>
                    """, 
                    user.getName(), 
                    user.getEmail(), 
                    privacyClass,
                    privacyIcon,
                    user.getPrivacy().toString()
                ));
            }
        }
        
        html.append("""
                </div>
            </body>
            </html>
            """);
        
        return html.toString();
    }
    
    public String generateSuccessMessage(String message) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>Sucesso</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; text-align: center; }
                    .success { background: #d4edda; color: #155724; padding: 20px; border-radius: 8px; max-width: 500px; margin: 50px auto; }
                    .back-btn { background: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; display: inline-block; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="success">
                    <h2>✅ %s</h2>
                    <a href="/" class="back-btn">Voltar ao Início</a>
                </div>
            </body>
            </html>
            """, message);
    }
}

