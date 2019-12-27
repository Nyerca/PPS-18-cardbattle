package controller

import java.io.{File, FileOutputStream, ObjectOutputStream}
import utility.TransitionFactory
import exception.DoubleMovementException
import javafx.scene.input.MouseEvent
import model.{Bottom, Cell, ChestPosition, Dashboard, EmptyPosition, EnemyCell, EnemyPosition, Left, MapPosition, PlayerPosition, PyramidPosition, RectangleCell, Right, StatuePosition, Top}
import scalafx.scene.input.KeyCode
import view.scenes.MapScene
import javafx.animation.Animation.Status
import scalafx.Includes._
import scala.util.Random
import model.Placeable._
import scalafx.util.Duration


trait MapController {
  def gameC: GameController
  def view_ (newView : MapScene): Unit
  def getAllEnemies: Int
  def reset(): Unit
  def handleSave(): Unit
  def handleKey(keyCode : KeyCode): Unit
  def handleMouseClicked(e:MouseEvent): Unit
  def dashboard: Dashboard
  def setDashboardSelected(selectedElem:Option[Cell]): Unit
  def setGold(money: Int): Unit
}

/**
  * The controller class of the MapScene.
  *
  * @param gameC the main controller of the game.
  * @param _list the list of RectangleCell.
  * @param startingDefined whether the game has been loaded and the player position is different from the starting one.
  * @param translationX the traslationX of the map whether the game has been loaded.
  * @param translationY the traslationY of the map whether the game has been loaded.
  */
class MapControllerImpl (override val gameC : GameController, var _list:List[RectangleCell], var startingDefined : Option[RectangleCell], translationX:Double, translationY:Double) extends MapController {

  val dashboard = model.Dashboard(_list, startingDefined, translationX, translationY, gameC.user)

  var view: MapScene = _

  def this(gameC : GameController) {this(gameC,MapController.setup(gameC),Option.empty,0,0)}

  override def view_ (newView : MapScene): Unit = {
    view = newView
    dashboard.addObserver(view)
    MovementAnimation.setAnimationNode(view.mapWindow)
    view.setMenu()
  }

  override def reset(): Unit = TransitionFactory.fadeTransitionFactory(Duration(2000), view.root.value, handle {gameC.setScene(view, MapScene(view.parentStage, gameC))}).play()

  override def getAllEnemies : Int = dashboard.getAllEnemies

  /**
    * Check the keyboard keys pressed and moves, whether possible, the player.
    *
    * @param keyCode the keycode pressed
    */
  override def handleKey(keyCode : KeyCode): Unit = {
    keyCode.getName match {
      case "W" => if(checkAnimationEnd(Top.url())) dashboard -> Top
      case "A" => if(checkAnimationEnd(Left.url())) dashboard -> Left
      case "S" => if(checkAnimationEnd(Bottom.url())) dashboard -> Bottom
      case "D" => if(checkAnimationEnd(Right.url())) dashboard -> Right
      case _ =>
    }
  }

  /**
    * Saves all the informations in a save.txt file.
    */
  override def handleSave(): Unit = {
    import FileManager._
    val path = System.getProperty("user.home") + System.getProperty("file.separator")
    val directory = new File(path + ".CARDBATTLE/")
    if(!directory.exists()) directory.mkdir
    val output = new ObjectOutputStream(new FileOutputStream(path + ".CARDBATTLE/save.txt"))
    save(output)(gameC.user)
    save(output)(gameC.difficulty)
    save(output)(dashboard.cells.map(el => el))
    save(output)(dashboard.player)
    save(output)(dashboard.translationX)
    save(output)(dashboard.translationY)
    output.close()
  }

  /**
    * Check the position clicked in the map, and whether an item on the bottom pane has been previusly selected, places that item to the map.
    *
    * @param e Position clicked.
    */
  override def handleMouseClicked(e:MouseEvent): Unit = {
    val cell = dashboard ? (dashboard.cells, e.x - dashboard.translationX, e.y - dashboard.translationY)
    dashboard.selected match {
      case Some(rc:RectangleCell) =>
        rc.x_(e.x - dashboard.translationX - e.x % 200)
        rc.y_(e.y - dashboard.translationY - e.y % 200)
        place(rc,cell,dashboard)
      case Some(ec:EnemyCell) => place(ec,cell,dashboard)
      case _ =>
    }
  }

  override def setDashboardSelected(selectedElem:Option[Cell]): Unit = dashboard.selected = selectedElem

  override def setGold(money: Int): Unit = gameC.user ++ money

  private def checkAnimationEnd(url: String):Boolean = MovementAnimation.anim.status.value match {
    case Status.STOPPED =>
      dashboard.setPlayer(dashboard.player.position, url + ".png")
      true
    case _ => throw new DoubleMovementException
  }
}

object MapController {
  /**
    * Creates a randomly generated map.
    *
    * @param gameC the main controller of the game
    * @return the list of RectangleCell created.
    */
  def setup(gameC: GameController): List[RectangleCell] = {
    var list :List[RectangleCell] = List()

    var newList: List[MapPosition] =List(PlayerPosition, EnemyPosition, StatuePosition, PyramidPosition, ChestPosition)
    for(_<-0 until Random.nextInt(5)) {
      val rnd = math.random()
      if(rnd < 0.8) newList = newList:+EmptyPosition
      else if(rnd <0.9) newList = newList:+StatuePosition
      else newList = newList:+EnemyPosition
    }

    var excludedValues: Map[Int,List[Int]] = Map()
    newList.foreach(el => {
      val rect =  el.create(gameC,excludedValues)
      list = list:+rect

      if(!excludedValues.contains(rect.x.toInt)) excludedValues += (rect.x.toInt -> List[Int](rect.y.toInt))
      else excludedValues += (rect.x.toInt -> (excludedValues(rect.x.toInt) :+ rect.y.toInt))
    })
    list
  }
}