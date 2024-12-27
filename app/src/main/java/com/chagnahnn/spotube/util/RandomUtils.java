package com.chagnahnn.spotube.util;

import androidx.annotation.NonNull;

import java.util.Random;

@SuppressWarnings("unused")
public class RandomUtils {
    private static final Random RANDOM = new Random();

    /**
     * Generate a random integer within a specified range.
     *
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return Random integer between min and max
     */
    public static int getRandomInt(int min, int max) {
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    /**
     * Generate a random double within a specified range.
     *
     * @param min Minimum value (inclusive)
     * @param max Maximum value (exclusive)
     * @return Random double between min and max
     */
    public static double getRandomDouble(double min, double max) {
        return min + (max - min) * RANDOM.nextDouble();
    }

    /**
     * Generate a random character from a specified character set.
     *
     * @return Random character
     */
    public static char getRandomChar() {
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return charSet.charAt(RANDOM.nextInt(charSet.length()));
    }

    /**
     * Generate a random string of a specified length.
     *
     * @param length Length of the random string
     * @return Random string of the specified length
     */
    @NonNull
    public static String getRandomString(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(getRandomChar());
        }
        return result.toString();
    }

    /**
     * Generate a random boolean value.
     *
     * @return Random boolean (true/false)
     */
    public static boolean getRandomBoolean() {
        return RANDOM.nextBoolean();
    }

    /**
     * Generate a random element from an array.
     *
     * @param array Array to pick an element from
     * @param <T>   Type of elements in the array
     * @return Random element from the array
     */
    public static <T> T getRandomElement(@NonNull T[] array) {
        return array[RANDOM.nextInt(array.length)];
    }
}
