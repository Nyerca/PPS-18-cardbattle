package model

import scala.util.Random
import scala.language.postfixOps

trait Battle extends Observable {

  def user: Player2
  def enemy: Player2
  def drawCard(player: Player2): Unit
  def fight(userCard: Card, enemyCard: Card): Unit
  def checkWinner(): (Option[Player2],Option[Player2])

}

object Battle {

  private class BattleImpl(var user: Player2, var enemy: Player2) extends Battle {

    override def drawCard(player: Player2): Unit = notifyObserver(player.battleDeck(Random.nextInt(player.battleDeck.length)), player)

    override def fight(userCard: Card, enemyCard: Card): Unit = {
      (userCard.family._1, enemyCard.family._1) match {
        case (Category.Attack, Category.Attack) =>
          user = user  - enemyCard.value
          enemy = enemy - userCard.value
        case (Category.Defense, Category.Defense) => ;
        case (Category.Attack, Category.Defense) => calculateDamage(userCard, enemyCard, enemy)
        case (_,_) => calculateDamage(enemyCard, userCard, user)
      }
      notifyObserver(user, userCard)
      notifyObserver(enemy, enemyCard)
    }

    override def checkWinner(): (Option[Player2], Option[Player2]) = {
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

    private def calculateDamage(card1: Card, card2: Card, player: Player2): Unit = {
      if (card1.family._2 == card2.family._2) {
        hitPlayer(player, if(card1.value - card2.value > 0) card1.value - card2.value else 0)
      } else {
        hitPlayer(player, card1 value)
      }
    }

    private def hitPlayer(player: Player2, damage: Int): Unit =  player match {
      case _: User2 => user =  user - damage
      case _ => enemy = enemy - damage
    }
  }

  def apply(user: User2, enemy: Enemy2): Battle = new BattleImpl(user, enemy)
}
