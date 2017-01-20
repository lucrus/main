package com.lucrus.main.validation;

import java.math.BigInteger;

/**
 * Created by lucrus on 13/10/16.
 */
public class CodiceFiscale extends BaseValidator {

    public CodiceFiscale(Object obj) {
        super(obj);
    }

    @Override
    public boolean validate(String s) {
        if (s == null || s.trim().length() == 0) return true;
        String cod = s.toUpperCase();

        return controllaCodiceFiscale(cod);
    }

    public boolean controllaCodiceFiscale(String codiceFiscale) {
        if (codiceFiscale == null)
            return false;
        if (codiceFiscale.length() < 16)
            return false;
        String check = calcolaLetteraControllo(codiceFiscale);
        String ultimaLettera = String.valueOf(codiceFiscale.charAt(15));
        if (!check.equalsIgnoreCase(ultimaLettera))
            return false;
        return true;
    }

    private String calcolaLetteraControllo(String codiceFiscale) {
        boolean lFlag = true;
        int totContr = 0;
        final String searchString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        codiceFiscale = codiceFiscale.trim();
        for (int i = 0; i < 15; i++) {
            char check = codiceFiscale.charAt(i);
            if (lFlag)
                totContr = totContr + convertiCarattereDispari(check);
            else {
                int checkValue;
                try {
                    checkValue = Integer.parseInt(String.valueOf(check));
                } catch (NumberFormatException nf) {
                    checkValue = searchString.indexOf(check);
                }
                totContr = totContr + checkValue;
            }
            lFlag = !lFlag;
        }
        BigInteger tot = new BigInteger("" + totContr);
        tot = tot.mod(new BigInteger("26"));

        char ck = searchString.charAt(tot.intValue());
        return String.valueOf(ck);
    }

    private int convertiCarattereDispari(char carattere) {
        int carConv = -1;
        switch (carattere) {
            case 'A':
            case '0':
                carConv = 1;
                break;
            case 'B':
            case '1':
                carConv = 0;
                break;
            case 'C':
            case '2':
                carConv = 5;
                break;
            case 'D':
            case '3':
                carConv = 7;
                break;
            case 'E':
            case '4':
                carConv = 9;
                break;
            case 'F':
            case '5':
                carConv = 13;
                break;
            case 'G':
            case '6':
                carConv = 15;
                break;
            case 'H':
            case '7':
                carConv = 17;
                break;
            case 'I':
            case '8':
                carConv = 19;
                break;
            case 'J':
            case '9':
                carConv = 21;
                break;
            case 'K':
                carConv = 2;
                break;
            case 'L':
                carConv = 4;
                break;
            case 'M':
                carConv = 18;
                break;
            case 'N':
                carConv = 20;
                break;
            case 'O':
                carConv = 11;
                break;
            case 'P':
                carConv = 3;
                break;
            case 'Q':
                carConv = 6;
                break;
            case 'R':
                carConv = 8;
                break;
            case 'S':
                carConv = 12;
                break;
            case 'T':
                carConv = 14;
                break;
            case 'U':
                carConv = 16;
                break;
            case 'V':
                carConv = 10;
                break;
            case 'W':
                carConv = 22;
                break;
            case 'X':
                carConv = 25;
                break;
            case 'Y':
                carConv = 24;
                break;
            case 'Z':
                carConv = 23;
                break;
        }
        return carConv;
    }
}
