package com.giovannyenes.estruturadados.model;

import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "area_desmatada")
public class DadosDesmatamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idBdq;    // ID_BDQ
    private String focoId;   // FOCO_ID
    private double latitude;
    private double longitude;
    private String pais;
    private String estado;
    private String municipio;
    private String bioma;

    @Column(nullable = false)
    private LocalDate data; // agora guardamos a data completa

    public DadosDesmatamento() {}

    public DadosDesmatamento(String idBdq, String focoId, double latitude, double longitude,
                             String pais, String estado, String municipio, String bioma, LocalDate data) {
        this.idBdq = idBdq;
        this.focoId = focoId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pais = pais;
        this.estado = estado;
        this.municipio = municipio;
        this.bioma = bioma;
        this.data = data;
    }

    // Getters e Setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getIdBdq() { return idBdq; }

    public void setIdBdq(String idBdq) { this.idBdq = idBdq; }

    public String getFocoId() { return focoId; }

    public void setFocoId(String focoId) { this.focoId = focoId; }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getPais() { return pais; }

    public void setPais(String pais) { this.pais = pais; }

    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }

    public String getMunicipio() { return municipio; }

    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getBioma() { return bioma; }

    public void setBioma(String bioma) { this.bioma = bioma; }

    public LocalDate getData() { return data; }

    public void setData(LocalDate data) { this.data = data; }
}
