package com.hp.snap.evaluation.imdb.business.cases.couchbase;

public class SystemProperty {
    public static final String OS_NAME_WINDOWS = "Windows";
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String TAB = "\t";
    public static final String LINE_HORIZON = "===========================================================================";

    public static String getNewLines(int aLines) {
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < aLines; i++) {
            result.append(System.getProperty("line.separator"));
        }

        return result.toString();
    }

    public static String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    public static String getHorizontalLine(int size) {
        char[] result = new char[size];
        for (int i = 0; i < size; i++) {
            result[i] = '=';
        }
        return result.toString();
    }

    public static String getSpaces(int size) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < size; i++) {
            result.append(' ');
        }
        return result.toString();
    }

    public static boolean isOsWindows() {
        String osName = System.getProperty("os.name");

        if (osName == null || osName.length() < 1)
            return false;

        return osName.contains(OS_NAME_WINDOWS);
    }

}
