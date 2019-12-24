package view.scenes

import utility.GUIObjectFactory
import controller._
import exception._
import javafx.beans.property.{SimpleDoubleProperty, SimpleStringProperty}
import javafx.scene.control.Alert.AlertType
import javafx.scene.input.MouseEvent
import javafx.scene.paint.ImagePattern
import model._
import scalafx.Includes._
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout._
import scalafx.scene.shape.Rectangle
import scalafx.stage.Stage
import scala.util.Random
import model.Cell
import scalafx.application.Platform

trait MapScene extends BaseScene with ObserverScene {
  def mapWindow: BorderPane
  def setMenu(): Unit
}
object MapScene {

  class MapSceneImpl (override val parentStage: Stage, val _controller : MapController, var gameC :GameController, translationX : Double = 0, translationY: Double = 0) extends MapScene {

    MusicPlayer.play(SoundType.MapSound)

    stylesheets.add("mapStyle.css")

    override def update[A](model: A): Unit = model match {
      case player:User =>
        observableGold.set("Gold: " +player.coins+ "x")
        removeEnemyCell()
        observableLevel.set("Player level: " + player.level)
        val ratio: Double = player.actualHealthPoint.toDouble / player.totalHealthPoint.toDouble
        if(ratio == 0)  gameC.setScene(this,GameOverScene(parentStage, gameC))
        observableHealthPoint._1.set(ratio)
        observableHealthPoint._2.set(player.name + ":" + player.actualHealthPoint + "hp")
      case enemy:Enemy => changeScene(gameC.user, enemy)
      case _:Pyramid => _controller.reset()
      case statue:Statue => showStatueAlert(statue.moneyRequired)
      case chest: Chest => showChestAlert(chest.money)
      case playerRep: PlayerRepresentation => playerImg_(playerRep)
      case (cellList: List[RectangleCell], remainingEnem : Int) => setPaneChildren(cellList); remainingEnemies.set("Enemies: " + remainingEnem); setBPane()

    }

    private val field: Pane = new Pane {maxHeight = 800; translateX=translationX; translateY=translationY}

    private val observableHealthPoint = (new SimpleDoubleProperty(gameC.user.actualHealthPoint.toDouble / gameC.user.totalHealthPoint.toDouble), new SimpleStringProperty(gameC.user.name + ":" + gameC.user.actualHealthPoint + "hp"))

    private val remainingEnemies = new SimpleStringProperty("Enemies: " + _controller.getAllEnemies)

    private val observableGold = new SimpleStringProperty("Gold: " +gameC.user.coins+ "x")

    private val observableLevel = new SimpleStringProperty("Player level: " + gameC.user.level)

    lazy val backToMainMenu: Unit = gameC.setScene(this, MainScene(parentStage))

    private val menu: VBox = new VBox {
      children = GUIObjectFactory.toolbarFactory(List(
        (new Button("Cards"){
          onAction = () => parentStage.scene_=(EquipmentScene(parentStage, gameC))
          layoutX = 110}, true),
        (new Button("Save"){onAction = () => _controller.handleSave()}, true),
        (new Button("Option"){onAction = () => gameC.difficulty = setDifficulty.getOrElse(gameC.difficulty)}, true),
        (new Button("Quit"){onAction = () => backToMainMenu}, true),
        (new StackPane {
          children = List(new ProgressBar {
            progress <== observableHealthPoint._1
            styleClass.add("life")
          }, new Label {
            styleClass.add("title")
            text <== observableHealthPoint._2
          })
        }, true),
        (new Label{text <== observableLevel}, true),
        (new Label{text <== observableGold}, false),
        (new ImageView(new Image("coin.png")), true),
        (new Label{text <== remainingEnemies}, true),
        (createSlider("volumeSlider"), false)
      ))
      minWidth = 1200
    }

    private val placeableCards: HBox = new HBox() {
      id ="bottomRngPane"
      translateX = 300
      translateY = 630
      maxWidth = 500
      maxHeight = 120
      children = createBottomCard()
    }

    val _mapWindow: BorderPane = new BorderPane {
      top = menu
      center = field
      bottom = placeableCards
    }
    override def mapWindow: BorderPane = _mapWindow

    _controller.view_(this)

    private var playerImg: Rectangle = icon(_controller.dashboard.cells.head, _controller.dashboard.player.url)
    def playerImg_(player: PlayerRepresentation):Unit = {playerImg.fill_=(new ImagePattern(new Image(player.url)))}

    private val playerPane: Pane = new Pane {
      children.append(_mapWindow)
      children.append(playerImg)
      children.append(PlayerAnimation.setup(_controller.dashboard.cells.head))
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

    setPaneChildren(_controller.dashboard.cells)
    root = playerPane


    override def setMenu(): Unit = field.toBack()

    def showStatueAlert(money: Int): Unit = {
      Platform.runLater(() -> {
        val alert = GUIObjectFactory.alertFactory(AlertType.CONFIRMATION, parentStage, "God statue", "Would you like to heal donating " + money + " golds?")
        alert.setGraphic(new ImageView(new Image("statue.png")))

        val res = alert.showAndWait()

        if( res.isDefined && res.get == ButtonType.OK) {
          if(gameC.user.coins >= money) {
            _controller.setGold(-money)
            PlayerAnimation.play(PlayerAnimation.HEAL_PREFIX)
          } else {
            println("You haven't got enough money!")
          }
        }
      })
    }

    def showChestAlert(money: Int): Unit = {
      Platform.runLater(() -> {
        val alert = GUIObjectFactory.alertFactory(AlertType.INFORMATION, parentStage, "Chest", "You obtained: " + money + " golds")
        alert.setGraphic(new ImageView(new Image("chest.png")))
        _controller.setGold(money)
        alert.showAndWait()
      })
    }

    def createBottomCard(): List[Button] = (0 to 4).toList.map(_ =>{
      new Button {
        var re: Cell = _
        if(math.random() <= 0.8) re = RectangleCell.generateRandomCard()
        else re = EnemyCell(gameC.spawnEnemy(Random.nextInt(4)))
        graphic = new ImageView(re.image) {fitWidth_=(100); fitHeight_=(100)}
        onAction = () => _controller.setDashboardSelected(Option(re))
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

    def setPaneChildren(list :List[RectangleCell]): Unit = {
      field.children = list
      list.filter(f =>f.mapEvent.isDefined ).map(m =>m.mapEvent.get ).foreach(el => {
        el.cellEvent match {
          case _:Enemy => field.children.append(icon(el.playerRepresentation.position, el.playerRepresentation.url, 100, 90))
          case _:Statue => field.children.append(icon(el.playerRepresentation.position, el.playerRepresentation.url, 38, 110))
          case _:Pyramid => field.children.append(icon(el.playerRepresentation.position, el.playerRepresentation.url, 80, 110))
          case _:Chest => field.children.append(icon(el.playerRepresentation.position, el.playerRepresentation.url, 50, 50))
        }
      })
    }

    def setBPane(): Unit = placeableCards.children = createBottomCard()

    def icon(rectangle: RectangleCell, url: String, elemWidth: Double = 60, elemHeight: Double = 80 ): Rectangle = new Rectangle() {
      x=rectangle.x+ rectangle.elementWidth/2 - elemWidth/2
      y=rectangle.y+rectangle.elementHeight/2 - elemHeight/2-30
      width = elemWidth
      height = elemHeight
      fill_=(new ImagePattern(new Image(url)))
    }

    def changeScene(user:User, enemy:Enemy): Unit = gameC.setScene(this, BattleScene(parentStage, enemy, gameC))

    def removeEnemyCell(): Unit = _controller.dashboard.remove[Enemy]()
  }

  def apply(parentStage: Stage, gameC : GameController): MapScene = new MapSceneImpl(parentStage, new MapControllerImpl(gameC),gameC)
  def apply(parentStage: Stage, gameC : GameController, list:List[RectangleCell], startingDefined : Option[RectangleCell], translationX : Double, translationY: Double): MapScene = new MapSceneImpl(parentStage, new MapControllerImpl(gameC, list, startingDefined,translationX,translationY),gameC,translationX,translationY)
}