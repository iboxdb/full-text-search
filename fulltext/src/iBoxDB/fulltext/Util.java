//Free
package iBoxDB.fulltext;

import java.util.*;

class Util {

    final StringUtil sUtil = new StringUtil();

    public ArrayList<KeyWord> fromString(long id, char[] str, boolean forIndex) {

        ArrayList<KeyWord> kws = new ArrayList<KeyWord>();

        KeyWordE k = null;
        int linkedCount = 0;
        int lastNPos = -2;
        for (int i = 0; i < str.length; i++) {
            char c = str[i];
            if (c == ' ') {
                if (k != null) {
                    kws.add(k);
                }
                k = null;

            } else if (sUtil.isWord(c)) {
                if (c == '"') {
                    if (linkedCount > 0) {
                        linkedCount = 0;
                    } else {
                        linkedCount = 1;
                    }
                    continue;
                }
                if (k == null && c != '-' && c != '#') {
                    k = new KeyWordE();
                    k.setID(id);
                    k.setKeyWord("");
                    k.setPosition(i);
                    linkedCount++;
                    if (linkedCount > 2) {
                        k.isLinked = true;
                    }
                }
                if (k != null) {
                    k.setKeyWord(k.getKeyWord() + Character.toString(c));
                }
            } else {
                if (k != null) {
                    kws.add(k);
                }
                k = null;

                KeyWordN n = new KeyWordN();
                n.setID(id);
                n.setPosition(i);
                n.longKeyWord(c, (char) 0, (char) 0);
                n.isLinked = i == (lastNPos + 1);
                kws.add(n);

                char c1 = str[i + 1];
                if ((c1 != ' ') && (!sUtil.isWord(c1))) {
                    n = new KeyWordN();
                    n.setID(id);
                    n.setPosition(i);
                    n.longKeyWord(c, c1, (char) 0);
                    n.isLinked = i == (lastNPos + 1);
                    kws.add(n);
                    if (!forIndex) {
                        kws.remove(kws.size() - 2);
                        i++;
                    }
                }

                lastNPos = i;

            }
        }

        return kws;
    }

}
