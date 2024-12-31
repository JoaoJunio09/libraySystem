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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Categoria;
import model.entities.Livro;
import model.services.CategoriaService;
import model.services.LivroService;

public class LivroFormController implements Initializable {
	
	private LivroService service;
	
	private CategoriaService categoriaService;
	
	private Livro entity;
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private TextArea txtAreaDescricao;
	
	@FXML
	private TextField txtAutor;
	
	@FXML
	private TextField txtEstoque;
	
	@FXML
	private TextField txtDisponibilidade;
	
	@FXML
	private ComboBox<Categoria> comboBoxCategoria;
	
	@FXML
	private Label labelErrorNome;
	
	@FXML
	private Label labelErrorAutor;
	
	@FXML
	private Label labelErrorEstoque;
	
	@FXML
	private Label labelErrorCategoria;
	
	@FXML
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	private ObservableList<Categoria> obsList;
	
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
			Alerts.showALert("Error database", "Erro ao salvar uma Livro", e.getMessage(), AlertType.ERROR);
		}
	}
	
	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	public void setServices(LivroService service, CategoriaService categoriaService) {
		this.service = service;
		this.categoriaService = categoriaService;
	}
	
	public void setLivro(Livro entity) {
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
		Constraints.setTextFieldInteger(txtEstoque);
		Constraints.setTextFieldMaxLength(txtNome, 80);
	}
	
	public Livro getFormData() {
		Livro obj = new Livro();
		
		ValidationException exception = new ValidationException("Validation Exception");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("nome", "Preencha o nome");
		}
		obj.setNome(txtNome.getText());
		
		obj.setDescricao(txtAreaDescricao.getText());
		
		if (txtAutor.getText() == null || txtAutor.getText().trim().equals("")) {
			exception.addError("autor", "Preencha o autor");
		}
		obj.setAutor(txtAutor.getText());
		
		if (txtEstoque.getText() == null || txtEstoque.getText().trim().equals("")) {
			exception.addError("estoque", "Preencha o estoque");
		}
		
		int estoqueUnidade = Utils.tryParseToInt(txtEstoque.getText());
		
		if (estoqueUnidade < 1) {
			exception.addError("estoqueUnidade", "Mínimo 1 unidade");
		}
		obj.setEstoque(estoqueUnidade);
		
		obj.setCategoria(comboBoxCategoria.getValue());
		
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
		txtAutor.setText(entity.getAutor());
		txtEstoque.setText(String.valueOf(entity.getEstoque()));
		txtDisponibilidade.setText(entity.getDisponibilidade());
		if (entity.getCategoria() == null) {
			comboBoxCategoria.getSelectionModel().selectFirst();
		}
		else {
			comboBoxCategoria.setValue(entity.getCategoria());
		}
		
		initializeComboBoxDepartment();
	}
	
	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.dataChanged();
		}
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		labelErrorNome.setText((fields.contains("nome") ? errors.get("nome") : ""));
		labelErrorAutor.setText((fields.contains("autor") ? errors.get("autor") : ""));
		labelErrorEstoque.setText((fields.contains("estoque") ? errors.get("estoque") : ""));
		labelErrorEstoque.setText((fields.contains("estoqueUnidade") ? errors.get("estoqueUnidade") : ""));
		labelErrorCategoria.setText((fields.contains("categoria") ? errors.get("categoria") : ""));
	}
	
	public void loadAssociatedCategoria() {
		List<Categoria> list = categoriaService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxCategoria.setItems(obsList);
	}
	
	private void initializeComboBoxDepartment() {
		Callback<ListView<Categoria>, ListCell<Categoria>> factory = lv -> new ListCell<Categoria>() {
			@Override
			protected void updateItem(Categoria item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxCategoria.setCellFactory(factory);
		comboBoxCategoria.setButtonCell(factory.call(null));
	}
}
