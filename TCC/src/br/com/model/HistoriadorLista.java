package br.com.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by herbeton on 23/04/16.
 */
public class HistoriadorLista {
    StringProperty nomePSV;
    StringProperty pressaoPSV;
    StringProperty tempoPSV;
    StringProperty pressaoDeAjuste;
    StringProperty pressaoMaxima;
    StringProperty pressaoMinima;
    StringProperty estadoPSV;

    public HistoriadorLista(String nomePSV, String pressaoPSV, String tempoPSV, String pressaoDeAjuste,
                            String pressaoMaxima, String pressaoMinima, String estadoPSV) {
        this.nomePSV = new SimpleStringProperty(nomePSV);
        this.pressaoPSV = new SimpleStringProperty(pressaoPSV);
        this.tempoPSV = new SimpleStringProperty(tempoPSV);
        this.pressaoDeAjuste = new SimpleStringProperty(pressaoDeAjuste);
        this.pressaoMaxima = new SimpleStringProperty(pressaoMaxima);
        this.pressaoMinima = new SimpleStringProperty(pressaoMinima);
        this.estadoPSV = new SimpleStringProperty(estadoPSV);
    }

    public String getNomePSV() {
        return nomePSV.get();
    }

    public StringProperty nomePSVProperty() {
        return nomePSV;
    }

    public void setNomePSV(String nomePSV) {
        this.nomePSV.set(nomePSV);
    }

    public String getPressaoPSV() {
        return pressaoPSV.get();
    }

    public StringProperty pressaoPSVProperty() {
        return pressaoPSV;
    }

    public void setPressaoPSV(String pressaoPSV) {
        this.pressaoPSV.set(pressaoPSV);
    }

    public String getTempoPSV() {
        return tempoPSV.get();
    }

    public StringProperty tempoPSVProperty() {
        return tempoPSV;
    }

    public void setTempoPSV(String tempoPSV) {
        this.tempoPSV.set(tempoPSV);
    }

    public String getPressaoDeAjuste() {
        return pressaoDeAjuste.get();
    }

    public StringProperty pressaoDeAjusteProperty() {
        return pressaoDeAjuste;
    }

    public void setPressaoDeAjuste(String pressaoDeAjuste) {
        this.pressaoDeAjuste.set(pressaoDeAjuste);
    }

    public String getPressaoMaxima() {
        return pressaoMaxima.get();
    }

    public StringProperty pressaoMaximaProperty() {
        return pressaoMaxima;
    }

    public void setPressaoMaxima(String pressaoMaxima) {
        this.pressaoMaxima.set(pressaoMaxima);
    }

    public String getPressaoMinima() {
        return pressaoMinima.get();
    }

    public StringProperty pressaoMinimaProperty() {
        return pressaoMinima;
    }

    public void setPressaoMinima(String pressaoMinima) {
        this.pressaoMinima.set(pressaoMinima);
    }

    public String getEstadoPSV() {
        return estadoPSV.get();
    }

    public StringProperty estadoPSVProperty() {
        return estadoPSV;
    }

    public void setEstadoPSV(String estadoPSV) {
        this.estadoPSV.set(estadoPSV);
    }
}
