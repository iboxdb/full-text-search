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

    public boolean indexText(Box box, long id, String str, boolean isRemove) {
        if (id == -1) {
            return false;
        }

        char[] cs = sUtil.clear(str);
        LinkedHashMap<Integer, KeyWord> map = util.fromString(id, cs);
        for (Map.Entry<Short, KeyWord> e
                : ((Map<Short, KeyWord>) map.clone()).entrySet()) {
            if (e.getValue().isWord && e.getValue().getKeyWord().length() < 3) {
                if (e.getValue().getKeyWord().equals("c#")
                        || e.getValue().getKeyWord().equals("f#")) {

                } else {
                    map.remove(e.getKey());
                }
            }
            if (e.getValue().isWord && e.getValue().getKeyWord().length()
                    > KeyWord.MAX_WORD_LENGTH) {
                map.remove(e.getKey());
            }
        }

        HashSet<String> words = new HashSet<String>();
        for (KeyWord kw : map.values()) {
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

    public Iterable<KeyWord> search(final Box box, String str) {
        char[] cs = sUtil.clear(str);
        LinkedHashMap<Integer, KeyWord> map = util.fromString(-1, cs);
        return search(box, map.values().toArray(new KeyWord[0]));
    }

    public Iterable<KeyWord> searchDistinct(final Box box, String str) {
        final Iterator<KeyWord> it = search(box, str).iterator();
        return new Iterable<KeyWord>() {

            @Override
            public Iterator<KeyWord> iterator() {
                return new Iterator<KeyWord>() {
                    long c_id = -1;
                    KeyWord current;

                    @Override
                    public boolean hasNext() {
                        while (it.hasNext()) {
                            current = it.next();
                            if (current.getID() == c_id) {
                                continue;
                            }
                            c_id = current.getID();
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public KeyWord next() {
                        return current;
                    }

                };
            }
        };
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
                    long r1_id = -1;

                    @Override
                    public boolean hasNext() {
                        if (r1 != null && r1.hasNext()) {
                            return true;
                        }
                        while (cd.hasNext()) {
                            r1_con = cd.next();
                            if (isWord) {
                                if (r1_id == r1_con.getID()) {
                                    continue;
                                }
                            }
                            r1_id = r1_con.getID();
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
