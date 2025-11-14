/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.Mob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class LockedNoSettler
extends Settler {
    public LockedNoSettler() {
        super(null);
    }

    @Override
    public DrawOptions getSettlerFaceDrawOptions(int drawX, int drawY, int size, Mob settlerMob) {
        GameTexture texture = Settings.UI.settler_locked;
        return texture.initDraw().size(size).pos(drawX, drawY);
    }

    @Override
    public String getGenericMobName() {
        return Localization.translate("misc", "settlementlockbed");
    }
}

