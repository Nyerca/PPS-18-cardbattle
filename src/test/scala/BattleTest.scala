


import scala.language.postfixOps
import model.{Card, Category, Enemy, Player, Type, User}
import org.junit.runner.RunWith
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BattleTest extends FunSpec with Matchers {

  describe("base card") {
    val card = Card("fireBall", "", 1, (Category.Attack, Type.Magic), 2, 1)
    it("should have level 2 when cardMissingForNextLevel = 0") {
      card.incrementCardNumber()
      card.incrementCardNumber()
      assert(card.level == 2)
    }
  }

  describe("base user")  {
    val baseUser: User = Player.userFactory("user", "", List())
    val baseEnemy: Enemy = Player.enemyFactory("enemy", "", List(), 1, 5)
    val card = Card("fireBall", "", 1, (Category.Attack, Type.Magic), 2, 1)

    it("should initially have empty deck ") {
        assert(baseUser.battleDeck.isEmpty)
    }
    it("should have some cards in battle deck when updated") {
      baseUser battleDeck = List(card)
      assert(baseUser.battleDeck.size == 1)
    }
    it("should gain card to deck if user does not already own the card") {
      baseUser -> card
      assert(baseUser.allCards.size == 1)
      assert(baseUser.allCards == List(card))
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
}
