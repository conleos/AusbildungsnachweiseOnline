package com.conleos.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

public class PasswordHasher {

    private static final Logger logger = LoggerFactory.getLogger(PasswordHasher.class);

    private static final String FIXED_SALT = "df1f2d3f4d77ac66e9c5a6c3d8f921b6";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA1";

    private PasswordHasher() {

    }

    /*
    * Hash a password using PBKDF2
    * */
    public static String hash(String password) {
        try {
            Random random = new Random(0);
            byte[] salt = FIXED_SALT.getBytes();
            random.nextBytes(salt);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory f = SecretKeyFactory.getInstance(KEY_ALGORITHM);
            byte[] hash = f.generateSecret(spec).getEncoded();
            Base64.Encoder enc = Base64.getEncoder();

            return enc.encodeToString(hash);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

}
