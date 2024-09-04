package de.plugh.asciiprooftree.tree

case class FormattedProofTree(lines: Lines, conclusionStart: Int, conclusionEnd: Int):
  def shift(delta: Int): FormattedProofTree = FormattedProofTree(
    lines = lines.shift(delta),
    conclusionStart = conclusionStart + delta,
    conclusionEnd = conclusionEnd + delta,
  )

  def extend(line: Line): FormattedProofTree = copy(lines = lines.extend(line))

  def joinHorizontally(right: FormattedProofTree): FormattedProofTree = FormattedProofTree.joinHorizontally(this, right)

  override def toString: String = lines.toString

object FormattedProofTree:
  val separation = 3

  def empty: FormattedProofTree = FormattedProofTree(lines = Lines.empty, conclusionStart = 0, conclusionEnd = 0)

  def joinHorizontally(left: FormattedProofTree, right: FormattedProofTree): FormattedProofTree =
    val (lines, deltaRight) = left.lines.joinHorizontally(right.lines)
    FormattedProofTree(
      lines = lines,
      conclusionStart = left.conclusionStart min (right.conclusionStart + deltaRight),
      conclusionEnd = left.conclusionEnd max (right.conclusionEnd + deltaRight),
    )
