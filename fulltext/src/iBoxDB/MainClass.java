// http://www.iboxdb.com/
package iBoxDB;

import iBoxDB.LocalServer.*;
import iBoxDB.fulltext.Engine;
import iBoxDB.fulltext.KeyWord;
import iBoxDB.fulltext.KeyWordN;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainClass {

    public static void main(String[] args) throws Exception {

        System.out.println(java.lang.Runtime.getRuntime().maxMemory());
        DB.root("/tmp/");
        //test1();
        test_big();
        //test_big_e();
    }

    public static void test1() {
        iBoxDB.LocalServer.BoxSystem.DBDebug.DeleteDBFiles(1);
        DB db = new DB(1);

        final String[] ts = new String[]{
            //ID=0
            "Setting up Git\n"
            + "\n"
            + "Download and install the latest version of GitHub Desktop. "
            + "This will automatically install Git and keep it up-to-date for you.\n"
            + "On your computer, open the Git Shell application.\n"
            + "Tell Git your name so your commits will be properly labeled. Type everything after the $ here:\n"
            + "\n babies "
            + "git config --global user.name \"YOUR NAME\"\n"
            + "Tell Git the email address that will be associated with your Git commits. "
            + "The email you specify should be the same one found in your email settings. "
            + "To keep your email address hidden,"
            + " 关于 see \"Keeping your C# Java NoSQL email address abc@global.com private\".",
            //ID=1
            "关于版本控制\n"
            + "什么是“版本控制”？我为什么要关心它呢？ 版本控制 android 是一种记录一个或若干文件内容变化，database"
            + "以便将来查阅特定版本修订情况的系统。 在本书所展示的例子中，我们对保存着软件源代码的文件作版本控制，"
            + "但实际上，C lang IT 美食 你可以对任何类型的文件进行版本控制。",
            //ID=2
            "バージョン管理に関して\n"
            + "\n"
            + "「バージョン管理」とは何でしょうか。また、なぜそれを気にする必要があるのでしょうか。 "
            + "バージョン管理とは、一つのファイルやファイルの集合に対して時間とともに加えられていく変更を記録するシステムで、"
            + "後で特定バージョンを呼び出すことができるようにするためのものです。"
            + " 本書の例では、バージョン管理されるファイルとしてソフトウェアのソースコードを用いていますが、"
            + "実際にはコンピューター上のあらゆる種類のファイルをバージョン管理のもとに置くことができます。",
            //ID=3
            "關於版本控制\n"
            + "什麼是版本控制？ 以及為什麼讀者會在意它？ "
            + "版本控制是一個能夠記錄一個或一組檔案在某一段時間的變更，"
            + "使得讀者以後能取回特定版本的系統。has NoSQL 1234567890ABCDEFGH"
            + "在本書的範例中，讀者會學到如何對軟體的原始碼做版本控制。"
            + " 即使實際上讀者幾乎可以針對電腦上任意型態的檔案做版本控制。",
            //ID=4
            "Git 简史\n"
            + "同生活中的许多伟大事物一样，Git 诞生于一个极富纷争大举创新的年代。nosql \n"
            + "\n"
            + "Linux 内核开源项目有着为数众广的参与者。 绝大多数的 Linux 内核维护工作都花在了提交补丁和保存归档的"
            + "繁琐事务上（1991－2002年间）。 到 2002 年，"
            + "整个项目组开始启用一个专有的分布式 version 版本控制系统 BitKeeper 来管理和维护代码。\n"
            + "\n"
            + "到了 2005 年，开发 BitKeeper 的商业公司同 Linux 内核开源社区的合作关系结束，"
            + "他们收回了 Linux 内核社区免费使用 BitKeeper 的权力。"
            + " 这就迫使 Linux 开源社区（特别是 Linux 的缔造者 Linux Torvalds）基于使用 BitKcheper 时的"
            + "经验教训，开发出自己的版本系统　。 他们对新的系统制订了若干目标：",
            //ID=5
            "버전 관리란?\n\n버전 관리는 무엇이고 우리는 왜 이것을 알아야 할까? 버전 관리 시스템은 파일 변화를 시간에 따라 "
            + "기록했다가 나중에 특정 시점의 버전을 다시 꺼내올 수 있는 시스템이다. 이 책에서는 버전 관리하는 예제로 소프트웨어 "
            + "소스 코드만 보여주지만, 실제로 거의 모든 컴퓨터 파일의 버전을 관리할 수 있다.\n\n그래픽 디자이너나"
            + "웹 디자이너도 버전 관리 시스템(VCS - Version Control System)을 사용할 수 있다. VCS로 이미지나 레이아웃의"
            + "버전(변경 이력 혹은 수정 내용)을 관리하는 것은 매우 현명하다. VCS를 사용하면 각 파일을 이전 상태로 되돌릴 수 있고,"
            + "프로젝트를 통째로 이전 is 상태로 되돌릴 수 있고, 시간에 따라 수정 내용을 비교해 볼 수 있고,"
            + "누가 문제를 일으켰는지도 추적할 수 있고, 누가 언제 만들어낸 이슈인지도 알 수 있다. VCS를 사용하면 파일을 잃어버리거나"
            + "잘못 고쳤을 때도 쉽게 복구할 수 있다. HAS GIT 이런 모든 장점을 큰 노력 없이 이용할 수 있다."
        };

        Engine engine = new Engine();
        engine.Config(db.getConfig().DBConfig);

        AutoBox auto = db.open();

        for (int i = 0; i < ts.length; i++) {
            try (Box box = auto.cube()) {
                engine.indexText(box, i, ts[i], false);
                box.commit().Assert();
            }
        }
        try (Box box = auto.cube()) {
            //engine.indexText(box, 4, ts[4], true);
            box.commit().Assert();
        }

        try (Box box = auto.cube()) {
            for (KeyWord kw : engine.search(box, "版本控")) {
                System.out.println(kw.toFullString());
                System.out.println(engine.getDesc(ts[(int) kw.getID()], kw, 20));
                System.out.println();
            }
            for (String skw : engine.discover(box,
                    'n', 's', 2,
                    '\u2E80', '\u9fa5', 2)) {
                System.out.println(skw);
            }
        }
    }

    public static void test_big() throws FileNotFoundException, IOException, InterruptedException {
        String path = System.getProperty("user.home") + "/hero.txt";

        RandomAccessFile rf = new RandomAccessFile(path, "r");
        byte[] bs = new byte[(int) rf.length()];
        rf.readFully(bs);

        //iBoxDB.LocalServer.BoxSystem.DBDebug.DeleteDBFiles(1);
        DB db = new DB(1);

        rf.close();

        String[] tstmp = new String(bs).split("。");
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 2; i++) {
            for (String str : tstmp) {
                list.add(str);
            }
        }
        final String[] ts = list.toArray(new String[0]);

        final Engine engine = new Engine();
        engine.Config(db.getConfig().DBConfig);

        final AutoBox auto = db.open();

        long begin;
        /*
        ExecutorService pool = Executors.newFixedThreadPool(8);
        begin = System.currentTimeMillis();

        for (int i = 0; i < ts.length; i++) {
            final int tsi = i;
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try (Box box = auto.cube()) {
                        engine.indexText(box, tsi, ts[tsi], false);
                        box.commit().Assert();
                    }
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);
        System.out.println("Index " + ((System.currentTimeMillis() - begin) / 1000.0));
         */
        String strkw = "黄蓉";
        String strkw2 = "郭靖";
        String strkw3 = "洪七公";
        //strkw = "时察";
        //strkw = "的";
        // strkw = "七十二路";
        //strkw = "十八掌";

        int c;
        for (int i = 0; i < 20; i++) {
            begin = System.currentTimeMillis();
            c = 0;
            try (Box box = auto.cube()) {
                //search  searchDistinct
                for (KeyWord kw : engine.searchDistinct(box, strkw + " " + strkw2 + " " + strkw3)) {
                    c++;
                    //System.out.println(engine.getDesc(ts[0], kw, 15));
                    //System.out.println(kw.toFullString());
                }
            }
            System.out.println(c + " " + ((System.currentTimeMillis() - begin) / 1000.0));
        }

        begin = System.currentTimeMillis();
        c = 0;

        for (int i = 0; i < ts.length; i++) {
            if (ts[i].contains(strkw) && ts[i].contains(strkw2) && ts[i].contains(strkw3)) {
                c++;
            }
        }
        System.out.println(c + " " + ((System.currentTimeMillis() - begin) / 1000.0) + " -" + ts.length);

    }

    public static void test_big_e() throws FileNotFoundException, IOException, InterruptedException {
        String path = System.getProperty("user.home") + "/phoenix.txt";
        RandomAccessFile rf = new RandomAccessFile(path, "r");
        byte[] bs = new byte[(int) rf.length()];
        rf.readFully(bs);

        iBoxDB.LocalServer.BoxSystem.DBDebug.DeleteDBFiles(1);
        DB db = new DB(1);

        String ts[];
        {
            ArrayList<String> tts = new ArrayList<String>();
            String sbs = new String(bs);
            int begin = 0;
            int end = begin + 2000;
            while (end <= sbs.length() && begin != end) {
                tts.add(sbs.substring(begin, end));
                begin = end;
                end = begin + 2000;
                if (end > sbs.length()) {
                    end = sbs.length();
                }
            }
            //tts.addAll((ArrayList) tts.clone()); 
            ts = tts.toArray(new String[0]);
        }
        rf.close();

        Engine engine = new Engine();
        engine.Config(db.getConfig().DBConfig);

        AutoBox auto = db.open();

        long begin;
        begin = System.currentTimeMillis();
        try (Box box = auto.cube()) {
            for (int i = 0; i < ts.length; i++) {
                engine.indexText(box, i, ts[i], false);
            }
            box.commit().Assert();
        }
        System.out.println("Index " + ((System.currentTimeMillis() - begin) / 1000.0));

        String strkw = "Harry";
        //strkw = "Philosopher";
        int c;

        for (int i = 0; i < 20; i++) {
            begin = System.currentTimeMillis();
            c = 0;
            try (Box box = auto.cube()) {
                for (KeyWord kw : engine.search(box, strkw)) {
                    c++;
                    //System.out.println(engine.getDesc(ts[0], kw, 15));
                    //System.out.println(kw.toFullString());
                }
            }
            System.out.println(c + " " + ((System.currentTimeMillis() - begin) / 1000.0));
        }

        begin = System.currentTimeMillis();
        c = 0;
        for (int i = 0; i < ts.length; i++) {
            int p = ts[i].indexOf(strkw);
            if (p > 0) {
                c++;
            }
        }
        System.out.println(c + " " + ((System.currentTimeMillis() - begin) / 1000.0));
        //System.out.println(ts.length);

    }

}
