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
    case PlayerType.User => conditionalDraw(playerType)
    case _ => battleScene.drawCard(playerType)(getCardAndReinsert(game.enemy))
  }

  def fight(nameUserCard: String, nameEnemyCard: String): Unit = game.fight(findCardFromName(nameUserCard)(game.user), findCardFromName(nameEnemyCard)(game.enemy)) match {
    case (Some(_), Some(_)) =>
      battleScene.updateHealthPoint(PlayerType.User, game.healthPointPlayer1 / game.user.healthPoint)
      battleScene.updateHealthPoint(PlayerType.EnemyType, game.healthPointPlayer2 / game.enemy.healthPoint)
      drawCard(PlayerType.EnemyType)
    case (Some(_), None) =>
      battleScene.updateHealthPoint(PlayerType.User, game.healthPointPlayer1 / game.user.healthPoint)
      drawCard(PlayerType.EnemyType)
    case (None, Some(_)) =>
      battleScene.updateHealthPoint(PlayerType.EnemyType, game.healthPointPlayer2 / game.enemy.healthPoint)
      drawCard(PlayerType.EnemyType)
    case (_,_) => drawCard(PlayerType.EnemyType)

  }

  private def conditionalDraw(playerType: PlayerType): Unit = if(battleScene.isDrawingAllowed) {
    battleScene.drawCard(playerType)(getCardAndReinsert(game.user))
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

  private def findCardFromName(name: String)(player: Player): Card = findDeck(player).find(card => card.name == name).get
}

case class BattleControllerImpl(override val game: Game, override val battleScene: BattleScene) extends BattleController

object BattleController {
  def apply(game: Game, battleScene: BattleScene): BattleController = BattleControllerImpl(game, battleScene)
}