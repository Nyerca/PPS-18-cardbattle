package controller
import exception.{MissingCellException, NoMovementException}
import model.{Bottom, Left, Move, PlayerRepresentation, RectangleCell, RectangleWithCell, Right, Top}

import scala.collection.mutable.ListBuffer

trait Dashboard {
  def cells: ListBuffer[RectangleWithCell]
  def player: PlayerRepresentation
  def player_(newPlayer: PlayerRepresentation):Unit

  def toString(): String
  def setCells(newList: ListBuffer[RectangleWithCell])
  def searchPosition(newX : Double, newY : Double): Option[RectangleCell]
  def searchPosition(newX : Double, newY : Double, movement: Move): Option[RectangleCell]
  def traslationX: Double
  def traslationY: Double
  def translationX_(newVal: Double):Unit
  def translationY_(newVal: Double):Unit

  def reset(list: ListBuffer[RectangleWithCell]): Unit

  def move(movement : Move, fun:(RectangleCell, String, Boolean) => Unit): Unit
}

class DashboardImpl (var cells: ListBuffer[RectangleWithCell]) extends  Dashboard {

  var player:PlayerRepresentation = _

  override def toString :String = {
    "Player: (" + player.position.x + ", " + player.position.getY + ")" + "  Translation: ("+ _traslationX + ", " +_traslationY + ")" + cells
  }

  override def player_(newPlayer: PlayerRepresentation):Unit = player = newPlayer

  override def setCells(newList: ListBuffer[RectangleWithCell]) { cells = newList}

  override def searchPosition(newX : Double, newY : Double): Option[RectangleCell] = {
    (for (rectangle <- cells if rectangle.isRectangle(newX, newY)) yield rectangle.rectCell).headOption
  }

  override def searchPosition(newX : Double, newY : Double, movement: Move): Option[RectangleCell] = {
    //println("Searching: (" + newX + ", " + newY + ")")
    (for (rectangle <- cells if  rectangle.isRectangle(newX, newY) && rectangle.rectCell.isMoveAllowed(movement)) yield rectangle.rectCell).headOption
  }

  private var _traslationX = 0.0
  private var _traslationY = 0.0
  override def traslationX: Double = _traslationX
  override def traslationY: Double = _traslationY
  override def translationX_(newVal: Double):Unit = _traslationX = newVal
  override def translationY_(newVal: Double):Unit = _traslationY = newVal

  override def reset(list: ListBuffer[RectangleWithCell]): Unit = {
    cells = list
    _traslationX = 0.0
    _traslationY = 0.0
    MovementAnimation.setAnimation(0, 0, 0, 0)
  }

  private def move(url : String, movement:Move, incX : Double, incY : Double, fun:(RectangleCell, String, Boolean) => Unit): Unit = {
    MovementAnimation.setAnimation(traslationX, traslationX + incX.toInt * (-5), traslationY, traslationY + incY.toInt * (-5))

    val newRectangle = this.searchPosition(player.position.x + incX.toInt * (-5), player.position.getY + incY.toInt * (-5), movement.opposite)

    println("PLAYER POSITION: " + player.position)
    println("MAP: " + this.toString)

    if(player.position.isMoveAllowed(movement)) {
      if(newRectangle.isDefined) {
        MovementAnimation.setAnimationIncrement(newRectangle.get, incX, incY, url, fun)
         _traslationX += incX * 5;
        _traslationY += incY * 5;

        println("ANIM: " + MovementAnimation.anim.fromX + " " + MovementAnimation.anim.fromY + " " + MovementAnimation.anim.toX + " " + MovementAnimation.anim.toY)
        MovementAnimation.anim.play();

      } else throw new MissingCellException
    } else throw new NoMovementException
  }


  override def move(movement : Move, fun:(RectangleCell, String, Boolean) => Unit): Unit = movement match {
    case Top  => move("top", movement, 0,+40, fun)
    case Right => move("right",movement, -40,0, fun)
    case Bottom => move("bot", movement, 0,-40, fun)
    case Left => move("left", movement, +40,0, fun)
    case _  => {}
  }

}
