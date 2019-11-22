


import scala.language.postfixOps
import model.{Card, Category, Enemy, Game, Player, Type, User}
import org.junit.runner.RunWith
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BattleTest extends FunSpec with Matchers {

  describe("base card") {
    val card = Card("fireBall", "", (Category.Attack, Type.Magic))
    it("should have level 1") {
      assert(card.level == 1)
    }
    it("should have 2 missing card for next level when level = 1") {
      assert(card.cardMissingForNextLevel == 2)
    }
    it("should have level 2 when cardMissingForNextLevel = 0") {
      card.incrementCardNumber()
      card.incrementCardNumber()
      assert(card.level == 2)
    }
  }

  describe("base user")  {
    val baseUser: User = Player.userFactory("user", "")
    val card = Card("fireBall", "", (Category.Attack, Type.Magic))

    it("should initially have empty deck ") {
        assert(baseUser.battleDeck.isEmpty)
    }
    it("should have some cards in battle deck when updated") {
      baseUser battleDeck = List(card)
      assert(baseUser.battleDeck.size == 1)
    }
    it("should gain card to deck if user does not already own the card") {
      baseUser gainCard card
      assert(baseUser.allCards.size == 1)
      assert(baseUser.allCards == List(card))
    }
    it("should have the same card number when earns a card he/she already got") {
      baseUser gainCard card
      baseUser gainCard card
      baseUser gainCard card
      assert(baseUser.allCards.size == 1)
    }
    it("should have his/her level increased when missing experience turn to 0") {
      baseUser addExperience 4
      assert(baseUser.level == 2)
    }
  }

  describe("in a game") {
    val deck: List[Card] =  List(Card("fireBall", "", (Category.Attack, Type.Magic)),
      Card("iceBall", "", (Category.Attack, Type.Magic)),
      Card("ariete", "", (Category.Attack, Type.Physic)),
      Card("magicShield", "", (Category.Defense, Type.Magic)),
      Card("physicShield", "", (Category.Defense, Type.Physic)))
    it("two attack type cards should decrement life points of the two players") {
      val baseUser: User = Player.userFactory("user", "", deck, deck)
      val baseEnemy: Enemy = Player.enemyFactory("enemy", "", deck)
      val game: Game = Game(baseUser, baseEnemy)
      assert(game.fight(deck(0), deck(1)) == (Some(baseUser), Some(baseEnemy)))
      assert(game.healthPointPlayer2 == baseEnemy.healthPoint - deck(0).value)
      assert(game.healthPointPlayer1 == baseUser.healthPoint - deck(1).value)
    }
    it("magic/physic type card vs defense one should result in the difference betweend their values") {
      val baseUser: User = Player.userFactory("user", "", deck, deck)
      val baseEnemy: Enemy = Player.enemyFactory("enemy", "", deck)
      val game: Game = Game(baseUser, baseEnemy)
      assert(game.fight(deck(0), deck(3)) == (None, Some(baseEnemy)))
      assert(game.healthPointPlayer2 == baseEnemy.healthPoint - (deck(0).value - deck(3).value))
      assert(game.healthPointPlayer1 == baseUser.healthPoint)
      assert(game.fight(deck(3), deck(0)) == (Some(baseUser),None))
      assert(game.healthPointPlayer1 == baseUser.healthPoint - (deck(3).value - deck(0).value))
      assert(game.healthPointPlayer2 == baseEnemy.healthPoint)
    }
  }

}
