/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.VicinityBuff
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTooltips.ListGameTooltips
 */
package aphorea.buffs.Banners;

import aphorea.registry.AphModifiers;
import java.io.FileNotFoundException;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.VicinityBuff;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class AphBannerBuff
extends VicinityBuff {
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public void updateLocalDisplayName() {
        this.displayName = new LocalMessage("item", this.getRealName());
    }

    public void loadTextures() {
        try {
            this.iconTexture = GameTexture.fromFileRaw((String)("buffs/" + this.getRealName()));
        }
        catch (FileNotFoundException var2) {
            this.iconTexture = GameTexture.fromFile((String)"buffs/unknown");
        }
    }

    public String getRealName() {
        String name = this.getStringID();
        if (name.startsWith("aph_")) {
            return name.replace("aph_", "");
        }
        return name;
    }

    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        ab.getModifierTooltips().forEach(modifierTooltip -> tooltips.add((Object)modifierTooltip.toTooltip(true)));
        return tooltips;
    }

    public float getInspirationEffect(ActiveBuff ab) {
        return ab.owner == null ? 1.0f : ((Float)ab.owner.buffManager.getModifier(AphModifiers.INSPIRATION_EFFECT)).floatValue();
    }

    public static boolean shouldChange(ActiveBuff antAb, ActiveBuff newAb) {
        return (antAb.owner == null ? 1.0f : ((Float)antAb.owner.buffManager.getModifier(AphModifiers.INSPIRATION_EFFECT)).floatValue()) < (newAb.owner == null ? 1.0f : ((Float)newAb.owner.buffManager.getModifier(AphModifiers.INSPIRATION_EFFECT)).floatValue());
    }
}

