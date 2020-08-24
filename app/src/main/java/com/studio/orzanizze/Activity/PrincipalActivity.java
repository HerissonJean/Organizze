package com.studio.orzanizze.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.studio.orzanizze.Adapter.AdapterMovimentacao;
import com.studio.orzanizze.Config.ConfiguracaoFirebase;
import com.studio.orzanizze.Model.Movimentacao;
import com.studio.orzanizze.Model.Usuario;
import com.studio.orzanizze.R;
import com.studio.orzanizze.Helper.Base64Custom;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView txtSaudacao, txtSaldo;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDB();
    private DatabaseReference usuarioRef;
    private Double despesaTt = 0.00;
    private Double receitaTt = 0.00;
    private Double resumoTt = 0.00;
    private ValueEventListener valueEventListenerUsuario;
    private ValueEventListener valueEventListenerMoviementacoes;
    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private Movimentacao movimentacao;
    private DatabaseReference movimentacaoRef = ConfiguracaoFirebase.getFirebaseDB();
    private String mesAno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.tb_principal_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" Organizze");

        txtSaldo = findViewById(R.id.text_Contp_saldo);
        txtSaudacao = findViewById(R.id.textV_ContP_saudacao);
        calendarView = findViewById(R.id.calen_Contp_calendarView);
        recyclerView = findViewById(R.id.rec_Contp_movimentacoes);
        configuraCalendarView();
        swipe();

        //configurando adapter
        adapterMovimentacao = new AdapterMovimentacao(movimentacoes, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMovimentacao);
    }

    public void swipe() {
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                //deixa inativo o mtd de clicar e arrastar (IDLE)
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                // movimento do swipe
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirMovimentacao(viewHolder);
            }
        };

        //anexao ao recycler
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    public void excluirMovimentacao(final RecyclerView.ViewHolder viewHolder) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Excluir movimentação da conta");
        alertDialog.setMessage("Você tem certeza que deseja excluir movimentação ?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int position = viewHolder.getAdapterPosition();
                movimentacao = movimentacoes.get(position);
                String emailUsu = autenticacao.getCurrentUser().getEmail();
                String idUsuario = Base64Custom.codificarBase64(emailUsu);
                movimentacaoRef = firebase.child("movimentacao")
                        .child(idUsuario)
                        .child(mesAno);
                movimentacaoRef.child(movimentacao.getKey()).removeValue();
                adapterMovimentacao.notifyItemRemoved(position);
                atualizarSaldo();

            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(PrincipalActivity.this, "Cancelado", Toast.LENGTH_LONG).show();
                adapterMovimentacao.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();


    }

    public void atualizarSaldo() {

        String emailUsu = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsu);
        usuarioRef = firebase.child("usuarios")
                .child(idUsuario);

        if (movimentacao.getTipo().equals("R")) {
            receitaTt = receitaTt - movimentacao.getValor();
            usuarioRef.child("receitaTotal").setValue(receitaTt);
        }
        if (movimentacao.getTipo().equals("D")) {
            despesaTt = despesaTt - movimentacao.getValor();
            usuarioRef.child("despesaTotal").setValue(despesaTt);
        }
    }

    public void recuperarMovimentacoes() {

        String emailUsu = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsu);

        movimentacaoRef = firebase.child("movimentacao")
                .child(idUsuario)
                .child(mesAno);

        valueEventListenerMoviementacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movimentacoes.clear();
                for (DataSnapshot dados : snapshot.getChildren()) {

                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    movimentacao.setKey(dados.getKey());
                    movimentacoes.add(movimentacao);
                }
                adapterMovimentacao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void recuperaResumo() {

        String emailUsu = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsu);
        usuarioRef = firebase.child("usuarios").child(idUsuario);

        valueEventListenerUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
            //carrega e converte para classe Usuario
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario0 = snapshot.getValue(Usuario.class);
                despesaTt = (Double) usuario0.getDespesaTotal();
                receitaTt = (Double) usuario0.getReceitaTotal();
                resumoTt = receitaTt - despesaTt;

                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String resultadoFormatado = decimalFormat.format(resumoTt);

                txtSaudacao.setText("Olá, " + usuario0.getNome());
                txtSaldo.setText("R$ " + resultadoFormatado);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //cria o menu de opções e seus itens
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //usado para tratar eventos de menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSair:
                autenticacao.signOut();
                startActivity(new Intent(this, ControleIntroActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configuraCalendarView() {

        CalendarDay data = calendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d", data.getMonth() + 1);
        mesAno = String.valueOf(mesSelecionado + "" + data.getYear());

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", date.getMonth() + 1);
                mesAno = String.valueOf(mesSelecionado + "" + date.getYear());

                movimentacaoRef.removeEventListener(valueEventListenerMoviementacoes);
                recuperarMovimentacoes();
            }
        });
    }

    public void adicionarDespesa(View view) {
        startActivity(new Intent(this, DespesasActivity.class));
    }

    public void adicionarReceita(View view) {
        startActivity(new Intent(this, ReceitaActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperaResumo();
        recuperarMovimentacoes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(valueEventListenerUsuario);
        movimentacaoRef.removeEventListener(valueEventListenerMoviementacoes);
    }
}