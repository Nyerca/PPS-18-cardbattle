package utility

import javafx.event.{ActionEvent, EventHandler}
import scalafx.scene.Node
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.layout.Pane
import scalafx.stage.Stage

object GUIObjectFactory {

  val  DEFAULT_ON_ACTION: EventHandler[ActionEvent] = null

  val DEFAULT_STYLE: String = null

  def buttonFactory(marginX: Double, marginY: Double, mouseTransparency: Boolean, action: EventHandler[ActionEvent] = DEFAULT_ON_ACTION, styleString: String = DEFAULT_STYLE, buttonText: String = "")(classes: String*): Button = new Button {
    classes.foreach(c => styleClass.add(c))
    translateX = marginX
    translateY = marginY
    mouseTransparent = mouseTransparency
    style = styleString
    onAction = action
    text = buttonText
  }

  def alertFactory(alertType: AlertType, owner: Stage, alertTitle: String, alertHeaderText: String = ""): Alert = new Alert(alertType) {
    initOwner(owner)
    title = alertTitle
    headerText = alertHeaderText
  }

  def labelFactory(marginX: Double, marginY: Double, labelText: String = "", classOfStyle: String = ""): Label = new Label {
    translateX = marginX
    translateY = marginY
    text = labelText
    styleClass.add(classOfStyle)
  }

  def paneFactory(nodes: List[Node])(classes: String*)(marginX: Double, marginY: Double): Pane = new Pane {
    classes.foreach(c => styleClass.add(c))
    translateX = marginX
    translateY = marginY
    children = nodes
  }

  def toolbarFactory(list: List[(Node, Boolean)]): ToolBar = {
    val toolbar = new ToolBar()
    list.map(m=> {
      toolbar.getItems.add(m._1)
      if(m._2) toolbar.getItems.add(new Separator())
    })
    toolbar
  }
}
