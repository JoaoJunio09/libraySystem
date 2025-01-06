package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import model.services.EmprestimoService;

public class GraficosEmprestimosPorMesController implements Initializable {
	
	private EmprestimoService service;
	
	@FXML
	private BarChart<String, Integer> barChartPorMes;
	
	@FXML
	private BarChart<String, Integer> barChartDevolvidosPorMes;
	
	@FXML
	private CategoryAxis categoryAxisPorMes;
	
	@FXML
	private CategoryAxis categoryAxisDevolvidosPorMes;
	
	@FXML
	private NumberAxis numberAxisPorMes;
	
	@FXML
	private NumberAxis numberAxisDevolvidoPorMes;
	
	private ObservableList<String> observableListMeses = FXCollections.observableArrayList();
	
	private ObservableList<String> observableListMesesDevolvidos = FXCollections.observableArrayList();
	
	public void setEmprestimoService(EmprestimoService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
	
	public void updateBarChatPorMes() {
		// Obtém an array com nomes dos meses em Inglês.
        String[] arrayMeses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        // Converte o array em uma lista e adiciona em nossa ObservableList de meses.
        observableListMeses.addAll(Arrays.asList(arrayMeses));

        // Associa os nomes de mês como categorias para o eixo horizontal.
        categoryAxisPorMes.setCategories(observableListMeses);
        
        Map<Integer, ArrayList<Integer>> dados = service.listarQuantidadeEmprestimosPorMes();

        for (Entry<Integer, ArrayList<Integer>> dadosItem : dados.entrySet()) {
            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            series.setName(dadosItem.getKey().toString());

            for (int i = 0; i < dadosItem.getValue().size(); i = i + 2) {
                String mes;
                Integer quantidade;

                mes = retornaNomeMes((int) dadosItem.getValue().get(i));
                quantidade = (Integer) dadosItem.getValue().get(i + 1);

                series.getData().add(new XYChart.Data<>(mes, quantidade));
            }
            barChartPorMes.getData().add(series);
        }
    }
	
	public void updateBarChatDevolvidosPorMes() {
		// Obtém an array com nomes dos meses em Inglês.
        String[] arrayMeses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        // Converte o array em uma lista e adiciona em nossa ObservableList de meses.
        observableListMesesDevolvidos.addAll(Arrays.asList(arrayMeses));

        // Associa os nomes de mês como categorias para o eixo horizontal.
        categoryAxisDevolvidosPorMes.setCategories(observableListMesesDevolvidos);
        
        Map<Integer, ArrayList<Integer>> dados = service.listarQuantidadeEmprestimosDevolvidosPorMes();

        for (Entry<Integer, ArrayList<Integer>> dadosItem : dados.entrySet()) {
            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            series.setName(dadosItem.getKey().toString());

            for (int i = 0; i < dadosItem.getValue().size(); i = i + 2) {
                String mes;
                Integer quantidade;

                mes = retornaNomeMes((int) dadosItem.getValue().get(i));
                quantidade = (Integer) dadosItem.getValue().get(i + 1);

                series.getData().add(new XYChart.Data<>(mes, quantidade));
            }
            barChartDevolvidosPorMes.getData().add(series);
        }
    }

    public String retornaNomeMes(int mes) {
        switch (mes) {
            case 1:
                return "Jan";
            case 2:
                return "Fev";
            case 3:
                return "Mar";
            case 4:
                return "Abr";
            case 5:
                return "Mai";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Ago";
            case 9:
                return "Set";
            case 10:
                return "Out";
            case 11:
                return "Nov";
            case 12:
                return "Dez";
            default:
                return "";
        }
    }
}