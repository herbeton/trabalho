package br.com.controller;

import br.com.model.ConexaoDB;
import br.com.model.DadosPSVGrafico;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
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
    public List listaDePSVs = new ArrayList<String>();
    public MenuButton txtListaPSVs;

    public windowRegisterDataPSVsController(){
        //insereDadosPSVNaListaDaVies();
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
            String sql = "select nomePSV from PSVs";
            ResultSet resultSet = conexao.createStatement().executeQuery(sql);
            //adicionando dados a lista
            while (resultSet.next()){
                listaDePSVs.add(resultSet.getString(1));
            }
            //insereDadosPSVNaListaDaVies();
        }
        catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public void insereDadosPSVNaListaDaVies(){
        MenuItem menuItem = new MenuItem();
        menuItem.setText("oi");
        txtListaPSVs.getItems().add(menuItem);
    }
}
