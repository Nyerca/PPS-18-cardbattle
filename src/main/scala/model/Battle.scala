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

object Battle {

  private class BattleImpl(var user: Player, var enemy: Player) extends Battle {

    override def drawCard(player: Player): Unit = notifyObserver(player.battleDeck(Random.nextInt(player.battleDeck.length)), player)

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

    override def checkWinner(): (Option[Player], Option[Player]) = {
      if(user.actualHealthPoint > 0 && enemy.actualHealthPoint <= 0) {
        notifyObserver(Some(user))
        (Some(user), Some(enemy))
      } else if(user.actualHealthPoint <= 0) {
        notifyObserver(Some(enemy))
        (None,None)
      }
      else {
        notifyObserver(None)
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
