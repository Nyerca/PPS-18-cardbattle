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

trait OperationType2

object OperationType2 {
  case object LoadGame extends OperationType2
  case object NewGame extends OperationType2
}

trait EnemyType2

object EnemyType2 {
  case object Sphinx extends EnemyType2
  case object Griffin extends EnemyType2
  case object EgyptWarrior extends EnemyType2
  case object Cobra extends EnemyType2
  case object YellowBlob extends EnemyType2
}

trait Difficulty2

object Difficulty2 {
  case object Easy extends Difficulty2
  case object Medium extends Difficulty2
  case object Hard extends Difficulty2
}

trait GameController2 {

  def gameMap: MapScene2

  var user: User2

  def allCards: List[Card] = GameObjectFactory.createCards(1)

  var difficulty: Difficulty2 = _

  def setScene(fromScene: BaseScene2, toScene: BaseScene2 = gameMap): Unit

  def setUserInformation(operationType: OperationType2, fromScene: BaseScene2, name: String = ""): Unit

  def spawnEnemy(randomIndex: Int): Enemy2
}


object GameController2 {
  private class GameControllerImpl2() extends GameController2 {

    private var enemyCount: Map[EnemyType2, Int] = Map(EnemyType2.Sphinx -> -1, EnemyType2.Cobra -> -1, EnemyType2.EgyptWarrior -> -1, EnemyType2.Griffin -> -1, EnemyType2.YellowBlob -> -1)

    var user: User2 = _

    var gameMap: MapScene2 = _

    override def setScene(fromScene: BaseScene2, toScene: BaseScene2): Unit = {
      (fromScene, toScene) match {
        case (_: MainScene2, _: MapScene2) => MusicPlayer.play(SoundType.MapSound)
        case (_: MapScene2, _: BattleScene2) => MusicPlayer.play(SoundType.BattleSound)
        case (_: BattleScene2, _: GameOverScene2) => MusicPlayer.play(SoundType.LoseSound)
        case (_: BattleScene2, _: RewardScene2) => MusicPlayer.play(SoundType.WinningSound)
        case (_: RewardScene2, _: MapScene2) =>
          MusicPlayer.play(SoundType.MapSound)
          gameMap.removeEnemyCell()

        case (_, _: MainScene2) => MusicPlayer.changeStatus(Status.Paused)
        case (_: MapScene2, newMap: MapScene2) =>
          MusicPlayer.play(SoundType.MapSound)
          gameMap = newMap
        case _ => ;
      }
      fromScene.changeScene(toScene)
    }

    override def setUserInformation(operationType: OperationType2, fromScene: BaseScene2, name: String): Unit = {
      operationType match {
        case OperationType2.NewGame =>
          val tmp: List[Card] = Random.shuffle(allCards).take(8)
          user = User2(name, "images/user.png", 1, tmp, 10, 10, 1, 0, tmp)
          gameMap = MapScene2(fromScene.parentStage, this)
        case _ => loadData(fromScene)
      }
      setScene(fromScene)
    }

    override def spawnEnemy(randomIndex: Int): Enemy2 = difficulty match {
      case Difficulty2.Easy => createEnemy(enemyCount.keys.toList(randomIndex), if ( user.level - 1 > 0 ) user.level - 1 else user.level, if ( Math.round(user.battleDeck.map(card => card.level).sum.toDouble / user.battleDeck.size.toDouble).toInt - 1 > 0 ) Math.round(user.battleDeck.map(card => card.level).sum.toDouble / user.battleDeck.size.toDouble).toInt - 1 else Math.round(user.battleDeck.map(card => card.level).sum.toDouble / user.battleDeck.size.toDouble) toInt)
      case Difficulty2.Medium => createEnemy(enemyCount.keys.toList(randomIndex), user.level, Math.round(user.battleDeck.map(card => card.level).sum.toDouble / user.battleDeck.size.toDouble) toInt)
      case Difficulty2.Hard => createEnemy(enemyCount.keys.toList(randomIndex), user.level + 1, Math.round(user.battleDeck.map(card => card.level).sum.toDouble / user.battleDeck.size.toDouble).toInt + 1)
    }


    private def loadData(fromScene: BaseScene2): Unit = {
      import FileManager._
      Try(new ObjectInputStream(new FileInputStream("./src/main/saves/save.txt"))) match {
        case Success(value) =>
          //user = load[User](value)
          //difficulty = FileManager.load[Difficulty](value)
          //gameMap = MapScene(fromScene.parentStage, this, load[List[RectangleCell]](value), Option(load[PlayerRepresentation](value).position), load[Double](value), load[Double](value))
          value.close()
        case Failure(_) => GUIObjectFactory.alertFactory(AlertType.Error, fromScene.parentStage, "File not Found", "Load file not found").showAndWait()
      }
    }

    private def createEnemy(enemyType: EnemyType2, enemyLevel: Int, cardLevel: Int): Enemy2 = {
      enemyCount += (enemyType -> (enemyCount(enemyType) + 1))
      enemyType match {
        case EnemyType2.Sphinx => Enemy2("Sphinx", "images/sphinx.png", enemyLevel, Random.shuffle(createCards(cardLevel)).take(8), 5 + enemyCount(enemyType), 5 + enemyCount(enemyType), enemyLevel, enemyLevel)
        case EnemyType2.Cobra => Enemy2("Cobra", "images/cobra.png", enemyLevel, Random.shuffle(createCards(cardLevel)).take(8), 5 + enemyCount(enemyType), 5 + enemyCount(enemyType), enemyLevel, enemyLevel)
        case EnemyType2.Griffin => Enemy2("Griffin", "images/griffin.png", enemyLevel, Random.shuffle(createCards(cardLevel)).take(8), 5 + enemyCount(enemyType), 5 + enemyCount(enemyType), enemyLevel, enemyLevel)
        case EnemyType2.EgyptWarrior => Enemy2("Egypt Warrior", "images/warrior.png", enemyLevel, Random.shuffle(createCards(cardLevel)).take(8), 5 + enemyCount(enemyType), 5 + enemyCount(enemyType), enemyLevel, enemyLevel)
        case EnemyType2.YellowBlob => Enemy2("Yellow Blob", "images/blob.png", enemyLevel, Random.shuffle(createCards(cardLevel)).take(8), 5 + enemyCount(enemyType), 5 + enemyCount(enemyType), enemyLevel, enemyLevel)
      }
    }
  }
  def apply(): GameController2 = new GameControllerImpl2()
}