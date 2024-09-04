package de.plugh.asciiprooftree

import de.plugh.asciiprooftree.file.Formatter

import java.nio.file.{Files, Path}

@main
def main(args: String*): Unit = args match
  case Seq() => run()
  case Seq(path) => run(Path.of(path))
  case Seq(path, marker) => run(Path.of(path), marker)
  case _ =>
    println("Usage: asciiprooftree [path] [marker]")
    System.exit(1)

@main
def testMain(path: String): Unit = run(Path.of(path))

def run(path: Path = Path.of(""), marker: String = "§"): Unit =
  println(s"Path: $path")
  println(s"Marker: $marker")
  val text = Files.readString(path)
  val newText = Formatter.reformat(text, marker = marker)
  Files.writeString(path, newText)
