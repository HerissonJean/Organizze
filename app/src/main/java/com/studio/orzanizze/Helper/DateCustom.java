package com.studio.orzanizze.Helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String dataAtual() {
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String data = simpleDateFormat.format(date);
        return data;
    }

    public static String mesAnoDataEscolhida(String data) {
        //quebra a string no char selecionado e armazena em arrays
        String dataConver[] = data.split("/");
        String dia = dataConver[0];
        String mes = dataConver[1];
        String ano = dataConver[2];
        String mesAno = mes + ano;
        return mesAno;
    }

}
