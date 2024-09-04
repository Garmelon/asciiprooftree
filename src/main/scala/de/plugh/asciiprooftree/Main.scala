package de.plugh.asciiprooftree

import java.nio.file.Path

@main
def main(args: String*): Unit = args match
  case Seq() => run()
  case Seq(path) => run(Path.of(path))
  case Seq(path, marker) => run(Path.of(path), marker)
  case _ =>
    println("Usage: asciiprooftree [path] [marker]")
    System.exit(1)

def run(path: Path = Path.of(""), marker: String = "§"): Unit = println(s"$path $marker")
