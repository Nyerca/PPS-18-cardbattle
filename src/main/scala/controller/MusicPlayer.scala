package controller

import controller.SoundType._
import javafx.beans.property.SimpleDoubleProperty
import scalafx.scene.media.MediaPlayer.Status
import scalafx.scene.media.{Media, MediaPlayer}

trait SoundType

object SoundType {
  case object MapSound extends SoundType
  case object BattleSound extends SoundType
  case object WinningSound extends SoundType
  case object LoseSound extends SoundType
}

object MusicPlayer {

  private var mediaPlayer: Option[MediaPlayer] = None

  val observableVolume = new SimpleDoubleProperty(0.3)

  observableVolume.addListener(_ => mediaPlayer.get.volume = observableVolume.get)

  def play(soundType:SoundType): Unit = {
    changeStatus(Status.Paused)
    mediaPlayer = setMedia(soundType)
    mediaPlayer.get.volume = observableVolume.get
    mediaPlayer.get.cycleCount = MediaPlayer.Indefinite
    mediaPlayer.get.play()
  }

  def changeStatus(status: Status): Unit = turnToStatusIfExists(mediaPlayer, status)


  private def setMedia(soundType: SoundType): Option[MediaPlayer] = soundType match {
    case MapSound => Some(new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Dungeon1.m4a").toString)))
    case BattleSound => Some(new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Battle1.m4a").toString)))
    case WinningSound => Some(new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Winning.m4a").toString)))
    case LoseSound => Some(new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Losing.m4a").toString)))
  }

  private def turnToStatusIfExists(mediaPlayer: Option[MediaPlayer], status: Status): Unit = mediaPlayer match {
    case Some(mp) => setStatus(mp, status)
    case _ => ;
  }

  private def setStatus(mediaPlayer: MediaPlayer, status: Status): Unit = status match {
    case Status.Paused => mediaPlayer.pause()
    case Status.Ready => mediaPlayer.play()
    case _ => ;
  }

}
