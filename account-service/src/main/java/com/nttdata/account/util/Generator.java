package com.nttdata.account.util;

import java.util.Random;

public class Generator {
    private static Random random = new Random(System.currentTimeMillis());

    public static String generateBankAccountNumber(String accountType) {
        String type = accountType.equals("CREDIT")      ? "192" :
                      accountType.equals("CREDIT_CARD") ? "193" : "191";

        StringBuilder b = new StringBuilder(type);
        for (int i = 0; i <= 14; i++) {
            int number = random.nextInt(10);
            b.append(number);
        }
        return b.toString();
    }
}
