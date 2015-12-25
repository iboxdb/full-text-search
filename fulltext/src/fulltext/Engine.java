//Free
package fulltext;

import iBoxDB.LocalServer.Box;
import iBoxDB.LocalServer.CommitResult;
import iBoxDB.LocalServer.DB;
import java.io.IOException;
import java.util.*;

public class Engine implements java.io.Closeable {

    Util util = new Util();

    DB.AutoBox auto;

    public Engine(long addr, long cache) {
        DB db = new DB(addr);
        KeyWord.config(db.getConfig().DBConfig, cache);
        auto = db.open();
    }

    @Override
    public void close() throws IOException {
        auto.getDatabase().close();
        auto = null;
    }

    public boolean addText(long id, String str) {
        if (id == -1) {
            return false;
        }
        LinkedHashMap<Short, KeyWord> map = util.fromString(id, str);
        map.putAll(util.getFixedKeyWord(id, str));
        try (Box box = auto.cube()) {
            for (KeyWord kw : map.values()) {
                if (kw.getKeyWord().equals(" ")) {
                    continue;
                }
                if (kw.isWord) {
                    box.bind("E").insert(kw);
                } else {
                    box.bind("N").insert(kw);
                }
            }
            return box.commit() == CommitResult.OK;
        }
    }

    public ArrayList<Long> search(String str) {
        LinkedHashMap<Short, KeyWord> map = util.fromString(-1, str);
        try (Box box = auto.cube()) {
            for (KeyWord kw : map.values()) {

                if (kw.isWord) {

                }
            }
        }
    }

    public boolean remove(long id) {
        return false;
    }

    public boolean rankUp(long id, String keyword, short step) {
        return false;
    }
}
