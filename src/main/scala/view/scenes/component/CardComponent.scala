package view.scenes.component

import controller.BattleController
import javafx.event.{ActionEvent, EventHandler}
import model.{Card, Category, Type}
import scalafx.animation.{FadeTransition, Transition}
import scalafx.scene.Node
import scalafx.scene.control.{Button, Label}
import scalafx.util.Duration

trait CardComponent {
  val DEFAULT_ON_FINISHED = null
  var card: Card = _
  def battleController: BattleController
  def clickableCard: Button
  def cardName: Label
  def cardDamage: Label
  def cardLevel: Label
  def setCardInformation(card: Card): Unit
  def fadeOutAll(action: EventHandler[ActionEvent] = DEFAULT_ON_FINISHED ): Unit
}

class CardComponentImpl(override val battleController: BattleController, marginX: Double, marginY: Double, mouseTransparency: Boolean, action: EventHandler[ActionEvent]) extends CardComponent {

  override val clickableCard: Button = new Button {
    styleClass.add("card")
    translateX = marginX
    translateY = marginY
    mouseTransparent = mouseTransparency
    onAction = action
  }

  override val cardName: Label = new Label {
    translateX = marginX + 20
    translateY = marginY + 11

  }

  override val cardDamage: Label = new Label {
    translateX = marginX + 20
    translateY = marginY + 150
  }

  override val cardLevel: Label = new Label {
    translateX = marginX + 20
    translateY = marginY + 132
  }



  override def setCardInformation(c: Card): Unit = {
    card = c
    fadeInAll()
    clickableCard.style = "-fx-background-image: url(" + card.image + ")"
    cardName.text = card.name
    cardDamage.text = if (card.family._1 == Category.Attack) printType(card.family._2) + " " + printCategory(card.family._1) + "\n\nDamage: " + card.value else printType(card.family._2) + " " + printCategory(card.family._1) + "\n\nDefense: " + card.value
    cardLevel.text = "Level: " + card.level
  }

  override def fadeOutAll(action: EventHandler[ActionEvent]): Unit = {
    fadeTransitionFactory(Duration(300), cardName, -1, 1, action).play()
    fadeTransitionFactory(Duration(300), clickableCard, -1, 1).play()
    fadeTransitionFactory(Duration(300), cardDamage, -1, 1).play()
    fadeTransitionFactory(Duration(300), cardLevel, -1, 1).play()
  }

  private def fadeTransitionFactory(duration: Duration, node: Node, byVal: Double, cycles: Int, action: EventHandler[ActionEvent] = DEFAULT_ON_FINISHED): Transition = new FadeTransition(duration, node) {
    byValue = byVal
    cycleCount = cycles
    onFinished = action
  }

  private def printType(t: Type): String = t match {
    case Type.Magic => "Magic"
    case _ => "Physic"
  }

  private def printCategory(c: Category): String = c match {
    case Category.Attack => "Attack"
    case _ => "Defense"
  }

  private def fadeInAll(): Unit = {
    clickableCard.opacity = 1
    cardName.opacity = 1
    cardDamage.opacity = 1
    cardLevel.opacity = 1
    clickableCard.mouseTransparent = mouseTransparency
  }
}


object CardComponent {
  def apply(battleController: BattleController, marginX: Double, marginY: Double, mouseTransparency: Boolean, action: EventHandler[ActionEvent]): CardComponent = new CardComponentImpl(battleController, marginX, marginY, mouseTransparency, action)
}