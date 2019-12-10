package controller


import java.io.{FileInputStream, ObjectInputStream}

import Utility.GameObjectFactory.createCards
import Utility.GameObjectFactory
import model._
import scalafx.stage.Stage
import view.scenes.{BaseScene, BattleScene, EquipmentScene, MainScene, MapScene, RewardScene}

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


  override def setScene(fromScene: BaseScene, toScene: BaseScene): Unit =  {
    fromScene match {
      case _: EquipmentScene => ;
      case _: MainScene => MusicPlayer.play(SoundType.MapSound)
      case _: BattleScene => MusicPlayer.play(SoundType.LoseSound)
      case _: RewardScene =>
        MusicPlayer.play(SoundType.MapSound)
        gameMap.removeEnemyCell()
        checkUserLevelUp
      case _ => MusicPlayer.mediaPlayer.get.pause()
    }
    fromScene.changeScene(toScene)
  }

  override def setUserInformation(operationType: OperationType, parentStage: Stage): Unit = operationType match {
    case OperationType.NewGame =>
      user = Player.userFactory("Player 1", "images/user.png", Random.shuffle(allCards).take(8))
      gameMap = MapScene(parentStage, this)
    case _ => loadData(parentStage)
  }

  override def spawnEnemy(randomIndex: Int): Enemy = difficulty match {
    case Difficulty.Easy => createEnemy(enemyCount.keys.toList(randomIndex), if (user.level - 1 > 0) user.level - 1 else user.level, if(getCardLevelAvg - 1 > 0) getCardLevelAvg - 1 else getCardLevelAvg)
    case Difficulty.Medium => createEnemy(enemyCount.keys.toList(randomIndex), user.level, getCardLevelAvg)
    case Difficulty.Hard => createEnemy(enemyCount.keys.toList(randomIndex), user.level + 1, getCardLevelAvg + 1)
  }


  private def loadData(parentStage: Stage): Unit = {
    import FileManager._
    input = new ObjectInputStream(new FileInputStream("./src/main/saves/save2.txt"))
    user = load[User](input)
    difficulty = FileManager.load[Difficulty](input)
    gameMap = MapScene(parentStage, this, load[ListBuffer[RectangleCell]](input).map(rc => new RectangleWithCell(rc.getWidth, rc.getHeight, rc.x, rc.y, rc) {
      fill = RectangleCell.createImage(rc.url, rc.rotation)
    }), Option(load[PlayerRepresentation](input).position), load[Double](input), load[Double](input))
    //gameMap.setPaneChildren(list, Option.empty)
    input.close()
  }

  private def checkUserLevelUp: Unit = user.experience match {
    case n if n <= 0 =>
      user.experience = 3 * user.level - n
      gameMap.playLevelUpAnimation()
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