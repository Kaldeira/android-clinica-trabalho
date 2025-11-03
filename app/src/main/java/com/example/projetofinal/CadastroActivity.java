package com.example.projetofinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projetofinal.Controle.BancoDados;
import com.example.projetofinal.Modelo.ClasseUsuario;

public class CadastroActivity  extends AppCompatActivity {

    Button fazerCadastro;
    EditText usuarioNome, usuarioEmail, cadastroSenha;
    Spinner tipoUsuario;
    SQLiteDatabase db;
    ImageButton voltarButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad);

        fazerCadastro = (Button)findViewById(R.id.fazerCadastro);
        usuarioNome = (EditText)findViewById(R.id.cadastroNome);
        usuarioEmail = (EditText)findViewById(R.id.cadastroEmail);
        cadastroSenha = (EditText)findViewById(R.id.cadastroSenha);
        tipoUsuario = (Spinner)findViewById(R.id.tipoUsuario);
        voltarButton = (ImageButton)findViewById(R.id.btnVoltar);
        BancoDados banco = new BancoDados(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        voltarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CadastroActivity.this, MainActivity.class);
                CadastroActivity.this.startActivity(i);
                finish();
            }
        });


        fazerCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = usuarioNome.getText().toString();
                String email = usuarioEmail.getText().toString();
                String senha = cadastroSenha.getText().toString();
                String tipo = tipoUsuario.getSelectedItem().toString();

                if(nome.isEmpty() || email.isEmpty() || senha.isEmpty() || tipo.isEmpty()) {
                    MostrarMensagem(builder,"Preencha todos os campos");
                    return;
                }

                if (tipoUsuario.getSelectedItemPosition() == 0) {
                    MostrarMensagem(builder,"Selecione um tipo de usuário");
                    return;
                } else if (tipoUsuario.getSelectedItemPosition() == 1) {
                    tipo = "M";
                } else if (tipoUsuario.getSelectedItemPosition() == 2) {
                    tipo = "P";
                }
                else if (tipoUsuario.getSelectedItemPosition() == 3) {
                    tipo = "A";
                }

                db = banco.getWritableDatabase();

                try {
                    Cursor c = db.rawQuery(
                            "SELECT ID_Usuario FROM Usuarios WHERE Email = ? COLLATE NOCASE",
                            new String[]{email}
                    );

                    if (c.moveToFirst()) {
                        // usuário já existe
                        MostrarMensagem(builder, "Já existe um usuário cadastrado com este e-mail!");
                        c.close();
                        return;
                    }
                    c.close();

                    String sql = "INSERT INTO Usuarios (NomeCompleto, Email, Senha, Tipo) VALUES (?, ?, ?, ?)";
                    db.execSQL(sql, new Object[]{nome, email, senha, tipo});
                    db.close();

                    usuarioNome.setText("");
                    usuarioEmail.setText("");
                    cadastroSenha.setText("");

                    builder.setMessage("Cadastro realizado com sucesso!");
                    builder.setPositiveButton("OK", (dialog, id) -> {
                        Intent i = new Intent(CadastroActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } catch (Exception e) {
                    MostrarMensagem(builder,"Erro ao cadastrar: " + e.getMessage());
                }
            }
        });

    }

    public void MostrarMensagem(AlertDialog.Builder builder, String msg) {
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
