package com.studio.orzanizze.Config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    //static pq devemos ter um unico autenicador e em todas as intancias do objeto ela devera ser a msm
    private static FirebaseAuth autenticacao;
    private static DatabaseReference firebase;

    //retorna a instancia do firebaseDatabase
    public static DatabaseReference getFirebaseDB() {
        if (firebase == null) {
            firebase = FirebaseDatabase.getInstance().getReference();
        }
        return firebase;
    }

    //retorna a instancia do firebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao() {
        if (autenticacao == null) {
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }
}
