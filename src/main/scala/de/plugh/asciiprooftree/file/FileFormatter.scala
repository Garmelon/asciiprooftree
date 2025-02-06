package de.plugh.asciiprooftree.file

import de.plugh.asciiprooftree.tree.{Parser, ProofTreeFormatter}

import scala.collection.mutable
import scala.util.boundary
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

case class FileFormatter(blockRe: Regex, lineRe: Regex, heuristics: Boolean, indent: Int, lineOverhang: Int):
  private val blockReI = blockRe.pattern.namedGroups().get("block")
  private val lineReI = lineRe.pattern.namedGroups().get("block")
  private val proofTreeFormatter = ProofTreeFormatter(lineOverhang = lineOverhang)

  private def parseBlockLine(line: String): Option[Block] = boundary:
    val m = lineRe.findFirstMatchIn(line).getOrElse(boundary.break(None))
    require(m.end(lineReI) == line.length)
    val prefix = line.slice(0, m.start(lineReI))
    val content = line.slice(m.start(lineReI), line.length)
    Some(Block(prefix, content))

  private def parseBlockLines(lines: String): Option[Block] =
    val blocks = lines.linesIterator.map(parseBlockLine).toSeq
    if blocks.isEmpty || blocks.exists(_.isEmpty) then return None
    Some(blocks.flatten.reduce(_.extend(_)))

  private def parseBlock(text: String, m: Match): Option[BlockInfo] = boundary:
    val block = parseBlockLines(m.group(blockReI)).getOrElse(boundary.break(None))
    val tree = Parser(block.content).parse.getOrElse(boundary.break(None))
    if heuristics && tree.containsNoLines then return None
    Some(BlockInfo(
      block = block,
      tree = tree,
      start = m.start(blockReI),
      end = m.end(blockReI),
      endsWithNewline = text.endsWith("\n"),
    ))

  def findBlocks(text: String): Seq[BlockInfo] = blockRe.findAllMatchIn(text).flatMap(parseBlock(text, _)).toSeq

  def reformat(text: String): String =
    // Things just become nicer if we can assume that even the last line ends with a newline.
    val cleanText = if text.endsWith("\n") then text else text + "\n"

    val result = StringBuilder()
    var resultEnd = 0

    for info <- findBlocks(cleanText) do
      if resultEnd < info.start then result.append(cleanText.slice(resultEnd, info.start))
      val formattedTree = proofTreeFormatter.formatTree(info.tree)
      val block = info.block.replace(formattedTree.shiftAlignLeft.toLines)
      result.append(block.toLines(indent).mkString("\n"))
      if info.endsWithNewline then result.append("\n")
      resultEnd = info.end

    // No need to update resultEnd since we don't need it from this point on
    if resultEnd < cleanText.length then result.append(cleanText.slice(resultEnd, cleanText.length))

    // Remove final newline if the original text didn't have it
    if text.endsWith("\n") then result.toString() else result.toString().stripLineEnd
