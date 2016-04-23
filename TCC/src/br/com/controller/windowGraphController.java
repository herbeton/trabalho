package br.com.controller;

import br.com.model.ConexaoDB;
import br.com.model.DadosPSV;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public ArrayList<XYChart.Series> listaDeSeriesDasPSVs = new ArrayList<XYChart.Series>();
    public List listaDeIdPSVs = new ArrayList<Integer>();
    private int idEstado = 0;


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
//            if (txtPressaoSetPSV.getText().matches("\\d*") && txtPressaoMaxima.getText().matches("\\d*") &&
//                    txtPressaoMinima.getText().matches("\\d*")) {
                if((Double.valueOf(txtPressaoMaxima.getText().toString()) > Double.valueOf(txtPressaoSetPSV.getText().toString())) &&
                        (Double.valueOf(txtPressaoSetPSV.getText().toString())> Double.valueOf(txtPressaoMinima.getText().toString()))){

                    //graphWindow.getData().clear();
                    plotParametrosPSVUsuario();

                }
                else{
                    JOptionPane.showConfirmDialog(null, "Os valores das pressões da PSV não está no padrão!", "Alerta!", JOptionPane.OK_CANCEL_OPTION);
                }

//            } else {
//                JOptionPane.showConfirmDialog(null, "Os dados não condizem com os parametros da PSV!", "Alerta!", JOptionPane.OK_CANCEL_OPTION);
//            }
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
        verificarInserirDadosUsuarioDB();
        //remove as series anteriores
        for(int i=0 ; i<listaDeSeriesDasPSVs.size() ; i++){
            graphWindow.getData().remove(listaDeSeriesDasPSVs.get(i));
        }

        pegandoOsDados();

        //depois remover os dados do usuário para deixar apenas linhas pretas, sem ser series


        for(int i=0 ; i<listaDeSeriesDasPSVs.size() ; i++){
            graphWindow.getData().add(listaDeSeriesDasPSVs.get(i));
        }
        graphWindow.getData().addAll(seriesSetPressaoPSV, seriesMaxPressaoPSV, seriesMinPressaoPSV, serieInformacao);
        graphWindow.getLegendSide();
    }

    private void verificarInserirDadosUsuarioDB(){
        Connection conexao = null;

        String sqlVerifica = "select * from HistoricoPSV where pressaoDeAjuste = '" +
                txtPressaoSetPSV.getText().toString() + "' and pressaoMaxima = '"+ txtPressaoMaxima.getText().toString()
                +"' and pressaoMinima = '" + txtPressaoMinima.getText().toString() + "'";
        ResultSet resultVerifica = null;
        try {
            conexao = ConexaoDB.conectar();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            resultVerifica = conexao.createStatement().executeQuery(sqlVerifica);
            if(!resultVerifica.next()){
                String sqlInsereNoDBHistoricoPSV = "INSERT INTO HistoricoPSV (pressaoDeAjuste, pressaoMaxima, " +
                        "pressaoMinima) VALUES ( '"+txtPressaoSetPSV.getText().toString() + "', '" +
                        txtPressaoMaxima.getText().toString() + "', '" + txtPressaoMinima.getText().toString() + "')";
                conexao.createStatement().executeUpdate(sqlInsereNoDBHistoricoPSV);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void pegandoOsDados(){
        Connection conexao;
        pegaMaiorTempo = 0d;
        pegaMaiorPressao = 0d;
        DadosPSV dadosPSV = new DadosPSV();
        seriesSetPressaoPSV.getData().clear();
        seriesMaxPressaoPSV.getData().clear();
        seriesMinPressaoPSV.getData().clear();
        listaDeSeriesDasPSVs.clear();
        listaDeIdPSVs.clear();
        try {
            conexao = ConexaoDB.conectar();

            setListaDeIdPSVs(conexao);

            int quantPSVs = listaDeIdPSVs.size();
            //adicionando dados a serie
            setListaDeSeriesDasPSVs(conexao, quantPSVs, dadosPSV);
            testeInformacaoPSV(dadosPSV);
            plotParameterSetMaxMinPSV();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setListaDeSeriesDasPSVs(Connection conexao, int quantPSVs, DadosPSV dadosPSV){
        Integer iDPSV;
        for(int i = 0; i < quantPSVs ; i++) {
            iDPSV = (Integer) listaDeIdPSVs.get(i);
            String sql = "select * from DadosPSV WHERE idPSV = '" + listaDeIdPSVs.get(i) + "'";
            String sqlNomePSV = "select nomePSV from PSVs where idPSV = '" + listaDeIdPSVs.get(i) + "'";
            String nomePSVAtual = "";
            ResultSet resultadoNomePSV = null;
            ResultSet resultSet = null;
            Double pressaoAnteriroPSV = 0d;
            Double pressaoAtualPSV;
            String estadoPSV = "";
            try {
                resultSet = conexao.createStatement().executeQuery(sql);
                resultadoNomePSV = conexao.createStatement().executeQuery(sqlNomePSV);
                resultadoNomePSV.next();
                nomePSVAtual = resultadoNomePSV.getString(1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if(resultSet.next()) {//caso exista dados, pode passar
                    while (resultSet.next()) {//faz para cada PSV
                        pressaoAtualPSV = resultSet.getDouble(2);
                        series.getData().add(new XYChart.Data(resultSet.getDouble(3), resultSet.getDouble(2)));//tempoXpressao
                        if (resultSet.getDouble(3) > pegaMaiorTempo) {
                            pegaMaiorTempo = resultSet.getDouble(3);
                        }
                        if (resultSet.getDouble(3) > pegaMaiorPressao) {
                            pegaMaiorPressao = resultSet.getDouble(2);
                            dadosPSV.setPressaoPSV(pegaMaiorPressao);
                            dadosPSV.setTempoPSV(resultSet.getDouble(3));
                        }
                        dadosPSV.setPressaoPSV(resultSet.getDouble(2));
                        dadosPSV.setTempoPSV(resultSet.getDouble(3));
                        estadoPSV = analisaEstadoPSV(pressaoAtualPSV, pressaoAnteriroPSV);
                        inserirEstadoPSVNoDB(estadoPSV, conexao);
                        inserirHistoricoPSVNoDB(estadoPSV,conexao, iDPSV, dadosPSV);
                        isenrirDadosPSVNoDB(conexao, dadosPSV, iDPSV);
                    }
                    series.setName(nomePSVAtual);
                    listaDeSeriesDasPSVs.add(series);
                    series = new XYChart.Series();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void isenrirDadosPSVNoDB(Connection conexao, DadosPSV dadosPSV, Integer iDPSV) {
        int idHistorico = 0;
        String sqlPegaIdHistorico = "select idHistoricoPSV from HistoricoPSV where pressaoDeAjuste = '"+txtPressaoSetPSV.getText().toString()
                +"' and pressaoMaxima = '"+txtPressaoMaxima.getText().toString()+"' and pressaoMinima = '"+
                txtPressaoMinima.getText().toString()+"' and idEstadoPSV ='" + idEstado + "'";


        ResultSet resultSelecionaEstadoPSV = null;
        ResultSet resultVerificaDadosPSV = null;
        try {
            resultSelecionaEstadoPSV = conexao.createStatement().executeQuery(sqlPegaIdHistorico);
            resultSelecionaEstadoPSV.next();
            idHistorico = resultSelecionaEstadoPSV.getInt(1);
            String sqlVerificaDadosPSV = "select * from DadosPSV where IdPSV = '" + iDPSV + "' and pressaoPSV = '" +
                    dadosPSV.getPressaoPSV() + "' and tempoPSV = '" + dadosPSV.getTempoPSV() + "' and idHistoricoPSV = '" +
                    idHistorico + "'";
            String sqlInsereDadosPSV = "insert into DadosPSV (pressaoPSV, tempoPSV, idPSV, idHistoricoPSV) VALUES ( '" +
                    dadosPSV.getPressaoPSV() + "','" + dadosPSV.getTempoPSV() + "','" + iDPSV + "','" + idHistorico + "')";
            resultVerificaDadosPSV = conexao.createStatement().executeQuery(sqlVerificaDadosPSV);
            if(!resultVerificaDadosPSV.next()){
                conexao.createStatement().executeUpdate(sqlInsereDadosPSV);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void inserirHistoricoPSVNoDB(String estadoPSV, Connection conexao, Integer indiceIDPSV, DadosPSV dadosPSV) {
        String sqlSelectEstadoIdPSV = "select IdEstadoPSV from EstadoHistoricoPSV where EstadoPSV = '" + estadoPSV + "'";


        try {
            ResultSet resultSelecionaEstadoPSV = conexao.createStatement().executeQuery(sqlSelectEstadoIdPSV);
            resultSelecionaEstadoPSV.next();
            idEstado = resultSelecionaEstadoPSV.getInt(1);
            String sqlVerificaHistoricoPSV = "select * from HistoricoPSV where pressaoDeAjuste = '" +
                    txtPressaoSetPSV.getText().toString() + "' and pressaoMaxima = '" + txtPressaoMaxima.getText().toString()
                    + "' and pressaoMinima = '" + txtPressaoMinima.getText().toString() + "' and idEstadoPSV = '" +  idEstado + "'" ;
            String sqlInsereHistoricoPSV = "insert into HistoricoPSV (pressaoDeAjuste, pressaoMaxima, pressaoMinima, idEstadoPSV) values ('"
                    + txtPressaoSetPSV.getText().toString() + "' , '" + txtPressaoMaxima.getText().toString() + "','" +
                    txtPressaoMinima.getText().toString() + "','" + idEstado + "')";
            ResultSet resultVerificaHistoricoPSV = conexao.createStatement().executeQuery(sqlVerificaHistoricoPSV);
            if(!resultVerificaHistoricoPSV.next()){
                conexao.createStatement().executeUpdate(sqlInsereHistoricoPSV);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void inserirEstadoPSVNoDB(String estadoPSV, Connection conexao) {
        String sqlVerificaEstadoPSV = "select * from EstadoHistoricoPSV where EstadoPSV = '" + estadoPSV + "'";
        String sqlInsertEstadoPSV = "insert into EstadoHistoricoPSV (EstadoPSV) values ('" + estadoPSV + "')";
        try {
            ResultSet resultVerificaEstadoPSV = conexao.createStatement().executeQuery(sqlVerificaEstadoPSV);
            if(!resultVerificaEstadoPSV.next()){
                conexao.createStatement().executeUpdate(sqlInsertEstadoPSV);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setListaDeIdPSVs(Connection conexao){
        String sql = "select idPSV from PSVs";
        try {
            ResultSet resultSet = conexao.createStatement().executeQuery(sql);
            while (resultSet.next()){
                listaDeIdPSVs.add(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String analisaEstadoPSV(Double pressaoAtualPSV, Double pressaoAnteriroPSV){
        String estadoPSV = "";
        if((pressaoAtualPSV/(pressaoAnteriroPSV + 0.01d)) >= 0.1){
            estadoPSV = "A PSV abriu!";
        }
        else if(pressaoAtualPSV == Double.parseDouble(txtPressaoMaxima.getText().toString())){
            estadoPSV = "A PSV está com sua pressão máxima!";
        }
        else if(pressaoAtualPSV > Double.parseDouble(txtPressaoMaxima.getText().toString())){
            estadoPSV = "A PSV está acima de sua pressão máxima!";
        }
        else if(pressaoAtualPSV == Double.parseDouble(txtPressaoMinima.getText().toString())){
            estadoPSV = "A PSV está com sua pressão mínima! ";
        }
        else if(pressaoAtualPSV < Double.parseDouble(txtPressaoMinima.getText().toString())){
            estadoPSV = "A PSV está abaixo de sua pressão mínima!";
        }
        return estadoPSV;
    }

    public void testeInformacaoPSV(DadosPSV dadosPSV){
        if(pegaMaiorPressao >= Double.parseDouble(txtPressaoMaxima.getText().toString())){
            serieInformacao.getData().add(new XYChart.Data(dadosPSV.getTempoPSV(), dadosPSV.getPressaoPSV()));
            serieInformacao.setName("A PSV passou do limite!");
        }
        else if(pegaMaiorPressao >= Double.parseDouble(txtPressaoSetPSV.getText().toString())){
            serieInformacao.getData().add(new XYChart.Data(dadosPSV.getTempoPSV(), dadosPSV.getPressaoPSV()));
            serieInformacao.setName("A PSV abriu!");
        }
        else if(pegaMaiorPressao >= Double.parseDouble(txtPressaoMinima.getText().toString())){
            serieInformacao.getData().add(new XYChart.Data(dadosPSV.getTempoPSV(), dadosPSV.getPressaoPSV()));
            serieInformacao.setName("A PSV não abriu!");
        }
    }

    //tentar substituir depois
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

    //remover depois
    public void openSample(ActionEvent event){
        Stage stage = new Stage();
        Scene scene = new Scene(new VBox());
        stage.setTitle("sample");
        stage.setResizable(false);//para não deixar mecher no tamanho da janela
        stage.setScene(scene);
        stage.show();
    }

    public void abrirCadastroPSV(ActionEvent event) throws IOException {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("br/com/view/windowRegisterPSVs.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Cadastro das PSVs");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cadastroDeDadosDaPSV(ActionEvent event) throws IOException {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("br/com/view/windowRegisterDataPSVs.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Cadastro de dados da PSVs");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
