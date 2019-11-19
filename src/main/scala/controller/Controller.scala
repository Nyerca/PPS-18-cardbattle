package controller



import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.layout._
import java.util.HashMap


class Controller() {
  private var screenMap :HashMap[String, Pane] = new HashMap()
  private var _stage: PrimaryStage = null

  def addScreen(name: String, pane: Pane): Unit = {
    screenMap.put(name, pane)
  }

  def removeScreen(name: String) :Unit = {
    screenMap.remove(name)
  }

  /*
  def activate(name: String) :Unit = {
    stage.setRoot( screenMap.get(name) );
  }
  */

  def stage_(stage : PrimaryStage): Unit = {
    _stage = stage
  }
  def show() = {
    _stage.show()
  }




}