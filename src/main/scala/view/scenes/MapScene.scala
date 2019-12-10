package view.scenes

import Utility.GUIObjectFactory
import controller._
import exception._
import javafx.beans.property.{SimpleDoubleProperty, SimpleStringProperty}
import javafx.scene.control.Alert.AlertType
import javafx.scene.input.MouseEvent
import javafx.scene.paint.ImagePattern
import model._
import scalafx.Includes._
import scalafx.scene.Node
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout._
import scalafx.scene.shape.Rectangle
import scalafx.stage.Stage
import scala.collection.mutable.ListBuffer
import scala.util.Random
import model.Cell
import scalafx.application.Platform



class MapScene (override val parentStage: Stage, var _controller : MapController, var gameC :GameController,traslationX : Double = 0, traslationY: Double = 0) extends BaseScene{
  stylesheets.add("mapStyle.css")

  private val _pane: Pane = new Pane {maxHeight = 800; translateX=traslationX; translateY=traslationY}

  private val observableHealthPoint = (new SimpleDoubleProperty(gameC.user.actualHealthPoint.toDouble / gameC.user.totalHealthPoint.toDouble), new SimpleStringProperty("Player: " + gameC.user.actualHealthPoint + "hp"))
  private val life: StackPane = new StackPane {
    children = List(new ProgressBar {
      progress <== observableHealthPoint._1
      styleClass.add("life")
    }, new Label {
      styleClass.add("title")
      text <== observableHealthPoint._2
    })
  }

  private val remainingEnemies = new SimpleStringProperty("Enemies: " + _controller.getAllEnemies.size)
  private val enemies: Label = new Label{text <== remainingEnemies}

  private val observableGold = new SimpleStringProperty("Gold: " +gameC.user.coins+ "x")
  private val gold: Label = new Label{text <== observableGold}

  private val observableLevel = new SimpleStringProperty("Player level: " + gameC.user.level)
  private val level: Label = new Label{text <== observableLevel}

  var volumeSlider: Slider = createSlider("volumeSlider")

  lazy val backToMainMenu: Unit = gameC.setScene(this, MainScene(parentStage))

  def add(tool: ToolBar, list: List[(Node, Boolean)]): Unit = {
    list.map(m=> {
      tool.getItems.add(m._1)
      if(m._2) tool.getItems.add(new Separator())
    })
  }

  private val menu: VBox = new VBox {
    children = GUIObjectFactory.toolbarFactory(List(
      (new Button("Cards"){
        onAction = () => parentStage.scene_=(EquipmentScene(parentStage, gameC))
        layoutX = 110}, true),
      (new Button("Save"){onAction = () => _controller.handleSave()}, true),
      (new Button("Option"){onAction = () => gameC.difficulty = setDifficulty.getOrElse(gameC.difficulty)}, true),
      (new Button("Quit"){onAction = () => backToMainMenu}, true),
      (life, true),
      (level, true),
      (gold, false),
      (new ImageView(new Image("coin.png")), true),
      (enemies, true),
      (volumeSlider, false)
    ))
    minWidth = 1200
  }

  private val bottomPane: HBox = new HBox() {
    id ="bottomRngPane"
    translateX = 300
    translateY = 630
    maxWidth = 500
    maxHeight = 120
    children = createBottomCard()
  }

  private val _bpane: BorderPane = new BorderPane {
    top = menu
    center = _pane
    bottom = bottomPane
  }
  def bpane: BorderPane = _bpane

  _controller.view_(this)

  private var playerImg: Rectangle = icon(_controller.list.head.rectCell, _controller.player.url)
  def playerImg_(player: PlayerRepresentation):Unit = {playerImg.fill_=(new ImagePattern(new Image(player.url)))}

  private val playerPane: Pane = new Pane {
    children = _controller.list
    children.append(_bpane)
    children.append(playerImg)
    children.append(LevelUpAnimation.setup(_controller.list.head.rectCell))
    id = "rootPane"

    onKeyPressed = (ke : KeyEvent) =>  {
      try {
        _controller.handleKey(ke.code)
      } catch {
        case _ : DoubleMovementException => println("You can't move while the previus movement is still executing.")
        case _ : MissingCellException => println("You can't move on a missing cell.")
        case _ : NoMovementException => println("You can't move in that direction because your cell doesn't allow that.")
      }

    }
    onMouseClicked = (e: MouseEvent) => {
      try {
        _controller.handleMouseClicked(e)
      } catch {
        case _ : DoubleCellException => println("You can't place a cell on top of another cell.")
        case _ : MissingCellException => println("You can't place an enemy on a missing cell.")
        case _ : DoubleEnemyException => println("You can't place an enemy on top of another enemy.")
      }
    }
  }

  setPaneChildren(_controller.list)
  root = playerPane


  def setMenu(): Unit = _pane.toBack()

  def updateHP(): Unit = {
    val ratio: Double = gameC.user.actualHealthPoint.toDouble / gameC.user.totalHealthPoint.toDouble
    if(ratio == 0)  gameC.setScene(this,GameOverScene(parentStage, gameC))
    observableHealthPoint._1.set(ratio)
    observableHealthPoint._2.set("Player: " + gameC.user.actualHealthPoint + "hp")
  }

  def updateParameters(): Unit = {
    updateHP()
    remainingEnemies.set("Enemies: " + _controller.getAllEnemies.size)
    observableGold.set("Gold: " +gameC.user.coins+ "x")
    observableLevel.set("Player level: " + gameC.user.level)
  }

  def showStatueAlert(money: Int): Unit = {
    Platform.runLater(() -> {
      val alert = new Alert(AlertType.CONFIRMATION)
      alert.setTitle("God statue")
      alert.setGraphic(new ImageView(new Image("statue.png")))
      alert.setHeaderText("Would you like to heal donating " + money + " golds?")

      val res = alert.showAndWait()

      if( res.isDefined && res.get == ButtonType.OK) {
        println("USER_Money: " + gameC.user.coins)
        if(gameC.user.coins >= money) {
          gameC.user.coins= gameC.user.coins - money
          gameC.user.actualHealthPoint = gameC.user.totalHealthPoint
          updateParameters()
        } else {
          println("You haven't got enough money!")
        }
      }
    })
  }

  def createBottomCard(): List[Button] = (0 to 4).toList.map(el =>{
    new Button {
      var re: Cell = _
      if(math.random() <= 0.8) re = RectangleCell.generateRandomCard()
      else re = new EnemyCell(gameC.spawnEnemy(Random.nextInt(4)))
      graphic = new ImageView(re.image) {
        fitWidth_=(100)
        fitHeight_=(100)
      }
      onAction = () => _controller.selected_(Option(re))
      defaultButton = true
      styleClass.add("bottomButton")
    }
  })

  private def setDifficulty: Option[Difficulty] = {
    new ChoiceDialog(Difficulty.Medium, List(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)) {
      title = "Select difficulty"
      headerText = "Select difficulty"
    }.showAndWait()
  }

  private def createSlider(sliderId: String): Slider = new Slider {
    min = 0
    max = 1
    value <==> MusicPlayer.observableVolume
    id = sliderId
  }

  def setPaneChildren(list :ListBuffer[RectangleWithCell]): Unit = _pane.children = list ++ list.filter(f =>f.rectCell.mapEvent.isDefined ).map(m =>m.rectCell.mapEvent.get ).map(el => {
    el.callEvent match {
      case e:Enemy => icon(el.playerRepresentation.position, el.playerRepresentation.url, 100, 90)
      case s:Statue => icon(el.playerRepresentation.position, el.playerRepresentation.url, 38, 110)
      case p:Pyramid => icon(el.playerRepresentation.position, el.playerRepresentation.url, 80, 110)
    }
  })

  def setBPane(): Unit = bottomPane.children = createBottomCard()

  def icon(rectangle: RectangleCell, url: String, elemWidth: Double = 60, elemHeight: Double = 80 ): Rectangle = new Rectangle() {
    x=rectangle.x+ rectangle.getWidth/2 - elemWidth/2
    y=rectangle.y+rectangle.getHeight/2 - elemHeight/2-30
    width = elemWidth
    height = elemHeight
    fill_=(new ImagePattern(new Image(url)))
  }

  def changeScene(user:User, enemy:Enemy): Unit = parentStage.scene_=(BattleScene(parentStage, user,enemy, gameC))

  def removeEnemyCell(): Unit = _controller.removeEnemyCell()
}

object MapScene {
  def apply(parentStage: Stage, gameC : GameController): MapScene = new MapScene(parentStage, new MapControllerImpl(gameC),gameC)
  def apply(parentStage: Stage, gameC : GameController, list:ListBuffer[RectangleWithCell],startingDefined : Option[RectangleCell], traslationX : Double, traslationY: Double): MapScene = new MapScene(parentStage, new MapControllerImpl(gameC, list, startingDefined,traslationX,traslationY),gameC,traslationX,traslationY)
}