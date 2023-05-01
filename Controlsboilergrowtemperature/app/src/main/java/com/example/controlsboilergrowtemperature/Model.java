package com.example.controlsboilergrowtemperature;

import java.util.ArrayList;

public class Model {
    String tempGor;
    String tempXol;
    String tempKomn;
    String PNagr;
    String pomp;
    String error;

    String rasxGaza;
    String rasxVozd;
    String davlVod;
    String davlGaza;

    public String getRasxGaza() {
        return rasxGaza;
    }

    public void setRasxGaza(String rasxGaza) {
        this.rasxGaza = rasxGaza;
    }

    public String getRasxVozd() {
        return rasxVozd;
    }

    public void setRasxVozd(String rasxVozd) {
        this.rasxVozd = rasxVozd;
    }

    public String getDavlVod() {
        return davlVod;
    }

    public void setDavlVod(String davlVod) {
        this.davlVod = davlVod;
    }

    public String getDavlGaza() {
        return davlGaza;
    }

    public void setDavlGaza(String davlGaza) {
        this.davlGaza = davlGaza;
    }

    public String getTempGor() {
        return tempGor;
    }

    public void setTempGor(String tempGor) {
        this.tempGor = tempGor;
    }

    public String getTempXol() {
        return tempXol;
    }

    public void setTempXol(String tempXol) {
        this.tempXol = tempXol;
    }

    public String getTempKomn() {
        return tempKomn;
    }

    public void setTempKomn(String tempKomn) {
        this.tempKomn = tempKomn;
    }

    public String getPNagr() {
        return PNagr;
    }

    public void setPNagr(String PNagr) {
        this.PNagr = PNagr;
    }

    public String getPomp() {
        return pomp;
    }

    public void setPomp(String pomp) {
        this.pomp = pomp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
