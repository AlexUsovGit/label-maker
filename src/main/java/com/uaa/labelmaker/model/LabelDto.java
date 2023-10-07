package com.uaa.labelmaker.model;

public class LabelDto
{
    private String model;
    private String razmer;
    private String polnota;
    private String dataVypuska;
    private String materVerh;
    private String materPodkladka;
    private String materNiz;

    public LabelDto()
    {
    }

    public LabelDto(String model, String razmer, String polnota, String dataVypuska, String materVerh, String materPodkladka, String materNiz)
    {
        this.model = model;
        this.razmer = razmer;
        this.polnota = polnota;
        this.dataVypuska = dataVypuska;
        this.materVerh = materVerh;
        this.materPodkladka = materPodkladka;
        this.materNiz = materNiz;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getRazmer()
    {
        return razmer;
    }

    public void setRazmer(String razmer)
    {
        this.razmer = razmer;
    }

    public String getPolnota()
    {
        return polnota;
    }

    public void setPolnota(String polnota)
    {
        this.polnota = polnota;
    }

    public String getDataVypuska()
    {
        return dataVypuska;
    }

    public void setDataVypuska(String dataVypuska)
    {
        this.dataVypuska = dataVypuska;
    }

    public String getMaterVerh()
    {
        return materVerh;
    }

    public void setMaterVerh(String materVerh)
    {
        this.materVerh = materVerh;
    }

    public String getMaterPodkladka()
    {
        return materPodkladka;
    }

    public void setMaterPodkladka(String materPodkladka)
    {
        this.materPodkladka = materPodkladka;
    }

    public String getMaterNiz()
    {
        return materNiz;
    }

    public void setMaterNiz(String materNiz)
    {
        this.materNiz = materNiz;
    }

    @Override
    public String toString()
    {
        return
                "model='" + model + '\'' +
                ", razmer='" + razmer + '\'' +
                ", polnota='" + polnota + '\'' +
                ", dataVypuska='" + dataVypuska + '\'' +
                ", materVerh='" + materVerh + '\'' +
                ", materPodkladka='" + materPodkladka + '\'' +
                ", materPodoshva='" + materNiz + '\'';
    }
}
