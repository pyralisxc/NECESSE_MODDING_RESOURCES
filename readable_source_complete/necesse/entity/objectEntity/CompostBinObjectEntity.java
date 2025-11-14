/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ProcessingTechInventoryObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;

public class CompostBinObjectEntity
extends ProcessingTechInventoryObjectEntity {
    public CompostBinObjectEntity(Level level, int x, int y) {
        super(level, "compostbin", x, y, 2, 2, RecipeTechRegistry.COMPOST_BIN);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isProcessing() && GameRandom.globalRandom.nextInt(10) == 0) {
            int startHeight = 24 + GameRandom.globalRandom.nextInt(16);
            this.getLevel().entityManager.addParticle(this.tileX * 32 + GameRandom.globalRandom.nextInt(32), this.tileY * 32 + 32, Particle.GType.COSMETIC).color(new Color(150, 150, 150)).heightMoves(startHeight, startHeight + 20).lifeTime(1000);
        }
    }

    @Override
    public int getProcessTime() {
        return 45000;
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        StringTooltips tooltips = new StringTooltips(this.getObject().getDisplayName());
        if (this.isProcessing()) {
            tooltips.add(Localization.translate("ui", "composting"));
        } else {
            tooltips.add(Localization.translate("ui", "needcompost"));
        }
        GameTooltipManager.addTooltip(tooltips, TooltipLocation.INTERACT_FOCUS);
    }
}

