package br.com.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by herbeton on 04/04/16.
 */
public class ConexaoDB {
    private static Connection conexao;
    private static String url = "jdbc:mysql://localhost/tcc";
    private static String usuario = "root";
    private static String senha = "";

    public static Connection conectar() throws SQLException {
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        conexao = DriverManager.getConnection(url, usuario, senha);
        return conexao;
    }

    public static Connection getConexao() throws SQLException {
        if(conexao != null && !conexao.isClosed()){
            return conexao;
        }
        else {//para se a conexão não estiver aberta
            conectar();
            return conexao;
        }
    }
}
