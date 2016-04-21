package br.com.controller;

import br.com.model.ConexaoDB;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by herbeton on 19/04/16.
 */
public class windowRegisterPSVsController {

    public TextField txtNomeDaPSV;
    public TextArea txtDescricaoPSV;
    public Button btnCadastrarPSV;

    public void verificarCadastrarPSV(){
        if(!txtNomeDaPSV.getText().isEmpty()) {

            inserePSVNoDB();

        }
        else{
            JOptionPane.showConfirmDialog(null, "O campos nome tem que ser preenchidos!", "Alerta!", JOptionPane.OK_CANCEL_OPTION);
        }
    }

    public void inserePSVNoDB(){
        Connection conexao;
        try {
            conexao = ConexaoDB.conectar();
            String sql = "INSERT INTO PSVs (nomePSV, descricaoPSV) VALUES ('" + txtNomeDaPSV.getText().toString()
                    + "' , '" + txtDescricaoPSV.getText().toString() + "')";
            conexao.createStatement().executeUpdate(sql);
            txtNomeDaPSV.clear();
            txtDescricaoPSV.clear();
        }
        catch (Exception e){

        }
    }
}
