package org.wrkr.clb.common.util.strings;

import java.util.List;

public abstract class ExtStringUtils {

    private static final int ABSENT_INDEX = -1;

    public static int indexOfNonWhitespace(String string, int offset) {
        for (int i = offset; i < string.length(); i++) {
            if (!Character.isWhitespace(string.charAt(i))) {
                return i;
            }
        }
        return ABSENT_INDEX;
    }

    public static int indexOfNonWhitespace(String string) {
        return indexOfNonWhitespace(string, 0);
    }

    public static int lastIndexOfNonWhitespace(String string) {
        for (int i = string.length() - 1; i >= 0; i--) {
            if (!Character.isWhitespace(string.charAt(i))) {
                return i;
            }
        }
        return ABSENT_INDEX;
    }

    public static boolean equalsWithoutWhitespace(String string, String substring) {
        int beginIndex = indexOfNonWhitespace(string);
        int endIndex = lastIndexOfNonWhitespace(string);

        if (beginIndex == ABSENT_INDEX || endIndex == ABSENT_INDEX) {
            return substring.isEmpty();
        }
        if (endIndex - beginIndex + 1 != substring.length()) {
            return false;
        }

        return string.substring(beginIndex, endIndex + 1).equals(substring);
    }

    public static boolean startsWithWhitespaceAndPrefix(String string, String prefix) {
        int i = indexOfNonWhitespace(string);
        if (i == ABSENT_INDEX) {
            return prefix.isEmpty();
        }
        return string.startsWith(prefix, i);
    }

    public static boolean endsWithSuffixAndWhitespace(String string, String suffix) {
        int i = lastIndexOfNonWhitespace(string);
        if (i + 1 < suffix.length()) {
            return false;
        }
        return string.startsWith(suffix, (i + 1) - suffix.length());
    }

    public static String substring(String string, int limit) {
        return (string.length() > limit ? string.substring(0, limit) : string);
    }

    public static String substringByFirstSymbol(String string, char symbol) {
        int firstSymbolIndex = string.indexOf(symbol);
        if (firstSymbolIndex >= 0) {
            return string.substring(0, firstSymbolIndex);
        }
        return string;
    }

    public static String substringByFirstSymbol(String string, char symbol, int fromIndex) {
        int firstSymbolIndex = string.indexOf(symbol, fromIndex);
        if (firstSymbolIndex >= 0) {
            return string.substring(fromIndex, firstSymbolIndex);
        }
        return string;
    }

    public static String substringFromFirstSymbol(String string, char symbol) {
        int firstSymbolIndex = string.indexOf(symbol);
        if (firstSymbolIndex >= 0) {
            return string.substring(firstSymbolIndex + 1);
        }
        return string;
    }

    public static String substringFromLastSymbol(String string, char symbol) {
        int lastSymbolIndex = string.lastIndexOf(symbol);
        if (lastSymbolIndex >= 0) {
            return string.substring(lastSymbolIndex + 1);
        }
        return string;
    }

    public static String substringByLimitOrSymbols(String string, int limit, List<String> symbols) {
        String substring = (string.length() > limit ? string.substring(0, limit) : string);

        int firstSymbolIndex = substring.length();
        for (String symbol : symbols) {
            int symbolIndex = substring.indexOf(symbol);
            if (symbolIndex >= 0) {
                firstSymbolIndex = Integer.min(firstSymbolIndex, symbolIndex);
            }
        }

        return substring.substring(0, firstSymbolIndex);
    }

    public static String addLeadingCharacter(String string, char leadingCharacter, int targetLength) {
        if (string.length() >= targetLength) {
            return string;
        }

        StringBuilder builder = new StringBuilder(targetLength);
        for (int i = 0; i < targetLength - string.length(); i++) {
            builder.append(leadingCharacter);
        }
        return builder.append(string).toString();
    }

    public static String toHumanReadable(String string) {
        if (string == null) {
            return null;
        }
        if (string.length() <= 1) {
            return string.toUpperCase();
        }

        StringBuilder builder = new StringBuilder(string.length())
                .append(Character.toUpperCase(string.charAt(0)));
        for (int i = 1; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == '_') {
                builder.append(' ');
            } else {
                builder.append(Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }
}
