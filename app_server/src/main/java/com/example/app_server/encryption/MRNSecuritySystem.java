package com.example.app_server.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.Console;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.*;
import java.util.Base64;
import org.mindrot.jbcrypt.BCrypt;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2;

public class MRNSecuritySystem {
    private static final SecureRandom random = new SecureRandom();

    public static String genPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    public static Map<String, Map<String, String>> askPasswords(Scanner scanner, Console console) {
        System.out.print("Manual passwords for stakeholders? (y/n): ");
        boolean manual = scanner.nextLine().trim().equalsIgnoreCase("y");

        String[] roles = {"doctor", "dietician", "physio"};
        Map<String, Map<String, String>> store = new HashMap<>();

        for (String role : roles) {
            String pwd;
            if (manual) {
                pwd = new String(console.readPassword("Password for " + role + ": "));
            } else {
                pwd = genPassword(16);
                System.out.println("Auto " + role + " pwd: " + pwd);
            }
            Map<String, String> info = new HashMap<>();
            info.put("pwd", pwd);
            store.put(role, info);
        }
        return store;
    }

    public static byte[] deriveKey(String password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 250000, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        return factory.generateSecret(spec).getEncoded();
    }

    public static String encrypt(String plaintext, String password) throws Exception {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        byte[] key = deriveKey(password, salt);
        byte[] nonce = new byte[12];
        random.nextBytes(nonce);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec spec = new GCMParameterSpec(128, nonce);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);

        byte[] ct = cipher.doFinal(plaintext.getBytes());
        byte[] all = new byte[salt.length + nonce.length + ct.length];
        System.arraycopy(salt, 0, all, 0, salt.length);
        System.arraycopy(nonce, 0, all, salt.length, nonce.length);
        System.arraycopy(ct, 0, all, salt.length + nonce.length, ct.length);
        return Base64.getUrlEncoder().encodeToString(all);
    }

    public static String decrypt(String encoded, String password) throws Exception {
        byte[] data = Base64.getUrlDecoder().decode(encoded);
        byte[] salt = Arrays.copyOfRange(data, 0, 16);
        byte[] nonce = Arrays.copyOfRange(data, 16, 28);
        byte[] ct = Arrays.copyOfRange(data, 28, data.length);

        byte[] key = deriveKey(password, salt);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec spec = new GCMParameterSpec(128, nonce);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);

        return new String(cipher.doFinal(ct));
    }

    public static String hashMRN_Bcrypt(String mrn) {
        return BCrypt.hashpw(mrn, BCrypt.gensalt());
    }

    public static boolean verifyBcrypt(String hashed, String plain) {
        return BCrypt.checkpw(plain, hashed);
    }

    public static String hashMRN_Argon2(String mrn) {
        Argon2 argon2 = Argon2Factory.create();
        return argon2.hash(2, 65536, 1, mrn.toCharArray());
    }

    public static boolean verifyArgon2(String hashed, String plain) {
        Argon2 argon2 = Argon2Factory.create();
        return argon2.verify(hashed, plain.toCharArray());
    }

    public static void initialMRNDisplay(Scanner scanner, Console console) throws Exception {
        System.out.println("-- Initial MRN Setup --");
        System.out.print("Manual MRN input? (y/n): ");
        boolean manual = scanner.nextLine().trim().equalsIgnoreCase("y");

        String mrn = manual ? scanner.nextLine().trim() : "DNL20250101";
        System.out.println("MRN: " + mrn);

        String encPwd = new String(console.readPassword("Enter password to encrypt the MRN: "));
        String encrypted = encrypt(mrn, encPwd);
        String hashed = hashMRN_Bcrypt(mrn);

        System.out.println("\n[SECURE MRN RECORD]");
        System.out.println("Encrypted MRN (AES-GCM): " + encrypted);
        System.out.println("Hashed MRN (bcrypt): " + hashed);
        System.out.println("-------------------------------------\n");
    }

    public static void mrnRegistration(Scanner scanner, Console console) throws Exception {
        System.out.print("Manual MRN entry? (y/n): ");
        boolean manual = scanner.nextLine().trim().equalsIgnoreCase("y");
        String mrn = manual ? scanner.nextLine().trim() : "DNL0012435";
        System.out.println("MRN: " + mrn);

        String pwd1 = new String(console.readPassword("Enter 1st encryption password: "));
        String pwd2 = new String(console.readPassword("Enter 2nd encryption password: "));

        byte[] salt = new byte[16];
        random.nextBytes(salt);

        byte[] key1 = deriveKey(pwd1, salt);
        byte[] key2 = deriveKey(pwd2, salt);

        byte[] combined = new byte[32];
        System.arraycopy(key1, 0, combined, 0, 16);
        System.arraycopy(key2, 0, combined, 16, 16);

        byte[] nonce = new byte[12];
        random.nextBytes(nonce);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(combined, "AES");
        GCMParameterSpec spec = new GCMParameterSpec(128, nonce);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
        byte[] ct = cipher.doFinal(mrn.getBytes());

        byte[] full = new byte[salt.length + nonce.length + ct.length];
        System.arraycopy(salt, 0, full, 0, salt.length);
        System.arraycopy(nonce, 0, full, salt.length, nonce.length);
        System.arraycopy(ct, 0, full, salt.length + nonce.length, ct.length);

        String encrypted = Base64.getUrlEncoder().encodeToString(full);
        String h1 = hashMRN_Bcrypt(mrn);
        String h2 = hashMRN_Argon2(mrn);

        System.out.println("\n-- MRN Registration --");
        System.out.println("Encrypted MRN (2-key AES-GCM): " + encrypted);
        System.out.println("Hashed MRN (bcrypt): " + h1);
        System.out.println("Hashed MRN (argon2): " + h2);
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        Console console = System.console();

        if (console == null) {
            throw new IllegalStateException("Please run in a terminal/console to securely enter passwords.");
        }

        initialMRNDisplay(scanner, console);
        Map<String, Map<String, String>> ks = askPasswords(scanner, console);
        mrnRegistration(scanner, console);

        // Additional role assignment & verification steps can be added here similarly.
    }
}