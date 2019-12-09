package controller

import java.io.{FileInputStream, ObjectInputStream}

import Utility.GameObjectFactory.createCards
import Utility.{GUIObjectFactory, GameObjectFactory}
import model._
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage
import view.scenes.{BaseScene, MapScene}

import scala.collection.mutable.ListBuffer
import scala.util.Random
import scalafx.Includes._

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
  var gameMap: MapScene = _

  var user: User = _

  val allCards: List[Card] = GameObjectFactory.createCards(1)

  var difficulty: Difficulty

  def setScene(fromScene: BaseScene, toScene: BaseScene = gameMap): Unit

  def setUserInformation(operationType: OperationType, parentStage: Stage): Unit

  def spawnEnemy(randomIndex: Int): Enemy


}


class GameControllerImpl(var difficulty: Difficulty = Difficulty.Medium) extends GameController {

  private var enemyCount: Map[EnemyType, Int] = Map(EnemyType.Sphinx -> -1, EnemyType.Cobra -> -1, EnemyType.EgyptWarrior -> -1, EnemyType.Griffin -> -1, EnemyType.YellowBlob -> -1)

  override def setScene(fromScene: BaseScene, toScene: BaseScene): Unit =  toScene match {
    case map: MapScene =>
      fromScene.changeScene(map)
      map.removeEnemyCell()
      checkUserLevelUp
    case _ => fromScene.changeScene(toScene)
  }

  override def setUserInformation(operationType: OperationType, parentStage: Stage): Unit = operationType match {
    case OperationType.NewGame =>
      user = Player.userFactory("Player 1", "images/user.png", Random.shuffle(allCards).take(8))
      MusicPlayer.play(SoundType.MapSound)
      gameMap = MapScene(parentStage, this)

    case _ =>{MusicPlayer.play(SoundType.MapSound); loadData(parentStage)}
  }

  override def spawnEnemy(randomIndex: Int): Enemy = difficulty match {
    case Difficulty.Easy => createEnemy(enemyCount.keys.toList(randomIndex), if (user.level - 1 > 0) user.level - 1 else user.level, if(getCardLevelAvg - 1 > 0) getCardLevelAvg - 1 else getCardLevelAvg)
    case Difficulty.Medium => createEnemy(enemyCount.keys.toList(randomIndex), user.level, getCardLevelAvg)
    case Difficulty.Hard => createEnemy(enemyCount.keys.toList(randomIndex), user.level + 1, getCardLevelAvg + 1)
  }


  private def loadData(parentStage: Stage): Unit = {
    val input = new ObjectInputStream(new FileInputStream("./src/main/saves/save2.txt"))
    val list  : List[RectangleCell] = input.readObject().asInstanceOf[List[RectangleCell]]
    val player : PlayerRepresentation = input.readObject().asInstanceOf[PlayerRepresentation]
    user = input.readObject().asInstanceOf[User]
   difficulty = input.readObject().asInstanceOf[Difficulty]

    println("DIFFICULTY: " + difficulty)
    val traslationX = input.readObject().asInstanceOf[Double]
    val traslationY = input.readObject().asInstanceOf[Double]
    input.close()

    val lis :ListBuffer[RectangleWithCell] = new ListBuffer[RectangleWithCell]
    for (tmpRect <- list) {
      lis.append(new RectangleWithCell(tmpRect.getWidth, tmpRect.getHeight, tmpRect.x, tmpRect.y,tmpRect) {
        fill = RectangleCell.createImage(tmpRect.url, tmpRect.rotation)
      } )
    }

    gameMap = MapScene(parentStage, this, lis, Option(player.position),traslationX,traslationY)
    gameMap.setPaneChildren(lis, Option.empty)

  }

  private def checkUserLevelUp: Unit = user.experience match {
    case n if n <= 0 =>
      user.experience = 5 * user.level - n
      GUIObjectFactory.alertFactory(AlertType.Information, gameMap.parentStage, "User level up", "Congratulations, you raised level " + user.level).showAndWait()
    case _ => ;
  }


  private def getCardLevelAvg: Int = {
    val avg: Double = user.battleDeck.map(card => card.level).sum.toDouble / user.battleDeck.size.toDouble
    if (avg - avg.toInt > 0.4) {
      avg.toInt + 1
    } else {
      avg.toInt
    }
  }

  private def createEnemy(enemyType: EnemyType, enemyLevel: Int, cardLevel: Int): Enemy = {
    enemyCount += (enemyType -> (enemyCount(enemyType) + 1))
    enemyType match {
      case EnemyType.Sphinx => Player.enemyFactory("Sphinx", "images/sphinx.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 5 + enemyCount(enemyType))
      case EnemyType.Cobra => Player.enemyFactory("Cobra", "images/cobra.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 5 + enemyCount(enemyType))
      case EnemyType.Griffin => Player.enemyFactory("Griffin", "images/griffin.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 5 + enemyCount(enemyType))
      case EnemyType.EgyptWarrior => Player.enemyFactory("Egypt Warrior", "images/warrior.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 5 + enemyCount(enemyType))
      case EnemyType.YellowBlob => Player.enemyFactory("Yellow Blob", "images/blob.png", Random.shuffle(createCards(cardLevel)).take(8),enemyLevel, 5 + enemyCount(enemyType))
    }
  }

}

object GameController {
  def apply(): GameController = new GameControllerImpl()
}