package com.mohamed.lawyer.utils;

public class ArabicNormalizer {

    public static String normalize(String input) {
        if (input == null) return null;

        return input
                // Normalize Alif variants
                .replace("أ", "ا")
                .replace("إ", "ا")
                .replace("آ", "ا")

                .replace("ى", "ي")
                .replace("ي", "ي")
                .replace("ئ", "ي")
                .replace("ة", "ه")
                .replace("ؤ", "و")
                .replace("ئ", "ي")
                .replaceAll("[ًٌٍَُِّْ]", "")
                .replace("ـ", "")
                .replace("٠", "0")
                .replace("١", "1")
                .replace("٢", "2")
                .replace("٣", "3")
                .replace("٤", "4")
                .replace("٥", "5")
                .replace("٦", "6")
                .replace("٧", "7")
                .replace("٨", "8")
                .replace("٩", "9");
    }
}
