package controller

import controller.SoundType._
import scalafx.scene.control.Slider
import scalafx.scene.media.{Media, MediaPlayer}

trait SoundType {

}

object SoundType {
  case object MainMenuSound extends SoundType
  case object MapSound extends SoundType
  case object BattleSound extends SoundType
  case object WinningSound extends SoundType
  case object LoseSound extends SoundType
}

object MusicPlayer {
  var mediaPlayer :MediaPlayer = _

  def play(soundType:SoundType) = {
    soundType match {
      case MainMenuSound =>mediaPlayer = new MediaPlayer(new Media(getClass.getClassLoader.getResource("Theme1.m4a").toString)) {volume = if(mediaPlayer==null) 0.5 else mediaPlayer.volume.toDouble}
      case MapSound =>mediaPlayer = new MediaPlayer(new Media(getClass.getClassLoader.getResource("Theme1.m4a").toString)) {volume = if(mediaPlayer==null) 0.5 else mediaPlayer.volume.toDouble}
      case BattleSound =>mediaPlayer = new MediaPlayer(new Media(getClass.getClassLoader.getResource("Battle1.m4a").toString)) {volume = if(mediaPlayer==null) 0.5 else mediaPlayer.volume.toDouble}
      case WinningSound =>mediaPlayer = new MediaPlayer(new Media(getClass.getClassLoader.getResource("Theme1.m4a").toString))
      case LoseSound => mediaPlayer = new MediaPlayer(new Media(getClass.getClassLoader.getResource("Theme1.m4a").toString))
    }
    mediaPlayer.cycleCount = MediaPlayer.Indefinite
    mediaPlayer.play()
  }


}
