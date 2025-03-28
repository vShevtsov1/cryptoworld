package com.harbourtech.cryptoworld.util;


import java.util.Random;
import java.util.UUID;

public class UUIDGenerator {


    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }


    public static String generateShortUUID() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateRandom6Digits() {
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000);
        return String.valueOf(randomNumber);
    }


}
