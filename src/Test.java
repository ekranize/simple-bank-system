public class Test {
    private static final int SHIFT = 3;

    public static String method1(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = (char) (text.charAt(i) + SHIFT);
            result.append(c);
        }
        return result.toString();
    }

    public static String method2(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = (char) (text.charAt(i) - SHIFT);
            result.append(c);
        }
        return result.toString();
    }
}
