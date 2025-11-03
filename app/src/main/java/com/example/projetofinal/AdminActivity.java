package com.example.projetofinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projetofinal.Controle.BancoDados;
import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {
    ListView listaUsuarios;
    BancoDados banco;
    SQLiteDatabase db;
    int idAdmin;
    String nomeAdmin;

    ArrayList<String> arrayUsuarios = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        listaUsuarios = (ListView) findViewById(R.id.listPacientes);
        banco = new BancoDados(this);
        db = banco.getWritableDatabase();

        idAdmin = getIntent().getIntExtra("ID_USUARIO", -1);
        nomeAdmin = getIntent().getStringExtra("NOME_USUARIO");

        carregarUsuarios();
    }

    private void carregarUsuarios() {
        arrayUsuarios.clear();

        Cursor c = db.rawQuery(
                "SELECT * FROM Usuarios", null);

        while (c.moveToNext()) {
            int idUsuario = c.getInt(0);
            String nomeUsuario = c.getString(1);
            String emailUsuario = c.getString(2);
            String senhaUsuario = c.getString(3);
            String tipoUsuario = c.getString(4);

            if (tipoUsuario.equals("M"))
                tipoUsuario = "Medico";
            else if (tipoUsuario.equals("P"))
                tipoUsuario = "Paciente";
            else if (tipoUsuario.equals("A"))
                tipoUsuario = "Admin";

            String texto = "\nID: " + idUsuario + "\nUsuario: " + nomeUsuario + "\nEmail: " + emailUsuario + "\nSenha: " + senhaUsuario + "\nTipo: " + tipoUsuario;
            arrayUsuarios.add(texto);
        }

        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayUsuarios);
        listaUsuarios.setAdapter(adapter);
    }

}
