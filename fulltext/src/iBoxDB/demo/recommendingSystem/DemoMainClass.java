package iBoxDB.demo.recommendingSystem;

import iBoxDB.LocalServer.*;
import iBoxDB.fulltext.*;
import java.util.*;

public class DemoMainClass {

    public static void main(String[] args) throws Exception {

        DB.root("/tmp/");
        // User DataBase
        BoxSystem.DBDebug.DeleteDBFiles(101);
        DB userDB = new DB(101);
        userDB.getConfig().ensureTable(Favor.class, "/Favor", "userId", "itemId");
        AutoBox autoUser = userDB.open();

        // Favor Database
        Calendar today = java.util.Calendar.getInstance();

        long dailyTrackDB01 = Long.parseLong(
                today.get(Calendar.YEAR) + "" + (today.get(Calendar.MONTH) + 1)
                + "0" + today.get(Calendar.DAY_OF_MONTH));
        System.out.println(dailyTrackDB01);

        BoxSystem.DBDebug.DeleteDBFiles(dailyTrackDB01);
        DB dailyDB = new DB(dailyTrackDB01);
        Engine dailyEngine = new Engine();
        dailyEngine.Config(dailyDB.getConfig().DBConfig);
        dailyDB.getConfig().ensureTable(Favor.class, "/Favor", "userId", "itemId");
        AutoBox autoDaily = dailyDB.open();

        for (long userId = 1; userId < 100; userId++) {
            for (long itemId = 1000 + userId; itemId < 1000 + userId + 10; itemId++) {
                // User-->FavorItems(1000,1001,1002....)
                FavorWhenStay10Sec(dailyEngine, autoDaily, userId, itemId);
            }
        }

        //Search FavorItems(1000,1002) --> UserFavorItems(1000,1001,1002....) --> Recommend(1001)
        RealTimeRecommendPrint(dailyEngine, autoDaily, new long[]{1055, 1058});

        autoUser.getDatabase().close();
        autoDaily.getDatabase().close();

    }

    private static void RealTimeRecommendPrint(Engine dailyEngine, AutoBox autoDaily, long... relatedItemId) {
        StringBuilder sb = new StringBuilder();
        HashSet<Long> itemSet = new HashSet<Long>(relatedItemId.length + 1);
        for (long l : relatedItemId) {
            sb.append(l).append(" ");
            itemSet.add(l);
        }

        final long HowManyUsersAsReference = Long.MAX_VALUE;
        long maxPage = Long.MAX_VALUE;
        long startId = Long.MAX_VALUE;

        while (maxPage > 0) {
            long tempMaxPage = maxPage;
            maxPage = 0;

            System.out.println("Recommend PageNum:" + (Long.MAX_VALUE - (tempMaxPage - 1)));
            try (Box boxDaily = autoDaily.cube()) {

                for (KeyWord kw
                        : dailyEngine.searchDistinct(boxDaily, sb.toString(), startId, HowManyUsersAsReference)) {
                    maxPage = tempMaxPage - 1;
                    startId = kw.getID() - 1;
                    long userId = kw.getID();
                    for (Favor favor : boxDaily.select(Favor.class, "from /Favor where userId == ?", userId)) {
                        if (itemSet.contains(favor.itemId)) {
                            continue;
                        }
                        itemSet.add(favor.itemId);
                        System.out.print(favor.itemId + ",");
                    }
                }

            }
            System.out.println("");

        }

    }

    private static void FavorWhenStay10Sec(Engine dailyEngine, AutoBox autoDaily, long userId, long itemId) {

        try (Box box = autoDaily.cube()) {
            box.d("/Favor").insert(new Favor(userId, itemId));
            dailyEngine.indexText(box, userId, Long.toString(itemId), false);
            box.commit();
        }
    }

}
