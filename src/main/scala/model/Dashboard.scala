package model

import controller.MovementAnimation
import exception.{MissingCellException, NoMovementException}


trait Dashboard extends Observable{
  var list: List[RectangleCell]
  var selected: Option[Cell]
  var traslationX : Double
  var traslationY : Double
  def player: PlayerRepresentation

  def removeEnemyCell(): Unit
  def postInsert(): Unit
  def setPlayer(newPosition: RectangleCell, newUrl: String): Unit
  def getAllEnemies: Int
  def ? (cells: List[RectangleCell], newX : Double, newY : Double): Option[RectangleCell]
  def ? (cells: List[RectangleCell], newX : Double, newY : Double, movement: Move): Option[RectangleCell]
  def -> (movement : Move): Unit
}

object Dashboard {

  private class DashboardImpl(var list: List[RectangleCell], startingDefined : Option[RectangleCell], var traslationX:Double, var traslationY:Double, user: Player) extends Dashboard {
    var selected: Option[Cell] = Option.empty

    override def removeEnemyCell(): Unit = list.collect { case f if f.mapEvent.isDefined && f == player.position && f.mapEvent.get.cellEvent.isInstanceOf[Enemy] => f.mapEvent_(Option.empty); postInsert() }
    private def removeChestCell(): Unit = list.collect { case f if f.mapEvent.isDefined && f == player.position && f.mapEvent.get.cellEvent.isInstanceOf[Chest] => f.mapEvent_(Option.empty); postInsert() }

    var player : PlayerRepresentation = _
    startingDefined match {
      case Some(rect: RectangleCell) => player = PlayerRepresentation((? (list, rect.x, rect.y)).get, "/player/bot.png")
      case _ => player = PlayerRepresentation(list.head, "/player/bot.png")
    }


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

    private def move(movement:Move, cells: List[RectangleCell], player: PlayerRepresentation, incX : Double, incY : Double): Unit = {
      MovementAnimation.setAnimation(traslationX, traslationX + incX.toInt * (-5), traslationY, traslationY + incY.toInt * (-5))

      val newRectangle = ? (cells, player.position.x + incX.toInt * (-5), player.position.y + incY.toInt * (-5), movement.opposite)

      if(player.position.isMoveAllowed(movement)) {
        if(newRectangle.isDefined) {
          MovementAnimation.setAnimationIncrement(newRectangle.get, incX, incY, movement.url(), afterMovement)
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
      */
    override def ->(movement : Move): Unit = movement match {
      case Top  => move(movement, list, player, 0,+40)
      case Right => move(movement, list, player, -40,0)
      case Bottom => move(movement, list, player, 0,-40)
      case Left => move(movement, list, player, +40,0)
    }



    /**
      * Check for cell type and event after the movement.
      *
      * @param newRectangle the new cell reached after the movement.
      * @param stringUrl the new url reached.
      * @param isEnded whether the animation is ended.
      */
    private def afterMovement(newRectangle: RectangleCell ,stringUrl : String, isEnded: Boolean): Unit ={
      if(isEnded) {
        if(newRectangle.url.contains("Dmg")) {
          user - 1
        }
        setPlayer(newRectangle, player.url)

        val event = player.position.mapEvent
        if(event.isDefined) {
          event.get.cellEvent match {
            case enemy:Enemy => notifyObserver(enemy)
            case statue:Statue => notifyObserver(statue)
            case pyramid: Pyramid => if(player.position.mapEvent.get.playerRepresentation.url.contains("Door")) notifyObserver(pyramid)
            case chest: Chest => {notifyObserver(chest); removeChestCell()}
          }
        }
      } else setPlayer(player.position, stringUrl)
    }

    override def setPlayer(newPosition: RectangleCell, newUrl: String): Unit = {
      player = PlayerRepresentation(newPosition, newUrl)
      notifyObserver(player)
    }


    def getAllEnemies: Int = list.map(m=> m.mapEvent).count(f => f.isDefined && f.get.cellEvent.isInstanceOf[Enemy])

    private def pyramidDoor(url: String): Unit = {
      val pyramid = list.find(f => f.mapEvent.isDefined && f.mapEvent.get.cellEvent.isInstanceOf[Pyramid]).get
      pyramid.mapEvent_(Option(MapEvent(pyramid.mapEvent.get.cellEvent, PlayerRepresentation(pyramid, url))))
    }

    override def postInsert(): Unit = {
      notifyObserver(getAllEnemies)
      if(getAllEnemies > 0) pyramidDoor("pyramid.png")
      else pyramidDoor("pyramidDoor.png")
      notifyObserver(list)
      selected = Option.empty
    }
  }



  def apply(list: List[RectangleCell], startingDefined : Option[RectangleCell], traslationX:Double, traslationY:Double, user:Player): Dashboard = new DashboardImpl(list, startingDefined, traslationX, traslationY, user)

}