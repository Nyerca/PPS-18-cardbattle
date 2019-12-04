package Utility

import javafx.event.{ActionEvent, EventHandler}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, Label}
import scalafx.stage.Stage

object GUIObjectFactory {

  val  DEFAULT_ON_ACTION: EventHandler[ActionEvent] = null
  val DEFAULT_STYLE: String = null

  def buttonFactory(marginX: Double, marginY: Double, mouseTransparency: Boolean, action: EventHandler[ActionEvent], styleString: String, classes: String*): Button = new Button {
    classes.foreach(c => styleClass.add(c))
    translateX = marginX
    translateY = marginY
    mouseTransparent = mouseTransparency
    style = styleString
    onAction = action
  }

  def alertFactory(alertType: AlertType, owner: Stage, alertTitle: String, alertHeaderText: String): Alert = new Alert(alertType) {
    initOwner(owner)
    title = alertTitle
    headerText = alertHeaderText
  }

  def labelFactory(marginX: Double, marginY: Double, labelText: String = ""): Label = new Label {
    translateX = marginX
    translateY = marginY
    text = labelText
  }

}
