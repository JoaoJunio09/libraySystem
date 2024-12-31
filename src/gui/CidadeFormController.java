package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Cidade;
import model.entities.Estado;
import model.services.CidadeService;
import model.services.EstadoService;

public class CidadeFormController implements Initializable {
	
	private Cidade entity;
	
	private CidadeService service;
	
	private EstadoService estadoService;
	
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;
	
	@FXML
	private TextField txtCep;
	
	@FXML
	private ComboBox<Estado> comboBoxEstado;
	
	@FXML
	private Label labelErrorNome;
	
	@FXML
	private Label labelErrorCep;
	
	@FXML
	private Label labelErrorEstado;
	
	@FXML
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	private ObservableList<Estado> obsList;
	
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
			notifyDataChangeListener();
			Utils.currentStage(event).close();
		}
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showALert("Error database", "Erro ao tentar salvar Cidade", e.getMessage(), AlertType.ERROR);
		}
	}
	
	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	public void setCidade(Cidade entity) {
		this.entity = entity;
	}
	
	public void setServices(CidadeService service, EstadoService estadoService) {
		this.service = service;
		this.estadoService = estadoService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 50);
		Constraints.setTextFieldMaxLength(txtCep, 8);
		
		initializeComboBoxDepartment();
	}
	
	private Cidade getFormData() {
		Cidade obj = new Cidade();
		
		ValidationException exception = new ValidationException("Validation exception");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("nome", "Preencha o nome");
		}
		obj.setNome(txtNome.getText());
		
		if (txtCep.getText() == null || txtCep.getText().trim().equals("")) {
			exception.addError("cep", "Preencha o CEP");
		}
		obj.setCep(txtCep.getText());
		
		if (entity.getEstado() == null) {
			exception.addError("estado", "Selecione um estado");
		}
		obj.setEstado(comboBoxEstado.getValue());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getNome());
		txtCep.setText(entity.getCep());
		if (entity.getEstado() == null) {
			comboBoxEstado.getSelectionModel().selectFirst();
		}
		else {
			comboBoxEstado.setValue(entity.getEstado());
		}
	}
	
	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.dataChanged();
		}
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		labelErrorNome.setText((fields.contains("nome") ? errors.get("nome") : ""));
		labelErrorCep.setText((fields.contains("cep") ? errors.get("cep") : ""));
		labelErrorEstado.setText((fields.contains("estado") ? errors.get("estado") : ""));
	}
	
	public void loadAssociatedEstado() {
		if (estadoService == null) {
			throw new IllegalStateException("Estado service was null");
		}
		List<Estado> list = estadoService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxEstado.setItems(obsList);
	}
	
	private void initializeComboBoxDepartment() {
		Callback<ListView<Estado>, ListCell<Estado>> factory = lv -> new ListCell<Estado>() {
			@Override
			protected void updateItem(Estado item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxEstado.setCellFactory(factory);
		comboBoxEstado.setButtonCell(factory.call(null));
	}
}