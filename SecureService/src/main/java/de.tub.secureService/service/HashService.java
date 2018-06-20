package de.tub.secureService.service;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashService {
    public static String createObjectName() {
        MessageDigest messageDigest = null;
        int salt = ((int) Math.random()) * 9999999;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(Long.toBinaryString(System.currentTimeMillis() +salt).getBytes());
            byte[] hashName = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashName) {
                sb.append(String.format("%02X", b));
            }
            String objectName = sb.toString().toLowerCase();
            return objectName;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
