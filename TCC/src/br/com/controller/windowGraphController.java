package br.com.controller;

import br.com.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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
    public ArrayList<XYChart.Series> listaDeEstadosDeSeriesDasPSVs = new ArrayList<XYChart.Series>();
    public List listaDeIdPSVs = new ArrayList<Integer>();
    public TableView<PSVsLista> tablePsvs;
    public TableColumn<PSVsLista, String> tablePSVsNome;
    public TableColumn<PSVsLista, String> tablePSVsDescricao;
    public TabPane tablePane;
    public TableColumn<HistoriadorLista, String> columnHistoriadorPSV;
    public TableColumn<HistoriadorLista, String> columnHistoriadorPAtual;
    public TableColumn<HistoriadorLista, String> columnHistoriadorTAtual;
    public TableColumn<HistoriadorLista, String> columnHistoriadorPMaxima;
    public TableColumn<HistoriadorLista, String> columnHistoriadorPAjuste;
    public TableColumn<HistoriadorLista, String> columnHistoriadorPMinima;
    public TableColumn<HistoriadorLista, String> columnHistoriadorEstado;
    public TableView<HistoriadorLista> tableHistorian;
    private int idEstado = 0;
    private ObservableList<PSVsLista> psvsData = FXCollections.observableArrayList();
    private ObservableList<HistoriadorLista> historiadorData = FXCollections.observableArrayList();
    private boolean dadosUsuariosMudaram = false;
    private boolean dadosUsuarioUmavez = true;
    private Double pressaoAjusteUsuario = 0d;
    private Double pressaoMaximaeUsuario = 0d;
    private Double pressaoMinimseUsuario = 0d;
    private Controller controller = new Controller();

    @FXML LineChart<Number, Number> graphWindow;
    
    //definindo a série
    XYChart.Series series = new XYChart.Series();
    XYChart.Series seriesEstado = new XYChart.Series();
    XYChart.Series seriesSetPressaoPSV = new XYChart.Series();
    XYChart.Series seriesMaxPressaoPSV = new XYChart.Series();
    XYChart.Series seriesMinPressaoPSV = new XYChart.Series();
    XYChart.Series serieInformacao = new XYChart.Series();

    @FXML
    private void initialize() {
        //para a tabela de PSVs da view
        tablePSVsNome.setCellValueFactory(cellData -> cellData.getValue().getNomePSV());
        tablePSVsDescricao.setCellValueFactory(cellData -> cellData.getValue().getDescricaoPSV());

        //para a tabela de Historiados de PSVs da view
        columnHistoriadorPSV.setCellValueFactory(cellData -> cellData.getValue().nomePSVProperty());
        columnHistoriadorPAtual.setCellValueFactory(cellData -> cellData.getValue().pressaoPSVProperty());
        columnHistoriadorTAtual.setCellValueFactory(cellData -> cellData.getValue().tempoPSVProperty());
        columnHistoriadorPMaxima.setCellValueFactory(cellData -> cellData.getValue().pressaoMaximaProperty());
        columnHistoriadorPAjuste.setCellValueFactory(cellData -> cellData.getValue().pressaoDeAjusteProperty());
        columnHistoriadorPMinima.setCellValueFactory(cellData -> cellData.getValue().pressaoMinimaProperty());
        columnHistoriadorEstado.setCellValueFactory(cellData -> cellData.getValue().estadoPSVProperty());
    }


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

                    adicionaElementosListaPSVs();

                    adicionaElementosListaHistoriador();

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

    private void adicionaElementosListaHistoriador() {
        selecionaDadosListaHistoriador();
        tableHistorian.setItems(historiadorData);
    }

    private void selecionaDadosListaHistoriador() {
        Connection conexao = null;
        String sql = "select PSVS.nomePSV, DPSV.pressaoPSV, DPSV.tempoPSV, HPSV.pressaoDeAjuste, HPSV.pressaoMaxima," +
               " HPSV.pressaoMinima, EHPSV.estadoPSV from PSVs PSVS, DadosPSV DPSV, HistoricoPSV HPSV , EstadoHistoricoPSV EHPSV " +
                "where PSVS.idPSV = DPSV.idPSV and DPSV.idHistoricoPSV = HPSV.idHistoricoPSV\n" +
                "and HPSV.idEstadoPSV = EHPSV.idEstadoPSV";
        ResultSet resultVerifica = null;
        try {
            conexao = ConexaoDB.conectar();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            resultVerifica = conexao.createStatement().executeQuery(sql);
            while (resultVerifica.next()){
                historiadorData.add(new HistoriadorLista(resultVerifica.getString(1),resultVerifica.getString(2),
                        resultVerifica.getString(3), resultVerifica.getString(4), resultVerifica.getString(5),
                        resultVerifica.getString(6), resultVerifica.getString(7)));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void adicionaElementosListaPSVs() {
        tablePsvs.setItems(psvsData);
    }

    private void plotParametrosPSVUsuario(){
        verificarInserirDadosUsuarioDB();
        graphWindow.setLegendVisible(false);
//        graphWindow.setCreateSymbols(false);

        //remove as series anteriores
        for(int i=0 ; i<listaDeSeriesDasPSVs.size() ; i++){
            graphWindow.getData().remove(listaDeSeriesDasPSVs.get(i));
        }

        //remove as series anteriores referentes aos estados das psvs
        for(int i=0 ; i<listaDeEstadosDeSeriesDasPSVs.size() ; i++){
            graphWindow.getData().remove(listaDeEstadosDeSeriesDasPSVs.get(i));
        }

        pegandoOsDados();

        //depois remover os dados do usuário para deixar apenas linhas pretas, sem ser series

        //adiciona a lista de series ao grafico
        for(int i=0 ; i<listaDeSeriesDasPSVs.size() ; i++){
            graphWindow.getData().add(listaDeSeriesDasPSVs.get(i));
        }

        //adiciona a lista de estados das series ao grafico
        for(int i=0 ; i<listaDeEstadosDeSeriesDasPSVs.size() ; i++){
            graphWindow.getData().add(listaDeEstadosDeSeriesDasPSVs.get(i));
            for(int j=1 ; j<listaDeEstadosDeSeriesDasPSVs.size(); j++){
                if(i==listaDeEstadosDeSeriesDasPSVs.size() || j==listaDeEstadosDeSeriesDasPSVs.size()
                        || (i+j) == listaDeEstadosDeSeriesDasPSVs.size()){
                    break;
                }
                if (listaDeEstadosDeSeriesDasPSVs.get(i).equals(listaDeEstadosDeSeriesDasPSVs.get(i + j))) {
                    i++;
                }
            }
        }
        if(dadosUsuariosMudaram) {
            graphWindow.getData().addAll(seriesSetPressaoPSV, seriesMaxPressaoPSV, seriesMinPressaoPSV);
            dadosUsuariosMudaram = false;
        }
        Tooltip.install(seriesMaxPressaoPSV.getNode(), new Tooltip("Symbol-0"));

        //Muda as cores dos elementos do gráfico
        for (XYChart.Series s : graphWindow.getData()) {

            if(("A PSV1 abriu!").equals(s.getName()))
            {
                s.getNode().setStyle("-fx-stroke: #F00F0F; ");
            }
            else if(("PSV1").equals(s.getName())){
                s.getNode().setStyle("-fx-stroke: #0F00F0; ");
            }
            else if(("Ajuste PSV").equals(s.getName())){
                s.getNode().setStyle("-fx-stroke: #837676; ");
            }
            else if(("Max PSV").equals(s.getName()) || ("Min PSV").equals(s.getName())){
                s.getNode().setStyle("-fx-stroke: #000000; ");
            }
            else{
                s.getNode().setStyle("-fx-stroke: #0F00F0; ");
            }
            for (XYChart.Series<Number, Number> a : graphWindow.getData()) {
                for (XYChart.Data<Number, Number> d : a.getData()) {
                    Tooltip.install(d.getNode(), new Tooltip(
                            "Pressão da PSV: " + d.getYValue().toString() + "\n" +
                                    "Tempo da PSV: " + d.getXValue()));

                    //Adding class on hover
                    d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));

                    //Removing class on exit
                    d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                }
            }        }
//      Tooltip tooltip = new Tooltip();


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
        seriesEstado.getData().clear();
        listaDeSeriesDasPSVs.clear();
        listaDeEstadosDeSeriesDasPSVs.clear();
        listaDeIdPSVs.clear();
        psvsData.clear();
        historiadorData.clear();
        try {
            conexao = ConexaoDB.conectar();

            setListaDeIdPSVs(conexao);

            int quantPSVs = listaDeIdPSVs.size();
            //adicionando dados a serie
            setListaDeSeriesDasPSVs(conexao, quantPSVs, dadosPSV);
            //testeInformacaoPSV(dadosPSV);
            plotParameterSetMaxMinPSV();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void setListaDeSeriesDasPSVs(Connection conexao, int quantPSVs, DadosPSV dadosPSV){
        Integer iDPSV;
        for(int i = 0; i < quantPSVs ; i++) {
            iDPSV = (Integer) listaDeIdPSVs.get(i);
            String sql = "select * from DadosPSV WHERE idPSV = '" + listaDeIdPSVs.get(i) + "' order by idPSV,tempoPSV";
            String sqlNomePSV = "select nomePSV from PSVs where idPSV = '" + listaDeIdPSVs.get(i) + "'";
            String nomePSVAtual = "";
            ResultSet resultadoNomePSV = null;
            ResultSet resultSet = null;
            Double pressaoAnteriroPSV = 0d;
            Double pressaoAtualPSV;
            Double tempoAtualPSV;
            Double tempoAnteriorPSV = 0d;
            Double pressaoFuturaPSV = 0d;
            String estadoPSV = "";
            int contUmaVezAberturaPSV = 0;
            boolean insereUmaVezAPressao = true;
            try {
                resultSet = conexao.createStatement().executeQuery(sql);
                resultadoNomePSV = conexao.createStatement().executeQuery(sqlNomePSV);
                resultadoNomePSV.next();
                nomePSVAtual = resultadoNomePSV.getString(1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                Boolean verificaSeExiste = resultSet.next();
                if(verificaSeExiste) {//caso exista dados, pode passar
                    while (verificaSeExiste) {//faz para cada PSV
                        pressaoAtualPSV = resultSet.getDouble(2);
                        tempoAtualPSV = resultSet.getDouble(3);
                        verificaSeExiste = resultSet.next();
                        if(!verificaSeExiste){
                            break;
                        }
                        pressaoFuturaPSV = resultSet.getDouble(2);
                        if(insereUmaVezAPressao){
                            pressaoAnteriroPSV = pressaoAtualPSV + 0.01d;
                            tempoAnteriorPSV = tempoAtualPSV + 0.01d;
                            insereUmaVezAPressao = false;
                        }
                        series.getData().add(new XYChart.Data(tempoAtualPSV, pressaoAtualPSV));//tempoXpressao
                        //retirar depois que colocar os valores do usuário como linhas pretas no gráfico
                        if (tempoAtualPSV > pegaMaiorTempo) {
                            pegaMaiorTempo = tempoAtualPSV;
                        }
                        if (pressaoAtualPSV > pegaMaiorPressao) {
                            pegaMaiorPressao = pressaoAtualPSV;
                            dadosPSV.setPressaoPSV(pegaMaiorPressao);
                            dadosPSV.setTempoPSV(tempoAtualPSV);
                        }
                        dadosPSV.setPressaoPSV(pressaoAtualPSV);
                        dadosPSV.setTempoPSV(tempoAtualPSV);
                        estadoPSV = analisaEstadoPSV(pressaoAtualPSV, pressaoAnteriroPSV, pressaoFuturaPSV,
                                tempoAtualPSV, tempoAnteriorPSV);
                        if(estadoPSV == "A PSV abriu!"){
                            seriesEstado.getData().add(new XYChart.Data(tempoAtualPSV, pressaoAtualPSV));
                            seriesEstado.getData().add(new XYChart.Data(tempoAnteriorPSV, pressaoAnteriroPSV));
                            contUmaVezAberturaPSV++;
                        }
                        if(contUmaVezAberturaPSV == 1){
                            if(verificaSeNaoExisteSerieNaLista(seriesEstado, listaDeEstadosDeSeriesDasPSVs)){
                                seriesEstado.setName("A " + nomePSVAtual + " abriu!");
                                listaDeEstadosDeSeriesDasPSVs.add(seriesEstado);
                                seriesEstado = new XYChart.Series();
                            }
                            contUmaVezAberturaPSV = 0;
                        }
                        inserirEstadoPSVNoDB(estadoPSV, conexao);
                        inserirHistoricoPSVNoDB(estadoPSV,conexao, iDPSV, pressaoAnteriroPSV);
                        isenrirDadosPSVNoDB(conexao, dadosPSV, iDPSV);
                        pressaoAnteriroPSV = pressaoAtualPSV + 0.01d;
                        tempoAnteriorPSV = tempoAtualPSV + 0.01d;
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

    private boolean verificaSeNaoExisteSerieNaLista(XYChart.Series seriesEstado, ArrayList<XYChart.Series> listaDeEstadosDeSeriesDasPSVs) {
        boolean retorno = true;
        boolean saiDoLaco = false;
        for(int i=0 ; i<listaDeEstadosDeSeriesDasPSVs.size(); i++){
            if(listaDeEstadosDeSeriesDasPSVs.get(i).equals(seriesEstado)){
                saiDoLaco = true;
                retorno = false;
            }
            if(saiDoLaco){
                break;
            }
//            if((listaDeEstadosDeSeriesDasPSVs.size() - 1) == i){
//
//            }
        }
        return retorno;
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

    private void inserirHistoricoPSVNoDB(String estadoPSV, Connection conexao, Integer indiceIDPSV, Double pressaoAnteriroPSV) {
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
        String sql = "select idPSV, nomePSV, descricaoPSV from PSVs";
        try {
            ResultSet resultSet = conexao.createStatement().executeQuery(sql);
            while (resultSet.next()){
                listaDeIdPSVs.add(resultSet.getInt(1));
                psvsData.add(new PSVsLista(resultSet.getString(2), resultSet.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String analisaEstadoPSV(Double pressaoAtualPSV, Double pressaoAnteriroPSV, Double pressaoFuturaPSV,
                                   Double tempoAtualPSV, Double tempoAnteriorPSV){
        String estadoPSV = "";
        Double pressaoMaxima = Double.parseDouble(txtPressaoMaxima.getText().toString());
        Double pressaoMinima = Double.parseDouble(txtPressaoMinima.getText().toString());
        Double pressaoDeAjuste = Double.parseDouble(txtPressaoSetPSV.getText().toString());
//        if(((pressaoAtualPSV/(pressaoAnteriroPSV + 0.01d)) >= 0.1) && ((pressaoAtualPSV/(pressaoAnteriroPSV + 0.01d))
//                <= 0.5) || ((pressaoFuturaPSV/(pressaoAtualPSV + 0.01d)) >= 0.1) &&
//                ((pressaoFuturaPSV/(pressaoAtualPSV + 0.01d)) <= 0.5) && (pressaoAtualPSV >= pressaoDeAjuste)
//                && ((pressaoAtualPSV - pressaoFuturaPSV > 0))){
        boolean c = (pressaoAtualPSV - pressaoAnteriroPSV)/(tempoAtualPSV - tempoAnteriorPSV) < -15;
        if(((pressaoAtualPSV - pressaoAnteriroPSV)/(tempoAtualPSV - tempoAnteriorPSV) < -15)
                && (pressaoAnteriroPSV > pressaoAtualPSV) && (pressaoAnteriroPSV > pressaoDeAjuste)){
            estadoPSV = "A PSV abriu!";
        }

        else if(pressaoAtualPSV == pressaoMaxima){
            estadoPSV = "A PSV está com sua pressão máxima!";
        }
        else if(pressaoAtualPSV > pressaoMaxima){
            estadoPSV = "A PSV está acima de sua pressão máxima!";
        }
        else if(pressaoAtualPSV == pressaoMinima){
            estadoPSV = "A PSV está com sua pressão mínima! ";
        }
        else if(pressaoAtualPSV < pressaoMinima){
            estadoPSV = "A PSV está abaixo de sua pressão mínima!";
        }
        else{
            estadoPSV = "A PSV não abriu!";
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
        if(verificaSeDadosUsuarioMudaram(Double.valueOf(txtPressaoSetPSV.getText().toString()),
                Double.valueOf(txtPressaoMaxima.getText().toString()),
                Double.valueOf(txtPressaoMinima.getText().toString()))) {
            pegaMaiorTempo = pegaMaiorTempo + 5;
            //set PSV
            seriesSetPressaoPSV.getData().clear();
            seriesSetPressaoPSV.getData().add(new XYChart.Data(0, Double.valueOf(txtPressaoSetPSV.getText().toString())));//tempoXpressao
            seriesSetPressaoPSV.getData().add(new XYChart.Data(pegaMaiorTempo, Double.valueOf(txtPressaoSetPSV.getText().toString())));
            seriesSetPressaoPSV.setName("Ajuste PSV");

            //Max PSV
            seriesMaxPressaoPSV.getData().clear();
            seriesMaxPressaoPSV.getData().add(new XYChart.Data(0, Double.valueOf(txtPressaoMaxima.getText().toString())));
            seriesMaxPressaoPSV.getData().add(new XYChart.Data(pegaMaiorTempo, Double.valueOf(txtPressaoMaxima.getText().toString())));
            seriesMaxPressaoPSV.setName("Max PSV");

            //set PSV
            seriesMinPressaoPSV.getData().clear();
            seriesMinPressaoPSV.getData().add(new XYChart.Data(0, Double.valueOf(txtPressaoMinima.getText().toString())));
            seriesMinPressaoPSV.getData().add(new XYChart.Data(pegaMaiorTempo, Double.valueOf(txtPressaoMinima.getText().toString())));
            seriesMinPressaoPSV.setName("Min PSV");
        }
    }

    public boolean verificaSeDadosUsuarioMudaram(Double pressaoSet, Double pressaoMaxima, Double pressaoMinima){
        boolean retorno = false;
        if((pressaoAjusteUsuario != pressaoSet) || (pressaoMaximaeUsuario != pressaoMaxima) ||
                (pressaoMinimseUsuario != pressaoMinima) || dadosUsuarioUmavez){
            retorno = true;
            dadosUsuariosMudaram = true;
        }
        else{
            dadosUsuariosMudaram = false;
        }
        if(dadosUsuarioUmavez){
            pressaoAjusteUsuario = pressaoSet;
            pressaoMaximaeUsuario = pressaoMaxima;
            pressaoMinimseUsuario = pressaoMinima;
            dadosUsuarioUmavez = false;
        }
        return retorno;
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

    public void openWindowAbout(){
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("br/com/view/windowAbout.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Sobre este trabalho!");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
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
