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

import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Cliente;
import model.entities.Emprestimo;
import model.entities.Livro;
import model.services.ClienteService;
import model.services.EmprestimoService;
import model.services.LivroService;

public class DevolucaoViewController implements Initializable, DataChangeListener {
	
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
	private TableColumn<Emprestimo, Emprestimo> tableColumnRegistrarDevolucao;
	
	@FXML
	private Button btConcluido;
	
	@FXML
	private Button btCancelar;
	
	@FXML
	private DatePicker dpFiltrarDataInicial;
	
	@FXML
	private DatePicker dpFiltrarDataFinal;
	
	@FXML
	private TextField txtNomeCliente;
	
	@FXML
	private Button btPesquisar;
	
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
	public void onBtPesquisarAction() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			String sql = "WHERE ";
			int count = 0;
			
			Date dataEmprestimo = null;
			Date dataDevolucao = null;
			String nomeCliente = "";
			
			if (dpFiltrarDataInicial.getValue() != null && dpFiltrarDataFinal.getValue() != null) {
				String sqlDatas = "";
				
				Instant instantInicial = Instant.from(dpFiltrarDataInicial.getValue().atStartOfDay(ZoneId.systemDefault()));
				dataEmprestimo = Date.from(instantInicial);
				
				Instant instantFinal = Instant.from(dpFiltrarDataFinal.getValue().atStartOfDay(ZoneId.systemDefault()));
				dataDevolucao = Date.from(instantFinal);
				
				String dataInicial = new SimpleDateFormat("yyyy-MM-dd").format(dataEmprestimo);
				String dataFinal = new SimpleDateFormat("yyyy-MM-dd").format(dataDevolucao);
				
				if (count > 0) {										
					sqlDatas = "OR emp.DataEmprestimo BETWEEN '" + dataInicial + "' and '" + dataFinal + "'";
				}
				else {
					sqlDatas = "emp.DataEmprestimo BETWEEN '" + dataInicial + "' and '" + dataFinal + "'";
				}
				
				sql+= sqlDatas;
				count++;
			}
			
			if (txtNomeCliente.getText() != null) {
				String sqlNomeCliente = "";
				nomeCliente = txtNomeCliente.getText();
				
				if (count > 0) {
					sqlNomeCliente = "OR cli.Nome LIKE '" + nomeCliente + "%'";
				}
				else {
					sqlNomeCliente = "cli.Nome LIKE '" + nomeCliente + "%'";
				}
				
				sql += sqlNomeCliente;
				count++;
			}
			
			if (count > 0) {
				String sqlWherePendente = "AND emp.Status = 'Pendente'";
				sql += sqlWherePendente;
				count++;
			}
			else {
				String sqlWherePendente = "emp.Status = 'Pendente'";
				sql += sqlWherePendente;
				count++;
			}
			
			System.out.println(sql);
			List<Emprestimo> list = service.filtragemCompleta(sql);
			obsList = FXCollections.observableArrayList(list);
			tableViewEmprestimo.setItems(obsList);
		}
		catch (NullPointerException e) {
			e.printStackTrace();
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
		
		List<Emprestimo> list = service.findAllStatusPendente();
		obsList = FXCollections.observableArrayList(list);
		tableViewEmprestimo.setItems(obsList);
		
		initDevolverButtons();
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
			
			DevolucaoConfirmacaoController controller = loader.getController();
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

	private void initDevolverButtons() {
		tableColumnRegistrarDevolucao.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRegistrarDevolucao.setCellFactory(param -> new TableCell<Emprestimo, Emprestimo>() {
			private final Button button = new Button("Devolver");

			@Override
			protected void updateItem(Emprestimo obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DevolucaoConfirmacao.fxml", Utils.currentStage(event)));
			}
		});
	}

	@Override
	public void dataChanged() {
		updateTableView();
	}
}