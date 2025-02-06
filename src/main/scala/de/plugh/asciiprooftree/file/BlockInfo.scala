package de.plugh.asciiprooftree.file

import de.plugh.asciiprooftree.tree.ProofTree

case class BlockInfo(block: Block, tree: ProofTree, start: Int, end: Int, endsWithNewline: Boolean)
