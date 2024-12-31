package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.exceptions.ValidationException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Cidade;
import model.entities.Cliente;
import model.services.CidadeService;
import model.services.ClienteService;

public class ClienteFormController implements Initializable {

	private Cliente entity;

	private ClienteService service;

	private CidadeService cidadeService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtSobrenome;

	@FXML
	private DatePicker dpDataNascimento;
	
	@FXML
	private TextField txtTelefone;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private TextField txtEndereco;

	@FXML
	private ComboBox<Cidade> comboBoxCidade;

	@FXML
	private Label labelErrorNome;
	
	@FXML
	private Label labelErrorSobrenome;
	
	@FXML
	private Label labelErrorDataNascimento;
	
	@FXML
	private Label labelErrorTelefone;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorEndereco;
	
	@FXML
	private Label labelErrorCidade;
	
	@FXML
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;

	private ObservableList<Cidade> obsList;

	public void setCliente(Cliente entity) {
		this.entity = entity;
	}

	public void setServices(ClienteService service, CidadeService cidadeService) {
		this.service = service;
		this.cidadeService = cidadeService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} 
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} 
		catch (DbException e) {
			Alerts.showALert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.dataChanged();
		}
	}

	private Cliente getFormData() {
		Cliente obj = new Cliente();
		
		ValidationException exception = new ValidationException("Validation exception");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("nome", "Preencha o nome");
		}
		obj.setNome(txtNome.getText());
		
		if (txtSobrenome.getText() == null || txtSobrenome.getText().trim().equals("")) {
			exception.addError("sobrenome", "Preencha o sobrenome");
		}
		obj.setSobrenome(txtSobrenome.getText());
		
		if (dpDataNascimento.getValue() == null) {
			exception.addError("dataNascimento", "Informe a data de nascimento");
		}
		else {
			Instant instant = Instant.from(dpDataNascimento.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setDataNascimento(Date.from(instant));
		}
		
		if (txtTelefone.getText() == null || txtTelefone.getText().trim().equals("")) {
			exception.addError("telefone", "Informe um número de telefone");
		}
		obj.setTelefone(txtTelefone.getText());
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addError("email", "Informe um e-mail válido");
		}
		obj.setEmail(txtEmail.getText());
		
		if (txtEndereco.getText() == null || txtEndereco.getText().trim().equals("")) {
			exception.addError("endereco", "Informe um endereço");
		}
		obj.setEndereco(txtEndereco.getText());
		
		if (comboBoxCidade.getValue() == null) {
			exception.addError("cidade", "Selecione uma cidade");
		}
		obj.setCidade(comboBoxCidade.getValue());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 70);
		Constraints.setTextFieldMaxLength(txtSobrenome, 80);
		Constraints.setTextFieldMaxLength(txtEmail, 80);
		Utils.formatDatePicker(dpDataNascimento, "dd/MM/yyyy");
		
		initializeComboBoxCidade();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getNome());
		txtSobrenome.setText(entity.getSobrenome());
		if (entity.getDataNascimento() != null) {
			dpDataNascimento.setValue(LocalDate.ofInstant(entity.getDataNascimento().toInstant(), ZoneId.systemDefault()));
		}
		txtTelefone.setText(entity.getTelefone());
		txtEmail.setText(entity.getEmail());
		txtEndereco.setText(entity.getEndereco());
		if (entity.getCidade() == null) {
			comboBoxCidade.getSelectionModel().selectFirst();
		}
		else {
			comboBoxCidade.setValue(entity.getCidade());
		}
	}

	public void loadAssociatedCidade() {
		if (cidadeService == null) {
			throw new IllegalStateException("Cidade Service was null");
		}
		List<Cidade> list = cidadeService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxCidade.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		labelErrorNome.setText((fields.contains("nome") ? errors.get("nome") : ""));
		labelErrorSobrenome.setText((fields.contains("sobrenome") ? errors.get("sobrenome") : ""));
		labelErrorDataNascimento.setText((fields.contains("dataNascimento") ? errors.get("dataNascimento") : ""));
		labelErrorTelefone.setText((fields.contains("telefone") ? errors.get("telefone") : ""));
		labelErrorEmail.setText((fields.contains("email") ? errors.get("email") : ""));
		labelErrorEndereco.setText((fields.contains("endereco") ? errors.get("endereco") : ""));
		labelErrorCidade.setText((fields.contains("cidade") ? errors.get("cidade") : ""));
	}

	private void initializeComboBoxCidade() {
		Callback<ListView<Cidade>, ListCell<Cidade>> factory = lv -> new ListCell<Cidade>() {
			@Override
			protected void updateItem(Cidade item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxCidade.setCellFactory(factory);
		comboBoxCidade.setButtonCell(factory.call(null));
	}
}