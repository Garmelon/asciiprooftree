package de.plugh.asciiprooftree

import de.plugh.asciiprooftree.file.FileFormatter
import org.rogach.scallop.*

import java.nio.file.{Files, Path}
import scala.jdk.StreamConverters.*
import scala.util.matching.Regex

val markerBlockRe = "(?m)^(?<block>(\\s*)§.*\\n(?:\\2§.*\\n)*)".r
val markerLineRe = "^\\s*§(?<block>.*)$".r
val scalaDocstringBlockRe = "(?m)^(\\s*)\\* \\{\\{\\{\\n(?<block>(\\1\\*.*\\n)+?)\\1\\* }}}\\n".r
val scalaDocstringLineRe = "^\\s*\\*(?<block>.*)$".r

class Conf(args: Seq[String]) extends ScallopConf(args):
  val path: ScallopOption[Path] = trailArg[Path]()
  val blockRegex: ScallopOption[String] = opt[String]()
  val lineRegex: ScallopOption[String] = opt[String]()
  val useScalaDocstringRegexes: ScallopOption[Boolean] = opt[Boolean]()
  val noHeuristics: ScallopOption[Boolean] = opt[Boolean]()
  val indent: ScallopOption[Int] = opt[Int](default = Some(2))
  val lineOverhang: ScallopOption[Int] = opt[Int](default = Some(0))
  verify()

@main
def main(args: String*): Unit =
  val conf = new Conf(args)

  val (defaultBlockRe, defaultLineRe) =
    if conf.useScalaDocstringRegexes() then (scalaDocstringBlockRe, scalaDocstringLineRe)
    else (markerBlockRe, markerLineRe)

  val formatter = FileFormatter(
    blockRe = conf.blockRegex.map(Regex(_)).getOrElse(defaultBlockRe),
    lineRe = conf.lineRegex.map(Regex(_)).getOrElse(defaultLineRe),
    heuristics = !conf.noHeuristics(),
    indent = conf.indent(),
    lineOverhang = conf.lineOverhang(),
  )

  reformat(conf.path(), formatter)

def reformat(path: Path, formatter: FileFormatter): Unit =
  if Files.isDirectory(path) then
    val files = Files.list(path).toScala(Seq)
    for file <- files do reformat(file, formatter)
  else if Files.isRegularFile(path) then
    val text = Files.readString(path)
    val newText = formatter.reformat(text)
    if text != newText then
      println(path)
      Files.writeString(path, newText)
