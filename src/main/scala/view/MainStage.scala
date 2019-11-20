package view

import scalafx.application.JFXApp
import view.scenes.MainScene


trait MainStage extends JFXApp.PrimaryStage

class MainStageImpl() extends MainStage {
  title = "Dungeon of Engineer"
  resizable = false
  private val mainScene = new MainScene(this)
  scene = mainScene
}


object MainStage {
  def apply(): MainStage = new MainStageImpl()
}

