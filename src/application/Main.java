package application;
	
import java.io.IOException;

import gui.LoginViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.entities.Usuario;
import model.services.UsuarioService;

public class Main extends Application {
	
	private static Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LoginView.fxml"));
			Pane paneLogin = loader.load();
			
			LoginViewController controller = loader.getController();
			Usuario admin = new Usuario();
			controller.setAdmin(admin);
			controller.setAdminService(new UsuarioService());
			
			Scene loginScene = new Scene(paneLogin);
			
			primaryStage.setScene(loginScene);
			primaryStage.setTitle("Login System");
			primaryStage.show();			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Scene getMainScene() {
		return mainScene;
	}
	
	public static void setMainScene(Scene scene) {
		mainScene = scene;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
