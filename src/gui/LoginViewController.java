package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class LoginViewController implements Initializable {
	
	@FXML
	private Button btLogin;
	
	private static Scene mainScene;
	
	@FXML
	public void onBtLoginAction(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();
			
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			mainScene = new Scene(scrollPane);
			Main.setMainScene(mainScene);
			
			Stage mainStage = new Stage();
			mainStage.setScene(mainScene);
			mainStage.setTitle("Minha Biblioteca");
			mainStage.show();
			Utils.currentStage(event).close();
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showALert("IO Exception", "Erro ao carregar a view", e.getMessage(), AlertType.ERROR);
		}		
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
}