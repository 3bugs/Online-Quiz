package com.promlert.onlinequiz;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Promlert on 5/18/2016.
 */
public class Utils {

    public static String getSha256Hash(String password) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            String salt = "some_random_salt";
            String passWithSalt = password + salt;
            byte[] passBytes = passWithSalt.getBytes();
            byte[] passHashBytes = sha256.digest(passBytes);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < passHashBytes.length; i++) {
                sb.append(Integer.toString((passHashBytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMd5Hash(String password) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] passBytes = password.getBytes("UTF-8");
            byte[] passHashBytes = md5.digest(passBytes);
            return new String(passHashBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
