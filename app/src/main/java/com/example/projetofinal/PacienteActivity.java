package com.example.projetofinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
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

public class PacienteActivity extends AppCompatActivity {

    Spinner spinnerMedicos;
    EditText edtMensagemPaciente;
    Button btnEnviarMensagem, btnAgendar, btnMsg;;
    ListView listMensagensPaciente, listAgendamentos;
    LinearLayout layoutMensagens, layoutAgendamento;
    BancoDados banco;
    SQLiteDatabase db;
    int idPaciente;
    String nomePaciente;
    TextView textoTitulo;
    ImageButton btnLogout;

    ArrayList<String> listaAgendamento = new ArrayList<>();
    ArrayList<String> listaMensagens = new ArrayList<>();
    ArrayList<Integer> listaMensagensID = new ArrayList<>();      // armazena ID_Mensagem
    ArrayList<Integer> listaMensagensMedicoID = new ArrayList<>(); // armazena ID_Medico
    ArrayAdapter<String> adapterMensagens;
    ArrayList<Integer> listaMedicosID = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);

        //Variaveis da outra tela
        idPaciente = getIntent().getIntExtra("ID_USUARIO", -1);
        nomePaciente = getIntent().getStringExtra("NOME_USUARIO");

        //Variaveis dessa tela aqui
        spinnerMedicos = findViewById(R.id.spinnerMedicos);
        edtMensagemPaciente = findViewById(R.id.edtMensagemPaciente);
        btnEnviarMensagem = findViewById(R.id.btnEnviarMensagem);
        listMensagensPaciente = findViewById(R.id.listMensagensPaciente);
        listAgendamentos = findViewById(R.id.listAgendamentos);
        layoutMensagens = (LinearLayout) findViewById(R.id.layoutMensagens);
        layoutAgendamento = (LinearLayout) findViewById(R.id.layoutAgendamento);

        btnLogout = (ImageButton) findViewById(R.id.btnLogout);
        btnMsg = (Button) findViewById(R.id.btnMsg);
        btnAgendar = (Button) findViewById(R.id.btnConsulta);

        banco = new BancoDados(this);
        db = banco.getWritableDatabase();

        textoTitulo = (TextView)findViewById(R.id.textoTitulo);
        textoTitulo.setText("Bem Vindo, " + gerarPreview(nomePaciente));
        btnMsg.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#045a9b")));

        {
            carregarMedicos();
            carregarMensagens();
            carregarConsultas();
        }

        listMensagensPaciente.setOnItemClickListener((parent, view, position, id) -> CarregarRespostas(position));

        btnEnviarMensagem.setOnClickListener(v -> enviarMensagem());

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PacienteActivity.this);
                builder.setTitle("Sair");
                builder.setMessage("Deseja realmente sair?");

                Intent i = new Intent(PacienteActivity.this, MainActivity.class);
                PacienteActivity.this.startActivity(i);
                finish();
            }
        });

        btnAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutMensagens.setVisibility(View.GONE);
                layoutAgendamento.setVisibility(View.VISIBLE);
                btnAgendar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#045a9b")));
                btnMsg.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0576d2")));
            }
        });

        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutMensagens.setVisibility(View.VISIBLE);
                layoutAgendamento.setVisibility(View.GONE);
                btnMsg.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#045a9b")));
                btnAgendar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0576d2")));
            }
        });
    }

    private void carregarMedicos() {
        listaMedicosID.clear();
        ArrayList<String> nomesMedicos = new ArrayList<>();
        nomesMedicos.add("👨‍⚕️ Selecione o Medico");

        Cursor c = db.rawQuery("SELECT ID_Usuario, NomeCompleto FROM Usuarios WHERE Tipo = 'M'", null);
        while (c.moveToNext()) {
            listaMedicosID.add(c.getInt(0));

            String nomeMedico = c.getString(1);
            nomesMedicos.add("Dr. " + nomeMedico);
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nomesMedicos) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        spinnerMedicos.setAdapter(adapter);
        spinnerMedicos.setSelection(0);
    }


    private void enviarMensagem() {
        if (spinnerMedicos.getSelectedItemPosition() == -1 || spinnerMedicos.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione um médico!", Toast.LENGTH_SHORT).show();
            return;
        }

        int idMedico = listaMedicosID.get((spinnerMedicos.getSelectedItemPosition() - 1));
        String mensagem = edtMensagemPaciente.getText().toString().trim();

        if (mensagem.isEmpty()) {
            Toast.makeText(this, "Digite uma mensagem!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.execSQL("INSERT INTO Mensagens (ID_Paciente, ID_Medico, MensagemPaciente, DataEnvio) VALUES (?, ?, ?, datetime('now'))",
                new Object[]{idPaciente, idMedico, mensagem});

        Toast.makeText(this, "Mensagem enviada!", Toast.LENGTH_SHORT).show();
        edtMensagemPaciente.setText("");
        carregarMensagens();
    }

    private void carregarConsultas() {
        listaAgendamento.clear();

        Cursor c = db.rawQuery(
                "SELECT C.DataConsulta, C.Descricao, C.Status, C.Local, U.NomeCompleto AS NomeMedico " +
                        "FROM Consultas C " +
                        "INNER JOIN Usuarios U ON C.ID_Medico = U.ID_Usuario " +
                        "WHERE C.ID_Paciente = ? " +
                        "ORDER BY datetime(C.DataConsulta) DESC",
                new String[]{String.valueOf(idPaciente)}
        );

        if (c.getCount() == 0) {
            listaAgendamento.add("Você ainda não possui consultas agendadas.");
            adapterMensagens = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaAgendamento);
            listAgendamentos.setAdapter(adapterMensagens);
            c.close();
            return;
        }

        while (c.moveToNext()) {
            String dataConsulta = c.getString(0);
            String descricao = c.getString(1);
            String status = c.getString(2);
            String local = c.getString(3);
            String nomeMedico = c.getString(4);



            String display = "<b>👨‍⚕️ Médico:</b> " + nomeMedico + "<br>" +
                    "<b>📅 Data:</b> " + dataConsulta + "<br>" +
                    "<b>🏥 Local:</b> " + local + "<br>" +
                    "<b>📝 Observação:</b> " + descricao + "<br>";

            listaAgendamento.add(String.valueOf(Html.fromHtml(display, Html.FROM_HTML_MODE_LEGACY)));
        }

        c.close();

        adapterMensagens = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaAgendamento);
        listAgendamentos.setAdapter(adapterMensagens);
    }

    private void carregarMensagens() {
        listaMensagens.clear();
        listaMensagensID.clear();
        listaMensagensMedicoID.clear();

        Cursor c = db.rawQuery(
                "SELECT M.ID_Mensagem, M.ID_Medico, M.MensagemPaciente, M.MensagemMedico, U.NomeCompleto " +
                        "FROM Mensagens M INNER JOIN Usuarios U ON M.ID_Medico = U.ID_Usuario " +
                        "WHERE M.ID_Paciente = ? ORDER BY M.ID_Mensagem DESC",
                new String[]{String.valueOf(idPaciente)}
        );

        if (c.getCount() == 0) {
            listaMensagens.add("💬 Você ainda não enviou nenhuma mensagem.");
            adapterMensagens = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaMensagens);
            listMensagensPaciente.setAdapter(adapterMensagens);
            c.close();
            return;
        }

        while (c.moveToNext()) {
            int idMsg = c.getInt(0);
            int idMed = c.getInt(1);
            String msgPac = c.getString(2);
            String msgMed = c.getString(3);
            String nomeMedico = c.getString(4);

            String status;
            String display;

            if (msgMed == null || msgMed.isEmpty()) {
                status = "🕒 <i>Aguardando resposta...</i>";
                display = "👨‍⚕️ <b>Dr. " + nomeMedico + "</b><br>" +
                        "💬 <i>Sua mensagem foi enviada!</i><br>" +
                        status + "<br>";
            } else {
                status = "✅ <i>Dr. " + nomeMedico + " respondeu você!</i>";
                display = "👨‍⚕️ <b>Dr. " + nomeMedico + "</b><br>" +
                        status + "<br>";
            }

            listaMensagens.add(String.valueOf(Html.fromHtml(display, Html.FROM_HTML_MODE_LEGACY)));


            listaMensagensID.add(idMsg);
            listaMensagensMedicoID.add(idMed);
        }

        c.close();

        adapterMensagens = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaMensagens);
        listMensagensPaciente.setAdapter(adapterMensagens);
    }

    private void CarregarRespostas(int position) {
        if (position < 0 || position >= listaMensagensID.size()) {
            Toast.makeText(this, "Índice inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        int idMensagem = listaMensagensID.get(position);
        int idMedico = listaMensagensMedicoID.get(position);

        Cursor c = db.rawQuery(
                "SELECT M.MensagemPaciente, M.MensagemMedico, U.NomeCompleto, M.DataEnvio, M.DataResposta " +
                        "FROM Mensagens M INNER JOIN Usuarios U ON M.ID_Medico = U.ID_Usuario " +
                        "WHERE M.ID_Mensagem = ?",
                new String[]{ String.valueOf(idMensagem) }
        );

        if (!c.moveToFirst()) {
            Toast.makeText(this, "Mensagem não encontrada.", Toast.LENGTH_SHORT).show();
            c.close();
            return;
        }

        String msgPaciente = c.getString(c.getColumnIndexOrThrow("MensagemPaciente"));
        String msgMedico = c.getString(c.getColumnIndexOrThrow("MensagemMedico"));
        String nomeMedico = c.getString(c.getColumnIndexOrThrow("NomeCompleto"));
        String dataEnvio = null;
        String dataResposta = null;
        try {
            dataEnvio = c.getString(c.getColumnIndexOrThrow("DataEnvio"));
            dataResposta = c.getString(c.getColumnIndexOrThrow("DataResposta"));
        } catch (Exception ignored) {}

        c.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Conversa com Dr. " + nomeMedico);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        TextView tvPaciente = new TextView(this);
        {
            String text = "<b>Sua mensagem (" + (dataEnvio != null ? dataEnvio : "") + "):</b><br>" + msgPaciente + "<br>";
            tvPaciente.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        }
        layout.addView(tvPaciente);

        TextView tvMedico = new TextView(this);
        if (msgMedico == null || msgMedico.isEmpty()) {
            tvMedico.setText("(Sem resposta ainda)");
        } else {
            String texto = "<b>Resposta do Dr. " + nomeMedico + " (" + (dataResposta != null ? dataResposta : "") + "):</b>" + "<br>" + msgMedico;
            tvMedico.setText(Html.fromHtml(texto, Html.FROM_HTML_MODE_LEGACY));
        }
        layout.addView(tvMedico);

        if (msgMedico == null || msgMedico.isEmpty()) {
            builder.setNeutralButton("Excluir", (dialog, which) -> {
                try {
                    String sql = "DELETE FROM Mensagens WHERE ID_Mensagem = ?";
                    db.execSQL(sql, new Object[]{idMensagem});
                    Toast.makeText(this, "Mensagem excluída com sucesso!", Toast.LENGTH_SHORT).show();
                    carregarMensagens();

                } catch (Exception e) {
                    Toast.makeText(this, "Erro ao excluir mensagem: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            EditText inputResposta = new EditText(this);
            inputResposta.setHint(" Edite sua mensagem aqui...");
            layout.addView(inputResposta);

            builder.setPositiveButton("Atualizar", (dialog, which) -> {
                String resposta = inputResposta.getText().toString().trim();
                if (resposta.isEmpty()) {
                    Toast.makeText(this, "Resposta vazia não será enviada.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    db.execSQL(
                            "UPDATE Mensagens SET MensagemPaciente = ?, DataResposta = datetime('now') WHERE ID_Mensagem = ?",
                            new Object[]{resposta, idMensagem}
                    );
                    Toast.makeText(this, "Resposta enviada!", Toast.LENGTH_SHORT).show();

                    carregarMensagens();
                } catch (Exception e) {
                    Toast.makeText(this, "Erro ao enviar resposta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        builder.setView(layout);
        builder.setNegativeButton("Fechar", null);
        builder.show();
    }

    private String gerarPreview(String mensagem) {
        if (mensagem == null) return "";
        mensagem = mensagem.trim();

        int espacoIndex = mensagem.indexOf(" ");
        if (espacoIndex != -1) {
            return mensagem.substring(0, espacoIndex);
        } else {
            return mensagem;
        }
    }

}
