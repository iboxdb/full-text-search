# Full Text Search

```
try (Box box = auto.cube()) {
    for (KeyWord kw : engine.searchDistinct(box, "Linux")) {
      System.out.println(engine.getDesc(text, kw));
    }
}
```
