package br.com.controller;

import br.com.model.ConexaoDB;
import br.com.model.DadosPSVGrafico;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.text.html.ImageView;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by herbeton on 08/04/16.
 */
public class windowGraphController {
    public TextField txtPressaoMinima;
    public TextField txtPressaoMaxima;
    public TextField txtPressaoSetPSV;
    public Button btnPlotGraph;
    public Double pegaMaiorTempo;
    public Double pegaMaiorPressao;

    @FXML LineChart<Number, Number> graphWindow;

    @FXML
    private Axis xAxis= new NumberAxis();
    @FXML
    final Axis yAxis = new NumberAxis();
    //definindo a série
    XYChart.Series series = new XYChart.Series();
    XYChart.Series seriesSetPressaoPSV = new XYChart.Series();
    XYChart.Series seriesMaxPressaoPSV = new XYChart.Series();
    XYChart.Series seriesMinPressaoPSV = new XYChart.Series();
    XYChart.Series serieInformacao = new XYChart.Series();



    public void checkDatasPSVPlot(ActionEvent event) {
        System.out.println("Valor: " + txtPressaoSetPSV.getText().toString());
        if(!txtPressaoSetPSV.getText().isEmpty() && !txtPressaoMaxima.getText().isEmpty()
                && !txtPressaoMinima.getText().isEmpty()) {
            if (txtPressaoSetPSV.getText().matches("\\d*") && txtPressaoMaxima.getText().matches("\\d*") &&
                    txtPressaoMinima.getText().matches("\\d*")) {

                plotParametrosPSVUsuario();

            } else {
                JOptionPane.showConfirmDialog(null, "Os dados não condizem com os parametros da PSV!", "Alerta!", JOptionPane.OK_CANCEL_OPTION);
            }
        }
        else{
            JOptionPane.showConfirmDialog(null, "Todos os campos tem que ser preenchidos!", "Alerta!", JOptionPane.OK_CANCEL_OPTION);
        }
        System.out.println("Set: " + txtPressaoSetPSV.getText().matches("\\d*"));
        System.out.println("Maxima: " + txtPressaoMaxima.getText().matches("\\d*"));
        System.out.println("Minima: " + txtPressaoMinima.getText().matches("\\d*"));
        System.out.println("");
    }

    private void plotParametrosPSVUsuario(){
        //creating the chart
        series.setName("Pontos da PSV sem interpolação!");
        pegandoOsDados();
        //graphWindow.setLegendVisible(false);
        graphWindow.getData().addAll(series, seriesSetPressaoPSV, seriesMaxPressaoPSV, seriesMinPressaoPSV,serieInformacao);


        //graphWindow.setData(lineChartData);
        graphWindow.getLegendSide();

    }

    public void pegandoOsDados(){
        Connection conexao;
        pegaMaiorTempo = 0d;
        pegaMaiorPressao = 0d;
        DadosPSVGrafico dadosPSVGrafico = new DadosPSVGrafico();
        series.getData().clear();
        seriesSetPressaoPSV.getData().clear();
        seriesMaxPressaoPSV.getData().clear();
        seriesMinPressaoPSV.getData().clear();
        try {
            conexao = ConexaoDB.conectar();
            String sql = "select * from DadosPSVGrafico";
            ResultSet resultSet = conexao.createStatement().executeQuery(sql);
            //adicionando dados a serie
            while (resultSet.next()){
                series.getData().add(new XYChart.Data(resultSet.getDouble(3), resultSet.getDouble(2)));//tempoXpressao
                if(resultSet.getDouble(3) > pegaMaiorTempo){
                    pegaMaiorTempo = resultSet.getDouble(3);
                }
                if(resultSet.getDouble(3) > pegaMaiorPressao){
                    pegaMaiorPressao = resultSet.getDouble(2);
                    dadosPSVGrafico.setPressao(pegaMaiorPressao);
                    dadosPSVGrafico.setTempo(resultSet.getDouble(3));
                }
            }
            testeInformacaoPSV(dadosPSVGrafico);
            plotParameterSetMaxMinPSV();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void testeInformacaoPSV(DadosPSVGrafico dadosPSVGrafico){
        if(pegaMaiorPressao >= Double.parseDouble(txtPressaoMaxima.getText().toString())){
            serieInformacao.getData().add(new XYChart.Data(dadosPSVGrafico.getTempo(), dadosPSVGrafico.getPressao()));
            serieInformacao.setName("A PSV passou do limite!");
        }
        else if(pegaMaiorPressao >= Double.parseDouble(txtPressaoSetPSV.getText().toString())){
            serieInformacao.getData().add(new XYChart.Data(dadosPSVGrafico.getTempo(), dadosPSVGrafico.getPressao()));
            serieInformacao.setName("A PSV abriu!");
        }
        else if(pegaMaiorPressao >= Double.parseDouble(txtPressaoMinima.getText().toString())){
            serieInformacao.getData().add(new XYChart.Data(dadosPSVGrafico.getTempo(), dadosPSVGrafico.getPressao()));
            serieInformacao.setName("A PSV não abriu!");
        }
    }

    public void plotParameterSetMaxMinPSV(){
        pegaMaiorTempo = pegaMaiorTempo + 5;
        //set PSV
        seriesSetPressaoPSV.getData().add(new XYChart.Data(0, Double.valueOf(txtPressaoSetPSV.getText().toString())));//tempoXpressao
        seriesSetPressaoPSV.getData().add(new XYChart.Data(pegaMaiorTempo, Double.valueOf(txtPressaoSetPSV.getText().toString())));
        seriesSetPressaoPSV.setName("Ajuste PSV");

        //Max PSV
        seriesMaxPressaoPSV.getData().add(new XYChart.Data(0, Double.valueOf(txtPressaoMaxima.getText().toString())));
        seriesMaxPressaoPSV.getData().add(new XYChart.Data(pegaMaiorTempo, Double.valueOf(txtPressaoMaxima.getText().toString())));
        seriesMaxPressaoPSV.setName("Max PSV");

        //set PSV
        seriesMinPressaoPSV.getData().add(new XYChart.Data(0, Double.valueOf(txtPressaoMinima.getText().toString())));
        seriesMinPressaoPSV.getData().add(new XYChart.Data(pegaMaiorTempo, Double.valueOf(txtPressaoMinima.getText().toString())));
        seriesMinPressaoPSV.setName("Min PSV");
    }

    public void openSample(ActionEvent event){
        Stage stage = new Stage();
        Scene scene = new Scene(new VBox());
        stage.setTitle("sample");
        stage.setResizable(false);//para não deixar mecher no tamanho da janela
        stage.setScene(scene);
        stage.show();
    }
}
