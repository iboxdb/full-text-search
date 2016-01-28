//Free
package fulltext;

import iBoxDB.LocalServer.*;
import java.util.*;

public class Engine {

    final Util util = new Util();
    final StringUtil sUtil = new StringUtil();

    public void Config(DatabaseConfig config) {
        KeyWord.config(config);
    }

    public boolean indexText(Box box, long id, String str, String[] fixedKW, boolean isRemove) {
        if (id == -1) {
            return false;
        }

        char[] cs = sUtil.clear(str);
        LinkedHashMap<Integer, KeyWord> map = util.fromString(id, cs);
        for (Map.Entry<Short, KeyWord> e
                : ((Map<Short, KeyWord>) map.clone()).entrySet()) {
            if (e.getValue().isWord && e.getValue().getKeyWord().length() < 3) {
                map.remove(e.getKey());
            }
            if (e.getValue().isWord && e.getValue().getKeyWord().length()
                    > KeyWord.MAX_WORD_LENGTH) {
                map.remove(e.getKey());
            }
        }

        ArrayList<KeyWord> list = new ArrayList<KeyWord>(map.values());
        list.addAll(util.getFixedKeyWord(id, str, fixedKW).values());

        HashSet<String> words = new HashSet<String>();
        for (KeyWord kw : list) {
            Binder binder;
            if (kw.isWord) {
                if (words.contains(kw.getKeyWord())) {
                    continue;
                }
                words.add(kw.getKeyWord());
                binder = box.d("E", kw.getKeyWord(), kw.getID());
            } else {
                binder = box.d("N", kw.getKeyWord(), kw.getID(), kw.getPosition());
            }
            if (isRemove) {
                binder.delete();
            } else {
                binder.insert(kw, 1);
            }
        }

        return true;
    }

    public String replaceSearchInput(String str) {
        str = str.replaceAll("　", " ")
                .replaceAll("。", " ")
                .replaceAll("\\.", " ")
                .replaceAll("\"", " ")
                .replaceAll("  ", " ")
                .replaceAll("  ", " ")
                .toLowerCase().trim();
        return str;
    }

    public String[] splitInput(String str) {
        ArrayList<String> list = new ArrayList<String>();

        String t = "";
        boolean isUnder = str.charAt(0) < 255;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ' ') {
                if (t.length() > 0) {
                    list.add(t);
                }
                t = "";
                continue;
            }
            boolean tUnder = str.charAt(i) < 255;
            if (tUnder != isUnder) {
                if (t.length() > 0) {
                    list.add(t);
                }
                t = "";
            }
            isUnder = tUnder;
            t += str.charAt(i);
        }
        if (t.length() > 0) {
            list.add(t);
        }
        return list.toArray(new String[0]);
    }

    public Iterable<KeyWord> search(final Box box, String str) {

        str = replaceSearchInput(str);

        KeyWord kw = new KeyWord();
        kw.isWord = true;
        kw.setKeyWord(str);
        final Iterator<KeyWord> it = search(box, kw, null).iterator();

        if (it.hasNext()) {
            return new Iterable<KeyWord>() {
                @Override
                public Iterator<KeyWord> iterator() {
                    return new Iterator<KeyWord>() {
                        Iterator<KeyWord> self = null;

                        @Override
                        public boolean hasNext() {
                            if (self == null) {
                                self = it;
                                return true;
                            }
                            return self.hasNext();
                        }

                        @Override
                        public KeyWord next() {
                            return it.next();
                        }
                    };
                }
            };
        }

        String[] names = splitInput(str);
        ArrayList<KeyWord> kws = new ArrayList<KeyWord>();
        for (int i = 0; i < names.length; i++) {
            KeyWord k = null;
            if (sUtil.isWord(names[i].charAt(0))) {
                k = new KeyWord();
                k.setKeyWord(names[i]);
                k.setPosition(-100);
                k.isWord = true;
                kws.add(k);
            } else {
                int start = str.indexOf(names[i]);
                for (char c : names[i].toCharArray()) {
                    k = new KeyWord();
                    k.setKeyWord(Character.toString(c));
                    k.setPosition(start++);
                    k.isWord = false;
                    kws.add(k);
                }
            }
        }
        return search(box, kws.toArray(new KeyWord[0]));
    }

    // Base
    private Iterable<KeyWord> search(final Box box, final KeyWord[] kws) {
        if (kws.length == 1) {
            return search(box, kws[0], (KeyWord) null);
        }

        return search(box, kws[kws.length - 1],
                search(box, Arrays.copyOf(kws, kws.length - 1)),
                kws[kws.length - 1].getPosition()
                != (kws[kws.length - 2].getPosition() + 1));
    }

    private Iterable<KeyWord> search(final Box box, final KeyWord nw,
            Iterable<KeyWord> condition, final boolean isWord) {
        final Iterator<KeyWord> cd = condition.iterator();
        return new Iterable<KeyWord>() {

            @Override
            public Iterator<KeyWord> iterator() {
                return new Iterator<KeyWord>() {
                    Iterator<KeyWord> r1 = null;
                    KeyWord r1_con = null;

                    @Override
                    public boolean hasNext() {
                        if (r1 != null && r1.hasNext()) {
                            return true;
                        }
                        while (cd.hasNext()) {
                            r1_con = cd.next();
                            r1_con.isWord = isWord;
                            r1 = search(box, nw, r1_con).iterator();
                            if (r1.hasNext()) {
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public KeyWord next() {
                        KeyWord k = r1.next();
                        k.previous = r1_con;
                        return k;
                    }
                };

            }
        };

    }

    private Iterable<KeyWord> search(Box box, KeyWord kw, KeyWord condition) {

        if (kw.isWord) {
            if (condition == null) {
                return box.select(KeyWord.class, "from E where K==?", kw.getKeyWord());
            } else {
                return box.select(KeyWord.class, "from E where K==? &  I==?",
                        kw.getKeyWord(), condition.getID());
            }
        } else if (condition == null) {
            return box.select(KeyWord.class, "from N where K==?", kw.getKeyWord());
        } else if (condition.isWord) {
            return box.select(KeyWord.class, "from N where K==? &  I==?",
                    kw.getKeyWord(), condition.getID());
        } else {
            return box.select(KeyWord.class, "from N where K==? & I==? & P==?",
                    kw.getKeyWord(), condition.getID(), (condition.getPosition() + 1));
        }
    }

}
