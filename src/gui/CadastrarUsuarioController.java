package gui;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.exceptions.ValidationException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Usuario;
import model.services.UsuarioService;

public class CadastrarUsuarioController implements Initializable {
	
	private UsuarioService service;
	
	private Usuario entity;
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtLogin;
	
	@FXML
	private PasswordField pswSenha;
	
	@FXML
	private ComboBox<Integer> comboBoxTipo;
	
	@FXML
	private Label labelErrorLogin;
	
	@FXML
	private Label labelErrorSenha;
	
	@FXML
	private Label labelErrorTipo;
	
	@FXML
	private Button onBtCadastrar;
	
	private ObservableList<Integer> obsList;
	
	@FXML
	public void onBtCadastrarAction() {
		if (service == null) {
			throw new IllegalStateException("Service é nulo");
		}
		try {
			entity = obterDadosDoFormulario();
			service.saveOrUpdate(entity);
			
			limpaDadosDoFormulario();
			Alerts.showALert("Concluido", "Usuário:" + entity.getLogin(), "Usuário cadastrado com sucesso!", AlertType.INFORMATION);
		}
		catch (ValidationException e) {
			defineMenssagemErro(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showALert("Error database", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	public void setUsuario(Usuario entity) {
		this.entity = entity;
	}
	
	public void setUsuarioService(UsuarioService service) {
		this.service = service;
	}
	
	private Usuario obterDadosDoFormulario() {
		Usuario obj = new Usuario();
		
		ValidationException exception = new ValidationException("Validation exception");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtLogin.getText() == null || txtLogin.getText().trim().equals("")) {
			exception.addError("login", "Preencha o login");
		}
		obj.setLogin(txtLogin.getText());
		
		if (pswSenha.getText() == null || pswSenha.getText().trim().equals("")) {
			exception.addError("senha", "Informe a senha");
		}
		obj.setSenha(pswSenha.getText());
		
		if (comboBoxTipo.getValue() == null) {
			exception.addError("tipo", "Informe o tipo de Usuário");
		}
		obj.setTipo(comboBoxTipo.getValue());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;		
	}
	
	public void atualizaDadosFormulario() {
		if (service == null) {
			throw new IllegalStateException("Service é nulo");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtLogin.setText(entity.getLogin());
		pswSenha.setText(entity.getSenha());
		if (entity.getTipo() == null) {
			comboBoxTipo.getSelectionModel().selectFirst();
		}
		else {
			comboBoxTipo.setValue(entity.getTipo());
		}
	}
	
	public void associandoTipoDeUsuario() {
		List<Integer> list  = Arrays.asList(1, 2);
		obsList = FXCollections.observableArrayList(list);
		comboBoxTipo.setItems(obsList);
	}
	
	private void defineMenssagemErro(Map<String, String> map) {
		Set<String> chave = map.keySet();
		
		labelErrorLogin.setText((chave.contains("login") ? map.get("login") : ""));
		labelErrorSenha.setText((chave.contains("senha") ? map.get("senha") : ""));
		labelErrorTipo.setText((chave.contains("tipo") ? map.get("tipo") : ""));
	}
	
	private void limpaDadosDoFormulario() {
		labelErrorLogin.setText("");
		labelErrorSenha.setText("");
		labelErrorTipo.setText("");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Constraints.setTextFieldInteger(txtId);
		
		initializeComboBoxTipo();
	}
	
	private void initializeComboBoxTipo() {
		Callback<ListView<Integer>, ListCell<Integer>> factory = lv -> new ListCell<Integer>() {
			@Override
			protected void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.toString());
			}
		};
		comboBoxTipo.setCellFactory(factory);
		comboBoxTipo.setButtonCell(factory.call(null));
	}
}