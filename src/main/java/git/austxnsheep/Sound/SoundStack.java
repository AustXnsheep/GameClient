package git.austxnsheep.Sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

public class SoundStack {
    private Sound sound;
    private long soundId;
    private Vector2 soundPosition;
    private Vector2 listenerPosition;
    private float maxHearingDistance;

    public SoundStack(Sound sound, Vector2 initialSoundPosition, Vector2 listenerPosition, float maxHearingDistance) {
        this.sound = sound;
        this.soundPosition = initialSoundPosition;
        this.listenerPosition = listenerPosition;
        this.maxHearingDistance = maxHearingDistance;
        this.soundId = sound.play(0.0f);
        updateSound();
    }

    public void updateSound() {
        float distance = soundPosition.dst(listenerPosition);
        float volume = Math.max(0, 1 - distance / maxHearingDistance);
        float pan = (soundPosition.x - listenerPosition.x) / Gdx.graphics.getWidth();
        pan = Math.max(-1, Math.min(1, pan));

        sound.setVolume(soundId, volume);
        sound.setPan(soundId, pan, volume);
    }

    public void setSoundPosition(Vector2 soundPosition) {
        this.soundPosition = soundPosition;
        updateSound();
    }

    public void setListenerPosition(Vector2 listenerPosition) {
        this.listenerPosition = listenerPosition;
        updateSound();
    }

    public void dispose() {
        sound.stop(soundId);
        sound.dispose();
    }
}
