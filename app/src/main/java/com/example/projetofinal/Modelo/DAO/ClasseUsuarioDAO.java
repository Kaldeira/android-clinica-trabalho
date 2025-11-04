package com.example.projetofinal.Modelo.DAO;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.projetofinal.Modelo.ClasseUsuario;

import java.util.ArrayList;


public class ClasseUsuarioDAO {
    private ArrayList<ClasseUsuario> users = new ArrayList<>();

    public void updateUsuario(SQLiteDatabase db, ClasseUsuario user) {
        db.execSQL("UPDATE Usuarios SET NomeCompleto = ?, Email = ?, Senha = ?, Tipo = ? WHERE ID_Usuario = ?",
                    new Object[]{user.getNome(), user.getEmail(), user.getSenha(), user.getTipo(), user.getId()});

    }

    public void inserirUsuario(SQLiteDatabase db, ClasseUsuario user) {
        db.execSQL("INSERT INTO Usuarios (NomeCompleto, Email, Senha, Tipo) VALUES (?, ?, ?, ?)",
                new Object[]{user.getNome(), user.getEmail(), user.getSenha(), user.getTipo()});

    }

    public void deleteUsuario(SQLiteDatabase db, int idUsuario) {
        db.execSQL("DELETE FROM Usuarios WHERE ID_Usuario = ?",
                new Object[]{idUsuario});
    }

    public ArrayList<ClasseUsuario> selectUsuarios(SQLiteDatabase db)
    {
        users.clear();

        Cursor c = db.rawQuery(
                "SELECT * FROM Usuarios", null);

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String nome = c.getString(1);
            String email = c.getString(2);
            String senha = c.getString(3);
            String tipo = c.getString(4);

            ClasseUsuario user = new ClasseUsuario(id, nome, email, senha, tipo);
            users.add(user);
            c.close();
        }

        return users;
    }

    public boolean cadastrarUsuario(SQLiteDatabase db, ClasseUsuario user) {
        Cursor c = db.rawQuery(
                "SELECT ID_Usuario FROM Usuarios WHERE Email = ? COLLATE NOCASE",
                new String[]{user.getEmail()}
        );

        if (c.moveToFirst()) {
            // usuário já existe
            c.close();
            return false;
        }
        c.close();

        ClasseUsuario novoUsuario = new ClasseUsuario(0, user.getNome(), user.getEmail(), user.getSenha(), user.getTipo());

        try {
            this.inserirUsuario(db, novoUsuario);
        } catch (Exception e) {
            return false;
        }


        return true;
    }

    public boolean fazerLogin(SQLiteDatabase db, String email, String senha) {
        String sql = "SELECT * FROM Usuarios WHERE Email = ? COLLATE NOCASE AND Senha = ?";
        String[] args = {email, senha};
        Cursor c = db.rawQuery(sql, args);

        if (c.moveToFirst()) {
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }
}
