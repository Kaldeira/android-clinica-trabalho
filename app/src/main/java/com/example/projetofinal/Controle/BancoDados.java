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
                "FOREIGN KEY(ID_Paciente) REFERENCES Usuarios(ID_Usuario), " +
                "FOREIGN KEY(ID_Medico) REFERENCES Usuarios(ID_Usuario)" +
                ")";
        db.execSQL(createMensagens);

        db.execSQL("CREATE TABLE Consultas (" +
                "ID_Consulta INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ID_Medico INTEGER NOT NULL, " +
                "ID_Paciente INTEGER NOT NULL, " +
                "DataConsulta TEXT NOT NULL, " +
                "Descricao TEXT, " +
                "Status TEXT NOT NULL DEFAULT 'Agendada', " +
                "Local TEXT, " +
                "FOREIGN KEY (ID_Medico) REFERENCES Usuarios(ID_Usuario), " +
                "FOREIGN KEY (ID_Paciente) REFERENCES Usuarios(ID_Usuario))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Mensagens");
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
        onCreate(db);
    }
}
