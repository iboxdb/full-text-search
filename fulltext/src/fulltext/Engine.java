//Free
package fulltext;

import iBoxDB.LocalServer.*;
import java.util.*;

public class Engine {

    Util util = new Util();
    StringUtil sUtil = new StringUtil();

    public void Config(DatabaseConfig config) {
        KeyWord.config(config);
    }

    public boolean addText(Box box, long id, String str, String[] fixedKW) {
        if (id == -1) {
            return false;
        }

        char[] cs = sUtil.clear(str);
        LinkedHashMap<Short, KeyWord> map = util.fromString(id, cs);
        for (Map.Entry<Short, KeyWord> e
                : ((Map<Short, KeyWord>) map.clone()).entrySet()) {
            if (e.getValue().isWord && e.getValue().getKeyWord().length() < 3) {
                map.remove(e.getKey());
            }
        }
        util.rankKeyWord(map);

        ArrayList<KeyWord> list = new ArrayList<KeyWord>(map.values());
        for (Map.Entry<Short, KeyWord> e
                : util.getFixedKeyWord(id, str, fixedKW).entrySet()) {
            KeyWord kw = map.get(e.getKey());
            if (kw != null) {
                if (kw.getKeyWord().equals(e.getValue().getKeyWord())) {
                    kw.setRank((short) (kw.getRank() + e.getValue().getRank()));
                } else {
                    list.add(e.getValue());
                }
            } else {
                list.add(e.getValue());
            }
        }

        for (KeyWord kw : list) {
            if (kw.isWord) {
                box.d("E").insert(kw);
            } else {
                box.d("N").insert(kw);
            }
        }

        return true;
    }

    public Iterable<KeyWord> search(final Box box, final String str) {
        char[] cs = sUtil.clear(str.replaceAll("  ", " "));
        KeyWord kw = new KeyWord();
        kw.isWord = true;
        kw.setKeyWord(new String(cs).trim());
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

        KeyWord[] kws = util.fromString(-1, cs).values().toArray(new KeyWord[0]);
        return search(box, kws);
    }

    public boolean remove(Box box, long id, String reason) {
        return false;
    }

    public boolean rankUp(Box box, long id, String keyword, short step) {
        if (step > 1000) {
            step = 1000;
        }
        for (KeyWord k : box.select(KeyWord.class, "from E where I==? & K==?",
                id, keyword.toLowerCase().trim())) {
            int r = k.getRank() + step;
            if (r > (Short.MAX_VALUE - 2000)) {
                r = Short.MAX_VALUE - 2000;
            }
            k.setRank((short) r);
            box.d("E").update(r);
            return true;
        }
        return false;
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

                    @Override
                    public boolean hasNext() {
                        if (r1 != null && r1.hasNext()) {
                            return true;
                        }
                        while (cd.hasNext()) {
                            KeyWord c = cd.next();
                            c.isWord = isWord;
                            r1 = search(box, nw, c).iterator();
                            if (r1.hasNext()) {
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public KeyWord next() {
                        return r1.next();
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
                return box.select(KeyWord.class, "from E where I==? &  K==?",
                        condition.getID(), kw.getKeyWord());
            }
        } else if (condition == null) {
            return box.select(KeyWord.class, "from N where K==?", kw.getKeyWord());
        } else if (condition.isWord) {
            return box.select(KeyWord.class, "from N where I==? &  K==?",
                    condition.getID(), kw.getKeyWord());
        } else {
            return box.select(KeyWord.class, "from N where I==? & K==? & P==?",
                    condition.getID(), kw.getKeyWord(), (short) (condition.getPosition() + 1));
        }
    }

}
