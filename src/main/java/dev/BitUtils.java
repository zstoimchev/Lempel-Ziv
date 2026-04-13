package dev;

public final class BitUtils {

    private BitUtils() {
    }

    public static String toBitString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";

        StringBuilder sb = new StringBuilder(bytes.length * 8);
        for (byte b : bytes) {
            String bits = Integer.toBinaryString(b & 0xFF);
            sb.append("0".repeat(8 - bits.length()));
            sb.append(bits);
        }
        return sb.toString();
    }
}
