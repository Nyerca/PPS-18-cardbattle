package controller

import controller.SoundType._
import javafx.beans.property.SimpleDoubleProperty
import scalafx.scene.media.MediaPlayer.Status
import scalafx.scene.media.{Media, MediaPlayer}
import scalafx.Includes._

trait SoundType

object SoundType {
  case object MainMenuSound extends SoundType
  case object MapSound extends SoundType
  case object BattleSound extends SoundType
  case object WinningSound extends SoundType
  case object LoseSound extends SoundType
}

object MusicPlayer {
  var mediaPlayer: Option[MediaPlayer] = None
  var observableVolume = new SimpleDoubleProperty(0.5)
  observableVolume.addListener(_ => mediaPlayer.get.volume = observableVolume.get)

  def play(soundType:SoundType): Unit = {
    checkExistence(mediaPlayer)
    soundType match {
      case MainMenuSound => mediaPlayer = Some(new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/mainMusic.mp3").toString)))
      case MapSound => mediaPlayer = Some(new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Theme1.m4a").toString)))
      case BattleSound => mediaPlayer = Some(new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Battle1.m4a").toString)))
      case WinningSound => mediaPlayer = Some(new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Theme1.m4a").toString)))
      case LoseSound => mediaPlayer = Some(new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Theme1.m4a").toString)))
    }
    mediaPlayer.get.volume = observableVolume.get
    mediaPlayer.get.cycleCount = MediaPlayer.Indefinite
    mediaPlayer.get.play()
  }

  private def checkExistence(mediaPlayer: Option[MediaPlayer]): Unit = mediaPlayer match {
    case Some(mp) => checkMediaStatus(mp.status.value)
    case _ => ;
  }

  private def checkMediaStatus(status: MediaPlayer.Status): Unit = status match {
    case Status.Playing => mediaPlayer.get.pause()
    case _ => ;
  }
}
