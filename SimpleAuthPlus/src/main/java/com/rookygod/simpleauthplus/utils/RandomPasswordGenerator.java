package com.rookygod.simpleauthplus.utils;

import java.util.Random;

/**
 * Utility class for generating random passwords
 */
public class RandomPasswordGenerator {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final Random RANDOM = new Random();

    /**
     * Generate a random password
     *
     * @param length Length of the password
     * @return The generated password
     */
    public static String generatePassword(int length) {
        StringBuilder password = new StringBuilder();
        String chars = LOWERCASE + UPPERCASE + DIGITS;
        
        // Ensure at least one character from each category
        password.append(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
        password.append(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        
        // Fill the rest of the password
        for (int i = 3; i < length; i++) {
            password.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        
        // Shuffle the password
        char[] passwordArray = password.toString().toCharArray();
        for (int i = 0; i < passwordArray.length; i++) {
            int j = RANDOM.nextInt(passwordArray.length);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }
}

