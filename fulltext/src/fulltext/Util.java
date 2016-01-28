//Free
package fulltext;

import java.util.*;

public class Util {

    final StringUtil sUtil = new StringUtil();

    public LinkedHashMap<Integer, KeyWord> fromString(long id, char[] str) {

        LinkedHashMap<Integer, KeyWord> kws = new LinkedHashMap<Integer, KeyWord>();

        KeyWord k = null;
        for (int i = 0; i < str.length; i++) {
            char c = str[i];
            if (c == ' ') {
                if (k != null) {
                    kws.put(k.getPosition(), k);
                }
                k = null;
            } else if (sUtil.isWord(c)) {
                if (k == null && c != '-' && c != '@') {
                    k = new KeyWord();
                    k.isWord = true;
                    k.setID(id);
                    k.setKeyWord("");
                    k.setPosition(i);
                }
                if (k != null) {
                    k.setKeyWord(k.getKeyWord() + Character.toString(c));
                }
            } else {
                if (k != null) {
                    kws.put(k.getPosition(), k);
                }
                k = new KeyWord();
                k.isWord = false;
                k.setID(id);
                k.setKeyWord(Character.toString(c));
                k.setPosition(i);
                kws.put(k.getPosition(), k);
                k = null;
            }
        }

        return kws;
    }

    public LinkedHashMap<Integer, KeyWord> getFixedKeyWord(long id, String str,
            String[] fixedKeywords) {
        LinkedHashMap<Integer, KeyWord> kws = new LinkedHashMap<Integer, KeyWord>();
        if (fixedKeywords != null && fixedKeywords.length > 0) {
            str = str.toLowerCase();
            for (String s : fixedKeywords) {
                if (s.length() > KeyWord.MAX_WORD_LENGTH) {
                    continue;
                }
                String sl = s.toLowerCase();
                int p = str.indexOf(sl);
                if (p > -1) {
                    KeyWord k = new KeyWord();
                    k.isWord = true;
                    k.setID(id);
                    k.setKeyWord(sl);
                    k.setPosition(p);
                    kws.put(k.getPosition(), k);
                }
            }
        }
        return kws;
    }

}
