package de.plugh.asciiprooftree.tree

case class Sequent(ante: String, succ: String):
  override def toString: String = Seq(Some(ante).filter(_.isBlank), Some("⊢"), Some(succ).filter(_.isBlank))
    .flatten
    .mkString(" ")
