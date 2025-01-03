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
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.services.ClienteService;
import model.services.EmprestimoService;
import model.services.LivroService;

public class ServicoViewController implements Initializable {
	
	@FXML
	private Button btNovoEmprestimo;
	
	@FXML
	private Button btNovaDevolucao;
	
	@FXML
	private Button btNovoCliente;
	
	@FXML 
	private Button btNovoLivro;
	
	@FXML
	public void onBtNovoEmprestimoAction() {
		loadViewServico("Controle - Empréstimo", "/gui/EmprestimoList.fxml", (EmprestimoListController controller) -> {
			controller.setEmprestimoService(new EmprestimoService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onBtNovaDevolucaoAction() {
		System.out.println("onBtNovaDevolucaoAction");
	}
	
	@FXML
	public void onBtNovoClienteAction() {
		loadView("/gui/ClienteList.fxml", (ClienteListController controller) -> {
			controller.setClienteService(new ClienteService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onBtNovoLivroAction() {
		loadViewServico("Registro - Livro", "/gui/LivroList.fxml", (LivroListController controller) -> {
			controller.setLivroService(new LivroService());
			controller.updateTableView();
		});
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
	
	private synchronized <T> void loadViewServico(String title, String absoluteName, Consumer<T> initializing) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox empVBox = loader.load();
			
			Scene empScene = new Scene(empVBox);
			Stage empStage = new Stage();
			empStage.setScene(empScene);
			empStage.setTitle(title);
			empStage.show();
			
			T controller = loader.getController();
			initializing.accept(controller);
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showALert("IO Exception", "Erro ao carregar", e.getMessage(), AlertType.ERROR);
		}
	}
	
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializing) {
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
			Alerts.showALert("IO Exception", "Erro ao carregar", e.getMessage(), AlertType.ERROR);
		}
	}
}