//Free
package fulltext;

import java.util.*;

public class Util {

    public LinkedHashMap<Short, KeyWord> fromString(long id, char[] str) {

        LinkedHashMap<Short, KeyWord> kws = new LinkedHashMap<Short, KeyWord>();
      
        KeyWord k = null;
        for (int i = 0; i < str.length; i++) {
            char c = str[i];
            if (c == ' ') {
                if (k != null && k.getKeyWord().length() <= KeyWord.MAX_WORD_LENGTH) {
                    kws.put(k.getPosition(), k);
                }
                k = new KeyWord();
                k.setKeyWord(" ");
                k.setPosition((short) i);
                kws.put(k.getPosition(), k);
                k = null;
            } else if (c >= 'a' && c <= 'z') {
                if (k == null) {
                    k = new KeyWord();
                    k.isWord = true;
                    k.setID(id);
                    k.setRank((short) 1);
                    k.setKeyWord("");
                    k.setPosition((short) i);
                }
                k.setKeyWord(k.getKeyWord() + Character.toString(c));
            } else {
                if (k != null && k.getKeyWord().length() <= KeyWord.MAX_WORD_LENGTH) {
                    kws.put(k.getPosition(), k);
                }
                k = new KeyWord();
                k.isWord = false;
                k.setID(id);
                k.setRank((short) 1);
                k.setKeyWord(Character.toString(c));
                k.setPosition((short) i);
                kws.put(k.getPosition(), k);
            }
        }

        return kws;
    }

    private String[] fixedKeywords = new String[]{"java", "c#", "iboxdb", "nosql", "database", "数据库"};

    public LinkedHashMap<Short, KeyWord> getFixedKeyWord(long id, String str) {
        LinkedHashMap<Short, KeyWord> kws = new LinkedHashMap<Short, KeyWord>();
        for (String s : fixedKeywords) {
            if (s.length() > KeyWord.MAX_WORD_LENGTH) {
                continue;
            }
            int p = str.indexOf(s);
            if (p > -1) {
                KeyWord k = new KeyWord();
                k.isWord = true;
                k.setID(id);
                k.setRank((short) 10);
                k.setKeyWord(s);
                k.setPosition((short) p);
                kws.put(k.getPosition(), k);
            }
        }

        return kws;
    }

    public void rankKeyWord(LinkedHashMap<Short, KeyWord> kws) {
        HashMap<String, Short> rd = new HashMap<String, Short>();
        ArrayList<Short> remove = new ArrayList<Short>();
        for (KeyWord kw : kws.values()) {
            if (!kw.isWord) {
                continue;
            }
            Short r = rd.get(kw.getKeyWord());
            if (r == null) {
                r = 0;
            } else {
                remove.add(kw.getPosition());
            }
            r = (short) (r.shortValue() + kw.getRank());
            if (r < 0) {
                r = 0;
            }
            rd.put(kw.getKeyWord(), r);
        }

        for (Short r : remove) {
            kws.remove(r);
        }

        for (KeyWord kw : kws.values()) {
            Short r = rd.get(kw.getKeyWord());
            if (r != null) {
                kw.setRank(r);
            }
        }
    }

}
