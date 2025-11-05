package com.example.projetofinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projetofinal.Controle.BancoDados;
import com.example.projetofinal.Modelo.ClasseConsulta;
import com.example.projetofinal.Modelo.ClasseUsuario;
import com.example.projetofinal.Modelo.DAO.ClasseUsuarioDAO;

import java.util.ArrayList;
import java.util.Calendar;

public class MedicoActivity extends AppCompatActivity {
    ListView listPacientes, listAgendamentos;
    LinearLayout layoutMensagens, layoutAgendamento, layoutConsultas;
    BancoDados banco;
    SQLiteDatabase db;
    int idMedico;
    String nomeMedico;
    ImageButton btnLogout;
    Button btnMsg, btnAgendar, btnConfirmar, btnAgenda;
    Spinner spinnerPaciente;
    EditText editDataHora, editLocal, editDescricao;

    ArrayList<String> listaAgendamento = new ArrayList<>();
    ArrayList<String> listaMsgPacientes = new ArrayList<>();
    ArrayList<Integer> listaMsgPacienteID = new ArrayList<>();
    private ArrayList<Integer> listaPacientesID = new ArrayList<>();
    private ArrayList<Integer> listaConsultasID = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico);

        idMedico = getIntent().getIntExtra("ID_USUARIO", -1);
        nomeMedico = getIntent().getStringExtra("NOME_USUARIO");

        banco = new BancoDados(this);
        db = banco.getWritableDatabase();

        btnLogout = (ImageButton) findViewById(R.id.btnLogout);
        btnMsg = (Button) findViewById(R.id.btnMsg);
        btnAgendar = (Button) findViewById(R.id.btnConsulta);
        btnAgenda = (Button) findViewById(R.id.btnAgenda);


        //layout Mensagens
        listPacientes = (ListView) findViewById(R.id.listPacientes);
        layoutMensagens = (LinearLayout) findViewById(R.id.layoutMensagens);
        layoutAgendamento = (LinearLayout) findViewById(R.id.layoutAgendamento);
        layoutConsultas = (LinearLayout) findViewById(R.id.layoutConsultas);


        //layout Agendamento
        spinnerPaciente = (Spinner) findViewById(R.id.spinnerPaciente);
        editDataHora = (EditText) findViewById(R.id.editDataHora);
        editDataHora.setInputType(InputType.TYPE_NULL);
        editLocal = (EditText) findViewById(R.id.editLocal);
        editDescricao = (EditText) findViewById(R.id.editDescricao);
        btnConfirmar = (Button) findViewById(R.id.btnAgendar);
        listAgendamentos = (ListView) findViewById(R.id.listAgendamentos);

        ((TextView) findViewById(R.id.textoTitulo)).setText("Bem-vindo, Dr. " + nomeMedico);
        btnMsg.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#045a9b")));

        {
            carregarMensagensPacientes();
            carregarPacientes();
            carregarConsultas();
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MedicoActivity.this);
                builder.setTitle("Logout");
                builder.setMessage("Deseja realmente sair?");

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent i = new Intent(MedicoActivity.this, MainActivity.class);
                        MedicoActivity.this.startActivity(i);
                        finish();

                    }
                });

                builder.setNegativeButton("Não", null);
                builder.show();
            }
        });

        btnAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutMensagens.setVisibility(View.GONE);
                layoutAgendamento.setVisibility(View.VISIBLE);
                layoutConsultas.setVisibility(View.GONE);
                btnAgendar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#045a9b")));
                btnMsg.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0576d2")));
                btnAgenda.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0576d2")));
            }
        });

        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutMensagens.setVisibility(View.VISIBLE);
                layoutAgendamento.setVisibility(View.GONE);
                layoutConsultas.setVisibility(View.GONE);
                btnMsg.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#045a9b")));
                btnAgendar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0576d2")));
                btnAgenda.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0576d2")));
            }
        });

        btnAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutMensagens.setVisibility(View.GONE);
                layoutAgendamento.setVisibility(View.GONE);
                layoutConsultas.setVisibility(View.VISIBLE);
                btnMsg.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0576d2")));
                btnAgendar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0576d2")));
                btnAgenda.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#045a9b")));
            }
        });

        editDataHora.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {

                        TimePickerDialog timePicker = new TimePickerDialog(this,
                                (timeView, hourOfDay, minute) -> {
                                    String dataHora = String.format("%04d -%02d-%02d %02d:%02d",
                                            year, month + 1, dayOfMonth, hourOfDay, minute);
                                    editDataHora.setText(dataHora);
                                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
                        timePicker.show();

                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        btnConfirmar.setOnClickListener(v -> agendarConsulta());
    }

    private void carregarConsultas() {
        listaAgendamento.clear();
        listaConsultasID.clear();

        Cursor c = db.rawQuery(
                "SELECT C.ID_Consulta, C.DataConsulta, C.Descricao, C.Status, C.Local, U.NomeCompleto AS NomeMedico " +
                        "FROM Consultas C " +
                        "INNER JOIN Usuarios U ON C.ID_Paciente = U.ID_Usuario " +
                        "WHERE C.ID_Medico = ? " +
                        "ORDER BY datetime(C.DataConsulta) DESC",
                new String[]{String.valueOf(idMedico)}
        );

        if (c.getCount() == 0) {
            listaAgendamento.add("Você ainda não possui consultas agendadas.");
            listAgendamentos.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaAgendamento));
            c.close();
            return;
        }

        while (c.moveToNext()) {
            int idConsulta = c.getInt(0);
            String dataConsulta = c.getString(1);
            String descricao = c.getString(2);
            String status = c.getString(3);
            String local = c.getString(4);
            String nomePaciente = c.getString(5);

            listaConsultasID.add(idConsulta);

            String texto = idConsulta + ";" +
                    nomePaciente + ";" +
                    nomeMedico + ";" +
                    dataConsulta + ";" +
                    (descricao != null ? descricao : "—") + ";" +
                    (local != null ? local : "—");


//            String display = "<b>👤️ Paciente:</b> " + nomePaciente + "<br>" +
//                    "<b>📅 Data:</b> " + dataConsulta + "<br>" +
//                    "<b>🏥 Local:</b> " + local + "<br>" +
//                    "<b>📝 Observação:</b> " + descricao + "<br>";

            listaAgendamento.add(texto);
        }

        c.close();

        //listAgendamentos.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaAgendamento));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_consulta, R.id.tvPaciente, listaAgendamento) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    view = inflater.inflate(R.layout.list_item_consulta, parent, false);
                }

                String[] dados = getItem(position).split(";", -1);
                if (dados.length >= 6) {
                    TextView tvPaciente = view.findViewById(R.id.tvPaciente);
                    TextView tvMedico = view.findViewById(R.id.tvMedico);
                    TextView tvDataLocal = view.findViewById(R.id.tvDataLocal);
                    TextView tvDescricao = view.findViewById(R.id.tvDescricao);
                    //TextView tvStatus = view.findViewById(R.id.tvStatus);

                    tvPaciente.setText("👤 Paciente: " + dados[1]);
                    tvMedico.setText("👨‍⚕️ Médico: " + dados[2]);
                    tvDataLocal.setText("📅 " + dados[3] + "  |  📍 " + dados[5]);
                    tvDescricao.setText("💬 Descrição: " + dados[4]);
                    //tvStatus.setText("🕒 Status: " + dados[6]);
                }

                return view;
            }
        };

        listAgendamentos.setAdapter(adapter);


        listAgendamentos.setOnItemClickListener((parent, view, position, id) -> {
            //Toast.makeText(this, "Consulta selecionada: " + listaAgendamento.get(position), Toast.LENGTH_SHORT).show();

            int idConsulta = listaConsultasID.get(position);
            telaEditarConsulta(idConsulta);

//            AlertDialog dialog = new AlertDialog.Builder(this)
//                    .setTitle("Cancelar Consulta")
//                    .setMessage("Deseja realmente cancelar esta consulta?")
//                    .setPositiveButton("Sim", (dialogInterface, i) -> {
//                        int idConsulta = listaConsultasID.get(position);
//
//                        try {
//                            String sql = " DELETE FROM Consultas WHERE ID_Consulta = ?";
//                            db.execSQL(sql, new Object[]{idConsulta});
//                            carregarConsultas();
//                            Toast.makeText(this, "Consulta excluída com sucesso!", Toast.LENGTH_SHORT).show();
//
//                        } catch (Exception e) {
//                            Toast.makeText(this, "Erro ao excluir consulta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .setNegativeButton("Não", null)
//                    .create();
//            dialog.show();
//            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        });
    }

    private void carregarMensagensPacientes() {
        listaMsgPacientes.clear();
        listaMsgPacienteID.clear();

        // aqui ta carregando cada paciente que enviou uma mensagem, conta todas msg pelo id, soma quantas nao foram respondidas e a ultima mensagem
        Cursor c = db.rawQuery(
                "SELECT P.ID_Usuario, P.NomeCompleto, COUNT(M.ID_Mensagem) AS TotalMensagens, " +
                        "MAX(M.DataEnvio) AS UltimaMensagem, " +
                        "SUM(CASE WHEN M.MensagemMedico IS NULL OR M.MensagemMedico = '' THEN 1 ELSE 0 END) AS NaoRespondidas " +
                        "FROM Mensagens M " +
                        "INNER JOIN Usuarios P ON M.ID_Paciente = P.ID_Usuario " +
                        "WHERE M.ID_Medico = ? " +
                        "GROUP BY P.ID_Usuario, P.NomeCompleto " +
                        "ORDER BY MAX(datetime(M.DataEnvio)) DESC",
                new String[]{String.valueOf(idMedico)}
        );

        if (c.getCount() == 0) {
            listaMsgPacientes.add("Você ainda não possui nenhuma mensagem!");
            listPacientes.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaMsgPacientes));
            c.close();
            return;
        }

        while (c.moveToNext()) {
            int idPaciente = c.getInt(0);
            String nomePaciente = c.getString(1);
            int totalMensagens = c.getInt(2);
            String ultimaMensagem = c.getString(3);
            int naoRespondidas = c.getInt(4);

            listaMsgPacienteID.add(idPaciente);

//            String texto = "👤 Paciente: " + nomePaciente +
//                    "\n📨 Enviou " + totalMensagens + " mensagens" +
//                    (naoRespondidas > 0 ? " (" + naoRespondidas + " sem resposta)" : " (todas respondidas)") +
//                    "\n🕒 Última mensagem: " + ultimaMensagem;
//
//            listaMsgPacientes.add(texto);


            // Usa "—" caso a data esteja nula
            if (ultimaMensagem == null || ultimaMensagem.isEmpty())
                ultimaMensagem = "—";

            // Armazena os dados separados por "|"
            listaMsgPacientes.add(idPaciente + "|" + nomePaciente + "|" + totalMensagens + "|" + naoRespondidas + "|" + ultimaMensagem);
        }

        c.close();

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaMsgPacientes);
//        listPacientes.setAdapter(adapter);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_msg_paciente, listaMsgPacientes) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.list_item_msg_paciente, parent, false);
                }

                TextView tvNomePaciente = convertView.findViewById(R.id.tvNomePacienteLista);
                TextView tvResumoMensagens = convertView.findViewById(R.id.tvResumoMensagens);
                TextView tvUltimaMensagem = convertView.findViewById(R.id.tvUltimaMensagem);

                // Divide os dados armazenados
                String[] partes = getItem(position).split("\\|");

                int idPaciente = Integer.parseInt(partes[0]);
                String nomePaciente = partes[1];
                int totalMensagens = Integer.parseInt(partes[2]);
                int naoRespondidas = Integer.parseInt(partes[3]);
                String ultimaMensagem = partes[4];

                tvNomePaciente.setText("👤 Paciente: " + nomePaciente);
                tvResumoMensagens.setText("📨 Enviou " + totalMensagens + " mensagens" +
                        (naoRespondidas > 0 ? " (" + naoRespondidas + " sem resposta)" : " (todas respondidas)"));
                tvUltimaMensagem.setText("🕒 Última mensagem: " + ultimaMensagem);

                return convertView;
            }
        };

        listPacientes.setAdapter(adapter);

        listPacientes.setOnItemClickListener((parent, view, position, id) -> {
            int idPaciente = listaMsgPacienteID.get(position);
            Intent intent = new Intent(this, MensagensActivity.class);
            intent.putExtra("ID_USUARIO", idMedico);
            intent.putExtra("ID_PACIENTE", idPaciente);
            intent.putExtra("NOME_USUARIO", nomeMedico);
            startActivity(intent);
        });
    }

    private void carregarPacientes() {
        Cursor c = db.rawQuery("SELECT ID_Usuario, NomeCompleto FROM Usuarios WHERE Tipo = 'P'", null);

        listaPacientesID.clear();
        ArrayList<String> nomes = new ArrayList<>();
        nomes.add("👤 Selecione o paciente");

        if (c.moveToFirst()) {
            do {
                listaPacientesID.add(c.getInt(0));
                nomes.add(c.getString(1));
            } while (c.moveToNext());
        } else {
            nomes.add("Nenhum paciente cadastrado");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, nomes) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        spinnerPaciente.setAdapter(adapter);
        spinnerPaciente.setSelection(0);
        c.close();
    }

    private void agendarConsulta() {
        if (listaPacientesID.isEmpty()) {
            Toast.makeText(this, "Nenhum paciente disponível!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerPaciente.getSelectedItemPosition() == -1 || spinnerPaciente.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione um paciente!", Toast.LENGTH_SHORT).show();
            return;
        }

        int idPaciente = listaPacientesID.get((spinnerPaciente.getSelectedItemPosition() - 1));
        String dataHora = editDataHora.getText().toString().trim();
        String descricao = editDescricao.getText().toString().trim();
        String local = editLocal.getText().toString().trim();

        if (dataHora.isEmpty()) {
            Toast.makeText(this, "Informe a data e hora da consulta!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);

        try {
            String sql = "INSERT INTO Consultas (ID_Medico, ID_Paciente, DataConsulta, Descricao, Local) VALUES (?, ?, ?, ?, ?)";
            db.execSQL(
                    sql,
                    new Object[]{idMedico, idPaciente, dataHora, descricao, local}
            );

            editDataHora.setText("");
            editDescricao.setText("");
            editLocal.setText("");
            spinnerPaciente.setSelection(0);

            carregarConsultas();

            dialogo.setMessage("Consulta agendada com sucesso!");
            dialogo.setPositiveButton("OK", null);
            dialogo.show();

        } catch (Exception e) {
            dialogo.setMessage("Erro ao agendar consulta! " + e.getMessage());
            dialogo.setPositiveButton("OK", null);
            dialogo.show();
        }


    }

    private void telaEditarConsulta(int id)
    {
        Cursor c = db.rawQuery(
                "SELECT C.Descricao, C.Local, C.DataConsulta FROM Consultas C WHERE ID_Consulta = ?",
                new String[]{String.valueOf(id)}
        );

        if (!c.moveToFirst()) {
            Toast.makeText(this, "Consulta não encontrada.", Toast.LENGTH_SHORT).show();
            c.close();
            return;
        }

        int idConsulta = id;
        String descricao = c.getString(0);
        String local = c.getString(1);
        String dataConsulta = c.getString(2);
        c.close();

        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Editar Consulta");

        LinearLayout editLayout = new LinearLayout(this);
        editLayout.setOrientation(LinearLayout.VERTICAL);

        EditText editDescricao = new EditText(this);
        editDescricao.setHint("Descricao");
        editDescricao.setText(descricao);
        editLayout.addView(editDescricao);

        EditText editLocal = new EditText(this);
        editLocal.setHint("Local");
        editLocal.setText(local);
        editLayout.addView(editLocal);

        EditText editData = new EditText(this);
        editLocal.setHint("Data e Hora");
        editData.setText(dataConsulta);
        {
            editData.setOnClickListener(v -> {
                final Calendar cal = Calendar.getInstance();

                DatePickerDialog datePicker = new DatePickerDialog(this,
                        (view, year, month, dayOfMonth) -> {

                            TimePickerDialog timePicker = new TimePickerDialog(this,
                                    (timeView, hourOfDay, minute) -> {
                                        String dataHora = String.format("%04d -%02d-%02d %02d:%02d",
                                                year, month + 1, dayOfMonth, hourOfDay, minute);
                                        editData.setText(dataHora);
                                    }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                            timePicker.show();

                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePicker.show();
            });
        }
        editLayout.addView(editData);
        dialogo.setView(editLayout);


        dialogo.setPositiveButton("Salvar", (dialog, which) -> {
            try {
                db.execSQL("UPDATE Consultas SET Descricao = ?, Local = ?, DataConsulta = ? WHERE ID_Consulta = ?",
                        new Object[]{editDescricao.getText().toString().trim(),
                                editLocal.getText().toString().trim(),
                                editData.getText().toString().trim(),
                                idConsulta});
                carregarConsultas();

                Toast.makeText(this, "Consulta atualizada com sucesso!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Erro ao atualizar Consulta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        dialogo.setNegativeButton("Cancelar", (dialog, which) -> {

        });

        dialogo.setNeutralButton("Excluir", (dialog, which) -> {
            AlertDialog newDialog = new AlertDialog.Builder(this)
                    .setTitle("Cancelar Consulta")
                    .setMessage("Deseja realmente cancelar esta consulta?")
                    .setPositiveButton("Sim", (dialogInterface, i) -> {

                        try {
                            String sql = " DELETE FROM Consultas WHERE ID_Consulta = ?";
                            db.execSQL(sql, new Object[]{idConsulta});
                            carregarConsultas();
                            Toast.makeText(this, "Consulta excluída com sucesso!", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(this, "Erro ao excluir consulta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Não", null)
                    .create();
            newDialog.show();
            newDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        });

        dialogo.show();
    }
}
