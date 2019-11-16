package model

sealed abstract class Move

case object Top extends Move
case object Right extends Move
case object Bottom extends Move
case object Left extends Move




object Movement extends Enumeration {
  val Top, Right, Bottom, Left = Value
}