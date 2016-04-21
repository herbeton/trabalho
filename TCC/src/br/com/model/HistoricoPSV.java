package br.com.model;

/**
 * Created by herbeton on 20/04/16.
 */
public class HistoricoPSV {
    private int idHistoricoPSV;
    private double pressaoDeAjuste;
    private double pressaoMaxima;
    private double pressaoMinima;
    private String estadoPSV;

    public int getIdHistoricoPSV() {
        return idHistoricoPSV;
    }

    public void setIdHistoricoPSV(int idHistoricoPSV) {
        this.idHistoricoPSV = idHistoricoPSV;
    }

    public double getPressaoDeAjuste() {
        return pressaoDeAjuste;
    }

    public void setPressaoDeAjuste(double pressaoDeAjuste) {
        this.pressaoDeAjuste = pressaoDeAjuste;
    }

    public double getPressaoMaxima() {
        return pressaoMaxima;
    }

    public void setPressaoMaxima(double pressaoMaxima) {
        this.pressaoMaxima = pressaoMaxima;
    }

    public double getPressaoMinima() {
        return pressaoMinima;
    }

    public void setPressaoMinima(double pressaoMinima) {
        this.pressaoMinima = pressaoMinima;
    }

    public String getEstadoPSV() {
        return estadoPSV;
    }

    public void setEstadoPSV(String estadoPSV) {
        this.estadoPSV = estadoPSV;
    }
}
