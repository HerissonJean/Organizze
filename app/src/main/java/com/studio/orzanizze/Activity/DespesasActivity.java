package com.studio.orzanizze.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.studio.orzanizze.Config.ConfiguracaoFirebase;
import com.studio.orzanizze.Model.Movimentacao;
import com.studio.orzanizze.Model.Usuario;
import com.studio.orzanizze.R;
import com.studio.orzanizze.Helper.Base64Custom;
import com.studio.orzanizze.Helper.DateCustom;

public class DespesasActivity extends AppCompatActivity {

    private EditText campoValor;
    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private Movimentacao movimentacao;
    private DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDB();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaTt = 0.00, despesaAtualizada;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoData = findViewById(R.id.edit_despesa_data);
        campoCategoria = findViewById(R.id.edit_despesa_categoria);
        campoValor = findViewById(R.id.edit_despesa_valor);
        campoDescricao = findViewById(R.id.edit_despesa_descricao);

        campoData.setText(DateCustom.dataAtual());
        recuperaDespesaTt();
    }

    public void salvarDespesa(View view) {

        if (validaCamposD()) {
            movimentacao = new Movimentacao();
            String data = campoData.getText().toString();
            Double valorRecuperadoD = Double.parseDouble(campoValor.getText().toString());

            movimentacao.setValor(valorRecuperadoD);
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(data);
            movimentacao.setTipo("D");

            Double despesaAtualizada = despesaTt + valorRecuperadoD;
            atulizarDespesa(despesaAtualizada);

            movimentacao.salvar(data);
            finish();
        }
    }

    public boolean validaCamposD() {

        String TextoValor = campoValor.getText().toString();
        String TextoData = campoData.getText().toString();
        String TextoCategoria = campoCategoria.getText().toString();
        String TextoDescricao = campoDescricao.getText().toString();

        if (!TextoValor.isEmpty()) {
            if (!TextoData.isEmpty()) {
                if (!TextoCategoria.isEmpty()) {
                    if (!TextoDescricao.isEmpty()) {
                        Toast.makeText(DespesasActivity.this, " SAVED ", Toast.LENGTH_LONG).show();
                        return true;
                    } else {
                        Toast.makeText(DespesasActivity.this, " Insira uma Descrição ", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    Toast.makeText(DespesasActivity.this, " Insira um Categoria ", Toast.LENGTH_LONG).show();
                    return false;
                }
            } else {
                Toast.makeText(DespesasActivity.this, " Insira uma data ", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Toast.makeText(DespesasActivity.this, " Insira um valor ", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void recuperaDespesaTt() {

        String emailUsu = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsu);
        DatabaseReference usuarioRef = firebase.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //passado a classe, ela já nos devolve convertido
                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTt = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void atulizarDespesa(Double despesa) {

        String emailUsu = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsu);
        DatabaseReference usuarioRef = firebase.child("usuarios").child(idUsuario);
        usuarioRef.child("despesaTotal").setValue(despesa);
    }
}