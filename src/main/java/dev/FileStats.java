package dev;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileStats {
    private final Path sourceFile;
    private final long originalBytes;
    private final long compressedBytes;
    private final int tokenCount;
    private final long compressionTimeMs;

    public FileStats(Path sourceFile, long originalBytes, long compressedBytes, int tokenCount, long compressionTimeMs) {
        this.sourceFile = sourceFile;
        this.originalBytes = originalBytes;
        this.compressedBytes = compressedBytes;
        this.tokenCount = tokenCount;
        this.compressionTimeMs = compressionTimeMs;
    }

    public long getOriginalBytes() {
        return originalBytes;
    }

    public long getCompressedBytes() {
        return compressedBytes;
    }

    public int getTokenCount() {
        return tokenCount;
    }

    public long getCompressionTimeMs() {
        return compressionTimeMs;
    }

    public double ratio() {
        return originalBytes == 0 ? 0 : (double) compressedBytes / originalBytes;
    }

    public double spaceSavedPercent() {
        return (1.0 - ratio()) * 100.0;
    }

    public Map<String, Long> comparableSizes() {
        Map<String, Long> sizes = new LinkedHashMap<>();

        String baseName = baseNameOf(sourceFile.getFileName().toString());
        Path dir = sourceFile.toAbsolutePath().getParent();
        if (dir == null) return sizes;

        try (var stream = Files.list(dir)) {
            stream.filter(Files::isRegularFile).filter(p -> baseNameOf(p.getFileName().toString()).equalsIgnoreCase(baseName)).sorted().forEach(p -> {
                try {
                    String ext = extensionOf(p.getFileName().toString());
                    sizes.put(ext.toUpperCase(), Files.size(p));
                } catch (IOException ignored) {
                }
            });
        } catch (IOException e) {
            // directory scan failed — return whatever we have
        }

        return sizes;
    }

    public void printReport() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║           LZ78 Compression Report                ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.printf("║  Source file   : %-31s║%n", sourceFile.getFileName());
        System.out.printf("║  Original size : %-31s║%n", formatBytes(originalBytes));
        System.out.printf("║  Compressed    : %-31s║%n", formatBytes(compressedBytes));
        System.out.printf("║  Ratio         : %-31s║%n", String.format("%.4f (%.2f%% saved)", ratio(), spaceSavedPercent()));
        System.out.printf("║  Tokens        : %-31s║%n", String.format("%,d", tokenCount));
        System.out.printf("║  Time          : %-31s║%n", compressionTimeMs + " ms");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  Format comparison (same directory):             ║");

        Map<String, Long> comparables = comparableSizes();
        if (comparables.isEmpty()) {
            System.out.println("║  (no other formats found)                        ║");
        } else {
            for (Map.Entry<String, Long> entry : comparables.entrySet()) {
                String line = String.format("  %-8s %s", entry.getKey(), formatBytes(entry.getValue()));
                System.out.printf("║  %-47s║%n", line);
            }
        }

        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private static String baseNameOf(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(0, dot) : filename;
    }

    private static String extensionOf(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1) : "";
    }
}
