package model

import scala.util.Random
import scala.language.postfixOps

trait Battle extends Observable {

  def user: Player
  def enemy: Player
  def drawCard(player: Player): Unit
  def fight(userCard: Card, enemyCard: Card): Unit
  def checkWinner(): (Option[Player],Option[Player])

}

case class LevelUp()

object Battle {

  private class BattleImpl(var user: Player, var enemy: Player) extends Battle {

    /**
     * Handles draw card action for a specific player.
     * @param player has to draw.
     */
    override def drawCard(player: Player): Unit = notifyObserver(player.battleDeck(Random.nextInt(player.battleDeck.length)), player)

    /**
     * Checks over card type and family and compute damage for each player.
     * @param userCard played by the user.
     * @param enemyCard played by the enemy.
     */
    override def fight(userCard: Card, enemyCard: Card): Unit = {
      (userCard.family._1, enemyCard.family._1) match {
        case (Category.Attack, Category.Attack) =>
          user  - enemyCard.value
          enemy - userCard.value
        case (Category.Defense, Category.Defense) => ;
        case (Category.Attack, Category.Defense) => calculateDamage(userCard, enemyCard, enemy)
        case (_,_) => calculateDamage(enemyCard, userCard, user)
      }
      notifyObserver(user, userCard)
      notifyObserver(enemy, enemyCard)
    }

    /**
     * Checks the winner: user win if has HP > 0 and enemy has HP <= 0; enemy win if user's HP <= 0, otherwise game goes on.
     * @return (Option[Player],Option[Player]).
     */
    override def checkWinner(): (Option[Player], Option[Player]) = {
      if(user.actualHealthPoint > 0 && enemy.actualHealthPoint <= 0) {
        if(user.experience <= enemy.experience) notifyObserver(Some(user), Some(LevelUp))
        else notifyObserver(Some(user), None)
        (Some(user), Some(enemy))
      } else if(user.actualHealthPoint <= 0) {
        notifyObserver(Some(enemy), None)
        (None,None)
      }
      else {
        notifyObserver(None, None)
        (None,None)
      }
    }

    private def calculateDamage(card1: Card, card2: Card, player: Player): Unit = {
      if (card1.family._2 == card2.family._2) {
        hitPlayer(player, if(card1.value - card2.value > 0) card1.value - card2.value else 0)
      } else {
        hitPlayer(player, card1 value)
      }
    }

    private def hitPlayer(player: Player, damage: Int): Unit =  player match {
      case _: User => user - damage
      case _ => enemy - damage
    }
  }

  def apply(user: User, enemy: Enemy): Battle = new BattleImpl(user, enemy)
}
