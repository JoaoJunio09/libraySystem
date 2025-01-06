package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import application.Main;
import db.DbException;
import gui.exceptions.ValidationException;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.entities.Usuario;
import model.services.UsuarioService;

public class LoginViewController implements Initializable {
	
	private UsuarioService service;
	
	private Usuario entity;
	
	private static Scene mainScene;
	
	@FXML
	private Button btLogin;
	
	@FXML
	private TextField txtLogin;
	
	@FXML
	private PasswordField pswSenha;
	
	@FXML
	private TextField txtTipo;
	
	@FXML
	private Label labelErrorLogin;
	
	@FXML
	private Label labelErrorSenha;
	
	@FXML
	private Label labelErrorTipo;
	
	public void setAdmin(Usuario entity) {
		this.entity = entity;
	}
	
	public void setAdminService(UsuarioService service) {
		this.service = service;
	}
	
	@FXML
	public void onBtLoginAction(ActionEvent event) {
		try {
			entity = getFormData();
			boolean login = service.login(entity);
			
			if (!login) {
				Alerts.showALert("Erro ao fazer login", null, "Usuário incorreto", AlertType.ERROR);
			}
			else {
				loadMainView(event);
			}
		}
		catch (ValidationException e) {
			defineMensagemErro(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showALert("Erro database", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void loadMainView(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();
			
			MainViewController controller = loader.getController();
			controller.setUsuario(entity);
			controller.exibirUsuario();
			
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
	
	private Usuario getFormData() {
		Usuario obj = new Usuario();
		
		ValidationException exception = new ValidationException("Validation exception");
		
		obj.setId(null);
		
		if (txtLogin.getText() == null || txtLogin.getText().trim().equals("")) {
			exception.addError("login", "Preencha o login");
		}
		obj.setLogin(txtLogin.getText());
		
		if (pswSenha.getText() == null || pswSenha.getText().trim().equals("")) {
			exception.addError("senha", "Informe a senha");
		}
		obj.setSenha(pswSenha.getText());
		
		if (txtTipo.getText() == null || txtTipo.getText().trim().equals("")) {
			exception.addError("tipo", "Informe o tipo de usuário");
		}
		obj.setTipo(Utils.tryParseToInt(txtTipo.getText()));
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}
	
	private void defineMensagemErro(Map<String, String> map) {
		Set<String> chave = map.keySet();
		
		labelErrorLogin.setText((chave.contains("login") ? map.get("login") : ""));
		labelErrorSenha.setText((chave.contains("senha") ? map.get("senha") : ""));
		labelErrorTipo.setText((chave.contains("tipo") ? map.get("tipo") : ""));
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
}