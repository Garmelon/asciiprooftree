package de.plugh.asciiprooftree.file

import de.plugh.asciiprooftree.tree.{Line, Parser}

import scala.collection.mutable

private class Formatter(marker: String):
  private val lines: mutable.Buffer[String] = mutable.Buffer()
  private var block: Option[Block] = None

  private def flushBlock(): Unit = for block <- this.block do
    val newBlock = Parser(block.content).parse match
      case Some(tree) => block.replace(tree.formatted.toString.linesIterator.toIndexedSeq)
      case None => block
    lines.appendAll(newBlock.toLines)
    this.block = None

  private def pushBlockLine(prefix: String, content: String): Unit = this.block match
    case Some(block) if Line(block.last._1).width == Line(prefix).width =>
      this.block = Some(block.extend(prefix, content))
    case _ =>
      flushBlock()
      this.block = Some(Block(prefix, content))

  private def pushPlainLine(line: String): Unit =
    flushBlock()
    lines.append(line)

  private def pushLine(line: String): Unit =
    val i = line.indexOf(marker)
    if i < 0 then pushPlainLine(line)
    else
      val prefix = line.slice(0, i + marker.length)
      val content = line.slice(i + marker.length, line.length)
      pushBlockLine(prefix, content)

  private def pushText(text: String): Unit = text.linesIterator.foreach(pushLine)

  override def toString: String = lines.map(l => s"$l\n").mkString

object Formatter:
  def reformat(text: String, marker: String = "§"): String =
    val fmt = new Formatter(marker)
    fmt.pushText(text)
    fmt.toString
