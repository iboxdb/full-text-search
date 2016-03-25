//Free
package iBoxDB.fulltext;

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
            // -1 is internal default value as NULL
            return false;
        }
        char[] cs = sUtil.clear(str);
        ArrayList<KeyWord> map = util.fromString(id, cs, true);

        HashSet<String> words = new HashSet<String>();
        for (KeyWord kw : map) {
            Binder binder;
            if (kw instanceof KeyWordE) {
                if (words.contains(kw.getKeyWord().toString())) {
                    continue;
                }
                words.add(kw.getKeyWord().toString());
                binder = box.d("E", kw.getKeyWord(), kw.getID(), kw.getPosition());
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

    public LinkedHashSet<String> discover(final Box box,
            char efrom, char eto, int elength,
            char nfrom, char nto, int nlength) {
        LinkedHashSet<String> list = new LinkedHashSet<String>();
        Random ran = new Random();
        if (elength > 0) {
            int len = ran.nextInt(KeyWord.MAX_WORD_LENGTH) + 1;
            char[] cs = new char[len];
            for (int i = 0; i < cs.length; i++) {
                cs[i] = (char) (ran.nextInt(eto - efrom) + efrom);
            }
            KeyWordE kw = new KeyWordE();
            kw.setKeyWord(new String(cs));
            for (KeyWord tkw : lessMatch(box, kw)) {
                String str = tkw.getKeyWord().toString();
                if (str.length() < 3 || sUtil.mvends.contains(str)) {
                    continue;
                }
                int c = list.size();
                list.add(str);
                if (list.size() > c) {
                    elength--;
                    if (elength <= 0) {
                        break;
                    }
                }
            }
        }
        if (nlength > 0) {
            char[] cs = new char[2];
            for (int i = 0; i < cs.length; i++) {
                cs[i] = (char) (ran.nextInt(nto - nfrom) + nfrom);
            }
            KeyWordN kw = new KeyWordN();
            kw.longKeyWord(cs[0], cs[1], (char) 0);
            for (KeyWord tkw : lessMatch(box, kw)) {
                int c = list.size();
                list.add(((KeyWordN) tkw).toKString());
                if (list.size() > c) {
                    nlength--;
                    if (nlength <= 0) {
                        break;
                    }
                }
            }
        }
        return list;
    }

    public String getDesc(String str, KeyWord kw, int length) {
        return sUtil.getDesc(str, kw, length);
    }

    public Iterable<KeyWord> search(final Box box, String str) {
        char[] cs = sUtil.clear(str);
        ArrayList<KeyWord> map = util.fromString(-1, cs, false);
        sUtil.correctInput(map);

        if (map.size() > KeyWord.MAX_WORD_LENGTH || map.isEmpty()) {
            return new ArrayList();
        }
        ArrayList<KeyWord> kws = new ArrayList<KeyWord>();

        for (int i = 0; i < map.size(); i++) {
            KeyWord kw = map.get(i);
            if (kw instanceof KeyWordE) {
                String s = kw.getKeyWord().toString();
                if ((s.length() > 2) && (!sUtil.mvends.contains(s))) {
                    kws.add(kw);
                    map.set(i, null);
                }
            } else {
                KeyWordN kwn = (KeyWordN) kw;
                if (kwn.size() >= 2) {
                    kws.add(kw);
                    map.set(i, null);
                } else if (kws.size() > 0) {
                    KeyWord p = kws.get(kws.size() - 1);
                    if (p instanceof KeyWordN) {
                        if (kwn.getPosition() == (p.getPosition() + ((KeyWordN) p).size())) {
                            kws.add(kw);
                            map.set(i, null);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < map.size(); i++) {
            KeyWord kw = map.get(i);
            if (kw != null) {
                kws.add(kw);
            }
        }
        final MaxID maxId = new MaxID();
        final Iterator<KeyWord> iter = search(box, kws.toArray(new KeyWord[0]), maxId).iterator();
        return new Iterable<KeyWord>() {
            @Override
            public Iterator<KeyWord> iterator() {
                return new Iterator<KeyWord>() {
                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    @Override
                    public KeyWord next() {
                        KeyWord kw = iter.next();
                        maxId.id--;
                        return kw;
                    }

                };
            }
        };
    }

    private Iterable<KeyWord> search(final Box box, final KeyWord[] kws, MaxID maxId) {

        if (kws.length == 1) {
            return search(box, kws[0], (KeyWord) null, false, maxId);
        }

        boolean asWord = true;
        KeyWord kwa = kws[kws.length - 2];
        KeyWord kwb = kws[kws.length - 1];
        if ((kwa instanceof KeyWordN) && (kwb instanceof KeyWordN)) {
            asWord = kwb.getPosition() != (kwa.getPosition() + ((KeyWordN) kwa).size());
        }

        return search(box, kws[kws.length - 1],
                search(box, Arrays.copyOf(kws, kws.length - 1), maxId),
                asWord, maxId);
    }

    private Iterable<KeyWord> search(final Box box, final KeyWord nw,
            Iterable<KeyWord> condition, final boolean isWord, final MaxID maxId) {
        final Iterator<KeyWord> cd = condition.iterator();
        return new Iterable<KeyWord>() {

            @Override
            public Iterator<KeyWord> iterator() {
                return new EngineIterator<KeyWord>() {
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
                            r1 = search(box, nw, r1_con, isWord, maxId).iterator();
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

    private static final Iterable<KeyWord> emptySearch = new ArrayList();

    private static Iterable<KeyWord> search(Box box, KeyWord kw, KeyWord con, boolean asWord, MaxID maxId) {
        if (con != null) {
            if (con.I > maxId.id) {
                return emptySearch;
            } else {
                maxId.id = con.I;
            }
        }
        if (kw instanceof KeyWordE) {
            return new Index2KeyWordEIterable(box.select(Object.class, "from E where K==? & I<=?",
                    kw.getKeyWord(), maxId.id), box, kw, con, asWord, maxId);
        } else {

            if (con instanceof KeyWordE) {
                asWord = true;
            }
            if (con == null || asWord) {
                return new Index2KeyWordNIterable(box.select(Object.class, "from N where K==? &  I<=?",
                        kw.getKeyWord(), maxId.id), box, kw, con, asWord, maxId);
            } else {
                return new Index2KeyWordNIterable(box.select(Object.class, "from N where K==? & I==? & P==?",
                        kw.getKeyWord(), con.getID(), (con.getPosition() + ((KeyWordN) con).size())),
                        box, kw, con, asWord, maxId);
            }
        }
    }

    private static Iterable<KeyWord> lessMatch(Box box, KeyWord kw) {
        if (kw instanceof KeyWordE) {
            return new Index2KeyWordEIterable(box.select(Object.class, "from E where K<=? limit 0, 50", kw.getKeyWord()), null, null, null, true, new MaxID());
        } else {
            return new Index2KeyWordNIterable(box.select(Object.class, "from N where K<=? limit 0, 50", kw.getKeyWord()), null, null, null, true, new MaxID());
        }
    }

    private static final class MaxID {

        public long id = Long.MAX_VALUE;
    }

    private static final class Index2KeyWordEIterable extends Index2KeyWordIterable {

        public Index2KeyWordEIterable(Iterable<Object> findex,
                Box box, KeyWord kw, KeyWord con, boolean asWord, MaxID maxId) {
            super(findex, box, kw, con, asWord, maxId);
        }

        @Override
        protected KeyWord create() {
            return new KeyWordE();
        }
    }

    private static final class Index2KeyWordNIterable extends Index2KeyWordIterable {

        public Index2KeyWordNIterable(Iterable<Object> findex,
                Box box, KeyWord kw, KeyWord con, boolean asWord, MaxID maxId) {
            super(findex, box, kw, con, asWord, maxId);
        }

        @Override
        protected KeyWord create() {
            return new KeyWordN();
        }

    }

    private static abstract class Index2KeyWordIterable
            implements Iterable<KeyWord> {

        final Iterator<KeyWord> iterator;
        final Iterable<Object> findex;

        protected Index2KeyWordIterable(final Iterable<Object> findex,
                final Box box, final KeyWord kw, final KeyWord con, final boolean asWord, final MaxID maxId) {
            this.findex = findex;
            this.iterator = new EngineIterator<KeyWord>() {
                long firstMaxId = maxId.id;
                Iterator<Object[]> index = (Iterator<Object[]>) (Object) findex.iterator();
                KeyWord cache;

                @Override
                public boolean hasNext() {
                    if (con != null) {
                        if (con.I != maxId.id) {
                            return false;
                        }
                    }
                    
                    if (firstMaxId > maxId.id) {
                        firstMaxId = maxId.id;

                        Iterable<KeyWord> tmp = search(box, kw, con, asWord, maxId);
                        if (tmp instanceof Index2KeyWordIterable) {
                            index = (Iterator<Object[]>) (Object) ((Index2KeyWordIterable) tmp).findex.iterator();
                        }
                    }

                    if (index.hasNext()) {
                        Object[] os = index.next();
                        long osid = (Long) os[1];
                        maxId.id = osid;
                        firstMaxId = maxId.id;

                        if (con != null) {
                            if (con.I != maxId.id) {
                                return false;
                            }
                        }

                        cache = create();
                        cache.setKeyWord(os[0]);
                        cache.I = (Long) os[1];
                        cache.P = (Integer) os[2];

                        return true;
                    }
                    return false;
                }

                @Override
                public KeyWord next() {
                    return cache;
                }
            };
        }

        @Override
        public Iterator<KeyWord> iterator() {
            return iterator;
        }

        protected abstract KeyWord create();

    }

    private static abstract class EngineIterator<E> implements java.util.Iterator<E> {

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

    }
}
