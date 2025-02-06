package de.plugh.asciiprooftree.file

case class Block(lines: Seq[(String, String)]):
  require(lines.nonEmpty)

  def last: (String, String) = lines.last
  def content: Seq[String] = lines.map((_, content) => content)
  def toLines(indent: Int): Seq[String] = lines.map((prefix, content) => prefix + " " * indent + content)

  def extend(block: Block): Block = Block(lines ++ block.lines)
  def extend(prefix: String, content: String): Block = extend(Block(prefix, content))

  def resize(height: Int): Block =
    require(height > 0)
    if height < lines.length then return Block(lines.take(height))
    if height == lines.length then return this
    Block(lines ++ Seq.fill(height - lines.length)(lines.last))

  def replace(newContent: IndexedSeq[String]): Block =
    require(newContent.nonEmpty)
    Block(resize(newContent.length).lines.zip(newContent).map { case ((prefix, _), content) => (prefix, content) })

object Block:
  def apply(prefix: String, content: String): Block = Block(Seq((prefix, content)))
