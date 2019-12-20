package view.scenes.component

import controller.{Difficulty, Difficulty2}
import scalafx.Includes._
import scalafx.scene.control._
import scalafx.scene.layout.Pane

class CustomAlert2 extends Dialog[(Option[String], Option[Difficulty2])] {
  title = "Setting"
  val name: TextField = new TextField() {
    style = "-fx-pref-width:150px"
    translateY = 25
    translateX = 110
  }
  val nameLabel: Label = new Label("Enter your name") {
    translateY = 30
    translateX = 10
  }
  val difficulty: ComboBox[Difficulty2] = new ComboBox[Difficulty2](List(Difficulty2.Easy, Difficulty2.Medium, Difficulty2.Hard)) {
    translateY = 70
    translateX = 110
    style = "-fx-pref-width:150px"
  }
  val difficultyLabel: Label = new Label("Select difficulty") {
    translateY = 70
    translateX = 10
  }

  onHidden = handle(this.hide())

  dialogPane = new DialogPane() {
    headerText = "Customize your game"
    style = "-fx-pref-width:300px; -fx-pref-height:200px"
    buttonTypes = List(ButtonType.OK)
    resultConverter = {
      case ButtonType.OK => (if ( name.text.value == "" ) None else Some(name.text.value), if ( difficulty.value.value == null ) None else Some(difficulty.value.value))
      case _ => (None,None)
    }
    content = new Pane() {
      children = List(nameLabel, name, difficultyLabel, difficulty)
    }
  }

  def showAndWait(): (Option[String],Option[Difficulty2]) = {
    super.showAndWait()
    result.value
  }


}
