package controller


import java.io.{FileInputStream, ObjectInputStream}

import Utility.GameObjectFactory.createCards
import Utility.{GUIObjectFactory, GameObjectFactory}
import model._
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.media.MediaPlayer.Status
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

  def gameMap: MapScene

  def user: User

  def allCards: List[Card] = GameObjectFactory.createCards(1)

  var difficulty: Difficulty = _

  def setScene(fromScene: BaseScene, toScene: BaseScene = gameMap): Unit

  def setUserInformation(operationType: OperationType, fromScene: BaseScene, name: String = ""): Unit

  def spawnEnemy(randomIndex: Int): Enemy

}


object GameController {
  private class GameControllerImpl() extends GameController {

    private var enemyCount: Map[EnemyType, Int] = Map(EnemyType.Sphinx -> -1, EnemyType.Cobra -> -1, EnemyType.EgyptWarrior -> -1, EnemyType.Griffin -> -1, EnemyType.YellowBlob -> -1)

    var user: User = _

    var gameMap: MapScene = _

    override def setScene(fromScene: BaseScene, toScene: BaseScene): Unit = {
      (fromScene, toScene) match {
        case (_: MainScene, _: MapScene) => MusicPlayer.play(SoundType.MapSound)
        case (_: MapScene, _: BattleScene) => MusicPlayer.play(SoundType.BattleSound)
        case (_: BattleScene, _: GameOverScene) => MusicPlayer.play(SoundType.LoseSound)
        case (_: BattleScene, _: RewardScene) => MusicPlayer.play(SoundType.WinningSound)
        case (_: RewardScene, _: MapScene) =>
          MusicPlayer.play(SoundType.MapSound)
          gameMap.removeEnemyCell()
          checkUserLevelUp
        case (_, _: MainScene) => MusicPlayer.changeStatus(Status.Paused)
        case (_: MapScene, newMap: MapScene) =>
          MusicPlayer.play(SoundType.MapSound)
          gameMap = newMap
        case _ => ;
      }
      fromScene.changeScene(toScene)
    }

    override def setUserInformation(operationType: OperationType, fromScene: BaseScene, name: String): Unit = {
      operationType match {
        case OperationType.NewGame =>
          user = Player(name, "images/user.png", Random.shuffle(allCards).take(8)).asInstanceOf[User]
          gameMap = MapScene(fromScene.parentStage, this)
        case _ => loadData(fromScene)
      }
      setScene(fromScene)
    }

    override def spawnEnemy(randomIndex: Int): Enemy = difficulty match {
      case Difficulty.Easy => createEnemy(enemyCount.keys.toList(randomIndex), if (user.level - 1 > 0) user.level - 1 else user.level, if(Math.round(user.battleDeck.map(card => card.level).sum.toDouble / user.battleDeck.size.toDouble).toInt - 1 > 0) Math.round(user.battleDeck.map(card => card.level).sum.toDouble / user.battleDeck.size.toDouble).toInt - 1 else Math.round(user.battleDeck.map(card => card.level).sum.toDouble / user.battleDeck.size.toDouble) toInt)
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
        case Failure(_)  => GUIObjectFactory.alertFactory(AlertType.Error, fromScene.parentStage, "File not Found", "Load file not found").showAndWait()
      }
    }

    private def checkUserLevelUp: Unit = if(user.experience <= 0) {
      user.experience += 3 * user.level
      LevelUpAnimation.play(LevelUpAnimation.LEVELUP_PREFIX)
    }

    private def createEnemy(enemyType: EnemyType, enemyLevel: Int, cardLevel: Int): Enemy = {
      enemyCount += (enemyType -> (enemyCount(enemyType) + 1))
      enemyType match {
        case EnemyType.Sphinx => Player("Sphinx", "images/sphinx.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 5 + enemyCount(enemyType), 0, enemyLevel).asInstanceOf[Enemy]
        case EnemyType.Cobra => Player("Cobra", "images/cobra.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 5 + enemyCount(enemyType), 0, enemyLevel).asInstanceOf[Enemy]
        case EnemyType.Griffin => Player("Griffin", "images/griffin.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 5 + enemyCount(enemyType), 0, enemyLevel).asInstanceOf[Enemy]
        case EnemyType.EgyptWarrior => Player("Egypt Warrior", "images/warrior.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 5 + enemyCount(enemyType), 0, enemyLevel).asInstanceOf[Enemy]
        case EnemyType.YellowBlob => Player("Yellow Blob", "images/blob.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 5 + enemyCount(enemyType), 0, enemyLevel).asInstanceOf[Enemy]
      }
    }
  }

  def apply(): GameController = new GameControllerImpl()
}