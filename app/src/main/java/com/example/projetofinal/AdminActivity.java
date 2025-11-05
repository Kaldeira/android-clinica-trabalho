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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.projetofinal.Controle.BancoDados;
import com.example.projetofinal.Modelo.ClasseUsuario;
import com.example.projetofinal.Modelo.DAO.ClasseUsuarioDAO;
import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {
    ListView listaUsuarios, listaMsgs, listaAgendas;
    BancoDados banco;
    SQLiteDatabase db;
    int idAdmin;
    String nomeAdmin;
    LinearLayout layoutMensagens, layoutAgendamento, layoutUsuarios;
    Button btnUsuarios, btnMsgs, btnAgend;
    ImageButton btnLogout;

    ArrayList<String> arrayUsuarios = new ArrayList<>();
    ArrayList<String> arrayMensagens = new ArrayList<>();
    ArrayList<String> arrayConsultas = new ArrayList<>();
    ArrayList<Integer> arrayConsultasID = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        layoutUsuarios = (LinearLayout) findViewById(R.id.layoutUsuarios);
        layoutMensagens = (LinearLayout) findViewById(R.id.layoutMensagens);
        layoutAgendamento = (LinearLayout) findViewById(R.id.layoutAgendas);

        listaMsgs = (ListView) findViewById(R.id.listMensagens);
        listaAgendas = (ListView) findViewById(R.id.listAgenda);
        listaUsuarios = (ListView) findViewById(R.id.listUsers);
        banco = new BancoDados(this);
        db = banco.getWritableDatabase();

        idAdmin = getIntent().getIntExtra("ID_USUARIO", -1);
        nomeAdmin = getIntent().getStringExtra("NOME_USUARIO");

        btnUsuarios = (Button) findViewById(R.id.btnUsuarios);
        btnMsgs = (Button) findViewById(R.id.btnMsgs);
        btnAgend = (Button) findViewById(R.id.btnAgend);
        btnLogout = (ImageButton) findViewById(R.id.btnLogout);
        btnUsuarios.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#045a9b")));

        {
            carregarUsuarios();
            carregarMensagens();
            carregarConsultas();
        }

        btnUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutUsuarios.setVisibility(View.VISIBLE);
                layoutAgendamento.setVisibility(View.GONE);
                layoutMensagens.setVisibility(View.GONE);
                btnAgend.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0576d2")));
                btnMsgs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0576d2")));
                btnUsuarios.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#045a9b")));
            }
        });

        btnAgend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutUsuarios.setVisibility(View.GONE);
                layoutAgendamento.setVisibility(View.VISIBLE);
                layoutMensagens.setVisibility(View.GONE);
                btnAgend.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#045a9b")));
                btnMsgs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0576d2")));
                btnUsuarios.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0576d2")));
            }
        });

        btnMsgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutUsuarios.setVisibility(View.GONE);
                layoutAgendamento.setVisibility(View.GONE);
                layoutMensagens.setVisibility(View.VISIBLE);
                btnAgend.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0576d2")));
                btnMsgs.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#045a9b")));
                btnUsuarios.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0576d2")));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                builder.setTitle("Logout");
                builder.setMessage("Deseja realmente sair?");

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent i = new Intent(AdminActivity.this, MainActivity.class);
                        AdminActivity.this.startActivity(i);
                        finish();

                    }
                });

                builder.setNegativeButton("Não", null);
                builder.show();
            }
        });
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

//            String texto = "\nID: " + idUsuario +
//                    "\n👤 Usuário: " + nomeUsuario +
//                    "\n📧 Email: " + emailUsuario +
//                    "\n🔑 Senha: " + senhaUsuario +
//                    "\n🩺 Tipo: " + tipoUsuario;
//            arrayUsuarios.add(texto);

            String dados = idUsuario + ";" + nomeUsuario + ";" + emailUsuario + ";" + senhaUsuario + ";" + tipoUsuario;
            arrayUsuarios.add(dados);
        }

        c.close();

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayUsuarios);
//        listaUsuarios.setAdapter(adapter);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_usuarios, R.id.tvNomeUsuario, arrayUsuarios) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    view = inflater.inflate(R.layout.list_item_usuarios, parent, false);
                }

                String[] dados = getItem(position).split(";", -1);

                ((TextView) view.findViewById(R.id.tvIdUsuario)).setText("🆔 ID: " + dados[0]);
                ((TextView) view.findViewById(R.id.tvNomeUsuario)).setText("👤 Usuário: " + dados[1]);
                ((TextView) view.findViewById(R.id.tvEmailUsuario)).setText("📧 Email: " + dados[2]);
                ((TextView) view.findViewById(R.id.tvSenhaUsuario)).setText("🔑 Senha: " + dados[3]);
                ((TextView) view.findViewById(R.id.tvTipoUsuario)).setText("🩺 Tipo: " + dados[4]);

                return view;
            }
        };
        listaUsuarios.setAdapter(adapter);


        //AlertDialog.Builder dialogo = new AlertDialog.Builder(this);

        listaUsuarios.setOnItemClickListener((parent, view, position, id) -> {
            telaEditarUsuario(position);
        });
    }

    private void carregarMensagens() {
        arrayMensagens.clear();
        Cursor c = db.rawQuery(
                " SELECT M.ID_Mensagem, P.NomeCompleto, U.NomeCompleto, M.MensagemMedico, M.MensagemPaciente" +
                        " FROM Mensagens M" +
                        " INNER JOIN Usuarios P ON M.ID_Paciente = P.ID_Usuario" +
                        " INNER JOIN Usuarios U ON M.ID_Medico = U.ID_Usuario", null);

        while (c.moveToNext()) {
            String idMensagem = c.getString(0);
            String nomePaciente = c.getString(1);
            String nomeMedico = c.getString(2);
            String msgMedico = c.getString(3);
            String msgPaciente = c.getString(4);

//            String texto =
//                    "\n🧑 " + nomePaciente + ":\n" + (msgPaciente != null ? msgPaciente : "—") +
//                    "\n👨‍⚕️ " + nomeMedico + ":\n" + (msgMedico != null ? msgMedico : "—");


            // Usa "—" caso a mensagem seja nula
            if (msgPaciente == null || msgPaciente.isEmpty())
                msgPaciente = "—";
            if (msgMedico == null || msgMedico.isEmpty())
                msgMedico = "(Não Respondida)";

            //arrayMensagens.add(texto);
            arrayMensagens.add(idMensagem + "|" + nomePaciente + "|" + msgPaciente + "|" + nomeMedico + "|" + msgMedico);
        }

        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_mensagem, arrayMensagens) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.list_item_mensagem, parent, false);
                }

                // Pega os componentes do layout
                TextView tvNomePaciente = convertView.findViewById(R.id.tvNomePaciente);
                TextView tvMensagemPaciente = convertView.findViewById(R.id.tvMensagemPaciente);
                TextView tvNomeMedico = convertView.findViewById(R.id.tvNomeMedico);
                TextView tvMensagemMedico = convertView.findViewById(R.id.tvMensagemMedico);
                TextView tvStatusMensagem = convertView.findViewById(R.id.tvStatusMensagem);

                // Divide os dados armazenados
                String[] partes = getItem(position).split("\\|");

                String idMensagem = partes[0];
                String nomePaciente = partes[1];
                String msgPaciente = partes[2];
                String nomeMedico = partes[3];
                String msgMedico = partes[4];

                // Define o conteúdo dos TextViews
                tvNomePaciente.setText("🧑 Paciente: " + nomePaciente);
                tvMensagemPaciente.setText(msgPaciente);
                tvNomeMedico.setText("👨‍⚕️ Médico: " + nomeMedico);
                tvMensagemMedico.setText(msgMedico);
                tvStatusMensagem.setText("📅 ID Mensagem: " + idMensagem);

                return convertView;
            }
        };

       // ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayMensagens);
        listaMsgs.setAdapter(adapter);
    }

    private void carregarConsultas() {
        arrayConsultas.clear();
        arrayConsultasID.clear();
        Cursor c = db.rawQuery(
                "SELECT C.ID_Consulta, P.NomeCompleto, M.NomeCompleto, " +
                        "C.DataConsulta, C.Descricao, C.Local, C.Status " +
                        "FROM Consultas C " +
                        "INNER JOIN Usuarios P ON C.ID_Paciente = P.ID_Usuario " +
                        "INNER JOIN Usuarios M ON C.ID_Medico = M.ID_Usuario", null);;

        while (c.moveToNext()) {
            int idConsulta = c.getInt(0);
            String nomePaciente = c.getString(1);
            String nomeMedico = c.getString(2);
            String dataConsulta = c.getString(3);
            String descricao = c.getString(4);
            String local = c.getString(5);
            String status = c.getString(6);

            arrayConsultasID.add(idConsulta);

            String texto = idConsulta + ";" +
                    nomePaciente + ";" +
                    nomeMedico + ";" +
                    dataConsulta + ";" +
                    (descricao != null ? descricao : "—") + ";" +
                    (local != null ? local : "—");

//            String texto =
//                    "\n📅 Data: " + dataConsulta +
//                            "\n🧑 Paciente: " + nomePaciente +
//                            "\n👨‍⚕️ Médico: " + nomeMedico +
//                            "\n📍 Local: " + (local != null ? local : "—") +
//                            "\n📝 Descrição: " + (descricao != null ? descricao : "—") +
//                            "\n⚙️ Status: " + (status != null ? status : "—");

            arrayConsultas.add(texto);
        }


        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_consulta, R.id.tvPaciente, arrayConsultas) {
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

       // ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_view, arrayConsultas);
        listaAgendas.setAdapter(adapter);


        listaAgendas.setOnItemClickListener((parent, view, position, id) -> {
            //Toast.makeText(this, "Consulta selecionada: " + listaAgendamento.get(position), Toast.LENGTH_SHORT).show();

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Cancelar Consulta")
                    .setMessage("Deseja realmente cancelar esta consulta?")
                    .setPositiveButton("Sim", (dialogInterface, i) -> {
                        int idConsulta = arrayConsultasID.get(position);

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
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        });
    }

    private void telaEditarUsuario(int id)
    {
        Cursor c = db.rawQuery(
                "SELECT * FROM Usuarios", null);
        c.moveToPosition(id);

        int idUsuario = c.getInt(0);
        String nomeUsuario = c.getString(1);
        String emailUsuario = c.getString(2);
        String senhaUsuario = c.getString(3);
        String tipoUsuario = c.getString(4);
        c.close();

        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Editar Usuário");

        LinearLayout editLayout = new LinearLayout(this);
        editLayout.setOrientation(LinearLayout.VERTICAL);

//        TextView textNome = new TextView(this);
//        textNome.setText("Nome:");
//        editLayout.addView(textNome);

        EditText editNome = new EditText(this);
        editNome.setHint("Nome");
        editNome.setText(nomeUsuario);
        editLayout.addView(editNome);

        EditText editEmail = new EditText(this);
        editEmail.setText(emailUsuario);
        editLayout.addView(editEmail);

        EditText editSenha = new EditText(this);
        editSenha.setText(senhaUsuario);
        editLayout.addView(editSenha);

        Spinner spinnerTipo = new Spinner(this);
        spinnerTipo.setPrompt("Tipo");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tipo_privado, android.R.layout.simple_spinner_item);
        spinnerTipo.setAdapter(adapter);

        if (tipoUsuario.equals("M"))
            spinnerTipo.setSelection(0);
        else if (tipoUsuario.equals("P"))
            spinnerTipo.setSelection(1);
        else if (tipoUsuario.equals("A"))
            spinnerTipo.setSelection(2);

        editLayout.addView(spinnerTipo);
        dialogo.setView(editLayout);


        dialogo.setPositiveButton("Salvar", (dialog, which) -> {

            int posTipo = spinnerTipo.getSelectedItemPosition();

            String newTipo = "";
            if (posTipo == 0)
                newTipo = "M";
            else if (posTipo == 1)
                newTipo = "P";
            else if (posTipo == 2)
                newTipo = "A";

            if (!newTipo.isEmpty()) {
                ClasseUsuario user = new ClasseUsuario(idUsuario,
                        editNome.getText().toString().trim(),
                        editEmail.getText().toString().trim(),
                        editSenha.getText().toString().trim(),
                        newTipo);

                ClasseUsuarioDAO dao = new ClasseUsuarioDAO();
                try {
                    dao.updateUsuario(db, user);
                    carregarUsuarios();

                    Toast.makeText(this, "Usuário atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Erro ao atualizar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogo.setNegativeButton("Cancelar", (dialog, which) -> {

        });

        dialogo.setNeutralButton("Excluir", (dialog, which) -> {
            ClasseUsuarioDAO dao = new ClasseUsuarioDAO();

            if (tipoUsuario.equals("A")) {
                Toast.makeText(this, "Você não pode excluir um administrador!", Toast.LENGTH_SHORT).show();
                return;
            }



                AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);
                dialog2.setMessage("Voce realmente deseja deletar esse usuario?");

                dialog2.setNegativeButton("Não", null);

                dialog2.setPositiveButton("Sim", (dialogInterface, i) -> {

                    try {
                        dao.deleteUsuario(db, idUsuario);
                        carregarUsuarios();
                        Toast.makeText(this, "Usuário deletado com sucesso!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Usuario há vinculos no sistema", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog2.show();

        });

        dialogo.show();

    }

}
