package controller
import exception.{MissingCellException, NoMovementException}
import model.{Bottom, Left, Move, PlayerWithCell, RectangleCell, RectangleWithCell, Right, Top}


import scala.collection.mutable.ListBuffer

class Dashboard (var cells: ListBuffer[RectangleWithCell], player: PlayerWithCell) {
  def showMap(): Unit = {
    for (rectangle <- cells) yield {
      println("RECTANGLE: (" + rectangle.getX + ", " + rectangle.getY+ ") w: "+ rectangle.getWidth + " h: " + rectangle.getHeight)
    }
  }

  def setCells(newList: ListBuffer[RectangleWithCell]) { cells = newList}

  def searchPosition(newX : Double, newY : Double): Option[RectangleCell] = {
    //for(rectangle <- cells if(rectangle.getX == newX)) yield rectangle
    (for (rectangle <- cells if rectangle.getX <= newX && rectangle.getY <= newY && rectangle.getX + rectangle.getWidth > newX && rectangle.getY + rectangle.getHeight > newY) yield rectangle.rectCell).headOption
  }

  def searchPosition(newX : Double, newY : Double, movement: Move): Option[RectangleCell] = {
    //for(rectangle <- cells if(rectangle.getX == newX)) yield rectangle
    println("Searching: (" + newX + ", " + newY + ")")
    for (rectangle <- cells if  rectangle.getX <= newX && rectangle.getY <= newY && rectangle.getX + rectangle.getWidth > newX && rectangle.getY + rectangle.getHeight > newY) yield {println("RE: " + rectangle); if(rectangle.rectCell.isMoveAllowed(movement)) println("ALSO MOVE")}
    (for (rectangle <- cells if  rectangle.getX <= newX && rectangle.getY <= newY && rectangle.getX + rectangle.getWidth > newX && rectangle.getY + rectangle.getHeight > newY && rectangle.rectCell.isMoveAllowed(movement)) yield rectangle.rectCell).headOption
  }

  private var _traslationX = 0.0
  private var _traslationY = 0.0
  def traslationX = _traslationX
  def traslationY = _traslationY


  def printInfos(): Unit = {
    println("Player position: (" + player.player.position.x + ", " + player.player.position.getY + ")")
    println("Translation: (" + _traslationX + ", " +_traslationY + ")")
  }


  def move(url : String, movement:Move, incX : Double, incY : Double, fun:(RectangleCell, String, Boolean) => Unit): Unit = {
    MovementAnimation.setAnimation(traslationX, traslationX + incX.toInt * (-5), traslationY, traslationY + incY.toInt * (-5))
    //printInfos
    val newRectangle = this.searchPosition(player.player.position.x + incX.toInt * (-5), player.player.position.getY + incY.toInt * (-5), movement.opposite)

    if(player.player.position.isMoveAllowed(movement)) {
      if(newRectangle.isDefined) {
        MovementAnimation.setAnimationIncrement(newRectangle.get, incX, incY, url, fun,player)
         _traslationX += incX * 5;
        _traslationY += incY * 5;
        MovementAnimation.anim.play();

      } else {
        throw new MissingCellException
      }
    } else {
      throw new NoMovementException
    }
  }

  def printCells(): Unit = {
    println("ANY ENEMY?")
    for(el <- cells) {
      println(el.rectCell.enemy)
    }
  }


  def move(movement : Move, fun:(RectangleCell, String, Boolean) => Unit): Unit = movement match {
    case Top  => {
      move("top", movement, 0,+40, fun)
    }
    case Right => {
      move("right",movement, -40,0, fun)
    }
    case Bottom => {
      move("bot", movement, 0,-40, fun)
    }
    case Left => {
      move("left", movement, +40,0, fun)
    }
    case _  => {}
  }

}
