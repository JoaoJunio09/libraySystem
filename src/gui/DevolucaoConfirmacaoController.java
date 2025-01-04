package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Cliente;
import model.entities.Emprestimo;
import model.entities.Livro;
import model.services.ClienteService;
import model.services.EmprestimoService;
import model.services.LivroService;

public class DevolucaoConfirmacaoController implements Initializable {
	
	private Emprestimo entity;
	
	private EmprestimoService service;
	
	private ClienteService clienteService;
	
	private LivroService livroService;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtStatus;
	
	@FXML
	private TextField txtPesquisarCliente;
	
	@FXML
	private TextField txtPesquisarLivro;
	
	@FXML
	private DatePicker dpDataEmprestimo;
	
	@FXML
	private DatePicker dpDataDevolucao;
	
	@FXML
	private TextArea txtAreaDescricao;
	
	@FXML
	private ComboBox<Cliente> comboBoxCliente;
	
	@FXML
	private ComboBox<Livro> comboBoxLivro;
	
	@FXML
	private Button btConfirmar;
	
	@FXML
	private Button btCancelar;
	
	@FXML
	private Button btPesquisarCliente;
	
	@FXML
	private Button btPesquisarLivro;
	
	@FXML
	private Button btNovoCliente;
	
	@FXML
	private Label labelErrorDataEmprestimo;
	
	@FXML
	private Label labelErrorDataDevolucao;
	
	@FXML
	private Label labelErrorCliente;
	
	@FXML
	private Label labelErrorLivro;
	
	private ObservableList<Cliente> obsListCliente;
	
	private ObservableList<Livro> obsListLivro; 
	
	public void onBtConfirmarAction(ActionEvent event) {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		if (clienteService == null) {
			throw new IllegalStateException("Cliente Service was null");
		}
		if (livroService == null) {
			throw new IllegalStateException("Livro Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			if (updateStatusDevolvido(entity)) {
				System.out.println("true");
			}
			else {
				System.out.println("false");
			}
			Livro livro = entity.getLivro();
			if (updateEstoque(livro)) {
				Alerts.showALert("Estoque atualizado", null, "Estoque do livro " + livro.getNome() + " foi atualizado para: " + livro.getEstoque(), AlertType.INFORMATION);
			}
			notifyDataChangeListener();
			Utils.currentStage(event).close();
		}
		catch (ValidationException e) {
			setMessageError(e.getErrors());
		}
		catch (DbException e) {
			e.printStackTrace();
			Alerts.showALert("Error database", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private Emprestimo getFormData() {
		Emprestimo obj = new Emprestimo();
		
		ValidationException exception = new ValidationException("Validation exception");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setStatus(txtStatus.getText());
		obj.setDescricao(txtAreaDescricao.getText());
		
		if (dpDataEmprestimo.getValue() == null) {
			exception.addError("dataEmprestimo", "Informe a data do emprestimo");
		}
		else {
			Instant instant = Instant.from(dpDataEmprestimo.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setDataEmprestimo(Date.from(instant));
		}
		
		if (dpDataDevolucao.getValue() == null) {
			exception.addError("dataDevolucao", "Informe a data de devolução");
		}
		else {
			Instant instant = Instant.from(dpDataDevolucao.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setDataDevolucao(Date.from(instant));
		}
		
		if (comboBoxCliente.getValue() == null) {
			exception.addError("comboBoxCliente", "Selecione o Cliente");
		}
		obj.setCliente(comboBoxCliente.getValue());
		
		if (comboBoxLivro.getValue() == null) {
			exception.addError("comboBoxLivro", "Selecione o Livro");
		}
		obj.setLivro(comboBoxLivro.getValue());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}
	
	public void onBtCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	public void onBtPesquisarCliente(ActionEvent event) {
		if (clienteService == null) {
			throw new IllegalStateException("Cliente Service was null");
		}
		if (txtPesquisarCliente.getText() == null) {
			Alerts.showALert("Error search", null, "Preencha o campo de pesquisa", AlertType.ERROR);
		}
		
		String txtConteudoPesquisa = txtPesquisarCliente.getText();
		
		List<Cliente> list = clienteService.search(txtConteudoPesquisa);
		obsListCliente = FXCollections.observableArrayList(list);
		if (obsListCliente.isEmpty()) {
			Alerts.showALert("Importante", null, "Cliente " + txtConteudoPesquisa + " não encontrado", AlertType.INFORMATION);
		}
		else {
			Alerts.showALert("Aviso", null, "Pesquisado! Confira o resultado na Lista de Clientes", AlertType.INFORMATION);
			comboBoxCliente.setItems(obsListCliente);
		}
	}
	
	public void onBtPesquisarLivroAction(ActionEvent event) {
		if (livroService == null) {
			throw new IllegalStateException("Livro Service was null");
		}
		if (txtPesquisarLivro.getText() == null) {
			Alerts.showALert("Error search", null, "Preencha o campo de pesquisa", AlertType.ERROR);
		}
		
		String txtConteudoPesquisa = txtPesquisarLivro.getText();
		
		List<Livro> list = livroService.search(txtConteudoPesquisa);
		obsListLivro = FXCollections.observableArrayList(list);
		if (obsListLivro.isEmpty()) {
			Alerts.showALert("Importante", null, "Livro " + txtConteudoPesquisa + " não encontrado", AlertType.INFORMATION);
		}
		else {
			Alerts.showALert("Aviso", null, "Pesquisado! Confira o resultado na Lista de Livros", AlertType.INFORMATION);
			comboBoxLivro.setItems(obsListLivro);
		}
	}
	
	public void onBtNovoClienteAction(ActionEvent event) {
		MainViewController mainViewController = new MainViewController();
		mainViewController.loadView("/gui/ClienteList.fxml", (ClienteListController controller) -> {
			controller.setClienteService(new ClienteService());
			controller.updateTableView();
		});
		
		Utils.currentStage(event).close();
	}
	
	public void setEmprestimo(Emprestimo entity) {
		this.entity = entity;
	}
	
	public void setServices(EmprestimoService service, ClienteService clienteService, LivroService livroSerivce) {
		this.service = service;
		this.clienteService = clienteService;
		this.livroService = livroSerivce;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtStatus.setText("Devolvido");
		txtAreaDescricao.setText(entity.getDescricao());
		
		if (entity.getDataEmprestimo() != null) {
			dpDataEmprestimo.setValue(LocalDate.ofInstant(entity.getDataEmprestimo().toInstant(), ZoneId.systemDefault()));
		}
		if (entity.getDataDevolucao() != null) { 
			dpDataDevolucao.setValue(LocalDate.ofInstant(entity.getDataDevolucao().toInstant(), ZoneId.systemDefault()));
		}
		
		if (entity.getCliente() == null) {
			comboBoxCliente.getSelectionModel().selectFirst();
		}
		else {
			comboBoxCliente.setValue(entity.getCliente());
		}
		
		if (entity.getLivro() == null) {
			comboBoxLivro.getSelectionModel().selectFirst();
		}
		else {
			comboBoxLivro.setValue(entity.getLivro());
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Utils.formatDatePicker(dpDataEmprestimo, "dd/MM/yyyy");
		Utils.formatDatePicker(dpDataDevolucao, "dd/MM/yyyy");
		
		initializeComboBoxCliente();
		initializeComboBoxLivro();
	}
	
	public void loadAssociatedCliente() {
		if (clienteService == null) {
			throw new IllegalStateException("Cliente Service was null");
		}
		List<Cliente> list = clienteService.findAll();
		obsListCliente = FXCollections.observableArrayList(list);
		comboBoxCliente.setItems(obsListCliente);
	}
	
	public void loadAssociatedLivro() {
		if (livroService == null) {
			throw new IllegalStateException("Livro Service was null");
		}
		
		livroService.updateDataAll();
		
		List<Livro> list = livroService.findAll();	
		List<Livro> listObs = new ArrayList<>();
		
		for (Livro livro : list) {
			if (livro.getDisponibilidade().equals(livro.getLivroDisponivel())) {
				listObs.add(livro);
			}
		}
		
		obsListLivro = FXCollections.observableArrayList(listObs);
		comboBoxLivro.setItems(obsListLivro);
	}
	
	private void setMessageError(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		labelErrorDataDevolucao.setText((fields.contains("dataEmprestimo") ? errors.get("dataEmprestimo") : ""));
		labelErrorDataDevolucao.setText((fields.contains("dataDevolucao") ? errors.get("dataDevolucao") : ""));
		labelErrorCliente.setText((fields.contains("comboBoxCliente") ? errors.get("comboBoxCliente") : ""));
		labelErrorLivro.setText((fields.contains("comboBoxLivro") || fields.contains("comboBoxLivroIndisponivel") ? errors.get("comboBoxLivro") : ""));
	}
	
	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.dataChanged();
		}
	}
	
	private boolean updateEstoque(Livro livro) {
		try {
			livro.setEstoque(livro.getEstoque() + 1);
			livroService.saveOrUpdate(livro);
			return true;
		}
		catch (DbException e) {
			Alerts.showALert("Error database", null, e.getMessage(), AlertType.ERROR);
			return false;
		}
	}
	
	private boolean updateStatusDevolvido(Emprestimo emp) {
		try {
			emp.setStatus("Devolvido");
			service.saveOrUpdate(emp);
			return true;
		}
		catch (DbException e) {
			Alerts.showALert("Error database", null, e.getMessage(), AlertType.ERROR);
			return false;
		}
	}
	
	private void initializeComboBoxCliente() {
		Callback<ListView<Cliente>, ListCell<Cliente>> factory = lv -> new ListCell<Cliente>() {
			@Override
			protected void updateItem(Cliente item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxCliente.setCellFactory(factory);
		comboBoxCliente.setButtonCell(factory.call(null));
	}
	
	private void initializeComboBoxLivro() {
		Callback<ListView<Livro>, ListCell<Livro>> factory = lv -> new ListCell<Livro>() {	
			@Override
			protected void updateItem(Livro item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxLivro.setCellFactory(factory);
		comboBoxLivro.setButtonCell(factory.call(null));
	}
}