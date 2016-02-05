# Full Text Search

```
try (Box box = auto.cube()) {
    for (KeyWord kw : engine.searchDistinct(box, "Linux")) {
      System.out.println(engine.getDesc(text, kw));
    }
}
```

[FTS Web Server](https://github.com/iboxdb/ftserver)

![FTS Server](https://github.com/iboxdb/ftserver/raw/master/FTServer/web/css/fts.png)
