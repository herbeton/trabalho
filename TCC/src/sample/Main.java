package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;

public class Main extends Application {

    private ObservableList dados;
    final Axis eixoX= new NumberAxis();
    final Axis eixoY = new NumberAxis();
    //definindo a série
    XYChart.Series series = new XYChart.Series();

    @Override public void start(Stage stage) {
        //título da tela
        stage.setTitle("Gráfico da PSV");
        //definindo os eixos

        eixoX.setLabel("Tempo");
        eixoY.setLabel("Pressão");
        //creating the chart
        final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(eixoX,eixoY);
        lineChart.setTitle("Pressão X Tempo da PSV");
        series.setName("Pontos da PSV sem interpolação!");

        pegandoOsDados();

        Scene scene  = new Scene(lineChart,800,600);
        lineChart.getData().add(series);

        stage.setScene(scene);
        stage.show();
    }

    public void pegandoOsDados(){
        Connection conexao;
        try {
            conexao = ConexaoDB.conectar();
            String sql = "select * from DadosPSVGrafico";
            ResultSet resultSet = conexao.createStatement().executeQuery(sql);
            //adicionando dados a serie
            while (resultSet.next()){
                series.getData().add(new XYChart.Data(resultSet.getDouble(3), resultSet.getDouble(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
