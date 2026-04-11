package dev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZCompressor {

    public List<LZEntry> compress(String input) {
        if (input == null || input.isEmpty()) return List.of();

        Map<String, Integer> dictionary = new HashMap<>();
        int nextIndex = initializeDictionary(dictionary);

        List<LZEntry> output = new ArrayList<>();
        String w = "";

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            String wc = w + c;

            if (dictionary.containsKey(wc)) w = wc;
            else {
                int wIndex = w.isEmpty() ? 0 : dictionary.getOrDefault(w, 0);
                output.add(new LZEntry(wIndex, c));
                dictionary.put(wc, nextIndex++);
                w = "";
            }
        }

        if (!w.isEmpty()) {
            int wIndex = dictionary.getOrDefault(w, 0);
            output.add(new LZEntry(wIndex, '\0'));
        }

        return output;
    }

    private int initializeDictionary(Map<String, Integer> dictionary) {
        for (int i = 0; i < 256; i++) dictionary.put(String.valueOf((char) i), i + 1);
        return 257;
    }

    public static int bitsPerIndex(int maxIndex) {
        if (maxIndex <= 1) return 1;
        return (int) Math.ceil(Math.log(maxIndex) / Math.log(2));
    }

    public static long estimatedCompressedBytes(List<LZEntry> tokens, int dictSize) {
        int bitsPerToken = bitsPerIndex(dictSize) + 8; // index bits + 1 byte for char
        long totalBits = (long) tokens.size() * bitsPerToken;
        return (long) Math.ceil(totalBits / 8.0);
    }
}
