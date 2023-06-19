public class Cesar {

    public Cesar() {

    }

    static String criptografa(String s) {
        String Str = "";
        for (int i = 0; i < s.length(); i++) {
            Str += (char) (s.charAt(i) + 1);
        }
        return Str;
    }

    static String descriptografa(String s) {
        String Str = "";
        for (int i = 0; i < s.length(); i++) {
            Str += (char) (s.charAt(i) - 1);
        }
        return Str;
    }
}