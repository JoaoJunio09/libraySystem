package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entities.Cliente;
import model.entities.Emprestimo;
import model.entities.Livro;
import model.services.EmprestimoService;

public class EmprestimoRelatorioController implements Initializable {
	
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
	private Button btEmprestimosRegistradosNoMes;
	
	@FXML
	private Button btEmprestimosDevolvidos;
	
	@FXML
	private Button btEmprestimosNaoDevolvidos;
	
	@FXML
	private Button btEmprestimosPendentes;
	
	@FXML
	private Button btImprimir;
	
	@FXML
	private Button btCancelar;
	
	@FXML
	private Button btListarTodos;
	
	private ObservableList<Emprestimo> obsList;

	@FXML
	public void btImprimirAction() {
		System.out.println("btImprimirAction");
	}
	
	@FXML
	public void btCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@FXML
	public void onBtListarTodos() {
		System.out.println("onBtListarTodos");
	}
	
	public void setEmprestimoService(EmprestimoService service) {
		this.service = service;
	}
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
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
	}
}