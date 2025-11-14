/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.hudManager.floatText.thoughtBubble;

import necesse.engine.Settings;
import necesse.entity.mobs.Mob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.hudManager.floatText.thoughtBubble.TextureThoughtBubble;

public class AngerThoughtBubble
extends TextureThoughtBubble {
    public AngerThoughtBubble(Mob mob, int stayTime) {
        super(mob, stayTime);
    }

    @Override
    public GameTexture getThoughtContentTexture() {
        return Settings.UI.settler_thought_anger;
    }
}

