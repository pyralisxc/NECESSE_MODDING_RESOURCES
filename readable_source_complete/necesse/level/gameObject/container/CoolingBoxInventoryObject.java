/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FueledRefrigeratorObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.container.InventoryObject;
import necesse.level.maps.Level;

public class CoolingBoxInventoryObject
extends InventoryObject {
    public CoolingBoxInventoryObject(String textureName, int slots, ToolType toolType, Color mapColor) {
        super(textureName, slots, new Rectangle(32, 32), toolType, mapColor);
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
    }

    public CoolingBoxInventoryObject(String textureName, int slots, Color mapColor) {
        super(textureName, slots, new Rectangle(32, 32), mapColor);
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
        this.setItemCategory("objects", "furniture", "misc");
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "coolingboxtip"), 400);
        return tooltips;
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        super.tickEffect(level, layerID, tileX, tileY);
        FueledRefrigeratorObjectEntity coolingBoxObjectEntity = this.getCoolingBoxObjectEntity(level, tileX, tileY);
        if (coolingBoxObjectEntity != null && coolingBoxObjectEntity.hasFuel() && GameRandom.globalRandom.nextInt(10) == 0) {
            int startHeight = 16 + GameRandom.globalRandom.nextInt(16);
            level.entityManager.addParticle(tileX * 32 + GameRandom.globalRandom.getIntBetween(2, 30), tileY * 32 + 32, Particle.GType.COSMETIC).sprite(GameResources.puffParticles.sprite(0, 0, 12)).heightMoves(startHeight, startHeight - 12).lifeTime(3000).fadesAlphaTimeToCustomAlpha(500, 500, 0.25f).size(new ParticleOption.DrawModifier(){

                @Override
                public void modify(SharedTextureDrawOptions.Wrapper options, int lifeTime, int timeAlive, float lifePercent) {
                    options.size(24, 24);
                }
            });
        }
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation % 2 == 0) {
            return new Rectangle(x * 32 + 2, y * 32 + 6, 28, 20);
        }
        return new Rectangle(x * 32 + 6, y * 32 + 2, 20, 28);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new FueledRefrigeratorObjectEntity(level, x, y, 2, 40, 0.25f);
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        if (level.isServer()) {
            OEInventoryContainer.openAndSendContainer(ContainerRegistry.FUELED_REFRIGERATOR_INVENTORY_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    public FueledRefrigeratorObjectEntity getCoolingBoxObjectEntity(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity instanceof FueledRefrigeratorObjectEntity) {
            return (FueledRefrigeratorObjectEntity)objectEntity;
        }
        return null;
    }
}

