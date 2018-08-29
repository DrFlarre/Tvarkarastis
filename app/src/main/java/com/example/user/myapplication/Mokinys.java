package com.example.user.myapplication;

public class Mokinys {
    private String vardas, nuoroda;

    public Mokinys() {}
    public Mokinys(String vardas, String nuoroda) {
        this.vardas = vardas;
        this.nuoroda = nuoroda;
    }

    public String getVardas() {
        return vardas;
    }
    public void setVardas(String vardas) {
        this.vardas = vardas;
    }

    public String getNuoroda() {
        return nuoroda;
    }
    public void setNuoroda(String nuoroda) {
        this.nuoroda = nuoroda;
    }
}
