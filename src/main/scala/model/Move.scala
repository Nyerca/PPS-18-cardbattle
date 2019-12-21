package model

trait Move {
  def opposite : Move
  def url(): String
}

case object Top extends Move {
  override def opposite: Move = Bottom
  override def url():String = "/player/top"
}
case object Right extends Move {
  override def opposite: Move = Left
  override def url():String = "/player/right"
}
case object Bottom extends Move {
  override def opposite: Move = Top
  override def url():String = "/player/bot"
}
case object Left extends Move {
  override def opposite: Move = Right
  override def url():String = "/player/left"
}
