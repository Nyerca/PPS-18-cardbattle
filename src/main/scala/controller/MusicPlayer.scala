package controller

import controller.SoundType._
import javafx.beans.property.SimpleDoubleProperty
import scalafx.scene.media.{Media, MediaPlayer}

trait SoundType

object SoundType {
  case object MainMenuSound extends SoundType
  case object MapSound extends SoundType
  case object BattleSound extends SoundType
  case object WinningSound extends SoundType
  case object LoseSound extends SoundType
}

object MusicPlayer {
  var mediaPlayer :MediaPlayer = _
  var observableVolume = new SimpleDoubleProperty(0.5)
  observableVolume.addListener(_ => mediaPlayer.volume = observableVolume.get)

  def play(soundType:SoundType): Unit = {
    if (mediaPlayer != null) mediaPlayer.pause()
    soundType match {
      case MainMenuSound => mediaPlayer = new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/mainMusic.mp3").toString))
      case MapSound => mediaPlayer = new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Theme1.m4a").toString))
      case BattleSound => mediaPlayer = new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Battle1.m4a").toString))
      case WinningSound => mediaPlayer = new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Theme1.m4a").toString))
      case LoseSound => mediaPlayer = new MediaPlayer(new Media(getClass.getClassLoader.getResource("music/Theme1.m4a").toString))
    }
    mediaPlayer.volume = observableVolume.get
    mediaPlayer.cycleCount = MediaPlayer.Indefinite
    mediaPlayer.play()
  }
}
