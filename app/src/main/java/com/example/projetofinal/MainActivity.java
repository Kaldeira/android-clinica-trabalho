package com.example.projetofinal;

import android.content.DialogInterface;
import android.os.Bundle;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projetofinal.Controle.BancoDados;

public class MainActivity extends AppCompatActivity {

    Button fazerLogin, fazerCadastro;
    EditText usuarioLogin, senhaLogin;
    BancoDados banco;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        fazerLogin = (Button)findViewById(R.id.fazerLogin);
        fazerCadastro = (Button)findViewById(R.id.fazerCad);
        usuarioLogin = (EditText)findViewById(R.id.usuarioLogin);
        senhaLogin = (EditText)findViewById(R.id.senhaLogin);
        banco = new BancoDados(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fazerLogin.setOnClickListener(v -> {
            String email = usuarioLogin.getText().toString().trim();
            String senha = senhaLogin.getText().toString().trim();

            if(email.isEmpty() || senha.isEmpty()){
                MostrarMensagem(builder,"Preencha todos os campos");
                return;
            }

            db = banco.getReadableDatabase();

            String sql = "SELECT * FROM Usuarios WHERE Email = ? COLLATE NOCASE AND Senha = ?";
            String[] args = {email, senha};
            Cursor cursor = db.rawQuery(sql, args);

            if(cursor.moveToFirst()){
                int idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Usuario"));
                String nome = cursor.getString(cursor.getColumnIndexOrThrow("NomeCompleto"));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow("Tipo"));

                cursor.close();
                db.close();

                Toast.makeText(MainActivity.this, "Login feito com sucesso!", Toast.LENGTH_SHORT).show();

                if(tipo.equals("P")){
                    Intent i = new Intent(MainActivity.this, PacienteActivity.class);
                    i.putExtra("ID_USUARIO", idUsuario);
                    i.putExtra("NOME_USUARIO", nome);
                    MainActivity.this.startActivity(i);
                } else if(tipo.equals("M")){
                    Intent i = new Intent(MainActivity.this, MedicoActivity.class);
                    i.putExtra("ID_USUARIO", idUsuario);
                    i.putExtra("NOME_USUARIO", nome);
                    MainActivity.this.startActivity(i);
                } else if(tipo.equals("A")){
                    Intent i = new Intent(MainActivity.this, AdminActivity.class);
                    i.putExtra("ID_USUARIO", idUsuario);
                    i.putExtra("NOME_USUARIO", nome);
                    MainActivity.this.startActivity(i);
                }

                finish();
            } else {
                cursor.close();
                db.close();
                MostrarMensagem(builder,"Senha ou Usuario incorreto!");
                usuarioLogin.setText("");
                senhaLogin.setText("");
            }
        });

        fazerCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CadastroActivity.class);
                MainActivity.this.startActivity(i);
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