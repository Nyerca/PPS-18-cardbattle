package controller


import java.io.{FileInputStream, ObjectInputStream}
import utility.GameObjectFactory.createCards
import utility.{GUIObjectFactory, GameObjectFactory}
import model._
import scalafx.scene.control.Alert.AlertType
import view.scenes._
import scala.language.postfixOps
import scala.util.{Failure, Random, Success, Try}

trait OperationType

object OperationType {
  case object LoadGame extends OperationType
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
  var difficulty: Difficulty
  var user: User
  def gameMap: MapScene
  def allCards: List[Card] = GameObjectFactory.createCards(1)
  def setScene(fromScene: BaseScene, toScene: BaseScene = gameMap): Unit
  def setUserInformation(operationType: OperationType, fromScene: BaseScene, name: String = ""): Unit
  def spawnEnemy(randomIndex: Int): Enemy
}


object GameController {
  private class GameControllerImpl() extends GameController {

    private var enemyCount: Map[EnemyType, Int] = Map(EnemyType.Sphinx -> -1, EnemyType.Cobra -> -1, EnemyType.EgyptWarrior -> -1, EnemyType.Griffin -> -1, EnemyType.YellowBlob -> -1)

    var user: User = _

    var gameMap: MapScene = _

    var difficulty: Difficulty = _

    override def setScene(fromScene: BaseScene, toScene: BaseScene): Unit = {
      (fromScene, toScene) match {
        case (_: RewardScene, _: MapScene) => MusicPlayer.play(SoundType.MapSound)
        case (_: MapScene, newMap: MapScene) =>
          user.removeObserver(gameMap)
          gameMap = newMap
          user.addObserver(gameMap)
        case _ => ;
      }
      fromScene.changeScene(toScene)
    }

    override def setUserInformation(operationType: OperationType, fromScene: BaseScene, name: String): Unit = {
      operationType match {
        case OperationType.NewGame =>
          user = Player.User(name, "images/user.png", 1, Random.shuffle(allCards).take(8), 10, 10, 1, 0)
          gameMap = MapScene(fromScene.parentStage, this)
        case _ => loadData(fromScene)
      }
      user.addObserver(gameMap)
      setScene(fromScene)
    }

    override def spawnEnemy(randomIndex: Int): Enemy = difficulty match {
      case Difficulty.Easy => createEnemy(enemyCount.keys.toList(randomIndex), if ( user.level - 1 > 0 ) user.level - 1 else user.level, if ( Math.round(user.battleDeck.map(card => card.level).sum.toDouble / user.battleDeck.size.toDouble).toInt - 1 > 0 ) Math.round(user.battleDeck.map(card => card.level).sum.toDouble / user.battleDeck.size.toDouble).toInt - 1 else Math.round(user.battleDeck.map(card => card.level).sum.toDouble / user.battleDeck.size.toDouble) toInt)
      case Difficulty.Medium => createEnemy(enemyCount.keys.toList(randomIndex), user.level, Math.round(user.battleDeck.map(card => card.level).sum.toDouble / user.battleDeck.size.toDouble) toInt)
      case Difficulty.Hard => createEnemy(enemyCount.keys.toList(randomIndex), user.level + 1, Math.round(user.battleDeck.map(card => card.level).sum.toDouble / user.battleDeck.size.toDouble).toInt + 1)
    }


    private def loadData(fromScene: BaseScene): Unit = {
      import FileManager._
      Try(new ObjectInputStream(new FileInputStream("./src/main/saves/save.txt"))) match {
        case Success(value) =>
          user = load[User](value)
          difficulty = FileManager.load[Difficulty](value)
          gameMap = MapScene(fromScene.parentStage, this, load[List[RectangleCell]](value), Option(load[PlayerRepresentation](value).position), load[Double](value), load[Double](value))
          value.close()
        case Failure(_) => GUIObjectFactory.alertFactory(AlertType.Error, fromScene.parentStage, "File not Found", "Load file not found").showAndWait()
      }
    }

    private def createEnemy(enemyType: EnemyType, enemyLevel: Int, cardLevel: Int): Enemy = {
      enemyCount += (enemyType -> (enemyCount(enemyType) + 1))
      enemyType match {
        case EnemyType.Sphinx => Player.Enemy("Sphinx", "images/sphinx.png", enemyLevel, Random.shuffle(createCards(cardLevel)).take(8), 5 + enemyCount(enemyType), 5 + enemyCount(enemyType), enemyLevel, enemyLevel)
        case EnemyType.Cobra => Player.Enemy("Cobra", "images/cobra.png", enemyLevel, Random.shuffle(createCards(cardLevel)).take(8), 5 + enemyCount(enemyType), 5 + enemyCount(enemyType), enemyLevel, enemyLevel)
        case EnemyType.Griffin => Player.Enemy("Griffin", "images/griffin.png", enemyLevel, Random.shuffle(createCards(cardLevel)).take(8), 5 + enemyCount(enemyType), 5 + enemyCount(enemyType), enemyLevel, enemyLevel)
        case EnemyType.EgyptWarrior => Player.Enemy("Egypt Warrior", "images/warrior.png", enemyLevel, Random.shuffle(createCards(cardLevel)).take(8), 5 + enemyCount(enemyType), 5 + enemyCount(enemyType), enemyLevel, enemyLevel)
        case EnemyType.YellowBlob => Player.Enemy("Yellow Blob", "images/blob.png", enemyLevel, Random.shuffle(createCards(cardLevel)).take(8), 5 + enemyCount(enemyType), 5 + enemyCount(enemyType), enemyLevel, enemyLevel)
      }
    }
  }

  def apply(): GameController = new GameControllerImpl()
}