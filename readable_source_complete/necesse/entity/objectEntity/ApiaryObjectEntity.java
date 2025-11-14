/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;

public class ApiaryObjectEntity
extends AbstractBeeHiveObjectEntity {
    public static int maxStoredHoney = 10;
    public static int maxFrames = 5;
    public static int maxBees = 20;

    public ApiaryObjectEntity(Level level, int x, int y) {
        super(level, "apiary", x, y);
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        StringTooltips tooltips = new StringTooltips();
        tooltips.add(this.getObject().getDisplayName());
        if (this.hasQueen) {
            tooltips.add(Localization.translate("ui", "producinghoney"));
        } else {
            tooltips.add(Localization.translate("ui", "missingqueen"));
        }
        if (debug) {
            this.addDebugTooltips(tooltips);
        }
        GameTooltipManager.addTooltip(tooltips, TooltipLocation.INTERACT_FOCUS);
    }

    @Override
    public int getMaxBees() {
        return maxBees;
    }

    @Override
    public int getMaxFrames() {
        return maxFrames;
    }

    @Override
    public int getMaxStoredHoney() {
        return maxStoredHoney;
    }

    @Override
    public boolean canCreateQueens() {
        return true;
    }
}

