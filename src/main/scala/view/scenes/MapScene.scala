package view.scenes

import AudioPlayer1.Launcher.{getClass, stage}
import controller.MusicPlayer.mediaPlayer
import controller._
import exception._
import javafx.beans.property.{SimpleDoubleProperty, SimpleStringProperty}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.Alert.AlertType
import javafx.scene.input.MouseEvent
import javafx.scene.paint.ImagePattern
import model._
import scalafx.Includes._
import scalafx.scene.{Node, Parent, Scene}
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout._
import scalafx.scene.shape.Rectangle
import scalafx.stage.Stage

import scala.collection.mutable.ListBuffer
import scala.util.Random
import model.Cell
import scalafx.animation.{Interpolator, TranslateTransition}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.Platform
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.media.{Media, MediaPlayer}
import scalafx.scene.text.Text
import scalafx.util.Duration

class MapScene (override val parentStage: Stage, var _controller : MapController, var gameC :GameController,traslationX : Double = 0, traslationY: Double = 0) extends BaseScene{
  stylesheets.add("mapStyle.css")



  val _pane = new Pane {
    maxHeight = 800
  }


  def showStatueAlert(money: Int): Unit = {
    Platform.runLater(() -> {
      val alert = new Alert(AlertType.CONFIRMATION)
      alert.setTitle("God statue")
      alert.setGraphic(new ImageView(new Image("statue.png")))
      alert.setHeaderText("Would you like to heal donating " + money + " golds?")
      alert.setContentText("ITEM")


      //alert.setOnShown(() => {println("caciao")})
      //alert.show()
      val res = alert.showAndWait();
      // alert is exited, no button has been pressed.
      if( res.isDefined && res.get == ButtonType.OK) {
        println("USER_Money: " + gameC.user.coins)
        if(gameC.user.coins >= money) {
          gameC.user.coins= gameC.user.coins - money
          gameC.user.actualHealthPoint = gameC.user.totalHealthPoint
          updateHP();
        } else {
          println("You haven't got enough money!")
        }
      }
    });


  }

  def addToToolbar(toolbar: ToolBar, btn: Node, isLast:Boolean): Unit = {
    toolbar.getItems.add(btn)
    if(!isLast) toolbar.getItems.add(new Separator())
  }

  private val observableHealthPoint = (new SimpleDoubleProperty(gameC.user.actualHealthPoint.toDouble / gameC.user.totalHealthPoint.toDouble), new SimpleStringProperty("Player: " + gameC.user.actualHealthPoint + "hp"))

  def updateHP(): Unit = {
    val ratio: Double = gameC.user.actualHealthPoint.toDouble / gameC.user.totalHealthPoint.toDouble
    println("LIFE IS: " + gameC.user.actualHealthPoint)
    observableHealthPoint._1.set(if ( ratio > 0 ) ratio else 0)
    observableHealthPoint._2.set(if ( ratio > 0 ) "Player: " + gameC.user.actualHealthPoint + "hp" else "Player: 0hp")
  }

  def updateParameters(): Unit = {
    updateHP();
    println("NEW SIZE: " + _controller.getAllEnemies().size)
    remainingEnemies.set("Enemies: " + _controller.getAllEnemies().size)
    observableGold.set("Gold: " +gameC.user.coins+ "x")
    observableLevel.set("Level: " + gameC.user.level)
  }

  var life = new StackPane {
    children = List(new ProgressBar {
      progress <== observableHealthPoint._1
      styleClass.add("life")
    }, new Label {
      styleClass.add("title")
      text <== observableHealthPoint._2
    })
  }

  private val remainingEnemies = new SimpleStringProperty("Enemies: " + _controller.getAllEnemies().size)
  var enemies = new Label{text <== remainingEnemies}

  private val observableGold = new SimpleStringProperty("Gold: " +gameC.user.coins+ "x")
  var gold = new Label{text <== observableGold}

  private val observableLevel = new SimpleStringProperty("Level: " + gameC.user.level)
  var level = new Label{text <== observableLevel}

  private def setDifficulty: Difficulty = {
    new ChoiceDialog(Difficulty.Medium, List(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)) {
      title = "Select difficulty"
      headerText = "Select difficulty"
    }.showAndWait().get
  }

  private def createSlider(sliderId: String): Slider = new Slider {
    min = 0
    max = 1
    value = 0
    id = sliderId
  }

  var volumeSlider: Slider = createSlider("volumeSlider")

  private def addListenersAndBindings(mp: MediaPlayer): Unit = {
    volumeSlider.value <==> mp.volume
  }
  addListenersAndBindings(MusicPlayer.mediaPlayer)

  var menu: VBox = new VBox {
    val toolbar = new ToolBar()
    addToToolbar(toolbar, new Button("Cards"){
      onAction = () => parentStage.scene_=(EquipmentScene(parentStage, gameC))
      layoutX = 110
    }, false)
    addToToolbar(toolbar, new Button("Save"){onAction = () => _controller.handleSave()}, false)
    addToToolbar(toolbar, new Button("Option"){onAction = () =>
gameC.difficulty = setDifficulty
    }, false)
    addToToolbar(toolbar, new Button("Quit"){
      onAction = () => {
        System.exit(0)
      }
    }, false)


    addToToolbar(toolbar, life, false)
    addToToolbar(toolbar, level, false)

    addToToolbar(toolbar, gold, true)
    addToToolbar(toolbar, new ImageView(new Image("coin.png")), false)
    addToToolbar(toolbar, enemies, false)
    addToToolbar(toolbar, volumeSlider, true)

    children = toolbar
    minWidth = 1200
  }



  def setMenu(): Unit = {

    _pane.toBack()
  }


  def createBottomCard(): ListBuffer[Button] = {
    val tmpList = ListBuffer[Button]()
    val btn: Button = new Button {
      val re = new RectangleCellImpl(true, true, true, true, _x= 0.0, elementY=0.0)
      onAction = () => _controller.selected_(Option(re))
      defaultButton = true
      graphic = new ImageView(RectangleCell.createImage(re.url, re.rotation).getImage) {
        fitWidth_=(100)
        fitHeight_=(100)
      }
    }
    btn.getStyleClass().add("bottomButton");
    tmpList.append(btn)

    for(i<-0 until 4) {
      val btn_tmp: Button = new Button {
        var re: Cell = _
        if(math.random() <= 0.8) {
          re = RectangleCell.generateRandomCard()
        } else {
          re = new EnemyCell(gameC.spawnEnemy(Random.nextInt(4)))
        }
        graphic = new ImageView(re.image) {
          fitWidth_=(100)
          fitHeight_=(100)
        }
        onAction = () => _controller.selected_(Option(re))
        defaultButton = true

      }

      btn_tmp.getStyleClass().add("bottomButton");
      tmpList.append(btn_tmp)
    }
    tmpList
  }

  val bottomPane: HBox = new HBox() {
    id ="bottomRngPane"
    translateX = 300
    translateY = 630
    maxWidth = 500
    maxHeight = 120
    children = createBottomCard()
  }


  var _bpane: BorderPane = new BorderPane {
    top = menu
    center = _pane
    bottom = bottomPane
  }

  //_pane.layoutX_=(-200)

  def bpane: BorderPane = _bpane
  _controller.view_(this)

  def setPaneChildren(list :ListBuffer[RectangleWithCell], tmpRect : Option[RectangleCell]): Unit = {
    val listTmp = new ListBuffer[Node]()
    for (el <- list) yield {
      listTmp.append(el)
      /*
      if(el.rectCell.enemy._2.isDefined) {
        val tmp = el.rectCell.enemy._2.get;

        val cell = new PlayerRepresentation(tmp.position, tmp.url)
        listTmp.append(icon(cell, 100, 90))
      }
      */
      if(el.rectCell.mapEvent.isDefined) {
        if(el.rectCell.mapEvent.get.callEvent.isInstanceOf[Enemy]) listTmp.append(icon(el.rectCell.mapEvent.get.playerRepresentation, 100, 90))
        if(el.rectCell.mapEvent.get.callEvent.isInstanceOf[Statue]) listTmp.append(icon(el.rectCell.mapEvent.get.playerRepresentation, 38, 110))
        if(el.rectCell.mapEvent.get.callEvent.isInstanceOf[Pyramid]) listTmp.append(icon(el.rectCell.mapEvent.get.playerRepresentation, 80, 110))
      }
    }
    if(tmpRect.isDefined) listTmp.append(new RectangleWithCell(tmpRect.get.getWidth, tmpRect.get.getHeight, tmpRect.get.x, tmpRect.get.getY,tmpRect.get) {
      fill = RectangleCell.createImage(tmpRect.get.url, tmpRect.get.rotation)
    })

    _pane.children =listTmp
  }


  def setBPane(): Unit = {
    var addList = createBottomCard()
    bottomPane.children = addList
  }


  def icon(player: PlayerRepresentation, elemWidth: Double = 60, elemHeight: Double = 80 ): Rectangle = {
    new Rectangle() {
      x=player.position.x+player.position.getWidth/2 - elemWidth/2
      y=(player.position.getY+player.position.getHeight/2)-(elemHeight-10)
      width = elemWidth
      height = elemHeight
      fill_=(new ImagePattern(new Image(player.url)))
    }
  }

  var playerImg = new Rectangle() {
    x=_controller.list.head.rectCell.x+_controller.list.head.rectCell.getWidth/2 - 60/2
    y=(_controller.list.head.rectCell.getY+_controller.list.head.rectCell.getHeight/2)-(80-10)
    width = 60
    height = 80
    fill_=(new ImagePattern(new Image(_controller.player.url)))
  }
  def playerImg_(player: PlayerRepresentation):Unit = {playerImg.fill_=(new ImagePattern(new Image(player.url)))}

  val playerPane = new Pane {
    children = _controller.list
    children.append(_bpane)
    children.append(playerImg)

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
  _pane.translateX=traslationX
  _pane.translateY=traslationY

  setPaneChildren(_controller.list, Option.empty)
  root = playerPane



  def changeScene(user:User, enemy:Enemy): Unit = {
    parentStage.scene_=(BattleScene(parentStage, user,enemy, gameC))
  }

  def removeEnemyCell(): Unit = {
    _controller.removeEnemyCell();
  }

}

object MapScene {
  def apply(parentStage: Stage, gameC : GameController): MapScene = new MapScene(parentStage, new MapControllerImpl(gameC),gameC)
  def apply(parentStage: Stage, gameC : GameController, list:ListBuffer[RectangleWithCell],startingDefined : Option[RectangleCell], traslationX : Double, traslationY: Double): MapScene = new MapScene(parentStage, new MapControllerImpl(gameC, list, startingDefined,traslationX,traslationY),gameC,traslationX,traslationY)
}
