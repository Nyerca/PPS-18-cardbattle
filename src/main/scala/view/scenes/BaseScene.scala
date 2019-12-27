package view.scenes

import scalafx.scene.Scene
import scalafx.stage.Stage

trait BaseScene extends Scene{
  def parentStage: Stage
  def changeScene(newScene: Scene): Unit = parentStage.scene = newScene
}

