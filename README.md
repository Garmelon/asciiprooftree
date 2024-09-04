# asciiprooftree

Format and reformat ascii proof trees like this one:

```text
§                                                   *
§                                     ----------------------------- [;] compose
§                                     [a;b;]p(||) <-> [a;][b;]p(||)
§                               ----------------------------------------- US
§  [x:=*;][?x>0;][x:=x+1;]x>1   [?x>0;x:=x+1;]x>1 <-> [?x>0;][x:=x+1;]x>1
§  ---------------------------------------------------------------------- RewriteAt
§                         [x:=*;][?x>0;x:=x+1;]x>1
```
