package controller

import controller.SoundType._
import javafx.beans.property.SimpleDoubleProperty
import scalafx.scene.media.MediaPlayer.Status
import scalafx.scene.media.{Media, MediaPlayer}
import scalafx.Includes._

trait SoundType

object SoundType {
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
    mediaPlayer = setMedia(soundType)
    mediaPlayer.get.volume = observableVolume.get
    mediaPlayer.get.cycleCount = MediaPlayer.Indefinite
    mediaPlayer.get.play()
  }

  private def setMedia(soundType: SoundType): Option[MediaPlayer] = soundType match {
    case MapSound => Some(new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Dungeon1.m4a").toString)))
    case BattleSound => Some(new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Battle1.m4a").toString)))
    case WinningSound => Some(new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Winning.m4a").toString)))
    case LoseSound => Some(new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Losing.m4a").toString)))
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
