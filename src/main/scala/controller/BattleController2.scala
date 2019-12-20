package controller

import model.{Battle, Card, Player2, User2}
import view.scenes.BattleScene2

trait BattleController2 {

  def battleScene: BattleScene2
  def game: Battle
  def gameController: GameController2
  def drawCard(playerType: Player2): Unit
  def fight(userCard: Card, enemyCard: Card): Unit
  def checkWinner(): Unit

}

object BattleController2 {
  private class BattleController2Impl(override val battleScene: BattleScene2, override val game: Battle, override val gameController: GameController2) extends BattleController2 {

    game.addObserver(battleScene)

    override def drawCard(playerType: Player2): Unit = game.drawCard(playerType)

    override def fight(userCard: Card, enemyCard: Card): Unit = game.fight(userCard, enemyCard)

    override def checkWinner(): Unit = game.checkWinner() match {
      case (Some(user), Some(enemy)) => gameController.user = user.asInstanceOf[User2] ++ enemy
      case _ => ;
    }
  }
  def apply(battleScene2: BattleScene2, game: Battle, gameController2: GameController2): BattleController2 = new BattleController2Impl(battleScene2, game, gameController2)
}
