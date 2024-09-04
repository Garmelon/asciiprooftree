package de.plugh.asciiprooftree.tree

import scala.annotation.tailrec

case class Parser(lines: Lines):
  private def at(y: Int, start: Int, end: Int): Option[Line] = lines.at(y).flatMap(_.sliceOpt(start, end))

  def parse: Option[ProofTree] = parseAt(0, 0, Integer.MAX_VALUE)

  def parseAt(y: Int, start: Int, end: Int): Option[ProofTree] =
    val line = at(y, start, end) match
      case Some(value) => value.trim
      case None => return None

    val split = splitAtWhitespace(line)
    if split.isEmpty then None
    else if split.length == 1 then Some(parseSingleTreeAt(y, split.head))
    else Some(parseMultipleTreesAt(y, split))

  private def parseSingleTreeAt(y: Int, line: Line): ProofTree =
    if line.text == "*" then return ProofTree.star
    val (aboveStart, aboveEnd) = extendRange(y + 1, line.start, line.end)
    val above = parseAt(y + 1, aboveStart, aboveEnd).getOrElse(ProofTree.empty)
    parseAsLine(line) match
      case Some(rule) => above.addLine(rule.getOrElse(""))
      case None => above.addConclusion(line.text)

  private def parseMultipleTreesAt(y: Int, lines: Seq[Line]): ProofTree =
    ProofTree(premises = lines.map(parseSingleTreeAt(y, _)))

  private def parseAsLine(line: Line): Option[Option[String]] = """^-+( (\S.*))?$"""
    .r
    .findFirstMatchIn(line.text)
    .map(m => Option[String](m.group(2)))

  private def splitAtWhitespace(line: Line, atLeast: Int = 3): Seq[Line] =
    val trimmed = line.trim
    trimmed.indexOf(" " * atLeast) match
      case None if trimmed.text.isEmpty => Seq()
      case None => Seq(trimmed)
      case Some(start) =>
        val left = trimmed.slice(trimmed.start, start)
        val right = trimmed.slice(start, trimmed.end)
        left +: splitAtWhitespace(right, atLeast)

  private def extendRange(y: Int, start: Int, end: Int): (Int, Int) =
    val line = lines.at(y) match
      case Some(value) => value
      case None => return (start, end)
    val newStart = extendRangeStart(line, start, spaceAllowed = true)
    val newEnd = extendRangeEnd(line, end, spaceAllowed = true)
    (newStart, newEnd)

  @tailrec
  private def extendRangeStart(line: Line, start: Int, spaceAllowed: Boolean): Int = line.at(start - 1) match
    case Some(' ') if spaceAllowed => extendRangeStart(line, start - 1, spaceAllowed = false)
    case Some(c) if !Character.isWhitespace(c) => extendRangeStart(line, start - 1, spaceAllowed = true)
    case _ => start

  @tailrec
  private def extendRangeEnd(line: Line, end: Int, spaceAllowed: Boolean): Int = line.at(end) match
    case Some(' ') if spaceAllowed => extendRangeEnd(line, end + 1, spaceAllowed = false)
    case Some(c) if !Character.isWhitespace(c) => extendRangeEnd(line, end + 1, spaceAllowed = true)
    case _ => end

object Parser:
  def apply(lines: Seq[String]): Parser =
    val trimmedLines = lines.reverseIterator.map(Line(_).trim).toIndexedSeq
    Parser(Lines(trimmedLines))

  def apply(text: String): Parser = apply(text.linesIterator.toSeq)
