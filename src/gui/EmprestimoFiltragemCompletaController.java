package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entities.Cliente;
import model.entities.Emprestimo;
import model.entities.Livro;
import model.services.EmprestimoService;

public class EmprestimoFiltragemCompletaController implements Initializable {
	
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
	private TextField txtIdCliente;
	
	@FXML
	private TextField txtNomeCliente;
	
	@FXML
	private TextField txtEmailCliente;
	
	@FXML
	private TextField txtIdLivro;
	
	@FXML
	private TextField txtNomeLivro;
	
	@FXML
	private TextField txtAutorLivro;
	
	@FXML
	private TextField txtIdEmprestimo;
	
	@FXML
	private DatePicker dpDataInicialEmprestimo;
	
	@FXML
	private DatePicker dpDataFinalEmprestimo;
	
	@FXML
	private DatePicker dpDataInicialDevolucao;
	
	@FXML
	private DatePicker dpDataFinalDevolucao;
	
	@FXML
	private ComboBox<String> comboBoxStatus;
	
	@FXML
	private Label labelCodigoSQL;
	
	@FXML
	private Button btLimparCampos;
	
	private ObservableList<Emprestimo> obsList;
	
	private ObservableList<String> obsListStatus;
	
	public void onBtLimparCamposAction() {
		sql = "";
		txtIdCliente.setText("");
		txtNomeCliente.setText("");
		txtEmailCliente.setText("");
		txtIdLivro.setText("");
		txtNomeLivro.setText("");
		txtAutorLivro.setText("");
		txtIdEmprestimo.setText("");
		dpDataInicialEmprestimo.setValue(null);
		dpDataFinalEmprestimo.setValue(null);
		dpDataInicialDevolucao.setValue(null);
		dpDataFinalDevolucao.setValue(null);
		comboBoxStatus.setValue(null);
		labelCodigoSQL.setText(sql);
	}
	
	@FXML
	private Button btFiltrar;
	
	private int count = 0;
	private String sql = "";
	
	@FXML
	public void onBtFiltrarAction() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			createSqlFilter();	
			updateTableView();
			
			System.out.println(sql);
			
			sql = "WHERE ";
			count = 0;
		}
		catch (DbException e) {
			e.printStackTrace();
			Alerts.showALert("Error database", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void createSqlFilter() {
		if (labelCodigoSQL.getText() == "") {
			sql = "WHERE ";
			labelCodigoSQL.setText(sql);
		}
		
		if (!txtIdCliente.getText().equals("")) {
			String sqlIdCliente = "cli.Id = " + txtIdCliente.getText() + " ";
			sql += sqlIdCliente;
			labelCodigoSQL.setText(sql);
			count++;
		}
		
		if (!txtNomeCliente.getText().equals("")) {
			if (count > 0) {
				String sqlNomeCliente = "OR cli.Nome LIKE '" + txtNomeCliente.getText() + "%' ";
				sql += sqlNomeCliente;
				labelCodigoSQL.setText(sql);
				count++;
			}
			else {
				String sqlNomeCliente = "cli.Nome LIKE '" + txtNomeCliente.getText() + "%' ";
				sql += sqlNomeCliente;
				labelCodigoSQL.setText(sql);
				count++;
			}
		}
		
		if (!txtEmailCliente.getText().equals("")) {
			if (count > 0) {
				String sqlEmailCliente = "OR cli.Email LIKE '" + txtEmailCliente.getText() + "%' ";
				sql += sqlEmailCliente;
				labelCodigoSQL.setText(sql);
				count++;
			}
			else {
				String sqlEmailCliente = "cli.Email LIKE '" + txtEmailCliente.getText() + "%' ";
				sql += sqlEmailCliente;
				labelCodigoSQL.setText(sql);
				count++;
			}
		}
		
		if (!txtIdLivro.getText().equals("")) {
			if (count > 0) {
				String sqlIdLivro = "OR liv.Id = " + txtIdLivro.getText() + " ";
				sql += sqlIdLivro;
				labelCodigoSQL.setText(sql);
				count++;
			}
			else {
				String sqlIdLivro = "liv.Id = " + txtIdLivro.getText() + " ";
				sql += sqlIdLivro;
				labelCodigoSQL.setText(sql);
				count++;
			}
		}
		
		if (!txtNomeLivro.getText().equals("")) {
			if (count > 0) {
				String sqlNomeLivro = "OR liv.Nome LIKE '" + txtNomeLivro.getText() + "%' ";
				sql += sqlNomeLivro;
				labelCodigoSQL.setText(sql);
				count++;
			}
			else {
				String sqlNomeLivro = "liv.Nome LIKE '" + txtNomeLivro.getText() + "%' ";
				sql += sqlNomeLivro;
				labelCodigoSQL.setText(sql);
				count++;
			}
		}
		
		if (!txtAutorLivro.getText().equals("")) {
			if (count > 0) {
				String sqlAutorLivro = "OR liv.Autor LIKE '" + txtAutorLivro.getText() + "%' ";
				sql += sqlAutorLivro;
				labelCodigoSQL.setText(sql);
				count++;
			}
			else {
				String sqlAutorLivro = "liv.Autor LIKE '" + txtAutorLivro.getText() + "%' ";
				sql += sqlAutorLivro;
				labelCodigoSQL.setText(sql);
				count++;
			}
		}
		
		if (!txtIdEmprestimo.getText().equals("")) {
			if (count > 0) {
				String sqlIdEmprestimo = "OR emp.Id = " + txtIdEmprestimo.getText() + " ";
				sql += sqlIdEmprestimo;
				labelCodigoSQL.setText(sql);
				count++;
			}
			else {
				String sqlIdEmprestimo = "emp.Id = " + txtIdEmprestimo.getText() + " ";
				sql += sqlIdEmprestimo;
				labelCodigoSQL.setText(sql);
				count++;
			}
		}
		
		if (dpDataInicialEmprestimo.getValue() != null && dpDataFinalEmprestimo.getValue() != null) {
			if (count > 0) {
				Instant instantInicial = Instant.from(dpDataInicialEmprestimo.getValue().atStartOfDay(ZoneId.systemDefault()));
				Date dateInicial = Date.from(instantInicial);
				
				Instant instantFinal = Instant.from(dpDataFinalEmprestimo.getValue().atStartOfDay(ZoneId.systemDefault()));
				Date dateFinal = Date.from(instantFinal);

				String dataInicialEmprestimo = new SimpleDateFormat("yyyy-MM-dd").format(dateInicial);
				String dataFinalEmprestimo = new SimpleDateFormat("yyyy-MM-dd").format(dateFinal);
				
				String sqlDataEmprestimo = "OR emp.DataEmprestimo BETWEEN '" + dataInicialEmprestimo + "' and '" + dataFinalEmprestimo + "' ";
				sql += sqlDataEmprestimo;
				labelCodigoSQL.setText(sql);
				count++;
			}
			else {
				Instant instantInicial = Instant.from(dpDataInicialEmprestimo.getValue().atStartOfDay(ZoneId.systemDefault()));
				Date dateInicial = Date.from(instantInicial);
				
				Instant instantFinal = Instant.from(dpDataFinalEmprestimo.getValue().atStartOfDay(ZoneId.systemDefault()));
				Date dateFinal = Date.from(instantFinal);

				String dataInicialEmprestimo = new SimpleDateFormat("yyyy-MM-dd").format(dateInicial);
				String dataFinalEmprestimo = new SimpleDateFormat("yyyy-MM-dd").format(dateFinal);
				
				String sqlDataEmprestimo = "emp.DataEmprestimo BETWEEN '" + dataInicialEmprestimo + "' and '" + dataFinalEmprestimo + "' ";
				sql += sqlDataEmprestimo;
				labelCodigoSQL.setText(sql);
				count++;
			}
		}
		
		if (dpDataInicialDevolucao.getValue() != null && dpDataFinalDevolucao.getValue() != null) {
			if (count > 0) {
				Instant instantInicial = Instant.from(dpDataInicialDevolucao.getValue().atStartOfDay(ZoneId.systemDefault()));
				Date dateInicial = Date.from(instantInicial);
				
				Instant instantFinal = Instant.from(dpDataFinalDevolucao.getValue().atStartOfDay(ZoneId.systemDefault()));
				Date dateFinal = Date.from(instantFinal);
				
				String dataInicialDevolucao = new SimpleDateFormat("yyyy-MM-dd").format(dateInicial);
				String dataFinalDevolucao = new SimpleDateFormat("yyyy-MM-dd").format(dateFinal);
				
				String sqlDataDevolucao = "OR emp.DataDevolucao BETWEEN '" + dataInicialDevolucao + "' and '" + dataFinalDevolucao + "' ";
				sql += sqlDataDevolucao;
				labelCodigoSQL.setText(sql);
				count++;
			}
			else {
				Instant instantInicial = Instant.from(dpDataInicialDevolucao.getValue().atStartOfDay(ZoneId.systemDefault()));
				Date dateInicial = Date.from(instantInicial);
				
				Instant instantFinal = Instant.from(dpDataFinalDevolucao.getValue().atStartOfDay(ZoneId.systemDefault()));
				Date dateFinal = Date.from(instantFinal);
				
				String dataInicialDevolucao = new SimpleDateFormat("yyyy-MM-dd").format(dateInicial);
				String dataFinalDevolucao = new SimpleDateFormat("yyyy-MM-dd").format(dateFinal);
				
				String sqlDataDevolucao = "emp.DataDevolucao BETWEEN '" + dataInicialDevolucao + "' and '" + dataFinalDevolucao + "' ";
				sql += sqlDataDevolucao;
				labelCodigoSQL.setText(sql);
				count++;
			}
		}
		
		if (comboBoxStatus.getValue() != null) {
			if (count > 0) {
				String sqlStatusEmprestimo = "OR emp.Status LIKE '" + comboBoxStatus.getValue() + "%' ";
				sql += sqlStatusEmprestimo;
				labelCodigoSQL.setText(sql);
				count++;
			}
			else {
				String sqlStatusEmprestimo = "emp.Status LIKE '" + comboBoxStatus.getValue() + "%' ";
				sql += sqlStatusEmprestimo;
				labelCodigoSQL.setText(sql);
				count++;
			}
		}
	}
	
	public void setServices(EmprestimoService service) {
		this.service = service;
	}
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Emprestimo> list = service.filtragemCompleta(sql);
		obsList = FXCollections.observableArrayList(list);
		tableViewEmprestimo.setItems(obsList);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtIdCliente);
		
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
	
	public void associatedStatusEmprestimo() {		
		List<String> list = Arrays.asList("Pendente", "Devolvido", "Nao_Devolvido");
		obsListStatus = FXCollections.observableArrayList(list);
		comboBoxStatus.setItems(obsListStatus);
	}
}