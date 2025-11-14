/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.TrapObjectEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class TrapObject
extends GameObject {
    public TrapObject(Rectangle collision) {
        super(collision);
        this.setItemCategory("objects", "traps");
        this.displayMapTooltip = true;
        this.showsWire = true;
        this.objectHealth = 1;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
    }

    @Override
    public void onWireUpdate(Level level, int layerID, int tileX, int tileY, int wireID, boolean active) {
        ObjectEntity ent;
        if (active && (ent = level.entityManager.getObjectEntity(tileX, tileY)) != null) {
            ((TrapObjectEntity)ent).triggerTrap(wireID, level.getObjectRotation(tileX, tileY));
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new TrapObjectEntity(level, x, y, 10000L);
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "activatedwiretip"));
        return tooltips;
    }
}

