package de.plugh.asciiprooftree.tree

case class Lines(lines: IndexedSeq[Line]):
  def height: Int = lines.length
  def at(y: Int): Option[Line] = lines.lift(y)

  def shift(delta: Int): Lines = Lines(lines.map(_.shift(delta)))
  def extend(line: Line): Lines = Lines(line +: lines)

  def joinHorizontally(right: Lines): (Lines, Int) = Lines.joinHorizontally(this, right)

  override def toString: String = lines.reverse.mkString("\n")

object Lines:
  def empty: Lines = Lines(IndexedSeq())

  private def minDelta(left: Option[Line], right: Option[Line], separation: Int): Int = (left, right) match
    case (Some(left), Some(right)) => (separation - left.distanceTo(right)).max(0)
    case _ => 0

  private def minDeltaForVisualSeparation(left: Lines, right: Lines, separation: Int): Int =
    var result = 0
    for y <- 0 until (left.height max right.height) do
      result = result.max(minDelta(left.at(y), right.at(y - 1), separation))
      result = result.max(minDelta(left.at(y), right.at(y), separation))
      result = result.max(minDelta(left.at(y), right.at(y + 1), separation))
    result

  def joinHorizontally(left: Lines, right: Lines, separation: Int = 3): (Lines, Int) =
    val deltaRight = minDeltaForVisualSeparation(left, right, separation)
    val lines =
      for y <- 0 until (left.height max right.height) yield (left.at(y), right.at(y)) match
        case (Some(left), Some(right)) => left.join(right.shift(deltaRight))
        case (Some(left), None) => left
        case (None, Some(right)) => right.shift(deltaRight)
        case (None, None) => ???
    (Lines(lines), deltaRight)
