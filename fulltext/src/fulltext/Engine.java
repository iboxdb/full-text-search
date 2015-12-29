//Free
package fulltext;

import iBoxDB.LocalServer.*;
import java.io.IOException;
import java.util.*;

public class Engine implements java.io.Closeable {
    
    Util util = new Util();
    StringUtil sUtil = new StringUtil();
    
    public void Config(DatabaseConfig config) {
        KeyWord.config(config);
    }
    
    public boolean addText(AutoBox auto, long id, String str) {
        if (id == -1) {
            return false;
        }
        char[] cs = sUtil.clear(str);
        LinkedHashMap<Short, KeyWord> map = util.fromString(id, cs);
        map.putAll(util.getFixedKeyWord(id, str.toLowerCase()));
        util.rankKeyWord(map);
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
    
    public Iterable<KeyWord> search(Box box, String[] kws) {
        
    }
    
    public Iterable<KeyWord> search(Box box, KeyWord kw, KeyWord condition) {
        if (kw.isWord) {
            if (condition == null) {
                return box.select(KeyWord.class, "from E where K==?", kw.getKeyWord());
            } else {
                return box.select(KeyWord.class, "from E where I==? &  K==?",
                        condition.getID(), kw.getKeyWord());
            }
        } else if (condition == null) {
            return box.select(KeyWord.class, "from N where K==?", kw.getKeyWord());
        } else {
            return box.select(KeyWord.class, "from N where I==? & K==? & P==?",
                    condition.getID(), kw.getKeyWord(), condition.getPosition() + 1);
        }
    }
    
    public boolean remove(AutoBox auto, long id) {
        return false;
    }
    
    public boolean rankUp(AutoBox auto, long id, String keyword, short step) {
        return false;
    }
}
