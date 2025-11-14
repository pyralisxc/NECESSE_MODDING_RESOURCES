/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.objectEntity.FueledIncineratorObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class IncineratorInventoryObject
extends GameObject {
    public ObjectDamagedTextureArray texture;
    public ObjectDamagedTextureArray openTexture;
    public ObjectDamagedTextureArray activeTexture;
    public ObjectDamagedTextureArray activeOpenTexture;

    public IncineratorInventoryObject() {
        super(new Rectangle(2, 6, 28, 20));
        this.setItemCategory("objects", "craftingstations");
        this.setCraftingCategory("craftingstations");
        this.mapColor = new Color(115, 115, 127);
        this.displayMapTooltip = true;
        this.toolType = ToolType.ALL;
        this.rarity = Item.Rarity.COMMON;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.lightHue = 50.0f;
        this.lightSat = 0.2f;
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
        this.replaceCategories.add("workstation");
        this.canReplaceCategories.add("workstation");
        this.canReplaceCategories.add("wall");
        this.canReplaceCategories.add("furniture");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/incinerator");
        this.openTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/incinerator_open");
        this.activeTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/incinerator_active");
        this.activeOpenTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/incinerator_active_open");
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "incineratortip"), 400);
        return tooltips;
    }

    @Override
    public int getLightLevel(Level level, int layerID, int tileX, int tileY) {
        FueledIncineratorObjectEntity incineratorObjectEntity = this.getIncineratorObjectEntity(level, tileX, tileY);
        if (incineratorObjectEntity != null && incineratorObjectEntity.isFuelRunning()) {
            return 100;
        }
        return 0;
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        super.tickEffect(level, layerID, tileX, tileY);
        FueledIncineratorObjectEntity incineratorObjectEntity = this.getIncineratorObjectEntity(level, tileX, tileY);
        if (incineratorObjectEntity != null && incineratorObjectEntity.isFuelRunning()) {
            if (incineratorObjectEntity.isInUse()) {
                for (float buffer = 0.5f; buffer >= 1.0f || GameRandom.globalRandom.getChance(buffer); buffer -= 1.0f) {
                    int startHeight = 30;
                    int yOffset = 8;
                    ParticleOption particleOption = level.entityManager.addParticle(tileX * 32 + GameRandom.globalRandom.getIntBetween(11, 21), tileY * 32 + GameRandom.globalRandom.getIntBetween(10, 16) + yOffset, GameRandom.globalRandom.getChance(0.75f) ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.getFloatBetween(-1.0f, 1.0f), GameRandom.globalRandom.getFloatBetween(-1.0f, 1.0f)).heightMoves(startHeight - yOffset, startHeight + 10 - yOffset).colorRandom(30.0f, 1.0f, 0.9f, 12.0f, 0.1f, 0.1f).sizeFades(10, 14).lifeTime(2000);
                    if (!GameRandom.globalRandom.nextBoolean()) continue;
                    particleOption.onProgress(0.5f, p -> {
                        for (int i = 0; i < GameRandom.globalRandom.getIntBetween(1, 2); ++i) {
                            level.entityManager.addParticle(p.x + (float)((int)(GameRandom.globalRandom.nextGaussian() * 2.0)), p.y, Particle.GType.COSMETIC).smokeColor().sizeFades(8, 12).heightMoves(startHeight + 6, startHeight + 20);
                        }
                    });
                }
            } else if (GameRandom.globalRandom.nextInt(10) == 0) {
                int startHeight = 24 + GameRandom.globalRandom.nextInt(8);
                level.entityManager.addParticle(tileX * 32 + GameRandom.globalRandom.getIntBetween(8, 24), tileY * 32 + 32, Particle.GType.COSMETIC).smokeColor().heightMoves(startHeight, startHeight + 20).lifeTime(1000);
            }
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        ObjectEntity ent;
        ObjectDamagedTextureArray usedTexture;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        FueledIncineratorObjectEntity incineratorObjectEntity = this.getIncineratorObjectEntity(level, tileX, tileY);
        ObjectDamagedTextureArray objectDamagedTextureArray = usedTexture = incineratorObjectEntity != null && incineratorObjectEntity.isFuelRunning() ? this.activeTexture : this.texture;
        if (this.openTexture != null && (ent = level.entityManager.getObjectEntity(tileX, tileY)) != null && ent.implementsOEUsers() && ((OEUsers)((Object)ent)).isInUse()) {
            usedTexture = incineratorObjectEntity != null && incineratorObjectEntity.isFuelRunning() ? this.activeOpenTexture : this.openTexture;
        }
        GameTexture texture = usedTexture.getDamagedTexture(this, level, tileX, tileY);
        int rotation = level.getObjectRotation(tileX, tileY) % (texture.getWidth() / 32);
        boolean treasureHunter = perspective != null && perspective.buffManager.getModifier(BuffModifiers.TREASURE_HUNTER) != false;
        final SharedTextureDrawOptions draws = new SharedTextureDrawOptions(texture);
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
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        texture.initDraw().sprite(rotation %= texture.getWidth() / 32, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new FueledIncineratorObjectEntity(level, x, y, 2, 10);
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
            OEInventoryContainer.openAndSendContainer(ContainerRegistry.INCINERATOR_INVENTORY_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    public FueledIncineratorObjectEntity getIncineratorObjectEntity(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity instanceof FueledIncineratorObjectEntity) {
            return (FueledIncineratorObjectEntity)objectEntity;
        }
        return null;
    }

    @Override
    protected boolean shouldPlayInteractSound(Level level, int tileX, int tileY) {
        return true;
    }

    @Override
    protected SoundSettings getInteractSoundOpen() {
        return SoundSettingsRegistry.defaultOpen;
    }

    @Override
    protected SoundSettings getInteractSoundClose() {
        return null;
    }

    @Override
    protected boolean interactSoundIsGlobal() {
        return true;
    }

    @Override
    protected boolean interactSoundIsFirstAndLastOnly() {
        return true;
    }
}

