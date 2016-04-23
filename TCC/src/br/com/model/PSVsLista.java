package br.com.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by herbeton on 23/04/16.
 */
public class PSVsLista {
    StringProperty nomePSV;
    StringProperty descricaoPSV;

    public PSVsLista(String nomePSV, String descricaoPSV) {
        this.nomePSV = new SimpleStringProperty(nomePSV);
        this.descricaoPSV = new SimpleStringProperty(descricaoPSV);
    }

    public StringProperty getNomePSV() {
        return nomePSV;
    }

    public void setNomePSV(StringProperty nomePSV) {
        this.nomePSV = nomePSV;
    }

    public StringProperty getDescricaoPSV() {
        return descricaoPSV;
    }

    public void setDescricaoPSV(StringProperty descricaoPSV) {
        this.descricaoPSV = descricaoPSV;
    }
}
