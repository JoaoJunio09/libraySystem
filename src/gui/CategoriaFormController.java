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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.entities.Categoria;
import model.services.CategoriaService;

public class CategoriaFormController implements Initializable {
	
	private CategoriaService service;
	
	private Categoria entity;
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private TextArea txtAreaDescricao;
	
	@FXML
	private Label labelErrorNome;
	
	@FXML
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
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
			Alerts.showALert("Error database", "Erro ao salvar uma Categoria", e.getMessage(), AlertType.ERROR);
		}
	}
	
	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	public void setCategoriaService(CategoriaService service) {
		this.service = service;
	}
	
	public void setCategoria(Categoria entity) {
		this.entity = entity;
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
		Constraints.setTextFieldMaxLength(txtNome, 80);
	}
	
	public Categoria getFormData() {
		Categoria obj = new Categoria();
		
		ValidationException exception = new ValidationException("Validation Exception");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("nome", "Preencha o nome");
		}
		obj.setNome(txtNome.getText());
		
		obj.setDescricao(txtAreaDescricao.getText());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}
	
	public void updateFormData() {
		if (entity == null) {
			System.out.println("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getNome());
		txtAreaDescricao.setText(entity.getDescricao());
	}
	
	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.dataChanged();
		}
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		labelErrorNome.setText((fields.contains("nome") ? errors.get("nome") : ""));
	}
}
