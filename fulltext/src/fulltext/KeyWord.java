//Free
package fulltext;

import iBoxDB.LocalServer.DatabaseConfig;
import iBoxDB.LocalServer.NotColumn;

public class KeyWord {

    public final static int MAX_WORD_LENGTH = 16;

    public static void config(DatabaseConfig c) {
   

        // English Language or Word (max=16)              
        c.EnsureTable(KeyWord.class, "E", "K(" + MAX_WORD_LENGTH + ")", "R", "I");
        c.EnsureIndex(KeyWord.class, "E", "I", "K(" + MAX_WORD_LENGTH + ")");

        // Non-English Language or Character
        c.EnsureTable(KeyWord.class, "N", "K(1)", "I", "P");
        c.EnsureIndex(KeyWord.class, "N", "I", "K(1)", "P");

    }

    //Key Word
    public String K;

    @NotColumn
    public String getKeyWord() {
        return K;
    }

    @NotColumn
    public void setKeyWord(String k) {
        K = k;
    }

    //Rank
    public short R;

    @NotColumn
    public short getRank() {
        return R;
    }

    @NotColumn
    public void setRank(short r) {
        R = r;
    }

    //Position
    public short P;

    @NotColumn
    public short getPosition() {
        return P;
    }

    @NotColumn
    public void setPosition(short p) {
        P = p;
    }

    //Document ID
    public long I;

    @NotColumn
    public long getID() {
        return I;
    }

    @NotColumn
    public void setID(long i) {
        I = i;
    }

    @NotColumn
    public boolean isWord;

}
