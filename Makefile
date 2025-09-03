# Makefile para projeto Java MVC (Windows)
JAVAC = javac
JAVA = java
SRC_DIR = src
BIN_DIR = bin
MAIN_CLASS = Main

# Arquivos Java específicos para Windows
JAVA_FILES = src\model\Privacy.java src\model\User.java src\controller\UserController.java src\view\UserWebView.java src\Main.java

# Criar diretório bin se não existir
$(BIN_DIR):
	if not exist $(BIN_DIR) mkdir $(BIN_DIR)

# Compilar o projeto
compile: $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) $(JAVA_FILES)
	@echo ✅ Projeto compilado com sucesso!

# Executar o projeto
run: compile
	$(JAVA) -cp $(BIN_DIR) $(MAIN_CLASS)

# Limpar arquivos compilados
clean:
	if exist $(BIN_DIR) rmdir /s /q $(BIN_DIR)
	@echo 🧹 Arquivos compilados removidos!

# Executar testes (se existirem)
test: compile
	@echo 🧪 Nenhum teste configurado

# Ajuda
help:
	@echo Comandos disponíveis:
	@echo   make compile  - Compila o projeto
	@echo   make run      - Compila e executa o projeto
	@echo   make clean    - Remove arquivos compilados
	@echo   make test     - Executa testes
	@echo   make help     - Mostra esta ajuda

.PHONY: compile run clean test help
