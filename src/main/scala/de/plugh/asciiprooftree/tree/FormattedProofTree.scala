package de.plugh.asciiprooftree.tree

case class FormattedProofTree(lines: Lines, conclusionStart: Int, conclusionEnd: Int):
  def shift(delta: Int): FormattedProofTree = FormattedProofTree(
    lines = lines.shift(delta),
    conclusionStart = conclusionStart + delta,
    conclusionEnd = conclusionEnd + delta,
  )

  /** Shift so that the start is always zero. */
  def shiftAlignLeft: FormattedProofTree = shift(-lines.lines.map(_.start).minOption.getOrElse(0))

  def extend(line: Line): FormattedProofTree = copy(lines = lines.extend(line))

  def joinHorizontally(right: FormattedProofTree, separation: Int): FormattedProofTree = FormattedProofTree
    .joinHorizontally(this, right, separation)

  def toLines: IndexedSeq[String] = lines.toLines

object FormattedProofTree:
  def empty: FormattedProofTree = FormattedProofTree(lines = Lines.empty, conclusionStart = 0, conclusionEnd = 0)

  def joinHorizontally(left: FormattedProofTree, right: FormattedProofTree, separation: Int): FormattedProofTree =
    val (lines, deltaRight) = left.lines.joinHorizontally(right.lines, separation)
    FormattedProofTree(
      lines = lines,
      conclusionStart = left.conclusionStart min (right.conclusionStart + deltaRight),
      conclusionEnd = left.conclusionEnd max (right.conclusionEnd + deltaRight),
    )
