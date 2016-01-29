package iBoxDB.fulltext;

import java.util.*;

class StringUtil {

    HashSet<Character> set;

    public StringUtil() {
        String s = "!\"@$%&'()*+,./:;<=>?[\\]^_`{|}~\r\n"; //@-
        s += "， 　，《。》、？；：‘’“”【｛】｝——=+、｜·～！￥%……&*（）"; //@-#
        s += "｀～！＠￥％……—×（）——＋－＝【】｛｝：；’＇”＂，．／＜＞？’‘”“";//＃
        set = new HashSet<Character>();
        for (char c : s.toCharArray()) {
            set.add(c);
        }
    }

    public boolean isWord(char c) {
        //English
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c >= '0' && c <= '9') {
            return true;
        }
        //Russian
        if (c >= 0x0400 && c <= 0x052f) {
            return true;
        }
        //Germen
        if (c >= 0xc0 && c <= 0xff) {
            return true;
        }
        return c == '-' || c == '#';
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
