/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.postProcessing.PostProcessingEffects;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class SwampSporesBuff
extends Buff {
    public SwampSporesBuff() {
        this.isImportant = true;
        this.shouldSave = true;
        this.canCancel = false;
        this.sortByDuration = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        this.updateBlindnessModifier(buff);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.isClient() && buff.owner.getClient().getPlayer() == buff.owner) {
            Color color = new Color(213, 98, 232);
            float mod = (float)Math.pow((float)buff.getStacks() / 100.0f, 0.5);
            PostProcessingEffects.setSceneShade(GameMath.lerp(mod, 1.0f, (float)color.getRed() / 255.0f), GameMath.lerp(mod, 1.0f, (float)color.getGreen() / 255.0f), GameMath.lerp(mod, 1.0f, (float)color.getBlue() / 255.0f));
        }
    }

    @Override
    public void onStacksUpdated(ActiveBuff buff, ActiveBuff other) {
        super.onStacksUpdated(buff, other);
        this.updateBlindnessModifier(buff);
    }

    private void updateBlindnessModifier(ActiveBuff buff) {
        float blindness = (float)Math.pow(0.01f * (float)buff.getStacks(), 0.5);
        buff.setModifier(BuffModifiers.BLINDNESS, Float.valueOf(Math.min(blindness, 0.75f)));
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 100;
    }

    @Override
    public boolean overridesStackDuration() {
        return true;
    }

    @Override
    public int getStacksDisplayCount(ActiveBuff buff) {
        return 1;
    }

    @Override
    public String getDurationText(ActiveBuff buff) {
        return buff.getStacks() + "%";
    }

    @Override
    public int getRemainingStacksDuration(ActiveBuff buff, AtomicBoolean sendUpdatePacket) {
        return 25;
    }
}

