// http://www.iboxdb.com/
package fulltext;

import iBoxDB.JDB;
import iBoxDB.LocalServer.DB;

 
public class MainClass {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        DB.root("/tmp");
        
      System.out.println(  DB.toString(new KeyWord()));
      
      for(short s =0 ; s<=128; s ++){
          System.out.print((char)s);
      }
    }
    
}
