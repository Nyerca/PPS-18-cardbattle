package controller

import Utility.GameObjectFactory.createCards
import Utility.GameObjectFactory
import model.{Card, Enemy, Player, User}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage
import view.scenes.{BaseScene, MapScene}

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
  var gameMap: MapScene = _
  var user: User = _
  val allCards: List[Card] = GameObjectFactory.createCards(1)
  def difficulty: Difficulty
  def setMapScene(scene: BaseScene): Unit
  def setUserInformation(operationType: OperationType, parentStage: Stage): Unit
  def spawnEnemy(randomIndex: Int): Enemy
}


class GameControllerImpl(var difficulty: Difficulty = Difficulty.Easy) extends GameController {

  private var enemyCount: Map[EnemyType, Int] = Map(EnemyType.Sphinx -> 0, EnemyType.Cobra -> 0, EnemyType.EgyptWarrior -> 0, EnemyType.Griffin -> 0, EnemyType.YellowBlob -> 0)

  override def setMapScene(scene: BaseScene): Unit = {
    scene.changeScene(gameMap)
    checkUserLevelUp(scene)
  }


  override def setUserInformation(operationType: OperationType, parentStage: Stage): Unit = operationType match {
    case OperationType.NewGame =>
      user = Player.userFactory("Player 1", "images/user.png", Random.shuffle(allCards).take(8))
      gameMap = MapScene(parentStage, this)
    case _ => loadData
  }

  override def spawnEnemy(randomIndex: Int): Enemy = difficulty match {
    case Difficulty.Easy => createEnemy(enemyCount.keys.toList(randomIndex), if (user.level - 1 > 0) user.level - 1 else user.level, if(getCardLevelAvg - 1 > 0) getCardLevelAvg - 1 else getCardLevelAvg, enemyCount(enemyCount.keys.toList(randomIndex)))
    case Difficulty.Medium => createEnemy(enemyCount.keys.toList(randomIndex), user.level, getCardLevelAvg, enemyCount(enemyCount.keys.toList(randomIndex)))
    case Difficulty.Hard => createEnemy(enemyCount.keys.toList(randomIndex), user.level + 1, getCardLevelAvg + 1, enemyCount(enemyCount.keys.toList(randomIndex)))
  }

  private def loadData: Unit = ???

  private def checkUserLevelUp(scene: BaseScene): Unit = {
      if (user.missingExperience <= 0) {
        user.missingExperience = 5 * user.level - user.missingExperience
        new Alert(AlertType.Information) {
          initOwner(scene.parentStage)
          title = "Card level up"
          headerText = "Congratulations, you raised level " + user.level
        }.showAndWait()
    }
  }

  private def getCardLevelAvg: Int = user.allCards.map(card => card.level).sum / user.allCards.size

  private def createEnemy(enemyType: EnemyType, enemyLevel: Int, cardLevel: Int, enemyTypeCounter: Int): Enemy = {
    enemyCount += (enemyType -> (enemyCount(enemyType) + 1))
    enemyType match {
      case EnemyType.Sphinx => Player.enemyFactory("Sphinx", "images/sphinx.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 2 + enemyTypeCounter)
      case EnemyType.Cobra => Player.enemyFactory("Cobra", "images/cobra.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 2 + enemyTypeCounter)
      case EnemyType.Griffin => Player.enemyFactory("Griffin", "images/griffin.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 2 + enemyTypeCounter)
      case EnemyType.EgyptWarrior => Player.enemyFactory("Egypt Warrior", "images/warrior.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 2 + enemyTypeCounter)
      case EnemyType.YellowBlob => Player.enemyFactory("Yellow Blob", "images/blob.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 2 + enemyTypeCounter)
    }
  }
}

object GameController {
  def apply(): GameController = new GameControllerImpl()
}