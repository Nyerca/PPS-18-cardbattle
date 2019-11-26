package view

import javafx.scene.input.MouseEvent
import javafx.scene.paint.ImagePattern
import model._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.{Label, SplitPane}
import scalafx.scene.{Node, Scene}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.VBox
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Text, TextFlow}
import controller.{Controller, MapController}
import javafx.scene.control.Alert.AlertType
import javafx.scene.paint.ImagePattern
import model._
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.{Node, Scene}
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.collection.mutable.ListBuffer
import scala.util.Random
import javafx.scene.input.MouseEvent
import scalafx.geometry.{HPos, Pos}
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.text.Text
import scalafx.stage.Stage
import view.scenes.BaseScene

class cards (override val parentStage: Stage) extends BaseScene{

  def createCardPane(valX:Double, valY:Double): Pane = {
    val re = new Rectangle() {
      x=valX
      y=valY
      width = 220
      height = 300
      fill = new ImagePattern(new Image("card2.png"))
    }
    val btn = new Button() {
      graphic = new ImageView(new Image("card2.png"))
    }
    val t = new Text(valX + 20,valY + 30,"Fireball")
    val t2 = new Text(valX + 90,valY + 184,"MAGIC")
    val t3 = new Text(valX + 60,valY + 240,"DMG:     3x")
    val t4 = new ImageView(new Image("mdmg.png")) {
      x=valX + 120
      y=valY + 210
    }


    val pane = new Pane {
      children = new ListBuffer[Node]
      maxHeight = 800
      children.append(btn)
      children.append(t)
      children.append(t2)
      children.append(t3)
      children.append(t4)

    }
    pane
  }

  val hbox = new HBox() {

    children = new ListBuffer[Button]
    children.append(createCardPane(0,0))
    children.append(createCardPane(0,0))
    children.append(createCardPane(0,0))
    children.append(createCardPane(0,0))
    children.append(createCardPane(0,0))
    children.append(createCardPane(0,0))
    children.append(createCardPane(0,0))
    children.append(createCardPane(0,0))
    children.append(createCardPane(0,0))
  }


  val scrollPane = new ScrollPane() {
    hbarPolicy = ScrollBarPolicy.Always
    vbarPolicy = ScrollBarPolicy.AsNeeded
  }

  scrollPane.setContent(hbox);
  scrollPane.pannableProperty().set(true);



  //scrollPane2.setHbarPolicy(ScrollBarPolicy.AsNeeded);

  // Horizontal scroll bar is only displayed when needed
  //scrollPane2.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);

  val stage = new PrimaryStage {
    title = "Cardbattle"
    scene = new Scene(scrollPane, 550, 330)
  }

  def getStage(): PrimaryStage = {
    stage
  }
}

object cards {
  def apply(parentStage: Stage): cards = new cards(parentStage)
}