package controller

import exception.{MissingCellException, NoMovementException}
import model.{Bottom, Left, Move, PlayerRepresentation, RectangleCell, Right, Top}
import scala.collection.mutable.ListBuffer

trait Dashboard {
  def cells: List[RectangleCell]
  def player: PlayerRepresentation
  def toString: String
  def searchPosition(newX : Double, newY : Double): Option[RectangleCell]
  def searchPosition(newX : Double, newY : Double, movement: Move): Option[RectangleCell]
  def traslationX: Double
  def traslationY: Double
  def translationX_(newVal: Double):Unit
  def translationY_(newVal: Double):Unit
  def move(movement : Move, fun:(RectangleCell, String, Boolean) => Unit): Unit
}

class DashboardImpl (var cells: List[RectangleCell]) extends  Dashboard {

  var player:PlayerRepresentation = _

  override def toString :String = "Player: (" + player.position.x + ", " + player.position.y + ")" + "  Translation: ("+ _traslationX + ", " +_traslationY + ")" + cells

  override def searchPosition(newX : Double, newY : Double): Option[RectangleCell] = {
    (for (rectangle <- cells if rectangle.isRectangle(newX, newY)) yield rectangle).headOption
  }

  override def searchPosition(newX : Double, newY : Double, movement: Move): Option[RectangleCell] = {
    (for (rectangle <- cells if  rectangle.isRectangle(newX, newY) && rectangle.isMoveAllowed(movement)) yield rectangle).headOption
  }

  private var _traslationX = 0.0
  private var _traslationY = 0.0
  override def traslationX: Double = _traslationX
  override def traslationY: Double = _traslationY
  override def translationX_(newVal: Double):Unit = _traslationX = newVal
  override def translationY_(newVal: Double):Unit = _traslationY = newVal

  private def move(movement:Move, incX : Double, incY : Double, fun:(RectangleCell, String, Boolean) => Unit): Unit = {
    MovementAnimation.setAnimation(traslationX, traslationX + incX.toInt * (-5), traslationY, traslationY + incY.toInt * (-5))

    val newRectangle = this.searchPosition(player.position.x + incX.toInt * (-5), player.position.y + incY.toInt * (-5), movement.opposite)

    if(player.position.isMoveAllowed(movement)) {
      if(newRectangle.isDefined) {
        MovementAnimation.setAnimationIncrement(newRectangle.get, incX, incY, movement.url(), fun)
        _traslationX += incX * 5
        _traslationY += incY * 5
        MovementAnimation.anim.play()
      } else throw new MissingCellException
    } else throw new NoMovementException
  }

  override def move(movement : Move, fun:(RectangleCell, String, Boolean) => Unit): Unit = movement match {
    case Top  => move(movement, 0,+40, fun)
    case Right => move(movement, -40,0, fun)
    case Bottom => move(movement, 0,-40, fun)
    case Left => move(movement, +40,0, fun)
  }
}
