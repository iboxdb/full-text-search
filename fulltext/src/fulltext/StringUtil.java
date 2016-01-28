package fulltext;

import java.util.*;

public class StringUtil {

    public static StringUtil T = new StringUtil();

    HashSet<Character> set;

    public StringUtil() {
        String s = "!\"#$%&'()*+,./:;<=>?[\\]^_`{|}~\r\n"; //@-
        s += "， 　，《。》、？；：‘’“”【｛】｝——=+、｜·～！#￥%……&*（）"; //@-
        s += "｀～！＠＃￥％……—×（）——＋－＝【】｛｝：；’＇”＂，．／＜＞？’‘”“";
        set = new HashSet<Character>();
        for (char c : s.toCharArray()) {
            set.add(c);
        }
    }

    public boolean isWord(char c) {
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c >= '0' && c <= '9') {
            return true;
        }
        return c == '-' || c == '@';
    }

    public char[] clear(String str) {
        char[] cs = (str + " ").toLowerCase().toCharArray();
        for (int i = 0; i < cs.length; i++) {
            if (set.contains(cs[i])) {
                cs[i] = ' ';
            }
        }
        return cs;
    }

}
