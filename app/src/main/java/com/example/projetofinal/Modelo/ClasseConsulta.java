package com.example.projetofinal.Modelo;

public class ClasseConsulta {
    private int idConsulta;
    private int idMedico;
    private int idPaciente;
    private String dataConsulta;
    private String descricao;

    public int getIdConsulta() {
        return idConsulta;
    }
    public int getIdMedico() {
        return idMedico;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public String getDataConsulta() {
        return dataConsulta;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }
    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }
    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }
    public void setDataConsulta(String dataConsulta) {
        this.dataConsulta = dataConsulta;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
