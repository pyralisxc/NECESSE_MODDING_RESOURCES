/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import java.awt.Color;
import necesse.engine.PlayingMusicManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.IDData;
import necesse.engine.sound.gameSound.GameSound;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;

public class GameMusic {
    public final IDData idData = new IDData();
    public final String filePath;
    public GameMessage optionalTooltip;
    public GameMessage trackName;
    public float volumeModifier = 1.0f;
    public GameSound sound;
    public Color color1;
    public Color color2;
    public int fadeInMillis;
    public int fadeOutMillis;

    public final int getID() {
        return this.idData.getID();
    }

    public String getStringID() {
        return this.idData.getStringID();
    }

    public GameMusic(String filePath, GameMessage trackName, Color color1, Color color2, int fadeInMillis, int fadeOutMillis, GameMessage optionalTooltip) {
        this.filePath = filePath;
        this.trackName = trackName;
        this.color1 = color1;
        this.color2 = color2;
        this.fadeInMillis = fadeInMillis;
        this.fadeOutMillis = fadeOutMillis;
        this.optionalTooltip = optionalTooltip;
    }

    public GameMusic(String filePath, GameMessage trackName, Color color1, Color color2, GameMessage optionalTooltip) {
        this(filePath, trackName, color1, color2, PlayingMusicManager.DEFAULT_FADE_IN_TIME, PlayingMusicManager.DEFAULT_FADE_OUT_TIME, optionalTooltip);
    }

    public GameTexture loadVinylTexture() {
        GameTexture base = new GameTexture(GameTexture.fromFile("items/vinylbase", true));
        GameTexture color1 = new GameTexture(GameTexture.fromFile("items/vinylcolor1", true));
        GameTexture color2 = new GameTexture(GameTexture.fromFile("items/vinylcolor2", true));
        color1.applyColor(this.color1, MergeFunction.GLBLEND);
        color2.applyColor(this.color2, MergeFunction.GLBLEND);
        base.merge(color1, 0, 0, MergeFunction.NORMAL);
        base.merge(color2, 0, 0, MergeFunction.NORMAL);
        base.makeFinal();
        color1.makeFinal();
        color2.makeFinal();
        return base;
    }

    public void loadSound() {
        this.sound = GameSound.fromFileMusic(this.filePath);
        if (this.sound != null) {
            this.sound.setVolumeModifier(this.volumeModifier);
        }
    }

    public GameMusic setVolumeModifier(float volumeModifier) {
        this.volumeModifier = volumeModifier;
        if (this.sound != null) {
            this.sound.setVolumeModifier(volumeModifier);
        }
        return this;
    }
}

