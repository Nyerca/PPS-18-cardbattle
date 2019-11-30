package controller

import model.{Card, Game, Player, User}
import view.scenes.BattleScene

trait PlayerType

object PlayerType {
  case object User extends PlayerType
  case object EnemyType extends PlayerType
}

trait BattleController {
  def game: Game

  def battleScene: BattleScene

  def drawCard(playerType: PlayerType): Unit = playerType match {
    case PlayerType.User => battleScene.drawCard(playerType)(getCardAndReinsert(game.user))
    case _ => battleScene.drawCard(playerType)(getCardAndReinsert(game.enemy))
  }

  def fight(userCard: Card, enemyCard: Card): Unit = {
    game.fight(userCard, enemyCard)
    battleScene.playFightAnimation(userCard.family._1, PlayerType.User, game.healthPointPlayer1)
    battleScene.playFightAnimation(enemyCard.family._1, PlayerType.EnemyType, game.healthPointPlayer2)
  }

  private def getCardAndReinsert(player: Player): Card = {
    val card = findDeck(player).head
    game.reinsertCard(player, card)
    card
  }

  private def findDeck(player: Player): List[Card] = player match {
    case _: User => game.deckPlayer1
    case _ => game.deckPlayer2
  }
}

case class BattleControllerImpl(override val game: Game, override val battleScene: BattleScene) extends BattleController

object BattleController {
  def apply(game: Game, battleScene: BattleScene): BattleController = BattleControllerImpl(game, battleScene)
}