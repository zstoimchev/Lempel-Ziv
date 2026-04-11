package dev;

import java.util.ArrayList;
import java.util.List;

public class LZDecompressor {

    public String decompress(List<LZEntry> tokens) {
        if (tokens == null || tokens.isEmpty()) return "";

        List<String> dictionary = new ArrayList<>();
        dictionary.add("");

        for (int i = 0; i < 256; i++) dictionary.add(String.valueOf((char) i));
        StringBuilder result = new StringBuilder();

        for (LZEntry token : tokens) {
            String prefix = (token.index() < dictionary.size())
                    ? dictionary.get(token.index())
                    : "";

            String entry = (token.symbol() == '\0')
                    ? prefix
                    : prefix + token.symbol();

            result.append(entry);
            dictionary.add(entry);
        }

        return result.toString();
    }
}
