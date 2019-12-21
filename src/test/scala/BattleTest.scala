import scala.language.postfixOps
import model.{Battle, Card, Category, Enemy, Player, Type, User}
import org.junit.runner.RunWith
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BattleTest extends FunSpec with Matchers {

  describe("base card") {
    var card = Card("fireBall", "", 1, (Category.Attack, Type.Magic), 2, 1)
    it("should have level 2 when cardMissingForNextLevel = 0") {
      card = card.up.up
      assert(card.level == 2)
      assert(card.value == 4)
    }
  }

  describe("base user")  {
    val baseUser: User = Player.User("player1", "", 1, List(), 10, 10, 1, 1)
    val baseEnemy: Enemy = Player.Enemy("enemy", "", 1, List(), 5, 5, 1, 0)
    val card = Card("fireBall", "", 1, (Category.Attack, Type.Magic), 2, 1)

    it("should initially have empty deck ") {
        assert(baseUser.battleDeck.isEmpty)
    }
    it("should gain card to deck if user does not already own the card") {
      baseUser ++ card
      assert(baseUser.allCards.size == 1)
    }
    it("should have the same card number when earns a card he/she already got") {
      baseUser -> card
      baseUser -> card
      baseUser -> card
      assert(baseUser.allCards.size == 1)
    }
    it("should have his/her level increased when missing experience turn to 0") {
      baseUser ++ baseEnemy
      assert(baseUser.level == 2)
    }
  }

  describe("base battle") {
    val card1 = Card("fireBall", "", 1, (Category.Attack, Type.Magic), 2, 1)
    val card2 = Card("physicShield", "", 1, (Category.Defense, Type.Physic), 2, 1)
    val card3 = Card("magicShield", "", 1, (Category.Defense, Type.Magic), 2, 1)
    val card4 = Card("physicAttack", "", 1, (Category.Attack, Type.Physic), 2, 1)
    val baseUser: User = Player.User("player1", "", 1, List(card1,card2,card3,card4), 10, 10, 1, 1)
    val baseEnemy: Enemy = Player.Enemy("enemy", "", 1, List(card1,card2,card3,card4), 5, 5, 1, 0)
    val battle = Battle(baseUser, baseEnemy)
    it("should decrement someone's life point when suffer from attack without defense") {
      battle.fight(battle.user.battleDeck.head, battle.enemy.battleDeck.head)
      battle.fight(battle.user.battleDeck.head, battle.enemy.battleDeck(1))
      battle.fight(battle.user.battleDeck(1), battle.enemy.battleDeck.head)
      assert(baseEnemy.actualHealthPoint == 1)
      assert(baseUser.actualHealthPoint == 6)
      assert(battle.checkWinner() == (None, None))
    }
    it("should not decrement someone's life point when he/she denfens himself/herself") {
      battle.fight(battle.user.battleDeck.head, battle.enemy.battleDeck(2))
      assert(baseEnemy.actualHealthPoint == 1)
    }

    it("should win player 1 if he/she has life points > 0 and enemy has  life points <= 0") {
      battle.fight(battle.user.battleDeck.head, battle.enemy.battleDeck.head)
      assert(battle.checkWinner() == (Some(baseUser), Some(baseEnemy)))

    }


  }
}
