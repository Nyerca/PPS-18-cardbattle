package controller

import model.{Move, Player, RectangleCell, Top, Bottom, Left, Right}
import scalafx.animation.{Interpolator, TranslateTransition}
import scalafx.util.Duration

class Dashboard(cells: List[RectangleCell], player: Player) {

  var anim = new TranslateTransition {
    duration = Duration(200.0)
    node = player.icon
    interpolator = Interpolator.Linear
    // autoReverse = true
    // cycleCount = Timeline.Indefinite
  }


  def searchPosition(newX : Double, newY : Double): Option[RectangleCell] = {
    //for(rectangle <- cells if(rectangle.getX == newX)) yield rectangle
    (for (rectangle <- cells if rectangle.getX == newX && rectangle.getY == newY) yield rectangle).headOption
  }



  def setAnimation(fromX: Double, toX: Double, fromY:Double, toY: Double)   = {
    anim.fromX = fromX;
    anim.fromY= fromY;
    anim.toX = toX;
    anim.toY= toY;
  }

  def setAnimationIncrement(newRectangle:RectangleCell, incrementX : Double, incrementY: Double, stringUrl : String): Unit = {
    anim.toX = anim.fromX.toDouble + incrementX;
    anim.toY = anim.fromY.toDouble + incrementY;
    anim.setOnFinished(e =>  {
      player.position_(newRectangle, stringUrl + "2.png");
      anim.fromX = anim.toX.toDouble
      anim.fromY = anim.toY.toDouble
      anim.toX = anim.fromX.toDouble + incrementX;
      anim.toY = anim.fromY.toDouble + incrementY;
      anim.setOnFinished(e => {          player.position_(newRectangle, stringUrl + ".png");
        anim.fromX = anim.toX.toDouble
        anim.fromY = anim.toY.toDouble
        anim.toX = anim.fromX.toDouble + incrementX;
        anim.toY = anim.fromY.toDouble + incrementY;
        anim.setOnFinished(e => {          player.position_(newRectangle, stringUrl + "1.png");
          anim.fromX = anim.toX.toDouble
          anim.fromY = anim.toY.toDouble
          anim.toX = anim.fromX.toDouble + incrementX;
          anim.toY = anim.fromY.toDouble + incrementY;
          anim.setOnFinished(e => {  player.position_(newRectangle, stringUrl + ".png");
            anim.fromX = anim.toX.toDouble
            anim.fromY = anim.toY.toDouble
            anim.toX = anim.fromX.toDouble + incrementX;
            anim.toY = anim.fromY.toDouble + incrementY;
            anim.setOnFinished(e => {})
            anim.play();})
          anim.play();})
        anim.play();})
      anim.play();
    })
  }


  def move(movement : Move): Unit = movement match {
    case Top  => {
      setAnimation(player.position.getX, player.position.getX, player.position.getY, player.position.getY-200)

      val newRectangle = this.searchPosition( anim.toX.toDouble, anim.toY.toDouble)
      if(newRectangle.isDefined) {
        player.image_("top.png")
        println("From: (" + player.position.getX + ", " + player.position.getY + ") to: (" + anim.toX.toDouble + ", " + anim.toY.toDouble + ")")
        setAnimationIncrement(newRectangle.get, 0, -40, "top")
        anim.play();
      }
    }
    case Right => {
      setAnimation(player.position.getX, player.position.getX + 200, player.position.getY, player.position.getY)

      val newRectangle = this.searchPosition( anim.toX.toDouble, anim.toY.toDouble)
      if(newRectangle.isDefined) {
        player.image_("right.png")
        println("From: (" + player.position.getX + ", " + player.position.getY + ") to: (" + anim.toX.toDouble + ", " + anim.toY.toDouble + ")")
        setAnimationIncrement(newRectangle.get, 40, 0, "right")
        anim.play();
      }
    }
    case Bottom => {
      setAnimation(player.position.getX, player.position.getX, player.position.getY, player.position.getY + 200)
      player.image_("bot.png")
      val newRectangle = this.searchPosition( anim.toX.toDouble, anim.toY.toDouble)
      if(newRectangle.isDefined) {
        println("From: (" + player.position.getX + ", " + player.position.getY + ") to: (" + anim.toX.toDouble + ", " + anim.toY.toDouble + ")")
        setAnimationIncrement(newRectangle.get, 0, 40, "bot")
        anim.play();
      }

    }
    case Left => {
      setAnimation(player.position.getX, player.position.getX - 200, player.position.getY, player.position.getY)

      val newRectangle = this.searchPosition( anim.toX.toDouble, anim.toY.toDouble)
      if(newRectangle.isDefined) {
        player.image_("left.png")
        println("From: (" + player.position.getX + ", " + player.position.getY + ") to: (" + anim.toX.toDouble + ", " + anim.toY.toDouble + ")")
        setAnimationIncrement(newRectangle.get, -40, 0, "left")
        anim.play();
      }
    }
    case _  => {}
  }

}
