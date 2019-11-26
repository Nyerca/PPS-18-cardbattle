package model

trait Move {
  def opposite() : Move
}

case object Top extends Move {
  override def opposite(): Move = {
    Bottom
  }
}
case object Right extends Move {
  override def opposite(): Move = {
    Left
  }
}
case object Bottom extends Move {
  override def opposite(): Move = {
    Top
  }
}
case object Left extends Move {
  override def opposite(): Move = {
    Right
  }
}
