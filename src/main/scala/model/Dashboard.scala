package model

import controller.MovementAnimation
import exception.{MissingCellException, NoMovementException}

trait Dashboard extends Observable{
  var cells: List[RectangleCell]
  var selected: Option[Cell]
  var translationX : Double
  var translationY : Double
  def player: PlayerRepresentation
  def remove[A <: CellEvent]() :Unit
  def postInsert(changePlaceableCard: Boolean): Unit
  def setPlayer(newPosition: RectangleCell, newUrl: String): Unit
  def getAllEnemies: Int
  def ? (cells: List[RectangleCell], newX : Double, newY : Double): Option[RectangleCell]
  def ? (cells: List[RectangleCell], newX : Double, newY : Double, movement: Move): Option[RectangleCell]
  def -> (movement : Move): Unit
}

object Dashboard {

  private class DashboardImpl(var cells: List[RectangleCell], startingDefined : Option[RectangleCell], var translationX:Double, var translationY:Double, user: Player) extends Dashboard {
    var selected: Option[Cell] = Option.empty

    var player : PlayerRepresentation = _
    startingDefined match {
      case Some(rect: RectangleCell) => player = PlayerRepresentation(? (cells, rect.x, rect.y).get, "/player/bot.png")
      case _ => player = PlayerRepresentation(cells.head, "/player/bot.png")
    }

    override def remove[A]() :Unit = cells.collect { case f if f.mapEvent.isDefined && f == player.position && f.mapEvent.get.cellEvent.isInstanceOf[A] => f.mapEvent_(Option.empty); postInsert(false) }

    /**
      * Check for existing cell in the map given (x, y)
      *
      * @param cell_list the cells of the map
      * @param newX the x value of the cell to be found.
      * @param newY the y value of the cell to be found.
      * @return the Option which describe the RectangleCell whether it's there.
      */
    override def ? (cell_list: List[RectangleCell], newX : Double, newY : Double): Option[RectangleCell] = cell_list.find(rectangle => rectangle.isRectangle(newX, newY))

    /**
      * Check for existing cell in the map given (x, y) and the direction of the movement
      *
      * @param cell_list the cells of the map
      * @param newX the x value of the cell to be found.
      * @param newY the y value of the cell to be found.
      * @param movement the direction of tbe movement.
      * @return the Option which describe the RectangleCell whether it's there.
      */
    override def ? (cell_list: List[RectangleCell], newX : Double, newY : Double, movement: Move): Option[RectangleCell] = cell_list.find(rectangle => rectangle.isRectangle(newX, newY) && rectangle.isMoveAllowed(movement))

    /**
      * Handle the movement in a specific direction.
      *
      * @param movement the direction of the movement.
      */
    override def ->(movement : Move): Unit = movement match {
      case Top  => move(movement, 0,+40)
      case Right => move(movement, -40,0)
      case Bottom => move(movement, 0,-40)
      case Left => move(movement, +40,0)
    }

    override def setPlayer(newPosition: RectangleCell, newUrl: String): Unit = {
      player = PlayerRepresentation(newPosition, newUrl)
      notifyObserver(player)
    }

    override def getAllEnemies: Int = cells.map(m=> m.mapEvent).count(f => f.isDefined && f.get.cellEvent.isInstanceOf[Enemy])

    override def postInsert(changePlaceableCard: Boolean): Unit = {
      if(getAllEnemies > 0) pyramidDoor("pyramid.png")
      else pyramidDoor("pyramidDoor.png")
      notifyObserver(cells, getAllEnemies, changePlaceableCard)
      selected = Option.empty
    }

    private def pyramidDoor(url: String): Unit = {
      val pyramid = cells.find(f => f.mapEvent.isDefined && f.mapEvent.get.cellEvent.isInstanceOf[Pyramid]).get
      pyramid.mapEvent_(Option(MapEvent(pyramid.mapEvent.get.cellEvent, PlayerRepresentation(pyramid, url))))
    }

    private def move(movement:Move, incX : Double, incY : Double): Unit = {
      MovementAnimation.setAnimation(translationX, translationX + incX.toInt * (-5), translationY, translationY + incY.toInt * (-5))
      val newRectangle = ? (cells, player.position.x + incX.toInt * (-5), player.position.y + incY.toInt * (-5), movement.opposite)
      if(player.position.isMoveAllowed(movement)) {
        if(newRectangle.isDefined) {
          MovementAnimation.setAnimationIncrement(newRectangle.get, incX, incY, movement.url(), afterMovement)
          translationX += incX * 5
          translationY += incY * 5
          MovementAnimation.anim.play()
        } else throw new MissingCellException
      } else throw new NoMovementException
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
        if(newRectangle.url.contains("Dmg")) user - 1
        if(user.actualHealthPoint <= 0 ) notifyObserver(user.actualHealthPoint)
        setPlayer(newRectangle, player.url)
        val event = player.position.mapEvent
        if(event.isDefined && user.actualHealthPoint > 0) {
          event.get.cellEvent match {
            case enemy:Enemy => notifyObserver(enemy)
            case statue:Statue => notifyObserver(statue)
            case pyramid: Pyramid => if(player.position.mapEvent.get.playerRepresentation.url.contains("Door")) notifyObserver(pyramid)
            case chest: Chest => notifyObserver(chest); remove[Chest]()
          }
        }
      } else setPlayer(player.position, stringUrl)
    }
  }

  def apply(list: List[RectangleCell], startingDefined : Option[RectangleCell], traslationX:Double, traslationY:Double, user:Player): Dashboard = new DashboardImpl(list, startingDefined, traslationX, traslationY, user)
}