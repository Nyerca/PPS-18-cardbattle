package controller



import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.layout._
import java.util.HashMap

import scalafx.scene.Scene


class Controller(_stage: PrimaryStage) {

  def setScene(scene : Scene): Unit = {
    _stage.scene_=(scene)
  }

  def stage = _stage




}