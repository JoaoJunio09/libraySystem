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
import model.entities.Livro;
import model.services.CategoriaService;
import model.services.LivroService;

public class LivroListController implements Initializable, DataChangeListener {
	
	private LivroService service;

	@FXML
	private TableView<Livro> tableViewLivro;
	
	@FXML
	private TableColumn<Integer, Livro> tableColumnId;
	
	@FXML
	private TableColumn<String , Livro> tableColumnNome;
	
	@FXML
	private TableColumn<String, Livro> tableColumnDescricao;
	
	@FXML
	private TableColumn<String, Livro> tableColumnAutor;
	
	@FXML
	private TableColumn<Integer, Livro> tableColumnEstoque;
	
	@FXML
	private TableColumn<String, Livro> tableColumnDisponbilidade;
	
	@FXML
	private TableColumn<String, Livro> tableColumnCategoria;
	
	@FXML
	private TableColumn<Livro, Livro> tableColumnEDIT;
	
	@FXML
	private TableColumn<Livro, Livro> tableColumnREMOVE;
	
	@FXML
	private Button btNovo;
	
	@FXML
	private Button btDetalhes;
	
	private ObservableList<Livro> obsList;
	
	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage parent = Utils.currentStage(event);
		Livro obj = new Livro();
		createDialogForm(obj, "/gui/LivroForm.fxml", parent);
	}
	
	@FXML
	public void onBtDetalhesAction() {
		System.out.println("onBtDetalhesAction");
	}
	
	public void setLivroService(LivroService service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
		tableColumnAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
		tableColumnEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));
		tableColumnDisponbilidade.setCellValueFactory(new PropertyValueFactory<>("disponibilidade"));
		tableColumnCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewLivro.prefHeightProperty().bind(stage.heightProperty());
	}
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		service.updateDataAll();
		
		List<Livro> list = service.findAll();		
		obsList = FXCollections.observableArrayList(list);
		tableViewLivro.setItems(obsList);
		
		initEditButtons();
		initRemoveButtons();
	}
	
	private void createDialogForm(Livro obj, String absoluteName, Stage parent) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			LivroFormController controller = loader.getController();
			controller.setLivro(obj);
			controller.setServices(new LivroService(), new CategoriaService());
			controller.subscribeDataChangeListener(this);
			controller.loadAssociatedCategoria();
			controller.updateFormData();
			
			Stage stageDialog = new Stage();
			stageDialog.setTitle("Cadastrar Livro");
			stageDialog.setScene(new Scene(pane));
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
	
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Livro, Livro>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Livro obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/LivroForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Livro, Livro>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Livro obj, boolean empty) {
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
	
	private void removeEntity(Livro obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmar", "Deseja remover uma Livro?");
		
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.deleteById(obj);
				updateTableView();
			}
			catch (DbException e) {
				Alerts.showALert("Error database", "Erro ao remover", e.getMessage(), AlertType.ERROR);
			}
		}
	}

	@Override
	public void dataChanged() {
		updateTableView();
	}
}