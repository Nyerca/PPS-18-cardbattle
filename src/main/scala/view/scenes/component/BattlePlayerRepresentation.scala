package view.scenes.component

import Utility.{GUIObjectFactory, TransitionFactory}
import controller.BattleController
import javafx.beans.property.{SimpleDoubleProperty, SimpleStringProperty}
import javafx.event.{ActionEvent, EventHandler}
import model.{Category, Player, Type, User}
import scalafx.scene.control.{Button, Label, ProgressBar}
import scalafx.scene.layout.{Pane, StackPane}
import scalafx.util.Duration
import scalafx.Includes._


trait BattlePlayerRepresentation extends Pane {

  def battleController: BattleController

  def marginX: Double

  def marginY: Double

  def player: Player

  def playAnimation(byVal: Double = 0, family: (Category, Type), action: () => Unit = () => ()): Unit

  //def updateHP(action: EventHandler[ActionEvent]): Unit
}

class BattlePlayerRepresentationImpl(override val marginX: Double, override val marginY: Double, override val player: Player, override val battleController: BattleController) extends BattlePlayerRepresentation {
  private val observableHealthPoint = (new SimpleDoubleProperty(player.actualHealthPoint.toDouble / player.totalHealthPoint.toDouble), new SimpleStringProperty("Player: " + player.actualHealthPoint + "hp"))

  translateX = marginX

  translateY = marginY

  val life: StackPane = new StackPane {
    translateY = -10
    children = List(new ProgressBar {
      progress <== observableHealthPoint._1
      styleClass.add("life")
    }, new Label {
      styleClass.add("title")
      text <== observableHealthPoint._2
    })
  }

  val playerRepresentation: Button = GUIObjectFactory.buttonFactory(0, 20, mouseTransparency = true, GUIObjectFactory.DEFAULT_ON_ACTION, "-fx-background-image: url(" + player.image + ");")("image")

  val magicShield: Button = GUIObjectFactory.buttonFactory(playerRepresentation.translateX.value - 50, playerRepresentation.translateY.value - 20, mouseTransparency = true)( "magicShield")
  
  val physicShield: Button = GUIObjectFactory.buttonFactory(
    if(player.isInstanceOf[User]) playerRepresentation.translateX.value  + 200 else playerRepresentation.translateX.value  - 100,
    playerRepresentation.translateY.value + 60,
    mouseTransparency = true,
    GUIObjectFactory.DEFAULT_ON_ACTION,
    if(player.isInstanceOf[User]) "-fx-background-image: url('images/fshield.png');" else "-fx-background-image: url('images/fshield2.png');")("physicShield")


  val magicAttack: Button = GUIObjectFactory.buttonFactory(
    if(player.isInstanceOf[User]) playerRepresentation.translateX.value + 130 else playerRepresentation.translateX.value - 90,
    playerRepresentation.translateY.value,
    mouseTransparency = true)("magicAttack")

  children = List(life,playerRepresentation, physicShield, magicShield, magicAttack)

  override def playAnimation(byVal: Double = 0, family: (Category, Type), action: () => Unit): Unit = family._1 match {
    case Category.Attack =>
      attack(byVal, handle(updateHP(action)), family._2)
    case Category.Defense => defense(handle(updateHP(action)), family._2)
  }

  private def updateHP(action: () => Unit): Unit = {
    val ratio: Double = player.actualHealthPoint.toDouble / player.totalHealthPoint.toDouble
    action()
    if ( ratio != observableHealthPoint._1.value ) {
      damage()
      observableHealthPoint._1.set(if ( ratio > 0 ) ratio else 0)
      observableHealthPoint._2.set(if ( ratio > 0 ) "Player: " + player.actualHealthPoint + "hp" else "Player: 0hp")
      defeat(player.actualHealthPoint, handle(battleController.checkWinner(player)))
    }
  }

  private def defeat(hp: Double, action: EventHandler[ActionEvent]): Unit = hp match {
    case n if n <= 0 => TransitionFactory.fadeTransitionFactory(Duration(1000), this, action).play()
    case _ => ;
  }

  private def attack(byVal: Double, action: EventHandler[ActionEvent], cardType: Type): Unit = cardType match {
    case Type.Physic => TransitionFactory.translateTransitionFactory(Duration(200), playerRepresentation, action, byVal, 0, 2, autoReversible = true).play()
    case _ =>
      TransitionFactory.fadeTransitionFactory(Duration(150), magicAttack, TransitionFactory.DEFAULT_ON_FINISHED,1, 2, autoReversible = true).play()
      TransitionFactory.translateTransitionFactory(Duration(200), magicAttack, action, byVal, 0, 2, autoReversible = true).play()
  }

  private def defense(action: EventHandler[ActionEvent], cardType: Type): Unit = cardType match {
    case Type.Magic => TransitionFactory.fadeTransitionFactory(Duration(150), magicShield, action, 1, 2, autoReversible = true).play()
    case _ => TransitionFactory.fadeTransitionFactory(Duration(150), physicShield, action, 1, 2, autoReversible = true).play()
  }

  private def damage(): Unit = TransitionFactory.rotateTransitionFactory(Duration(20), playerRepresentation, TransitionFactory.DEFAULT_ON_FINISHED, 5, 20, autoReversible = true).play()
}

object BattlePlayerRepresentation {
  def apply(marginX: Double, marginY: Double, player:Player, battleController: BattleController): BattlePlayerRepresentation = new BattlePlayerRepresentationImpl(marginX, marginY, player, battleController)
}
