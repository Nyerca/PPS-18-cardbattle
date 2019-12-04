package controller

import model.{Card, Category, Enemy, Player, User}
import view.scenes.BattleScene

import scala.util.Random

trait BattleController {

  def user: User

  user.battleDeck = Random.shuffle(user.battleDeck)

  def enemy: Enemy

  enemy.battleDeck = Random.shuffle(enemy.battleDeck)

  def battleScene: BattleScene

  def drawCard(player: Player): Unit = player match {
    case _:User => battleScene.drawCard(player)(getCardAndReinsert(user))
    case _ => battleScene.drawCard(player)(getCardAndReinsert(enemy))
  }

  def fight(userCard: Card, enemyCard: Card): Unit = {
    (userCard.family._1, enemyCard.family._1) match {
      case (Category.Attack, Category.Attack) =>
        user.actualHealthPoint -= enemyCard.value
        enemy.actualHealthPoint -= userCard.value
      case (Category.Defense, Category.Defense) => ;
      case (Category.Attack, Category.Defense) => calculateDamage(userCard, enemyCard, enemy)
      case (_,_) => calculateDamage(enemyCard, userCard, user)
    }
    battleScene.playFightAnimation(userCard.family, user)
    battleScene.playFightAnimation(enemyCard.family, enemy)
  }

  def checkWinner(player: Player): Unit = player match {
    case _: User if user.actualHealthPoint <= 0 =>
    case _: Enemy if user.actualHealthPoint > 0 && enemy.actualHealthPoint <= 0 =>
      user.addExperience(enemy experience)
      battleScene fadeSceneChanging
    case _ => ;
  }

  private def calculateDamage(card1: Card, card2: Card, player: Player): Unit = {
    if (card1.family._2 == card2.family._2) {
      hitPlayer(player, if(card1.value - card2.value > 0) card1.value - card2.value else 0)
    } else {
      hitPlayer(player, card1 value)
    }
  }

  private def hitPlayer(player: Player, damage: Int): Unit = player match {
    case _:User => user.actualHealthPoint -= damage
    case _ => enemy.actualHealthPoint -= damage
  }


  private def getCardAndReinsert(player: Player): Card = {
    val card = findDeck(player).head
    player.battleDeck = player.battleDeck.filter(cardNotToMove => cardNotToMove != card) :+ card
    card
  }

  private def findDeck(player: Player): List[Card] = player match {
    case _:User => user.battleDeck
    case _ => enemy.battleDeck
  }
}

case class BattleControllerImpl(override val user: User, override val enemy: Enemy, override val battleScene: BattleScene) extends BattleController

object BattleController {
  def apply(user: User, enemy: Enemy, battleScene: BattleScene): BattleController = BattleControllerImpl(user, enemy, battleScene)
}