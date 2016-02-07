# Full Text Search

### Config

```
 Engine engine = new Engine();
 engine.Config(db.getConfig().DBConfig);
```


### Index 

```
try (Box box = auto.cube()) {
    engine.indexText(box, id, text, false);
    box.commit();
}
```


### Search

```
try (Box box = auto.cube()) {
    for (KeyWord kw : engine.searchDistinct(box, "Linux")) {
      System.out.println(engine.getDesc(text, kw));
    }
}
```

[FTS Web Server](https://github.com/iboxdb/ftserver)
