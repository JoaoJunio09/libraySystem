package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.CategoriaService;
import model.services.CidadeService;
import model.services.ClienteService;
import model.services.EstadoService;
import model.services.LivroService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemServico;
	
	@FXML
	private MenuItem menuItemEmprestimo;
	
	@FXML
	private MenuItem menuItemDevolucao;
	
	@FXML
	private MenuItem menuItemCliente;
	
	@FXML
	private MenuItem menuItemLivro;
	
	@FXML
	private MenuItem menuItemEstado;
	
	@FXML
	private MenuItem menuItemCidade;
	
	@FXML
	private MenuItem menuItemCategoria;
	
	@FXML
	private MenuItem menuItemCadastrarUsuario;
	
	@FXML
	private MenuItem menuItemSobre;
	
	@FXML
	private MenuItem menuItemVersao;
	
	@FXML
	private MenuItem menuItemEspecificacaoDoProjeto;
	
	@FXML
	private MenuItem menuItemContato;
	
	@FXML
	private MenuItem menuItemMaisInformacoes;
	
	@FXML
	private MenuItem menuItemSite;
	
	@FXML
	public void onMenuServicoAction() {
		loadViewServico("/gui/ServicoView.fxml", (ServicoViewController controller) -> {
			//
		});
	}
	
	@FXML
	public void onMenuItemEmprestimoAction() {
		System.out.println("onMenuItemEmprestimoAction");
	}
	
	@FXML
	public void onMenuItemDevolucaoAction() {
		System.out.println("onMenuItemDevolucaoAction");
	}
	
	@FXML
	public void onMenuItemClienteAction() {
		loadView("/gui/ClienteList.fxml", (ClienteListController controller) -> {
			controller.setClienteService(new ClienteService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemLivroAction() {
		loadView("/gui/LivroList.fxml", (LivroListController controller) -> {
			controller.setLivroService(new LivroService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemEstadoAction() {
		loadView("/gui/EstadoLista.fxml", (EstadoListController controller) -> {
			controller.setEstadoService(new EstadoService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemCidadeAction() {
		loadView("/gui/CidadeList.fxml", (CidadeListController controller) -> {
			controller.setCidadeService(new CidadeService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemCategoriaAction() {
		loadView("/gui/CategoriaList.fxml", (CategoriaListController controller) -> {
			controller.setCategoriaService(new CategoriaService());
			controller.updateTableView();
		});
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
	
	private synchronized <T> void loadViewServico(String absoluteName, Consumer<T> initializing) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			ScrollPane scrollPane = loader.load();
			
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			Node mainMenu = mainVBox.getChildren().get(0);			
			
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(scrollPane);
			
			T controller = loader.getController();
			initializing.accept(controller);
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showALert("IO Exception", "Erro ao carregar a view", e.getMessage(), AlertType.ERROR);
		}
	}
	
	protected synchronized <T> void loadView(String absoluteName, Consumer<T> initializing) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			Node mainMenu = mainVBox.getChildren().get(0);
			
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox);
			
			T controller = loader.getController();
			initializing.accept(controller);
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showALert("IO Exception", "Erro ao carregar a view", e.getMessage(), AlertType.ERROR);
		}
	}	
}