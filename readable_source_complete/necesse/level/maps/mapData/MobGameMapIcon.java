/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.mapData;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.registries.MobRegistry;
import necesse.level.maps.mapData.GameMapIcon;

public class MobGameMapIcon
extends GameMapIcon {
    protected String mobStringID;

    public MobGameMapIcon(String mobStringID) {
        this.mobStringID = mobStringID;
    }

    @Override
    public Rectangle getDrawBoundingBox() {
        return new Rectangle(-16, -16, 32, 32);
    }

    @Override
    public void drawIcon(int drawX, int drawY, Color color) {
        MobRegistry.getMobIcon(this.mobStringID).initDraw().color(color).posMiddle(drawX, drawY).draw();
    }
}

