package de.plugh.asciiprooftree.tree

case class ProofTreeFormatter(lineOverhang: Int = 0):
  private def formatLine(start: Int, end: Int, rule: String): Line =
    val lineStart = start - lineOverhang
    val lineEnd = end + lineOverhang
    val lineStr = "-" * (lineEnd - lineStart)
    val lineText = if rule.isEmpty then lineStr else s"$lineStr $rule"
    Line(lineText, lineStart)

  def formatTree(tree: ProofTree): FormattedProofTree =
    val fPremises = tree
      .premises
      .map(formatTree)
      .reduceOption(_.joinHorizontally(_))
      .getOrElse(FormattedProofTree.empty)

    val lConclusion = tree.conclusion match
      case Some(conclusion) => Line(conclusion)
      case None => return tree.line match
          case Some(rule) => fPremises.extend(formatLine(fPremises.conclusionStart, fPremises.conclusionEnd, rule))
          case None => fPremises

    val aboveMiddle = (fPremises.conclusionStart + fPremises.conclusionEnd) / 2
    val belowMiddle = (lConclusion.start + lConclusion.end) / 2

    val (aboveCentered, belowCentered) =
      if aboveMiddle < belowMiddle then (fPremises.shift(belowMiddle - aboveMiddle), lConclusion)
      else if aboveMiddle > belowMiddle then (fPremises, lConclusion.shift(aboveMiddle - belowMiddle))
      else (fPremises, lConclusion)

    val combined = tree.line match
      case Some(rule) =>
        val lineStart = aboveCentered.conclusionStart min belowCentered.start
        val lineEnd = aboveCentered.conclusionEnd max belowCentered.end
        aboveCentered.extend(formatLine(lineStart, lineEnd, rule)).extend(belowCentered)
      case None => aboveCentered.extend(belowCentered)

    combined.copy(conclusionStart = belowCentered.start, conclusionEnd = belowCentered.end)
