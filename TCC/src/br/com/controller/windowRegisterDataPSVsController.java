package br.com.controller;

import br.com.model.ConexaoDB;
import br.com.model.DadosPSVGrafico;
import br.com.model.PSVs;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by herbeton on 19/04/16.
 */
public class windowRegisterDataPSVsController {
    public TextField txtPressaoPSV;
    public TextField txtTempoPSV;
    public Button btnCadastrarDadosPSV;
    public ArrayList<PSVs> listaDePSVs = new ArrayList<PSVs>();
    public MenuButton txtListaPSVs;
    private boolean umaVezMouseMoved = false;
    private int indicePSVSelecionada;
    private Controller controller = new Controller();

    public windowRegisterDataPSVsController(){
        pegaListaDePSVs();
    }

    public void insereDadosPSVNoDB(ActionEvent event){
        Connection conexao;
        try {
            conexao = ConexaoDB.conectar();
            String sql = "INSERT INTO DadosPSV (pressaoPSV, tempoPSV) VALUES ('" + txtPressaoPSV.getText().toString()
                    + "' , '" + txtTempoPSV.getText().toString() + "')";
            conexao.createStatement().executeUpdate(sql);
            txtPressaoPSV.clear();
            txtTempoPSV.clear();
        }
        catch (Exception e){

        }
    }

    public void pegaListaDePSVs(){
        Connection conexao;
        try {
            conexao = ConexaoDB.conectar();
            String sql = "select idPSV,nomePSV from PSVs";
            ResultSet resultSet = conexao.createStatement().executeQuery(sql);
            //adicionando dados a lista
            while (resultSet.next()){
                PSVs psv = new PSVs();
                psv.setIdPSV(resultSet.getInt(1));
                psv.setNomePSV(resultSet.getString(2));
                listaDePSVs.add(psv);
            }
        }
        catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public void insereDadosPSVNaListaDaVies(){
        if(!umaVezMouseMoved) {
            for (int i=0 ; i<listaDePSVs.size() ; i++) {
                txtListaPSVs.getItems().add(new CheckMenuItem(listaDePSVs.get(i).getNomePSV()));
            }
            umaVezMouseMoved = true;
        }
    }

    public void atualizaTxtListaPSVs(){
        //txtListaPSVs.setText(txtListaPSVs.getId());
        //System.out.println("" + txtListaPSVs.isS);
//        System.out.println(""+txtListaPSVs.getItems().size());
//        System.out.println(""+txtListaPSVs.getItems().get(2).getText());
        for(MenuItem item : txtListaPSVs.getItems()) {
            CheckMenuItem checkMenuItem = (CheckMenuItem) item;
            if(checkMenuItem.isSelected()) {
//                System.out.println("Selected item :" + checkMenuItem.getText());
                txtListaPSVs.setText(checkMenuItem.getText());
                checkMenuItem.setSelected(false);
                pegaIndicePSVPeloNome(checkMenuItem.getText().toString());
            }
        }
    }

    private void pegaIndicePSVPeloNome(String nomePSV){
        for (int i=0 ; i<listaDePSVs.size() ; i++) {
            if(nomePSV == listaDePSVs.get(i).getNomePSV()){
                indicePSVSelecionada = listaDePSVs.get(i).getIdPSV();
            }
        }
    }

    public void verificarCadastrarDadosPSV(){
        if(!txtPressaoPSV.getText().isEmpty() && !txtTempoPSV.getText().isEmpty()) {
//            if (txtPressaoPSV.getText().matches("\\d*") && txtTempoPSV.getText().matches("\\d*")) {

                insereDadosPSVNoDB();

//            } else {
//                JOptionPane.showConfirmDialog(null, "Os dados não condizem com os parametros da PSV!", "Alerta!", JOptionPane.OK_CANCEL_OPTION);
//            }
//            if(controller.getWindowGraphController() != null) {
//                controller.getWindowGraphController().checkDatasPSVPlot();
//            }
        }

        else{
            JOptionPane.showConfirmDialog(null, "Todos os campos tem que ser preenchidos!", "Alerta!", JOptionPane.OK_CANCEL_OPTION);
        }
    }

    private void insereDadosPSVNoDB(){
        Connection conexao;
        try {
            conexao = ConexaoDB.conectar();
            String sql = "INSERT INTO DadosPSV (pressaoPSV, tempoPSV, idPSV) VALUES ('" + txtPressaoPSV.getText().toString()
                    + "' , '" + txtTempoPSV.getText().toString() + "' , '"+ indicePSVSelecionada + "')";
            conexao.createStatement().executeUpdate(sql);
            txtListaPSVs.setText("");
            txtPressaoPSV.clear();
            txtTempoPSV.clear();
        }
        catch (Exception e){

        }
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
}
