package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.services.EmprestimoService;

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
		loadViewServico("/gui/EmprestimoList.fxml", (EmprestimoListController controller) -> {
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
		System.out.println("onBtNovoClienteAction");
	}
	
	@FXML
	public void onBtNovoLivroAction() {
		System.out.println("onBtNovoLivroAction");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
	
	private synchronized <T> void loadViewServico(String absoluteName, Consumer<T> initializing) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox empVBox = loader.load();
			
			Scene empScene = new Scene(empVBox);
			Stage empStage = new Stage();
			empStage.setScene(empScene);
			empStage.setTitle("Controle - Emprestimo");
			empStage.setMaximized(true);
			empStage.show();
			
			T controller = loader.getController();
			initializing.accept(controller);
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showALert("IO Exception", "Erro ao carregar", e.getMessage(), AlertType.ERROR);
		}
	}
}
