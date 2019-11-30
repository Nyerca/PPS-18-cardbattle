package view

import scalafx.application.JFXApp
import view.scenes.MainScene


trait MainStage extends JFXApp.PrimaryStage

class MainStageImpl() extends MainStage {
  title = "Dungeon of Engineer"
  resizable = false
  scene = MainScene(this)
}


object MainStage {
  def apply(): MainStage = new MainStageImpl()
}

