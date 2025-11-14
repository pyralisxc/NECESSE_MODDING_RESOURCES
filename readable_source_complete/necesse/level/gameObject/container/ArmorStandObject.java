/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ArmorStandObjectEntity;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ArmorStandObject
extends FurnitureObject {
    public ObjectDamagedTextureArray base;
    public ObjectDamagedTextureArray head;
    public ObjectDamagedTextureArray body;
    public ObjectDamagedTextureArray leftArms;
    public ObjectDamagedTextureArray rightArms;
    public ObjectDamagedTextureArray feet;

    public ArmorStandObject() {
        super(new Rectangle(3, 10, 26, 20));
        this.mapColor = new Color(97, 74, 60);
        this.objectHealth = 50;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.furnitureType = "armorstand";
        this.hoverHitbox = new Rectangle(4, -20, 24, 52);
        this.hoverHitboxSortY = 19;
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "misc");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.base = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/armorstand_base");
        this.head = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/armorstand_head");
        this.body = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/armorstand_body");
        this.leftArms = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/armorstand_arms_left");
        this.rightArms = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/armorstand_arms_right");
        this.feet = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/armorstand_feet");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        DrawOptions options;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX) - 16;
        int drawY = camera.getTileDrawY(tileY) - 32;
        int rotation = level.getObjectRotation(tileX, tileY) % 4;
        ObjectEntity ent = level.entityManager.getObjectEntity(tileX, tileY);
        HumanDrawOptions human = new HumanDrawOptions(level).headTexture(this.head.getDamagedTexture(this, level, tileX, tileY)).bodyTexture(this.body.getDamagedTexture(this, level, tileX, tileY)).leftArmsTexture(this.leftArms.getDamagedTexture(this, level, tileX, tileY)).rightArmsTexture(this.rightArms.getDamagedTexture(this, level, tileX, tileY)).feetTexture(this.feet.getDamagedTexture(this, level, tileX, tileY)).sprite(0, rotation).dir(rotation).light(light);
        if (ent != null && ent.implementsOEInventory()) {
            InventoryItem helmetItem = ((InventoryObjectEntity)ent).inventory.getItem(0);
            InventoryItem chestItem = ((InventoryObjectEntity)ent).inventory.getItem(1);
            InventoryItem bootsItem = ((InventoryObjectEntity)ent).inventory.getItem(2);
            options = human.helmet(helmetItem).chestplate(chestItem).boots(bootsItem).pos(drawX, drawY);
        } else {
            options = human.pos(drawX, drawY);
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 19;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        GameTexture base = this.base.getDamagedTexture(this, level, tileX, tileY);
        TextureDrawOptionsEnd tileOptions = base.initDraw().sprite(0, rotation, 64).light(light).pos(drawX, drawY + 2);
        tileList.add(tm -> tileOptions.draw());
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX) - 16;
        int drawY = camera.getTileDrawY(tileY) - 32;
        GameTexture base = this.base.getDamagedTexture(0.0f);
        base.initDraw().sprite(0, rotation, 64).alpha(alpha).draw(drawX, drawY + 2);
        new HumanDrawOptions(level).headTexture(this.head.getDamagedTexture(0.0f)).bodyTexture(this.body.getDamagedTexture(0.0f)).leftArmsTexture(this.leftArms.getDamagedTexture(0.0f)).rightArmsTexture(this.rightArms.getDamagedTexture(0.0f)).feetTexture(this.feet.getDamagedTexture(0.0f)).sprite(0, rotation).dir(rotation).alpha(alpha).draw(drawX, drawY);
    }

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
        if (level.isServer()) {
            OEInventoryContainer.openAndSendContainer(ContainerRegistry.ARMOR_STAND_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new ArmorStandObjectEntity(level, x, y);
    }

    @Override
    public void doExplosionDamage(Level level, int layerID, int tileX, int tileY, int damage, float toolTier, Attacker attacker, ServerClient client) {
        boolean hasSettlement = SettlementsWorldData.getSettlementsData(level).hasSettlementAtTile(level, tileX, tileY);
        if (!hasSettlement) {
            super.doExplosionDamage(level, layerID, tileX, tileY, damage, toolTier, attacker, client);
        }
    }
}

