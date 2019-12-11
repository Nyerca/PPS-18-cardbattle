package controller

import controller.SoundType.{LoseSound, WinningSound}
import model.{Card, Category, Enemy, Player, User}
import view.scenes.BattleScene

import scala.language.postfixOps
import scala.util.Random

trait BattleController {

  def user: User

  def enemy: Enemy

  def battleScene: BattleScene

  def drawCard(player: Player): Unit

  def fight(userCard: Card, enemyCard: Card): Unit

  def checkWinner(player: Player): Unit
}

class BattleControllerImpl(override val user: User, override val enemy: Enemy, override val battleScene: BattleScene) extends BattleController {

  MusicPlayer.play(SoundType.BattleSound)

  enemy.battleDeck = Random.shuffle(enemy.battleDeck)

  user.battleDeck = Random.shuffle(user.battleDeck)

  override def drawCard(player: Player): Unit = player match {
    case _:User => battleScene.drawCard(player)(getCardAndReinsert(user))
    case _ => battleScene.drawCard(player)(getCardAndReinsert(enemy))
  }

  override def fight(userCard: Card, enemyCard: Card): Unit = {
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

  override def checkWinner(player: Player): Unit = player match {
    case _: User if user.actualHealthPoint <= 0 =>
      battleScene fadeSceneChanging enemy
      MusicPlayer.play(LoseSound)
    case _: Enemy if user.actualHealthPoint > 0 && enemy.actualHealthPoint <= 0 =>
      user.coins += enemy.reward
      user ++ enemy
      battleScene fadeSceneChanging user
      MusicPlayer.play(WinningSound)
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

object BattleController {
  def apply(user: User, enemy: Enemy, battleScene: BattleScene): BattleController = new BattleControllerImpl(user, enemy, battleScene)
}