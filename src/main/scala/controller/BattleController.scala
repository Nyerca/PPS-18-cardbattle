package controller

import model.{Card, Category, Player}
import view.scenes.BattleScene
import scala.language.postfixOps

trait BattleController {

  def battleScene: BattleScene

  def drawCard(player: Player): Unit

  def fight(userCard: Card, enemyCard: Card, user: Player, enemy: Player): Unit

  def checkWinner(playerToCheck: Player, otherPlayer: Player): Unit
}

class BattleControllerImpl(override val battleScene: BattleScene) extends BattleController {

  MusicPlayer.play(SoundType.BattleSound)

  override def drawCard(player: Player): Unit = battleScene.drawCard(player)(getCardAndReinsert(player))

  override def fight(userCard: Card, enemyCard: Card, user: Player, enemy: Player): Unit = {
    (userCard.family._1, enemyCard.family._1) match {
      case (Category.Attack, Category.Attack) =>
        user.actualHealthPoint -= enemyCard.value
        enemy.actualHealthPoint -= userCard.value
      case (Category.Defense, Category.Defense) => ;
      case (Category.Attack, Category.Defense) => calculateDamage(userCard, enemyCard, enemy)
      case (_,_) => calculateDamage(enemyCard, userCard, user)
    }
  }

  override def checkWinner(user: Player, enemy: Player): Unit = {
    if(user.actualHealthPoint > 0 && enemy.actualHealthPoint <= 0) {
      battleScene fadeSceneChanging user
    } else if(user.actualHealthPoint <= 0) {
      battleScene fadeSceneChanging enemy
    } else {
      battleScene.userHandCard.filter(cc => cc.clickableCard.opacity.value == 1) foreach(cc => cc.clickableCard.mouseTransparent = false)
      battleScene.userDeck.mouseTransparent = false
    }
  }

  private def calculateDamage(card1: Card, card2: Card, player: Player): Unit = {
    if (card1.family._2 == card2.family._2) {
      hitPlayer(player, if(card1.value - card2.value > 0) card1.value - card2.value else 0)
    } else {
      hitPlayer(player, card1 value)
    }
  }

  private def hitPlayer(player: Player, damage: Int): Unit = player.actualHealthPoint -= damage

  private def getCardAndReinsert(player: Player): Card = {
    player.battleDeck = player.battleDeck.filter(cardNotToMove => cardNotToMove.name != player.battleDeck.head.name) :+ player.battleDeck.head
    player.battleDeck.last
  }
}

object BattleController {
  def apply(battleScene: BattleScene): BattleController = new BattleControllerImpl(battleScene)
}