package com.example.projetofinal.Modelo;

public class ClasseMensagem {
    private int id;
    private int idPaciente;
    private int idMedico;
    private String mensagemPaciente;
    private String mensagemMedico;
    private String dataEnvio;
    private String dataResposta;

    public int getId() {
        return id;
    }
    public int getIdPaciente() {
        return idPaciente;
    }
    public int getIdMedico() {
        return idMedico;
    }
    public String getMensagemPaciente() {
        return mensagemPaciente;
    }
    public String getMensagemMedico() {
        return mensagemMedico;
    }
    public String getDataEnvio() {
        return dataEnvio;
    }
    public String getDataResposta() {
        return dataResposta;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }
    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }
    public void setDataEnvio(String dataEnvio) {
        this.dataEnvio = dataEnvio;
    }
    public void setDataResposta(String dataResposta) {
        this.dataResposta = dataResposta;
    }
    public void setMensagemMedico(String mensagemMedico) {
        this.mensagemMedico = mensagemMedico;
    }
    public void setMensagemPaciente(String mensagemPaciente) {
        this.mensagemPaciente = mensagemPaciente;
    }
}
