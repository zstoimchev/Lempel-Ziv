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

    public static byte[] toByteArray(String bits) {
        if (bits == null || bits.isEmpty()) return new byte[0];
        if (bits.length() % 8 != 0) {
            throw new IllegalArgumentException("Bit string length must be a multiple of 8, got: " + bits.length());
        }

        byte[] result = new byte[bits.length() / 8];
        for (int i = 0; i < result.length; i++) {
            String byteStr = bits.substring(i * 8, i * 8 + 8);
            result[i] = (byte) Integer.parseInt(byteStr, 2);
        }
        return result;
    }

    public static int packedByteSize(String bitString) {
        if (bitString == null || bitString.isEmpty()) return 0;
        return (int) Math.ceil(bitString.length() / 8.0);
    }
}
