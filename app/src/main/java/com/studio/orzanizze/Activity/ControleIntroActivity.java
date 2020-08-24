package com.studio.orzanizze.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.studio.orzanizze.Config.ConfiguracaoFirebase;
import com.studio.orzanizze.Fragments.intro_1;
import com.studio.orzanizze.Fragments.intro_2;
import com.studio.orzanizze.Fragments.intro_3;
import com.studio.orzanizze.Fragments.intro_4;
import com.studio.orzanizze.Fragments.intro_cadastro;
import com.studio.orzanizze.R;


public class ControleIntroActivity extends AppCompatActivity {

    private SmartTabLayout smartTabLayout;
    private ViewPager viewPager;
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smartTabLayout = findViewById(R.id.viewPagerTab);
        viewPager = findViewById(R.id.viewPager);

        //settings adapter for tabs
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("", intro_1.class)
                        .add("", intro_2.class)
                        .add("", intro_3.class)
                        .add("", intro_4.class)
                        .add("", intro_cadastro.class)
                        .create()
        );
        viewPager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewPager);
    }


    public void btEntrar(View view) {
        verificarUsuarioLogado();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void btCadastrar(View view) {
        startActivity(new Intent(this, CadastroActivity.class));
    }

    public void verificarUsuarioLogado() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut();
        if (autenticacao.getCurrentUser() != null) {
            abrirTelaprincipal();
        }
    }

    public void abrirTelaprincipal() {
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}