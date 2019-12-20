package controller

import model.{Battle, Card, Player, User}
import view.scenes.BattleScene

trait BattleController {

  def battleScene: BattleScene
  def game: Battle
  def gameController: GameController
  def drawCard(playerType: Player): Unit
  def fight(userCard: Card, enemyCard: Card): Unit
  def checkWinner(): Unit

}

object BattleController {
  private class BattleControllerImpl(override val battleScene: BattleScene, override val game: Battle, override val gameController: GameController) extends BattleController {

    game.addObserver(battleScene)

    override def drawCard(playerType: Player): Unit = game.drawCard(playerType)

    override def fight(userCard: Card, enemyCard: Card): Unit = game.fight(userCard, enemyCard)

    override def checkWinner(): Unit = game.checkWinner() match {
      case (Some(user), Some(enemy)) => gameController.user = user.asInstanceOf[User] ++ enemy
      case _ => ;
    }
  }
  def apply(battleScene2: BattleScene, game: Battle, gameController2: GameController): BattleController = new BattleControllerImpl(battleScene2, game, gameController2)
}
