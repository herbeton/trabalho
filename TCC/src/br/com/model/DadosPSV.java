package br.com.model;

/**
 * Created by herbeton on 20/04/16.
 */
public class DadosPSV {
    private int idDados;
    private double pressaoPSV;
    private double tempoPSV;
    private int idPSV;
    private int idHistorico;

    public int getIdDados() {
        return idDados;
    }

    public void setIdDados(int idDados) {
        this.idDados = idDados;
    }

    public double getPressaoPSV() {
        return pressaoPSV;
    }

    public void setPressaoPSV(double pressaoPSV) {
        this.pressaoPSV = pressaoPSV;
    }

    public double getTempoPSV() {
        return tempoPSV;
    }

    public void setTempoPSV(double tempoPSV) {
        this.tempoPSV = tempoPSV;
    }

    public int getIdPSV() {
        return idPSV;
    }

    public void setIdPSV(int idPSV) {
        this.idPSV = idPSV;
    }

    public int getIdHistorico() {
        return idHistorico;
    }

    public void setIdHistorico(int idHistorico) {
        this.idHistorico = idHistorico;
    }
}
