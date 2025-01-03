package gui;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Cliente;
import model.entities.Emprestimo;
import model.entities.Livro;
import model.services.ClienteService;
import model.services.EmprestimoService;
import model.services.LivroService;

public class EmprestimoListController implements Initializable, DataChangeListener {
	
	private EmprestimoService service;
	
	@FXML
	private TableView<Emprestimo> tableViewEmprestimo;
	
	@FXML
	private TableColumn<Emprestimo, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Emprestimo, Cliente> tableColumnCliente;
	
	@FXML
	private TableColumn<Emprestimo, Livro> tableColumnLivro;
	
	@FXML
	private TableColumn<Emprestimo, Date> tableColumnDataEmprestimo;
	
	@FXML
	private TableColumn<Emprestimo, Date> tableColumnDataDevolucao;
	
	@FXML
	private TableColumn<Emprestimo, String> tableColumnDescricao;
	
	@FXML
	private TableColumn<Emprestimo, String> tableColumnStatus;
	
	@FXML
	private Button btNovoEmprestimo;
	
	@FXML
	private Button btRelatorio;
	
	@FXML
	private Button btFiltrarPesquisar;
	
	@FXML
	private Button btCliente;
	
	@FXML
	private Button btConcluido;
	
	@FXML
	private Button btCancelar;
	
	@FXML
	private DatePicker dpFiltrarDataInicial;
	
	@FXML
	private DatePicker dpFiltrarDataFinal;
	
	@FXML
	private Button btFiltrar;
	
	@FXML
	public void onBtNovoEmprestimoAction(ActionEvent event) {
		Stage parent = Utils.currentStage(event);
		Emprestimo obj = new Emprestimo();
		createDialogForm(obj, "/gui/EmprestimoForm.fxml", parent);
	}
	
	@FXML
	public void onBtRelatorioAction(ActionEvent event) {
		Stage parent = Utils.currentStage(event);
		createRelatorio("/gui/EmprestimoRelatorio.fxml", parent);
	}
	
	@FXML
	public void obBtFiltrarPesquisarAction(ActionEvent event) {
		Stage parent = Utils.currentStage(event);
		createFilter("/gui/EmprestimoFiltragemCompleta.fxml", parent);
	}
	
	@FXML
	public void onBtClienteAction() {
		loadView("Registro - Cliente", "/gui/ClienteList.fxml", (ClienteListController controller) -> {
			controller.setClienteService(new ClienteService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onBtConcluidoAction(ActionEvent event) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmar", "Concluído - Deseja sair?");
		
		if (result.get() == ButtonType.OK) {
			Utils.currentStage(event).close();
		}
	}
	
	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@FXML
	public void onBtFiltrarAction() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			Date dataEmprestimo = null;
			Date dataDevolucao = null;
			
			if (dpFiltrarDataInicial != null) {
				Instant instant = Instant.from(dpFiltrarDataInicial.getValue().atStartOfDay(ZoneId.systemDefault()));
				dataEmprestimo = Date.from(instant);
			}
			
			if (dpFiltrarDataFinal != null) {
				Instant instant = Instant.from(dpFiltrarDataFinal.getValue().atStartOfDay(ZoneId.systemDefault()));
				dataDevolucao = Date.from(instant);
			}
			
			String dataInicial = new SimpleDateFormat("yyyy-MM-dd").format(dataEmprestimo);
			String dataFinal = new SimpleDateFormat("yyyy-MM-dd").format(dataDevolucao);
			
			List<Emprestimo> list = service.filtrar(dataInicial, dataFinal);
			obsList = FXCollections.observableArrayList(list);
			tableViewEmprestimo.setItems(obsList);
		}
		catch (NullPointerException e) {
			Alerts.showALert("Preencha a data", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private ObservableList<Emprestimo> obsList;
	
	public void setEmprestimoService(EmprestimoService service) {
		this.service = service;
	}
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		service.updateDataAll();
		
		List<Emprestimo> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewEmprestimo.setItems(obsList);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		tableColumnCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
		tableColumnLivro.setCellValueFactory(new PropertyValueFactory<>("livro"));
		tableColumnDataEmprestimo.setCellValueFactory(new PropertyValueFactory<>("dataEmprestimo"));
		Utils.formatTableColumnDate(tableColumnDataEmprestimo, "dd/MM/yyyy");
		tableColumnDataDevolucao.setCellValueFactory(new PropertyValueFactory<>("dataDevolucao"));
		Utils.formatTableColumnDate(tableColumnDataDevolucao, "dd/MM/yyyy");
		tableColumnDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
		tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		
		Utils.formatDatePicker(dpFiltrarDataInicial, "dd/MM/yyyy");
		Utils.formatDatePicker(dpFiltrarDataFinal, "dd/MM/yyyy");
	}
	
	private synchronized <T> void createDialogForm(Emprestimo obj, String absoluteName, Stage parent) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane empForm = loader.load();
			
			EmprestimoFormController controller = loader.getController();
			controller.setEmprestimo(obj);
			controller.setServices(new EmprestimoService(), new ClienteService(), new LivroService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			controller.loadAssociatedCliente();
			controller.loadAssociatedLivro();
			
			Stage stageDialog = new Stage();
			stageDialog.setTitle("Registrar Emprestimo");
			stageDialog.setScene(new Scene(empForm));
			stageDialog.setResizable(false);
			stageDialog.initOwner(parent);
			stageDialog.initModality(Modality.APPLICATION_MODAL);
			stageDialog.showAndWait();
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showALert("IO Exception", "Erro ao carregar", e.getMessage(), AlertType.ERROR);
		}
	}
	
	private synchronized <T> void createFilter(String absoluteName, Stage parent) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane empFiltragemView = loader.load();
			
			EmprestimoFiltragemCompletaController controller = loader.getController();
			controller.setServices(new EmprestimoService());
			controller.associatedStatusEmprestimo();
			
			Stage stageDialog = new Stage();
			stageDialog.setTitle("Filtragem completa - Empréstimo");
			stageDialog.setScene(new Scene(empFiltragemView));
			stageDialog.setResizable(false);
			stageDialog.initOwner(parent);
			stageDialog.initModality(Modality.APPLICATION_MODAL);
			stageDialog.showAndWait();
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showALert("IO Exception", "Erro ao carregar", e.getMessage(), AlertType.ERROR);
		}
	}
	
	private synchronized <T> void loadView(String title, String absoluteName, Consumer<T> initializing) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox empVBox = loader.load();
			
			Scene empScene = new Scene(empVBox);
			Stage empStage = new Stage();
			empStage.setScene(empScene);
			empStage.setTitle(title);
			empStage.setMaximized(false);
			empStage.show();
			
			T controller = loader.getController();
			initializing.accept(controller);
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showALert("IO Exception", "Erro ao carregar", e.getMessage(), AlertType.ERROR);
		}
	}
	
	private synchronized <T> void createRelatorio(String absoluteName, Stage parent) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane empRelatorio = loader.load();
			
			EmprestimoRelatorioController controller = loader.getController();
			controller.setEmprestimoService(new EmprestimoService());
			controller.updateTableView();
			
			Stage stageDialog = new Stage();
			stageDialog.setTitle("Relátorio - Empréstimo");
			stageDialog.setScene(new Scene(empRelatorio));
			stageDialog.setResizable(false);
			stageDialog.initOwner(parent);
			stageDialog.initModality(Modality.APPLICATION_MODAL);
			stageDialog.showAndWait();
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showALert("IO Exception", "Erro ao carregar", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void dataChanged() {
		updateTableView();
	}
}