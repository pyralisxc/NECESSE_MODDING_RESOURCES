/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.modifiers.Modifier
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.gfx.gameTexture.GameTexture
 */
package aphorea.buffs.Banners;

import aphorea.buffs.Banners.AphBasicBannerBuff;
import java.io.FileNotFoundException;
import java.util.function.BiFunction;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.Modifier;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.gameTexture.GameTexture;

public class AphMightyBasicBannerBuff
extends AphBasicBannerBuff {
    private GameTexture iconTextureInactive;
    private GameTexture iconTextureActive;

    public AphMightyBasicBannerBuff(BiFunction<Float, Float, Float> getValue, float baseValue, AphBasicBannerBuff.AphBasicBannerBuffModifier ... modifiers) {
        super(getValue, baseValue, modifiers);
    }

    public AphMightyBasicBannerBuff(AphBasicBannerBuff.AphBasicBannerBuffModifier ... modifiers) {
        super(modifiers);
    }

    public static AphMightyBasicBannerBuff floatModifier(BiFunction<Float, Float, Float> getValue, float baseValue, Modifier<Float> floatModifier, float value) {
        return new AphMightyBasicBannerBuff(getValue, baseValue, AphBasicBannerBuff.AphBasicBannerBuffModifier.floatModifier(floatModifier, value));
    }

    public static AphMightyBasicBannerBuff intModifier(BiFunction<Float, Float, Float> getValue, float baseValue, Modifier<Integer> intModifier, int value) {
        return new AphMightyBasicBannerBuff(getValue, baseValue, AphBasicBannerBuff.AphBasicBannerBuffModifier.intModifier(intModifier, value));
    }

    public static AphMightyBasicBannerBuff floatModifier(Modifier<Float> floatModifier, float value) {
        return new AphMightyBasicBannerBuff(AphBasicBannerBuff.AphBasicBannerBuffModifier.floatModifier(floatModifier, value));
    }

    public static AphMightyBasicBannerBuff intModifier(Modifier<Integer> intModifier, int value) {
        return new AphMightyBasicBannerBuff(AphBasicBannerBuff.AphBasicBannerBuffModifier.intModifier(intModifier, value));
    }

    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.buff.getStringID().endsWith("_normal")) {
            this.giveEffects(buff);
        }
    }

    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        if (buff.buff.getStringID().endsWith("_normal")) {
            this.giveEffects(buff);
        }
    }

    @Override
    public void giveEffects(ActiveBuff ab) {
        if (ab.buff.getStringID().endsWith("_normal") && ab.owner.buffManager.hasBuff(ab.buff.getStringID().replace("_normal", "_greater"))) {
            for (AphBasicBannerBuff.AphBasicBannerBuffModifier modifier : this.modifiers) {
                if (modifier.floatModifier != null) {
                    ab.setModifier(modifier.floatModifier, (Object)Float.valueOf(this.baseValue));
                }
                if (modifier.intModifier == null) continue;
                ab.setModifier(modifier.intModifier, (Object)((int)this.baseValue));
            }
            this.onInactive();
            return;
        }
        super.giveEffects(ab);
        this.onActive();
    }

    public void onActive() {
        this.iconTexture = this.iconTextureActive;
        this.displayName = new LocalMessage("item", this.getStringID());
    }

    public void onInactive() {
        this.iconTexture = this.iconTextureInactive;
        this.displayName = new LocalMessage("item", this.getStringID() + "_inactive");
    }

    @Override
    public void loadTextures() {
        try {
            this.iconTexture = GameTexture.fromFileRaw((String)("buffs/" + this.getStringID()));
            this.iconTextureActive = GameTexture.fromFileRaw((String)("buffs/" + this.getStringID()));
            this.iconTextureInactive = GameTexture.fromFileRaw((String)("buffs/" + this.getStringID() + "_inactive"));
        }
        catch (FileNotFoundException var2) {
            this.iconTexture = GameTexture.fromFile((String)"buffs/unknown");
            this.iconTextureActive = GameTexture.fromFile((String)"buffs/unknown");
            this.iconTextureInactive = GameTexture.fromFile((String)"buffs/unknown");
        }
    }

    @Override
    public void updateLocalDisplayName() {
        this.displayName = new LocalMessage("item", this.getStringID());
    }
}

