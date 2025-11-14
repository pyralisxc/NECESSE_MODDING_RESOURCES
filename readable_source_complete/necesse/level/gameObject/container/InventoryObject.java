/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class InventoryObject
extends FurnitureObject {
    public ObjectDamagedTextureArray texture;
    public ObjectDamagedTextureArray openTexture;
    protected String textureName;
    public int slots;
    protected final GameRandom drawRandom;

    public InventoryObject(String textureName, int slots, Rectangle collision, ToolType toolType, Color mapColor) {
        super(collision);
        this.textureName = textureName;
        this.toolType = toolType;
        this.slots = slots;
        this.mapColor = mapColor;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.drawRandom = new GameRandom();
    }

    public InventoryObject(String textureName, int slots, Rectangle collision, Color mapColor) {
        this(textureName, slots, collision, ToolType.ALL, mapColor);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
        try {
            this.openTexture = ObjectDamagedTextureArray.loadAndApplyOverlayRaw(this, "objects/" + this.textureName + "_open");
        }
        catch (FileNotFoundException e) {
            this.openTexture = null;
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        ObjectEntity ent;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        ObjectDamagedTextureArray usedTexture = this.texture;
        if (this.openTexture != null && (ent = level.entityManager.getObjectEntity(tileX, tileY)) != null && ent.implementsOEUsers() && ((OEUsers)((Object)ent)).isInUse()) {
            usedTexture = this.openTexture;
        }
        GameTexture texture = usedTexture.getDamagedTexture(this, level, tileX, tileY);
        final SharedTextureDrawOptions draws = new SharedTextureDrawOptions(texture);
        int rotation = level.getObjectRotation(tileX, tileY) % (texture.getWidth() / 32);
        boolean treasureHunter = perspective != null && perspective.buffManager.getModifier(BuffModifiers.TREASURE_HUNTER) != false;
        draws.addSprite(rotation, 0, 32, texture.getHeight()).spelunkerLight(light, treasureHunter, this.getID(), level).pos(drawX, drawY - texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                draws.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        texture.initDraw().sprite(rotation %= texture.getWidth() / 32, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
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
            OEInventoryContainer.openAndSendContainer(ContainerRegistry.OE_INVENTORY_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new InventoryObjectEntity(level, x, y, this.slots);
    }

    @Override
    public void doExplosionDamage(Level level, int layerID, int tileX, int tileY, int damage, float toolTier, Attacker attacker, ServerClient client) {
        boolean hasSettlement = SettlementsWorldData.getSettlementsData(level).hasSettlementAtTile(level, tileX, tileY);
        if (!hasSettlement) {
            super.doExplosionDamage(level, layerID, tileX, tileY, damage, toolTier, attacker, client);
        }
    }

    @Override
    protected boolean interactSoundIsFirstAndLastOnly() {
        return true;
    }

    @Override
    protected boolean interactSoundIsGlobal() {
        return true;
    }

    @Override
    protected SoundSettings getInteractSoundOpen() {
        return new SoundSettings(GameResources.chestopen).volume(0.7f);
    }

    @Override
    protected SoundSettings getInteractSoundClose() {
        return new SoundSettings(GameResources.chestclose).volume(0.4f);
    }
}

