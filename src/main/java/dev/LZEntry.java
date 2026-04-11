package dev;

public record LZEntry(int index, char symbol) {
    @Override
    public String toString() {
        return "(" + index + ", '" + symbol + "')";
    }
}
