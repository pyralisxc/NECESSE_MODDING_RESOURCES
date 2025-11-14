/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.hudManager.floatText.thoughtBubble;

import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.hudManager.floatText.thoughtBubble.ThoughtBubble;

public class MobThoughtBubble
extends ThoughtBubble {
    public int mobID;

    public MobThoughtBubble(Mob mob, int stayTime, int mobID) {
        super(mob, stayTime);
        this.mobID = mobID;
    }

    @Override
    public DrawOptions getThoughtContent(int drawX, int drawY, int size, float fadeInProgress, PlayerMob perspective) {
        GameTexture mobIcon = MobRegistry.getMobIcon(this.mobID);
        if (mobIcon == null) {
            return null;
        }
        return mobIcon.initDraw().size(size).pos(drawX, drawY);
    }
}

