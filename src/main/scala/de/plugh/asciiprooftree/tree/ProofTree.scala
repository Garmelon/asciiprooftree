package de.plugh.asciiprooftree.tree

case class ProofTree(premises: Seq[ProofTree] = Seq(), line: Option[String] = None, conclusion: Option[String] = None):
  def addPremise(premise: ProofTree): ProofTree = copy(premises = premises :+ premise)
  def addPremiseBefore(premise: ProofTree): ProofTree = copy(premises = premise +: premises)

  def addLine(rule: String = ""): ProofTree =
    if this.conclusion.isEmpty && this.line.isEmpty then copy(line = Some(rule))
    else ProofTree().addPremise(this).addLine(rule)

  def addConclusion(conclusion: String): ProofTree =
    if this.conclusion.isEmpty then copy(conclusion = Some(conclusion))
    else ProofTree().addPremise(this).addConclusion(conclusion)

  private def formatLine(start: Int, end: Int, rule: String): Line =
    val lineStr = "-" * (end - start)
    val lineText = if rule.isEmpty then lineStr else s"$lineStr $rule"
    Line(lineText, start)

  def formatted: FormattedProofTree =
    val fPremises = premises.map(_.formatted).reduceOption(_.joinHorizontally(_)).getOrElse(FormattedProofTree.empty)

    val lConclusion = conclusion match
      case Some(conclusion) => Line(conclusion)
      case None => return line match
          case Some(rule) => fPremises.extend(formatLine(fPremises.conclusionStart, fPremises.conclusionEnd, rule))
          case None => fPremises

    val aboveMiddle = (fPremises.conclusionStart + fPremises.conclusionEnd) / 2
    val belowMiddle = (lConclusion.start + lConclusion.end) / 2

    val (aboveCentered, belowCentered) =
      if aboveMiddle < belowMiddle then (fPremises.shift(belowMiddle - aboveMiddle), lConclusion)
      else if aboveMiddle > belowMiddle then (fPremises, lConclusion.shift(aboveMiddle - belowMiddle))
      else (fPremises, lConclusion)

    val combined = line match
      case Some(rule) =>
        val lineStart = aboveCentered.conclusionStart min belowCentered.start
        val lineEnd = aboveCentered.conclusionEnd max belowCentered.end
        aboveCentered.extend(formatLine(lineStart, lineEnd, rule)).extend(belowCentered)
      case None => aboveCentered.extend(belowCentered)

    combined.copy(conclusionStart = belowCentered.start, conclusionEnd = belowCentered.end)

object ProofTree:
  def empty: ProofTree = ProofTree()
  def star: ProofTree = ProofTree().addConclusion("*")
