package br.com.controller;

import br.com.model.ConexaoDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.sql.Connection;
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

    @FXML LineChart<Number, Number> graphWindow;

    ObservableList<XYChart.Series<Double, Double>> lineChartData = FXCollections.observableArrayList();
    @FXML
    private Axis xAxis= new NumberAxis();
    @FXML
    final Axis yAxis = new NumberAxis();
    //definindo a série
    XYChart.Series series = new XYChart.Series();



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
        graphWindow.getData().add(series);
        //graphWindow.setData(lineChartData);
        graphWindow.createSymbolsProperty();

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
}
