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

  def containsNoLines: Boolean = line.isEmpty && premises.forall(_.containsNoLines)

object ProofTree:
  def empty: ProofTree = ProofTree()
  def star: ProofTree = ProofTree().addConclusion("*")
