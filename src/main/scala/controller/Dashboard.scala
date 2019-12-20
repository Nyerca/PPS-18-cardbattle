package controller

import exception.{MissingCellException, NoMovementException}
import model.{Bottom, Left, Move, PlayerRepresentation, RectangleCell, Right, Top}

trait Dashboard {
  var cells: List[RectangleCell]
  def toString: String
  def ? (newX : Double, newY : Double): Option[RectangleCell]
  def ? (newX : Double, newY : Double, movement: Move): Option[RectangleCell]
  var traslationX: Double
  var traslationY: Double
  def ->(movement : Move, player: PlayerRepresentation, fun:(RectangleCell, String, Boolean) => Unit): Unit
}


object Dashboard {

  /**
    * The class that keeps track of the cells of the map and the current traslations.
    *
    * @param cells
    * @param traslationX
    * @param traslationY
    */
  private class DashboardImpl (var cells: List[RectangleCell], var traslationX: Double = 0.0, var traslationY: Double = 0.0) extends  Dashboard {

    override def toString :String = "Translation: ("+ traslationX + ", " +traslationY + ")" + cells

    override def ? (newX : Double, newY : Double): Option[RectangleCell] = {
      (for (rectangle <- cells if rectangle.isRectangle(newX, newY)) yield rectangle).headOption
    }

    override def ? (newX : Double, newY : Double, movement: Move): Option[RectangleCell] = {
      (for (rectangle <- cells if  rectangle.isRectangle(newX, newY) && rectangle.isMoveAllowed(movement)) yield rectangle).headOption
    }

    private def move(movement:Move, player: PlayerRepresentation, incX : Double, incY : Double, fun:(RectangleCell, String, Boolean) => Unit): Unit = {
      MovementAnimation.setAnimation(traslationX, traslationX + incX.toInt * (-5), traslationY, traslationY + incY.toInt * (-5))

      val newRectangle = ? (player.position.x + incX.toInt * (-5), player.position.y + incY.toInt * (-5), movement.opposite)

      if(player.position.isMoveAllowed(movement)) {
        if(newRectangle.isDefined) {
          MovementAnimation.setAnimationIncrement(newRectangle.get, incX, incY, movement.url(), fun)
          traslationX += incX * 5
          traslationY += incY * 5
          MovementAnimation.anim.play()
        } else throw new MissingCellException
      } else throw new NoMovementException
    }

    override def ->(movement : Move, player: PlayerRepresentation, fun:(RectangleCell, String, Boolean) => Unit): Unit = movement match {
      case Top  => move(movement, player, 0,+40, fun)
      case Right => move(movement, player, -40,0, fun)
      case Bottom => move(movement, player, 0,-40, fun)
      case Left => move(movement, player, +40,0, fun)
    }
  }

  def apply(cells: List[RectangleCell]): Dashboard = new DashboardImpl(cells)
  def apply(cells: List[RectangleCell], traslationX: Double, traslationY: Double): Dashboard = new DashboardImpl(cells, traslationX, traslationY)
}


