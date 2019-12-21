package view.scenes.component

import controller.Difficulty
import scalafx.scene.control.{ButtonType, ComboBox, Dialog, DialogPane, Label, TextField}
import scalafx.scene.layout.Pane
import scalafx.Includes._

class CustomAlert extends Dialog[(Option[String], Option[Difficulty])] {
  title = "Setting"
  val name: TextField = new TextField() {
    style = "-fx-pref-width:150px"
    text = "Player1"
    translateY = 25
    translateX = 110
  }
  val nameLabel: Label = new Label("Enter your name") {
    translateY = 30
    translateX = 10
  }
  val difficulty: ComboBox[Difficulty] = new ComboBox[Difficulty](List(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)) {
    value = Difficulty.Medium
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
      case ButtonType.OK => (if ( name.text.value == "" ) None else Some(name.text.value), Option(difficulty.value.value))
      case _ => (None,None)
    }
    content = new Pane() {
      children = List(nameLabel, name, difficultyLabel, difficulty)
    }
  }

  def showAndWait(): (Option[String],Option[Difficulty]) = {
    super.showAndWait()
    result.value
  }


}
