/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.GrainMillObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

abstract class GrainMillExtraObject
extends GameObject {
    public GrainMillExtraObject() {
        super(new Rectangle(32, 32));
        this.setItemCategory("objects", "craftingstations");
        this.setCraftingCategory("craftingstations");
        this.mapColor = new Color(93, 67, 24);
        this.displayMapTooltip = true;
        this.toolType = ToolType.ALL;
        this.objectHealth = 100;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, -32, 32, 64);
        this.replaceCategories.add("workstation");
        this.canReplaceCategories.add("workstation");
        this.canReplaceCategories.add("wall");
        this.canReplaceCategories.add("furniture");
    }

    protected abstract void setCounterIDs(int var1, int var2, int var3, int var4);

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        if (this.isMultiTileMaster()) {
            if (level.isServer()) {
                OEInventoryContainer.openAndSendContainer(ContainerRegistry.PROCESSING_INVENTORY_CONTAINER, player.getServerClient(), level, x, y);
            }
        } else {
            this.getMultiTile(level.getObjectRotation(x, y)).getMasterLevelObject(level, 0, x, y).ifPresent(e -> e.interact(player));
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        if (this.isMultiTileMaster()) {
            return new GrainMillObjectEntity(level, x, y);
        }
        return null;
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "grainmilltip"));
        return tooltips;
    }

    @Override
    protected boolean shouldPlayInteractSound(Level level, int tileX, int tileY) {
        return true;
    }
}

