/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.hudManager.floatText.thoughtBubble;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.hudManager.floatText.thoughtBubble.ThoughtBubble;

public abstract class TextureThoughtBubble
extends ThoughtBubble {
    public TextureThoughtBubble(Mob mob, int stayTime) {
        super(mob, stayTime);
    }

    public abstract GameTexture getThoughtContentTexture();

    @Override
    public int getThoughtContentSize() {
        GameTexture texture = this.getThoughtContentTexture();
        return Math.max(texture.getWidth(), texture.getHeight());
    }

    @Override
    public DrawOptions getThoughtContent(int drawX, int drawY, int size, float fadeInProgress, PlayerMob perspective) {
        return this.getThoughtContentTexture().initDraw().size(size).pos(drawX, drawY);
    }
}

