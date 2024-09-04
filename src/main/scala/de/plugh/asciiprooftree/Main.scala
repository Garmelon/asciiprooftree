package de.plugh.asciiprooftree

import de.plugh.asciiprooftree.file.Formatter
import org.rogach.scallop.*

import java.nio.file.{Files, Path}
import scala.jdk.StreamConverters.*

class Conf(args: Seq[String]) extends ScallopConf(args):
  val path: ScallopOption[Path] = trailArg[Path]()
  val marker: ScallopOption[String] = opt[String](default = Some("§"))
  verify()

@main
def main(args: String*): Unit =
  val conf = new Conf(args)
  reformat(conf.path(), conf.marker())

def reformat(path: Path, marker: String): Unit =
  if Files.isDirectory(path) then
    val files = Files.list(path).toScala(Seq)
    for file <- files do reformat(file, marker)
  else if Files.isRegularFile(path) then
    val text = Files.readString(path)
    val newText = Formatter.reformat(text, marker = marker)
    if text != newText then
      println(path)
      Files.writeString(path, newText)
