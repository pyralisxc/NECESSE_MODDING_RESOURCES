/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.hudManager.floatText.thoughtBubble;

import java.util.Objects;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.level.maps.hudManager.floatText.thoughtBubble.ThoughtBubble;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class OtherSettlerThoughtBubble
extends ThoughtBubble {
    public HumanMob targetSettler;

    public OtherSettlerThoughtBubble(Mob mob, int stayTime, HumanMob targetSettler) {
        super(mob, stayTime);
        Objects.requireNonNull(targetSettler);
        this.targetSettler = targetSettler;
    }

    @Override
    public DrawOptions getThoughtContent(int drawX, int drawY, int size, float fadeInProgress, PlayerMob perspective) {
        HumanDrawOptions humanOptions = new HumanDrawOptions(null, this.targetSettler.look, !this.targetSettler.customLook);
        this.targetSettler.setDefaultArmor(humanOptions);
        return Settler.getHumanFaceDrawOptions(humanOptions, size, drawX, drawY + (int)(fadeInProgress * 2.0f));
    }
}

