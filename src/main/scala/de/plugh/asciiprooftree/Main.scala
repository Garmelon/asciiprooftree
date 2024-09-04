package de.plugh.asciiprooftree

import de.plugh.asciiprooftree.file.Formatter
import org.rogach.scallop.*

import java.nio.file.{Files, Path}

class Conf(args: Seq[String]) extends ScallopConf(args):
  val path: ScallopOption[Path] = trailArg[Path]()
  val marker: ScallopOption[String] = opt[String](default = Some("§"))
  verify()

@main
def main(args: String*): Unit =
  val conf = new Conf(args)
  val text = Files.readString(conf.path())
  val newText = Formatter.reformat(text, marker = conf.marker())
  Files.writeString(conf.path(), newText)
