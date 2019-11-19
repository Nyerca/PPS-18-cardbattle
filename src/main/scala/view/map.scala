package view

import controller.{Controller, MapController}
import javafx.scene.paint.ImagePattern
import model._
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.collection.mutable.ListBuffer
import scala.util.Random
import javafx.scene.input.MouseEvent

class map (var _controller : MapController) {

  def setController(controller : MapController): Unit = {
  _controller = controller
  _controller.view_(
  this)
}

  val _pane = new Pane {
  children = _controller.list
  for(el <- _controller.getAllEnemies ) {
  children.append(el.icon)
}
}

  val _bpane = new BorderPane {
  center = _pane
  bottom = new HBox() {
  layoutX = 10
  layoutY = 580
  children = List()

  var addList = _controller.createBottomCard
  children = addList
}
}
  def pane = _pane
  def bpane = _bpane

  _controller.view_(this)

  def setPaneChildren(listTmp :ListBuffer[Rectangle]): Unit = {
  pane.children =(listTmp)
}
  def setBPane(): Unit = {
  _bpane.bottom = new HBox() {

  layoutX = 10
  layoutY = 580
  id = "pane"
  children = List()



  var addList = _controller.createBottomCard
  children = addList
}
}

  val stage = new PrimaryStage {
  title = "Cardbattle"
  scene = new Scene(1200, 800) {

  fill = (new ImagePattern(new Image( "noroad.png"), 0, 0, 200, 200, false));
  content = _bpane
  content.add(_controller.player.icon);

  onKeyPressed = (ke : KeyEvent) => _controller.handleKey(ke.code)
  onMouseClicked = (e: MouseEvent) => _controller.handleMouseClicked(e)
}
}

  def getStage(): PrimaryStage = {
  stage
}

  //stage.resizable = false
  //stage.fullScreen = true




}