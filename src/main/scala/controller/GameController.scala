package controller

import Utility.GameObjectFactory.createCards
import Utility.GameObjectFactory
import model.{Card, Enemy, Player, User}
import scalafx.stage.Stage
import view.scenes.BaseScene
import view.map

import scala.util.Random

trait OperationType

object OperationType {
  case object Load extends OperationType
  case object NewGame extends OperationType
}

trait EnemyType

object EnemyType {
  case object Sphinx extends EnemyType
  case object Griffin extends EnemyType
  case object EgyptWarrior extends EnemyType
  case object Cobra extends EnemyType
  case object YellowBlob extends EnemyType
}

trait Difficulty

object Difficulty {
  case object Easy extends Difficulty
  case object Medium extends Difficulty
  case object Hard extends Difficulty
}

trait GameController {
  var gameMap: map // = map(mainScene.parentStage)
  val allCards: List[Card] = GameObjectFactory.createCards(1)
  //def mainScene: BaseScene
  def difficulty: Difficulty
  var user: User = _
  def setMapScene(): Unit
  def setUserInformation(operationType: OperationType): Unit
  def spawnEnemy(randomIndex: Int): Enemy
}


class GameControllerImpl(parentStage: Stage, override val difficulty: Difficulty = Difficulty.Medium) extends GameController {
  private var enemyCount: Map[EnemyType, Int] = Map(EnemyType.Sphinx -> 0, EnemyType.Cobra -> 0, EnemyType.EgyptWarrior -> 0, EnemyType.Griffin -> 0, EnemyType.YellowBlob -> 0)
  var gameMap: map = _
  override def setMapScene(): Unit = parentStage.scene_=(gameMap.getScene())

  override def setUserInformation(operationType: OperationType): Unit = operationType match {
    case OperationType.NewGame =>  {
      println("START")
      user = Player.userFactory("Player 1", "images/user.png", Random.shuffle(allCards).take(8))
      println(user)
      gameMap = map(parentStage, this)
    }
    case _ => loadData
  }

  override def spawnEnemy(randomIndex: Int): Enemy = difficulty match {
    case Difficulty.Easy => createEnemy(enemyCount.keys.toList(randomIndex), if (user.level - 1 > 0) user.level - 1 else user.level, if(getCardLevelAvg - 1 > 0) getCardLevelAvg - 1 else getCardLevelAvg, enemyCount(enemyCount.keys.toList(randomIndex)))
    case Difficulty.Medium => {
      /*
      println("KEYS: " + (enemyCount.keys.toList))
      println("INDEX: " + enemyCount.keys.toList(randomIndex))
      println(user)
      println("CardlevelAVG: " + getCardLevelAvg)
      println("RESTO: " + enemyCount(enemyCount.keys.toList(randomIndex)))
      */
      createEnemy(enemyCount.keys.toList(randomIndex), user.level, getCardLevelAvg, enemyCount(enemyCount.keys.toList(randomIndex)))
    }
    case Difficulty.Hard => createEnemy(enemyCount.keys.toList(randomIndex), user.level + 1, getCardLevelAvg + 1, enemyCount(enemyCount.keys.toList(randomIndex)))
  }

  private def loadData: Unit = ???

  private def getCardLevelAvg: Int = user.allCards.map(card => card.level).sum / user.allCards.size

  private def createEnemy(enemyType: EnemyType, enemyLevel: Int, cardLevel: Int, enemyTypeCounter: Int): Enemy = {
    enemyCount += (enemyType -> (enemyCount(enemyType) + 1))
    enemyType match {
      case EnemyType.Sphinx => Player.enemyFactory("Sphinx", "images/sphinx.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 50 + enemyTypeCounter)
      case EnemyType.Cobra => Player.enemyFactory("Cobra", "images/cobra.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 35 + enemyTypeCounter)
      case EnemyType.Griffin => Player.enemyFactory("Griffin", "images/griffin.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 50 + enemyTypeCounter)
      case EnemyType.EgyptWarrior => Player.enemyFactory("Egypt Warrior", "images/warrior.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 20 + enemyTypeCounter)
      case EnemyType.YellowBlob => Player.enemyFactory("Yellow Blob", "images/blob.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 15 + enemyTypeCounter)
    }
  }
}

object GameController {
  def apply(stage: Stage): GameController = new GameControllerImpl(stage)
}