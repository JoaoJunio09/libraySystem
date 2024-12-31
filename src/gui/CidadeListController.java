package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
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
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Cidade;
import model.services.CidadeService;
import model.services.EstadoService;

public class CidadeListController implements Initializable, DataChangeListener {
	
	private CidadeService service;

	@FXML
	private Button btNovo;
	
	@FXML
	private Button btDetalhes;
	
	@FXML
	private TableView<Cidade> tableViewCidade;
	
	@FXML
	private TableColumn<Integer, Cidade> tableColumnId;
	
	@FXML
	private TableColumn<String, Cidade> tableColumnNome;
	
	@FXML
	private TableColumn<String, Cidade> tableColumnCep;
	
	@FXML
	private TableColumn<String, Cidade> tableColumnEstado;
	
	@FXML
	private TableColumn<Cidade, Cidade> tableColumnEDIT;
	
	@FXML
	private TableColumn<Cidade, Cidade> tableColumnREMOVE;
	
	private ObservableList<Cidade> obsList;
	
	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage parent = Utils.currentStage(event);
		Cidade obj = new Cidade();
		createDialogForm(obj, "/gui/CidadeForm.fxml", parent);
	}
	
	@FXML
	public void onBtDetalhesAction() {
		System.out.println("onBtDetalhesAction");
	}
	
	public void setCidadeService(CidadeService service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnCep.setCellValueFactory(new PropertyValueFactory<>("cep"));
		tableColumnEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewCidade.prefHeightProperty().bind(stage.heightProperty());
	}
	
	public void updateTableView() {
		List<Cidade> list = service.findAll();		
		obsList = FXCollections.observableList(list);		
		tableViewCidade.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}
	
	private synchronized void createDialogForm(Cidade obj, String absoluteName, Stage parent) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			CidadeFormController controller = loader.getController();
			controller.setCidade(obj);
			controller.setServices(new CidadeService(), new EstadoService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			controller.loadAssociatedEstado();		
			
			Stage stageForm = new Stage();
			stageForm.setTitle("Registrar novo Cidade");
			stageForm.setScene(new Scene(pane));
			stageForm.setResizable(false);
			stageForm.initOwner(parent);
			stageForm.initModality(Modality.APPLICATION_MODAL);
			stageForm.showAndWait();
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showALert("IO Exception", "Erro ao carregar", e.getMessage(), AlertType.ERROR);
		}		
	}
	
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Cidade, Cidade>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Cidade obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/CidadeForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Cidade, Cidade>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Cidade obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}
	
	private void removeEntity(Cidade obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmar", "Deseja remover o Cidade?");
		
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.deleteById(obj);
				updateTableView();
			}
			catch (DbException e) {
				Alerts.showALert("Erro ao remover", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

	@Override
	public void dataChanged() {
		updateTableView();		
	}
}