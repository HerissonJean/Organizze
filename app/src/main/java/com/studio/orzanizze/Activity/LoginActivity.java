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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.studio.orzanizze.Config.ConfiguracaoFirebase;
import com.studio.orzanizze.Model.Usuario;
import com.studio.orzanizze.R;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail, editSenha;
    Button btEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.tb_login_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Login");

        editEmail = findViewById(R.id.edit_login_email);
        editSenha = findViewById(R.id.edit_login_senha);
        btEntrar = findViewById(R.id.bt_login_entrar);

        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();

                if (!email.isEmpty()) {
                    if (!senha.isEmpty()) {

                        usuario = new Usuario();
                        usuario.setEmail(email);
                        usuario.setSenha(senha);

                        validarLogin();

                    } else {
                        Toast.makeText(LoginActivity.this, " Preencha a senha! ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, " Preencha o email! ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void validarLogin() {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    abrirTelaPrincipal();
                    Toast.makeText(LoginActivity.this, "Log in", Toast.LENGTH_SHORT).show();
                } else {

                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        excecao = "Usuário não cadastrado! ";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "Senha ou email incorreto!  ";
                    } catch (FirebaseAuthUserCollisionException e) {
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado! ";
                    } catch (Exception e) {
                        excecao = "Erro ao logar, " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void abrirTelaPrincipal() {
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }

}