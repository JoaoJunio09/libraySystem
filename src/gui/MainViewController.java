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
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.entities.Usuario;
import model.services.CategoriaService;
import model.services.CidadeService;
import model.services.ClienteService;
import model.services.EmprestimoService;
import model.services.EstadoService;
import model.services.LivroService;
import model.services.UsuarioService;

public class MainViewController implements Initializable {
	
	private Usuario usuario;
	
	@FXML
	private Menu menuUsuario;
	
	@FXML
	private Menu menuAdministrativo;

	@FXML
	private MenuItem menuItemServico;
	
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
	private MenuItem menuItemRelatorioEmprestimo;
	
	@FXML
	private MenuItem menuItemGraficos;
	
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
	private MenuItem menuItemCadastrarUsuario;
	
	@FXML
	private MenuItem menuItemSair;
	
	@FXML
	private Label labelLoginUsuario;
	
	@FXML
	private Label labelTipoUsuario;
	
	@FXML
	private Label labelAdmin;
	
	@FXML
	public void onMenuServicoAction() {
		loadView("/gui/ServicoView.fxml", x -> {});
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
	
	@FXML
	public void onMenuItemRelatorioEmprestimoAction() {
		loadView("/gui/EmprestimoRelatorio.fxml", (EmprestimoRelatorioController controller) -> {
			controller.setEmprestimoService(new EmprestimoService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemGraficosAction() {
		loadView("/gui/GraficosEmprestimosPorMes.fxml", (GraficosEmprestimosPorMesController controller) -> {
			controller.setEmprestimoService(new EmprestimoService());
			controller.updateBarChatPorMes();
			controller.updateBarChatDevolvidosPorMes();
		});
	}
	
	@FXML
	public void onMenuItemContatoAction() {
		loadView("/gui/ContatoView.fxml", x -> {});
	}
	
	@FXML
	public void onMenuItemMaisInformacoesAction() {
		loadView("/gui/MaisInformacoesView.fxml", x -> {});
	}
	
	@FXML
	public void onMenuItemSiteAction() {
		loadView("/gui/SiteView.fxml", x -> {});
	}
	
	@FXML
	public void onMenuItemVersao() {
		loadView("/gui/VersaoView.fxml", x -> {});
	}
	
	@FXML
	public void onMenuItemEspecificacaoDoProjetoAction() {
		loadView("/gui/EspecificacaoDoProjeto.fxml", x -> {});
	}
	
	@FXML
	public void onMenuItemSobreAction() {
		loadView("/gui/SobreView.fxml", x -> {});
	}
	
	@FXML
	public void onMenuItemCadastrarUsuarioAction() {
		loadView("/gui/CadastrarUsuarioView.fxml", (CadastrarUsuarioController controller) -> {
			controller.setUsuario(new Usuario());
			controller.setUsuarioService(new UsuarioService());
			controller.associandoTipoDeUsuario();
			controller.atualizaDadosFormulario();
		});
	}
	
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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
	
	public void exibirUsuario() {
		if (usuario == null) {
			throw new IllegalStateException("Usuário é nulo");
		}
		
		labelLoginUsuario.setText(usuario.getLogin());
		labelTipoUsuario.setText(String.valueOf(usuario.getTipo()));
		labelAdmin.setText((usuario.getTipo() == 1) ? "Sim" : "Não");
		
		if (usuario.getTipo() == 2) {
			menuUsuario.setDisable(true);
			menuAdministrativo.setDisable(true);
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
}