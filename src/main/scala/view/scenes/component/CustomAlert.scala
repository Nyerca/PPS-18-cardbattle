package view.scenes.component

import controller.Difficulty
import scalafx.scene.control.{ButtonType, ComboBox, Dialog, DialogPane, Label, TextField}
import scalafx.scene.layout.Pane
import scalafx.Includes._

class CustomAlert extends Dialog[(Option[String], Option[Difficulty])] {
  title = "Setting"
  val enterName: TextField = new TextField() {
    style = "-fx-pref-width:150px"
    translateY = 25
    translateX = 110
  }
  val labelName: Label = new Label("Enter your name") {
    translateY = 30
    translateX = 10
  }
  val setDifficulty: ComboBox[Difficulty] = new ComboBox[Difficulty](List(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)) {
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
      case ButtonType.OK => (if ( enterName.text.value == "" ) None else Some(enterName.text.value), if ( setDifficulty.value.value == null ) None else Some(setDifficulty.value.value))
      case _ => (None,None)
    }
    content = new Pane() {
      children = List(labelName, enterName, difficultyLabel, setDifficulty)
    }
  }

  def showAndWait(): (Option[String],Option[Difficulty]) = {
    super.showAndWait()
    result.value
  }


}
