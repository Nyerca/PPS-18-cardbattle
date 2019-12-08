package controller

import scalafx.scene.media.{Media, MediaPlayer}

trait SoundType

object SoundType {
  case object MainMenuSound extends SoundType
  case object MapSound extends SoundType
  case object WinningSound extends SoundType
  case object LoseSound extends SoundType
}
object MusicPlayer {


  private var mediaPlayer: MediaPlayer = _

  def play(soundType: SoundType): Unit = {
    soundType match {
      case SoundType.MainMenuSound => mediaPlayer = new MediaPlayer(new Media(""))
      case SoundType.LoseSound => mediaPlayer = new MediaPlayer(new Media(""))
      case SoundType.WinningSound => mediaPlayer = new MediaPlayer(new Media(""))
      case _ => mediaPlayer = new MediaPlayer(new Media(""))
    }
    mediaPlayer.play()
  }

  def pause: Unit = mediaPlayer.pause()

  def resume: Unit = mediaPlayer.play()

  def volumeUp: Unit = mediaPlayer.volume += 1

  def volumeDown: Unit = mediaPlayer.volume -= 1

}
