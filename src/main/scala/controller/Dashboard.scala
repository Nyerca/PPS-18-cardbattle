package controller

import exception.{MissingCellException, NoMovementException}
import model.{Bottom, Left, Move, PlayerRepresentation, RectangleCell, Right, Top}

/*
trait Dashboard {
  def toString: String
  def ? (cells: List[RectangleCell], newX : Double, newY : Double): Option[RectangleCell]
  def ? (cells: List[RectangleCell], newX : Double, newY : Double, movement: Move): Option[RectangleCell]
  var traslationX: Double
  var traslationY: Double
  def ->(movement : Move, cells: List[RectangleCell], player: PlayerRepresentation, fun:(RectangleCell, String, Boolean) => Unit): Unit
}


object Dashboard {
  /**
    * The class that handle the movement given the cells of the map and the current traslations.
    *
    * @param traslationX The current traslationX of the map.
    * @param traslationY The current traslationY of the map.
    */
  private class DashboardImpl (var traslationX: Double = 0.0, var traslationY: Double = 0.0) extends  Dashboard {

    override def toString :String = "Translation: ("+ traslationX + ", " +traslationY + ")"

    /**
      * Check for existing cell in the map given (x, y)
      *
      * @param cells the cells of the map
      * @param newX the x value of the cell to be found.
      * @param newY the y value of the cell to be found.
      * @return the Option which describe the RectangleCell whether it's there.
      */
    override def ? (cells: List[RectangleCell], newX : Double, newY : Double): Option[RectangleCell] = {
      (for (rectangle <- cells if rectangle.isRectangle(newX, newY)) yield rectangle).headOption
    }

    /**
      * Check for existing cell in the map given (x, y) and the direction of the movement
      *
      * @param cells the cells of the map
      * @param newX the x value of the cell to be found.
      * @param newY the y value of the cell to be found.
      * @param movement the direction of tbe movement.
      * @return the Option which describe the RectangleCell whether it's there.
      */
    override def ? (cells: List[RectangleCell], newX : Double, newY : Double, movement: Move): Option[RectangleCell] = {
      (for (rectangle <- cells if  rectangle.isRectangle(newX, newY) && rectangle.isMoveAllowed(movement)) yield rectangle).headOption
    }

    private def move(movement:Move, cells: List[RectangleCell], player: PlayerRepresentation, incX : Double, incY : Double, fun:(RectangleCell, String, Boolean) => Unit): Unit = {
      MovementAnimation.setAnimation(traslationX, traslationX + incX.toInt * (-5), traslationY, traslationY + incY.toInt * (-5))

      val newRectangle = ? (cells, player.position.x + incX.toInt * (-5), player.position.y + incY.toInt * (-5), movement.opposite)

      if(player.position.isMoveAllowed(movement)) {
        if(newRectangle.isDefined) {
          MovementAnimation.setAnimationIncrement(newRectangle.get, incX, incY, movement.url(), fun)
          traslationX += incX * 5
          traslationY += incY * 5
          MovementAnimation.anim.play()
        } else throw new MissingCellException
      } else throw new NoMovementException
    }

    /**
      * Handle the movement in a specific direction.
      *
      * @param movement the direction of the movement.
      * @param cells the cells of the map
      * @param player the player that is moving.
      * @param fun the function to call at the end of the movement.
      */
    override def ->(movement : Move, cells: List[RectangleCell], player: PlayerRepresentation, fun:(RectangleCell, String, Boolean) => Unit): Unit = movement match {
      case Top  => move(movement, cells, player, 0,+40, fun)
      case Right => move(movement, cells, player, -40,0, fun)
      case Bottom => move(movement, cells, player, 0,-40, fun)
      case Left => move(movement, cells, player, +40,0, fun)
    }
  }

  def apply(): Dashboard = new DashboardImpl()
  def apply(traslationX: Double, traslationY: Double): Dashboard = new DashboardImpl(traslationX, traslationY)
}
*/

