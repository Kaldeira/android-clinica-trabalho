package com.example.projetofinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projetofinal.Controle.BancoDados;
import java.util.ArrayList;

public class MensagensActivity extends AppCompatActivity {

    BancoDados banco;
    SQLiteDatabase db;
    private ListView listMensagens;
    private int idPaciente;
    private int idMedico;
    private String nomeMedico;
    TextView textoTitulo;
    ImageButton btnVoltar;

    ArrayList<String> listaPacientes = new ArrayList<>();
    ArrayList<Integer> listaPacientesID = new ArrayList<>();
    ArrayList<Integer> listaMensagensID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagens_paciente);

        banco = new BancoDados(this);
        db = banco.getWritableDatabase();
        listMensagens = findViewById(R.id.listMensagens);
        textoTitulo = findViewById(R.id.textoTitulo);
        idMedico = getIntent().getIntExtra("ID_USUARIO", -1);
        idPaciente = getIntent().getIntExtra("ID_PACIENTE", -1);
        nomeMedico = getIntent().getStringExtra("NOME_USUARIO");
        btnVoltar = findViewById(R.id.btnVoltar);


       // textoTitulo.setText("Mensagens com " + getIntent().getStringExtra("NOME_USUARIO"));

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MensagensActivity.this, MedicoActivity.class);
                i.putExtra("ID_USUARIO", idMedico);
                i.putExtra("NOME_USUARIO", nomeMedico);
                MensagensActivity.this.startActivity(i);
                finish();
            }
        });



        carregarMensagens();

        listMensagens.setOnItemClickListener((parent, view, position, id) -> responderPaciente(listaMensagensID.get(position)));
    }

    private void carregarMensagens() {
        listaPacientes.clear();
        listaPacientesID.clear();
        listaMensagensID.clear();

        Cursor c = db.rawQuery(
                "SELECT M.ID_Mensagem, P.ID_Usuario, P.NomeCompleto, M.MensagemMedico, M.DataEnvio, M.DataResposta, M.MensagemPaciente " +
                        "FROM Mensagens M " +
                        "INNER JOIN Usuarios P ON M.ID_Paciente = P.ID_Usuario " +
                        "WHERE M.ID_Medico = ? AND P.ID_Usuario = ?" +
                        "ORDER BY CASE WHEN M.MensagemMedico IS NULL OR M.MensagemMedico = '' THEN 0 ELSE 1 END ASC, datetime(M.DataEnvio) DESC",
                new String[]{String.valueOf(idMedico),String.valueOf(idPaciente) }
        );

        while (c.moveToNext()) {
            int idMensagem = c.getInt(0);
            int idPaciente = c.getInt(1);
            String nomePaciente = c.getString(2);
            String msgMed = c.getString(3);
            String dataEnvio = c.getString(4);
            String dataResposta = c.getString(5);
            String msgPaciente = c.getString(6);

            listaMensagensID.add(idMensagem);
            listaPacientesID.add(idPaciente);
            textoTitulo.setText("Mensagens com " + nomePaciente);


            String status;
            String texto;
            String preview = gerarPreview(msgPaciente, 40);

            if (msgMed == null || msgMed.isEmpty()) {
                // Sem resposta ainda
                status = "🕒 Aguardando resposta do médico";
                texto =
                        "💬 " + preview + "<br>" +
                                "📅 <i>Enviada em:</i> " + dataEnvio + "<br>" +
                                status;
            } else {
                // Já respondida
                status = "✅ Respondida em: " + dataResposta;
                texto =
                        "💬 " + preview + "<br>" +
                                "📅 <i>Enviada em:</i> " + dataEnvio + "<br>" +
                                status;
            }


            listaPacientes.add(String.valueOf(Html.fromHtml(texto, Html.FROM_HTML_MODE_LEGACY)));
        }

        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaPacientes);
        listMensagens.setAdapter(adapter);

        listMensagens.setOnItemClickListener((parent, view, position, id) -> {
            int idMensagem = listaMensagensID.get(position);
            responderPaciente(idMensagem);
        });
    }

    @SuppressLint("SetTextI18n")
    private void responderPaciente(int idMsg) {
        Cursor c = db.rawQuery(
                "SELECT M.ID_Mensagem, M.MensagemPaciente, M.MensagemMedico, M.DataEnvio, M.DataResposta, P.NomeCompleto " +
                        "FROM Mensagens M " +
                        "INNER JOIN Usuarios P ON M.ID_Paciente = P.ID_Usuario " +
                        "WHERE M.ID_Mensagem = ?",
                new String[]{ String.valueOf(idMsg) }
        );

        if (!c.moveToFirst()) {
            // Não encontrou mensagens
            Toast.makeText(this, "Nenhuma mensagem encontrada desse paciente.", Toast.LENGTH_SHORT).show();
            c.close();
            return;
        }
        int idMensagem = c.getInt(c.getColumnIndexOrThrow("ID_Mensagem"));
        String msgPaciente = c.getString(c.getColumnIndexOrThrow("MensagemPaciente"));
        String nomePaciente = c.getString(c.getColumnIndexOrThrow("NomeCompleto"));
        String lastMsg = c.getString(c.getColumnIndexOrThrow("MensagemMedico"));
        String dataEnvio = c.getString(c.getColumnIndexOrThrow("DataEnvio"));
        String dataResposta = c.getString(c.getColumnIndexOrThrow("DataResposta"));
        c.close();

        // 2) Montar dialog com a mensagem e um campo para resposta
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mensagem do Paciente");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        TextView tvMensagem = new TextView(this);
        {
            String text = "<b>" + nomePaciente + " (" + (dataEnvio != null ? dataEnvio : "") + "):</b><br>" + msgPaciente + "<br>";
            tvMensagem.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        }
        layout.addView(tvMensagem);

        TextView tvRespostaAtual = new TextView(this);
        {
            if (lastMsg != null && !lastMsg.isEmpty()) { //ver se a resposta ja existe
                String text = "<b>Sua Resposta (" + (dataResposta != null ? dataResposta : "") + "):</b><br>" + lastMsg + "<br>";
                tvRespostaAtual.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
            }
        }
        layout.addView(tvRespostaAtual);

        String nomeBotao = "Enviar";
        EditText inputResposta = new EditText(this);
        if (lastMsg != null && !lastMsg.isEmpty()) {
            inputResposta.setHint(" Edite sua resposta aqui...");
            nomeBotao = "Atualizar";
        }
        else
            inputResposta.setHint("Digite sua resposta aqui...");

        // se já existir resposta, pre preencher aq
//        if (lastMsg != null) {
//            inputResposta.setText(lastMsg);
//        }
        layout.addView(inputResposta);

        builder.setView(layout);

        builder.setPositiveButton(nomeBotao, (dialog, which) -> {
            String resposta = inputResposta.getText().toString().trim();
            if (resposta.isEmpty()) {
                Toast.makeText(this, "Resposta vazia não será enviada.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                db.execSQL(
                        "UPDATE Mensagens SET MensagemMedico = ?, DataResposta = datetime('now') WHERE ID_Mensagem = ?",
                        new Object[]{resposta, idMensagem}
                );
                Toast.makeText(this, "Resposta enviada!", Toast.LENGTH_SHORT).show();

                carregarMensagens();
            } catch (Exception e) {
                Toast.makeText(this, "Erro ao enviar resposta: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        if (lastMsg != null && !lastMsg.isEmpty()) {
            builder.setNeutralButton("Excluir", (dialog, which) -> {
                try {
                    String sql = "UPDATE Mensagens SET MensagemMedico = NULL, DataResposta = NULL WHERE ID_Mensagem = ?";
                    db.execSQL(sql, new Object[]{idMensagem});
                    Toast.makeText(this, "Resposta excluída com sucesso!", Toast.LENGTH_SHORT).show();
                    carregarMensagens();

                } catch (Exception e) {
                    Toast.makeText(this, "Erro ao excluir resposta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private String gerarPreview(String mensagem, int maxChars) {
        if (mensagem == null) return "";
        mensagem = mensagem.trim();
        if (mensagem.length() <= maxChars) {
            return mensagem;
        } else {
            return mensagem.substring(0, maxChars) + "...";
        }
    }
}

