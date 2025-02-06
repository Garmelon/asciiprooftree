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

To reformat all proof trees in your project, run:

```shell
java -jar asciiprooftree.jar path/to/your/src
```

For available options, run:

```shell
java -jar asciiprooftree.jar --help
```
