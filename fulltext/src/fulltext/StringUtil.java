package fulltext;

import java.util.*;

public class StringUtil {

    HashSet<Character> set;

    public StringUtil() {
        String s = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
        s += "， ，《。》、？；：‘’“”【｛】｝-——=+、｜·～！@#￥%……&*（）";
        set = new HashSet<Character>();
        for (char c : s.toCharArray()) {
            set.add(c);
        }
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
    
    public String[] searchSplit(String str){ 
        
        String[] ss = str.trim().replaceAll(" ", " ").split(" ");
        
    }
}
