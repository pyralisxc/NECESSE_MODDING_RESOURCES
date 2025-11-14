/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.io.FileNotFoundException;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class SoulseedConsecutiveExplosionsBuff
extends Buff {
    private GameTexture tip1;
    private GameTexture tip2;
    private GameTexture tip3;
    private GameTexture tip4;

    public SoulseedConsecutiveExplosionsBuff() {
        this.isImportant = true;
        this.isVisible = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 999999;
    }

    @Override
    public boolean overridesStackDuration() {
        return true;
    }

    @Override
    public boolean shouldDrawDuration(ActiveBuff buff) {
        return false;
    }

    @Override
    public GameTexture getDrawIcon(ActiveBuff buff) {
        int consecutiveExplosions = buff.getStacks();
        if (consecutiveExplosions < 20) {
            return this.tip1;
        }
        if (consecutiveExplosions < 100) {
            return this.tip2;
        }
        if (consecutiveExplosions < 200) {
            return this.tip3;
        }
        return this.tip4;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        try {
            this.tip1 = GameTexture.fromFileRaw("buffs/soulseedtip1");
            this.tip2 = GameTexture.fromFileRaw("buffs/soulseedtip2");
            this.tip3 = GameTexture.fromFileRaw("buffs/soulseedtip3");
            this.tip4 = GameTexture.fromFileRaw("buffs/soulseedtip4");
        }
        catch (FileNotFoundException e) {
            this.tip1 = GameTexture.fromFile("buffs/unknown");
            this.tip2 = GameTexture.fromFile("buffs/unknown");
            this.tip3 = GameTexture.fromFile("buffs/unknown");
            this.tip4 = GameTexture.fromFile("buffs/unknown");
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        int consecutiveExplosions = ab.getStacks();
        String tooltipText = Localization.translate("bufftooltip", "soulseedtip", "value", (Object)consecutiveExplosions);
        tooltips.add(tooltipText, 300);
        return tooltips;
    }
}

