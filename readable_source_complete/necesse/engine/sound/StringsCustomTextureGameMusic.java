/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import java.awt.Color;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.sound.GameMusic;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;

public class StringsCustomTextureGameMusic
extends GameMusic {
    public StringsCustomTextureGameMusic(String filePath, GameMessage trackName, Color color1, Color color2, int fadeInMillis, int fadeOutMillis, GameMessage optionalTooltip) {
        super(filePath, trackName, color1, color2, fadeInMillis, fadeOutMillis, optionalTooltip);
    }

    public StringsCustomTextureGameMusic(String filePath, GameMessage trackName, Color color1, Color color2, GameMessage optionalTooltip) {
        super(filePath, trackName, color1, color2, optionalTooltip);
    }

    @Override
    public GameTexture loadVinylTexture() {
        GameTexture base = new GameTexture(GameTexture.fromFile("items/stringsvinyl", true));
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
}

