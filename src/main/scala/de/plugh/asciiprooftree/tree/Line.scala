package de.plugh.asciiprooftree.tree

case class Line(text: String, start: Int = 0):
  val width: Int = text.codePointCount(0, text.length)
  val end: Int = start + width

  def at(i: Int): Option[Int] =
    if i < start || i >= end then return None
    Some(text.codePointAt(text.offsetByCodePoints(0, i - start)))

  def indexOf(str: String): Option[Int] =
    val result = text.indexOf(str)
    if result < 0 then return None
    Some(start + text.codePointCount(0, result))

  /** The distance from the right side of this line to the left side of the other line. */
  def distanceTo(right: Line): Int = right.start - end

  def shift(delta: Int): Line = copy(start = start + delta)

  def join(right: Line): Line =
    val between = distanceTo(right)
    require(between >= 0, "lines overlap")
    copy(text = this.text + (" " * between) + right.text)

  /** Take a slice of this line. The range specified must overlap or touch the text of the line. */
  def slice(start: Int, end: Int): Line =
    require(start <= end, "end before start")
    require(start <= this.end && this.start <= end, "non-overlapping range")
    val clampedStart = start max this.start
    val clampedEnd = end min this.end
    val sliceStart = text.offsetByCodePoints(0, clampedStart - this.start)
    val sliceEnd = text.offsetByCodePoints(sliceStart, clampedEnd - clampedStart)
    Line(text = text.slice(sliceStart, sliceEnd), start = clampedStart)

  def sliceOpt(start: Int, end: Int): Option[Line] =
    if end < this.start || this.end < start then return None
    Some(slice(start, end))

  def trim: Line =
    var textStart = 0
    while textStart < text.length && text.codePointAt(textStart) == ' ' do textStart += 1
    var textEnd = text.length
    while textStart < textEnd && text.codePointBefore(textEnd) == ' ' do textEnd -= 1
    slice(start + textStart, start + textEnd)

  override def toString: String =
    require(start >= 0)
    " " * start + text
