package controller

import java.io.{File, FileOutputStream, ObjectOutputStream}
import utility.TransitionFactory
import exception.DoubleMovementException
import javafx.scene.input.MouseEvent
import model.{Bottom, Cell, ChestPosition, EmptyPosition, EnemyCell, EnemyPosition, Left, MapPosition, PlayerPosition, PyramidPosition, RectangleCell, Right, StatuePosition, Top}
import scalafx.Includes._
import scalafx.scene.input.KeyCode
import view.scenes.MapScene
import scala.util.Random
import model.Placeable._
import scalafx.util.Duration


trait MapController {
  def gameC: GameController
  def selected(element: Option[Cell]):Unit
  def view_ (newView : MapScene): Unit
  def getAllEnemies: Int
  def reset(): Unit
  def handleSave(): Unit
  def handleKey(keyCode : KeyCode): Unit
  def handleMouseClicked(e:MouseEvent): Unit
  def dashboard:model.Dashboard
}

/**
  * The controller class of the MapScene.
  *
  * @param gameC the main controller of the game.
  * @param _list the list of RectangleCell.
  * @param startingDefined whether the game has been loaded and the player position is different from the starting one.
  * @param traslationX the traslationX of the map whether the game has been loaded.
  * @param traslationY the traslationY of the map whether the game has been loaded.
  */
class MapControllerImpl (override val gameC : GameController, var _list:List[RectangleCell], var startingDefined : Option[RectangleCell], traslationX:Double, traslationY:Double) extends MapController {

  def this(gameC : GameController) {this(gameC,MapController.setup(gameC),Option.empty,0,0)}

  val dashboard = model.Dashboard(_list, startingDefined, traslationX, traslationY, gameC.user)

  var view: MapScene = _
  override def view_ (newView : MapScene): Unit = {
    view = newView
    dashboard.addObserver(view)
    MovementAnimation.setAnimationNode(view.mapWindow)
    view.setMenu()
  }

  override def reset(): Unit = {
    TransitionFactory.fadeTransitionFactory(Duration(2000), view.root.value, handle {
      val newMap =  MapScene(view.parentStage, gameC)
      gameC.setScene(view, newMap)
    }).play()
  }

  private def checkAnimationEnd(url: String):Boolean = {
    if(MovementAnimation.checkAnimationEnd()) {
      dashboard.setPlayer(dashboard.player.position, url + ".png")
      true
    }
    else throw new DoubleMovementException
  }

  def selected(element :  Option[Cell]): Unit = dashboard.selected = element
  def getAllEnemies : Int = dashboard.getAllEnemies


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
    val directory = new File("./src/main/saves/")
    if(!directory.exists()) directory.mkdir
    val output = new ObjectOutputStream(new FileOutputStream("./src/main/saves/save.txt"))
    save(output)(gameC.user)
    save(output)(gameC.difficulty)
    save(output)(dashboard.list.map(el => el))
    save(output)(dashboard.player)
    save(output)(dashboard.traslationX)
    save(output)(dashboard.traslationY)
    output.close()
  }


  /**
    * Check the position clicked in the map, and whether an item on the bottom pane has been previusly selected, places that item to the map.
    *
    * @param e Position clicked.
    */
  override def handleMouseClicked(e:MouseEvent): Unit = {
    val cell = dashboard ? (dashboard.list, e.x - dashboard.traslationX, e.y - dashboard.traslationY)
    dashboard.selected match {
      case Some(rc:RectangleCell) =>
        rc.x_(e.x - dashboard.traslationX - e.x % 200)
        rc.y_(e.y - dashboard.traslationY - e.y % 200)
        place(rc,cell,dashboard)
      case Some(ec:EnemyCell) => place(ec,cell,dashboard)
      case _ =>
    }
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
    for(i<-0 until Random.nextInt(5)) {
      val rnd = math.random()
      if(rnd < 0.8) newList = newList:+EmptyPosition
      else if(rnd <0.9) newList = newList:+StatuePosition
      else newList = newList:+EnemyPosition
    }

    var excludedValues: Map[Int,List[Int]] = Map()
    newList.foreach(el => {
      val rect =  el.create(gameC,excludedValues)
      list = list:+rect
      if(!excludedValues.contains(rect.x.toInt)) {
        excludedValues += (rect.x.toInt -> List[Int](rect.y.toInt))
      } else {
        val tmplist: List[Int] = excludedValues.get(rect.x.toInt).get :+ rect.y.toInt
        excludedValues += (rect.x.toInt -> tmplist)
      }
    })
    list
  }

}