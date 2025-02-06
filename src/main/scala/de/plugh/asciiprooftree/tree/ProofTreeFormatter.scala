package de.plugh.asciiprooftree.tree

case class ProofTreeFormatter(separation: Int = 3, lineOverhang: Int = 0):
  private def formatLine(start: Int, end: Int, rule: String): Line =
    val lineStart = start - lineOverhang
    val lineEnd = end + lineOverhang
    val lineStr = "-" * (lineEnd - lineStart)
    val lineText = if rule.isEmpty then lineStr else s"$lineStr $rule"
    Line(lineText, lineStart)

  private def centerPremisesAndConclusion(above: FormattedProofTree, below: Line): (FormattedProofTree, Line) =
    val aboveStart = above.conclusionStart
    val aboveWidth = above.conclusionEnd - above.conclusionStart
    val belowStart = below.start
    val belowWidth = below.end - below.start

    // Distribute remaining space evenly, but breaking ties by making the left side smaller than the right side.
    // This biases the centering algorithm to the left when rounding.
    val remainingWidth = (aboveWidth max belowWidth) - (aboveWidth min belowWidth)
    val targetIndent = remainingWidth / 2

    if belowWidth < aboveWidth then
      val targetStart = aboveStart + targetIndent
      (above, below.shift(targetStart - belowStart))
    else if belowWidth > aboveWidth then
      val targetStart = belowStart + targetIndent
      (above.shift(targetStart - aboveStart), below)
    else (above, below)

  def formatTree(tree: ProofTree): FormattedProofTree =
    val fPremises = tree
      .premises
      .map(formatTree)
      .reduceOption(_.joinHorizontally(_, separation))
      .getOrElse(FormattedProofTree.empty)

    val lConclusion = tree.conclusion match
      case Some(conclusion) => Line(conclusion)
      case None => return tree.line match
          case Some(rule) => fPremises.extend(formatLine(fPremises.conclusionStart, fPremises.conclusionEnd, rule))
          case None => fPremises

    val (aboveCentered, belowCentered) = centerPremisesAndConclusion(fPremises, lConclusion)

    val combined = tree.line match
      case Some(rule) =>
        val lineStart = aboveCentered.conclusionStart min belowCentered.start
        val lineEnd = aboveCentered.conclusionEnd max belowCentered.end
        aboveCentered.extend(formatLine(lineStart, lineEnd, rule)).extend(belowCentered)
      case None => aboveCentered.extend(belowCentered)

    combined.copy(conclusionStart = belowCentered.start, conclusionEnd = belowCentered.end)
