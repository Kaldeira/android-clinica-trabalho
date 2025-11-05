package com.example.projetofinal.Controle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoDados extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "banco_secuide.db";
    private static final int VERSAO_BANCO = 1;

    public BancoDados(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsuarios = "CREATE TABLE Usuarios (" +
                "ID_Usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NomeCompleto TEXT NOT NULL, " +
                "Email TEXT NOT NULL UNIQUE, " +
                "Senha TEXT NOT NULL, " +
                "Tipo TEXT NOT NULL CHECK (Tipo IN ('P', 'M', 'A'))" +
                ")";
        db.execSQL(createUsuarios);

        String createMensagens = "CREATE TABLE Mensagens (" +
                "ID_Mensagem INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ID_Paciente INTEGER NOT NULL, " +
                "ID_Medico INTEGER NOT NULL, " +
                "MensagemPaciente TEXT NOT NULL, " +
                "MensagemMedico TEXT, " +
                "DataEnvio TEXT, " +
                "DataResposta TEXT, " +
                "FOREIGN KEY(ID_Paciente) REFERENCES Usuarios(ID_Usuario) ON DELETE RESTRICT, " +
                "FOREIGN KEY(ID_Medico) REFERENCES Usuarios(ID_Usuario) ON DELETE RESTRICT" +
                ")";
        db.execSQL(createMensagens);

        String createConsultas = "CREATE TABLE Consultas (" +
                "ID_Consulta INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ID_Medico INTEGER NOT NULL, " +
                "ID_Paciente INTEGER NOT NULL, " +
                "DataConsulta TEXT NOT NULL, " +
                "Descricao TEXT, " +
                "Status TEXT NOT NULL DEFAULT 'Agendada', " +
                "Local TEXT, " +
                "FOREIGN KEY (ID_Medico) REFERENCES Usuarios(ID_Usuario) ON DELETE RESTRICT, " +
                "FOREIGN KEY (ID_Paciente) REFERENCES Usuarios(ID_Usuario) ON DELETE RESTRICT" +
                ")";
        db.execSQL(createConsultas);

        inserirDadosPadrao(db);
    }

    private void inserirDadosPadrao(SQLiteDatabase db) {
        db.execSQL("INSERT INTO Usuarios (NomeCompleto, Email, Senha, Tipo) VALUES " +
                "('Administrador do Sistema', 'admin', 'admin', 'A')");

        db.execSQL("INSERT INTO Usuarios (NomeCompleto, Email, Senha, Tipo) VALUES " +
                "('Mario Cardoso', 'mario@email.com', '1234', 'M'), " +
                "('Maria Silva', 'maria@email.com', '1234', 'P'), " +
                "('Carlos Souza', 'carlos@email.com', '1234', 'P')");

        db.execSQL("INSERT INTO Consultas (ID_Medico, ID_Paciente, DataConsulta, Descricao, Status, Local) VALUES " +
                "(2, 3, '2025-11-05 10:00', 'Consulta de rotina', 'Agendada', 'Clínica Central'), " +
                "(2, 4, '2025-11-07 14:30', 'Revisão de exames', 'Agendada', 'Clínica Central')");

        db.execSQL("INSERT INTO Mensagens (ID_Paciente, ID_Medico, MensagemPaciente, MensagemMedico, DataEnvio, DataResposta) VALUES " +
                "(3, 2, 'Olá doutor, gostaria de saber o resultado do exame.', 'Olá Maria, está tudo bem com você.', '2025-11-01', '2025-11-02'), " +
                "(4, 2, 'Estou sentindo dores de cabeça frequentes.', NULL, '2025-11-03', NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Mensagens");
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
        onCreate(db);
    }
}
