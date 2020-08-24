package com.studio.orzanizze.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.studio.orzanizze.Model.Usuario;
import com.studio.orzanizze.R;
import com.studio.orzanizze.Config.ConfiguracaoFirebase;
import com.studio.orzanizze.Helper.Base64Custom;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button btCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        Toolbar toolbar = findViewById(R.id.tb_cadastro_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Cadastro");

        campoNome = findViewById(R.id.edit_cadastro_Nome);
        campoEmail = findViewById(R.id.edit_cadastro_Email);
        campoSenha = findViewById(R.id.edit_cadastro_Senha);
        btCadastrar = findViewById(R.id.btCadastrar);

        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                //verificado se os campos estão preenchidos
                if (!textoNome.isEmpty()) {
                    if (!textoEmail.isEmpty()) {
                        if (!textoSenha.isEmpty()) {

                            usuario = new Usuario();
                            usuario.setNome(textoNome);
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);
                            usuario.setDespesaTotal(0.00);
                            usuario.setReceitaTotal(0.00);

                            cadastrarUsuario();
                        } else {
                            Toast.makeText(CadastroActivity.this, " Preencha a Senha! ", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(CadastroActivity.this, " Preencha o E-mail! ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(CadastroActivity.this, " Preencha o Nome! ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void cadastrarUsuario() {
        //recupera o objeto que permite autenticar o usuario,
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            //task verifica se deu certo o cadastro
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    //converte email unico, em base64 no usuario
                    String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();
                    Toast.makeText(CadastroActivity.this, "Log in", Toast.LENGTH_SHORT).show();
                    abrirTelaprincipal();
                } else {
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        excecao = "Insira uma senha mais Forte! ";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "! ";
                    } catch (FirebaseAuthUserCollisionException e) {
                        excecao = "E-mail já cadastrado! ";
                    } catch (Exception e) {
                        excecao = "Erro ao cadastrar usuário, " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void abrirTelaprincipal() {
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}