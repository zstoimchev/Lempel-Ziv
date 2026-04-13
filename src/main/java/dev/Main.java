package dev;

import dev.utils.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {
//        if (args.length < 2) printUsage();

//        String command = args[0].toLowerCase();
//        Path inputPath = Path.of(args[1]);
        String command = "compress".toLowerCase();
        Path inputPath = Path.of("book.pdf");

        if (!Files.exists(inputPath)) {
            Logger.critical("File not found: " + inputPath);
            System.exit(1);
        }

        switch (command) {
            case "compress" -> runCompress(inputPath);
//            case "info" -> runInfo(inputPath);
            default -> printUsage();
        }
    }

    private static void runCompress(Path path) {
        Logger.info("Reading file: " + path.toAbsolutePath());

        byte[] rawBytes;
        try {
            rawBytes = Files.readAllBytes(path);
        } catch (IOException e) {
            Logger.critical("Could not read file: " + e.getMessage());
            System.exit(1);
            return;
        }

        Logger.info("File read successfully, starting compression...");
        Logger.info("Converting " + rawBytes.length + " bytes to bit string");

        String bitString = BitUtils.toBitString(rawBytes);
        System.out.printf("[*] Bit string   : %,d characters%n", bitString.length());
        Logger.info("Bit string: " + bitString.length() + " characters");

        Logger.info("Compressing with LZ...");
        long startMs = System.currentTimeMillis();

        LZCompressor compressor = new LZCompressor();
        List<LZEntry> tokens = compressor.compress(bitString);

        long elapsedMs = System.currentTimeMillis() - startMs;

        int dictSize = 256 + tokens.size();
        long estimatedBytes = LZCompressor.estimatedCompressedBytes(tokens, dictSize);

        FileStats stats = new FileStats(path, rawBytes.length, estimatedBytes, tokens.size(), elapsedMs);
        stats.printReport();
    }

    /*
    private static void runInfo(Path path) {
        System.out.println("[*] File info for: " + path.getFileName());
        try {
            long size = Files.size(path);
            System.out.printf("[*] Size: %,d bytes (%.2f MB)%n", size, size / (1024.0 * 1024));
        } catch (IOException e) {
            System.err.println("[ERROR] " + e.getMessage());
            System.exit(1);
        }

        // dummy stats just to trigger the directory scan
        FileStats stats = new FileStats(path, 0, 0, 0, 0);
        System.out.println("[*] Other formats in the same directory:");
        stats.comparableSizes().forEach((ext, bytes) ->
                System.out.printf("    %-8s %,d bytes%n", ext, bytes)
        );
    }
*/

    private static void printUsage() {
        Logger.info("""
                  Usage:
                    java -jar lempel-ziv.jar compress <file>   Compress a file and print stats
                    java -jar lempel-ziv.jar info     <file>   Print file size comparison only
                
                  Example:
                    java -jar lempel-ziv.jar compress book.pdf
                """);
        System.exit(1);
    }
}
