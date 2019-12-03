package controller

import model.{Card, Category, Enemy, Player, User}
import view.scenes.BattleScene

import scala.util.Random

trait PlayerType

object PlayerType {
  case object User extends PlayerType
  case object Enemy extends PlayerType
}

trait BattleController {
  
  def user: User

  user.battleDeck = Random.shuffle(user.battleDeck)

  def enemy: Enemy

  enemy.battleDeck = Random.shuffle(enemy.battleDeck)

  def battleScene: BattleScene

  def drawCard(playerType: PlayerType): Unit = playerType match {
    case PlayerType.User => battleScene.drawCard(playerType)(getCardAndReinsert(user))
    case _ => battleScene.drawCard(playerType)(getCardAndReinsert(enemy))
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
    battleScene.playFightAnimation(userCard.family, PlayerType.User)
    battleScene.playFightAnimation(enemyCard.family, PlayerType.Enemy)
  }

  def checkWinner(playerType: PlayerType): Unit = playerType match {
    case PlayerType.User if user.actualHealthPoint <= 0 =>
    case PlayerType.Enemy if user.actualHealthPoint > 0 && enemy.actualHealthPoint <= 0 =>
      user.addExperience(enemy.experience)
      battleScene.fadeSceneChanging()
    case _ => ;
  }

  private def calculateDamage(card1: Card, card2: Card, player: Player): Unit = {
    if (card1.family._2 == card2.family._2) {
      hitPlayer(player, if(card1.value - card2.value > 0) card1.value - card2.value else 0)
    } else {
      hitPlayer(player, card1.value)
    }
  }

  private def hitPlayer(player: Player, damage: Int): Unit = player match {
    case _: User => user.actualHealthPoint -= damage
    case _ => enemy.actualHealthPoint -= damage
  }


  private def getCardAndReinsert(player: Player): Card = {
    val card = findDeck(player).head
    player.battleDeck = player.battleDeck.filter(cardNotToMove => cardNotToMove != card) :+ card
    card
  }

  private def findDeck(player: Player): List[Card] = player match {
    case _: User => user.battleDeck
    case _ => enemy.battleDeck
  }
}

case class BattleControllerImpl(override val user: User, override val enemy: Enemy, override val battleScene: BattleScene) extends BattleController

object BattleController {
  def apply(user: User, enemy: Enemy, battleScene: BattleScene): BattleController = BattleControllerImpl(user, enemy, battleScene)
}