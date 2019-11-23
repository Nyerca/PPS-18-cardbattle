package view.scenes.component

import javafx.beans.property.SimpleDoubleProperty
import model.Player
import scalafx.scene.control.{Button, Label, ProgressBar}
import scalafx.scene.layout.{BorderPane, StackPane}


trait BattlePlayerRepresentation extends BorderPane{
  def marginX: Double
  def marginY: Double
  def player: Player
  def updateHP(hp: Double): Unit
}

class BattlePlayerRepresentationImpl(override val marginX: Double, override val marginY: Double, override val player: Player) extends BattlePlayerRepresentation {
  private val observableHealthPoint = new SimpleDoubleProperty(1)
  translateX = marginX
  translateY = marginY
  top = new StackPane {
    children = List(new ProgressBar {
      progress <== observableHealthPoint
      styleClass.add("life")
    }, new Label{
      styleClass.add("title")
      text = player.name
    })
  }

  center = new Button {
    styleClass.add("image")
    mouseTransparent = true
    style = "-fx-background-image: url(" + player.image + ")"
  }

  override def updateHP(hp: Double): Unit = observableHealthPoint.set(hp / player.healthPoint)
}

object BattlePlayerRepresentation {
  def apply(marginX: Double, marginY: Double, player:Player): BattlePlayerRepresentation = new BattlePlayerRepresentationImpl(marginX, marginY, player)
}
