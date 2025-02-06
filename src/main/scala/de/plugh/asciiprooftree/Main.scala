package de.plugh.asciiprooftree

import de.plugh.asciiprooftree.file.Formatter
import org.rogach.scallop.*

import java.nio.file.{Files, Path}
import scala.jdk.StreamConverters.*
import scala.util.matching.Regex

val blockRe = "(?m)^(?<block>(\\s*)§.*\\n(?:\\2§.*\\n)*)".r
val lineRe = "^\\s*§(?<block>.*)$".r

class Conf(args: Seq[String]) extends ScallopConf(args):
  val path: ScallopOption[Path] = trailArg[Path]()
  val blockRegex: ScallopOption[String] = opt[String](default = Some(blockRe.regex))
  val lineRegex: ScallopOption[String] = opt[String](default = Some(lineRe.regex))
  verify()

@main
def main(args: String*): Unit =
  val conf = new Conf(args)
  val formatter = Formatter(blockRe = Regex(conf.blockRegex()), lineRe = Regex(conf.lineRegex()))
  reformat(conf.path(), formatter)

def reformat(path: Path, formatter: Formatter): Unit =
  if Files.isDirectory(path) then
    val files = Files.list(path).toScala(Seq)
    for file <- files do reformat(file, formatter)
  else if Files.isRegularFile(path) then
    val text = Files.readString(path)
    val newText = formatter.reformat(text)
    if text != newText then
      println(path)
      Files.writeString(path, newText)
