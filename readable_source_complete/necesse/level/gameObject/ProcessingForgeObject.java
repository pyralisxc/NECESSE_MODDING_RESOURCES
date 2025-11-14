/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.ProcessingForgeObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageGlobalIngredientIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;
import necesse.level.maps.light.GameLight;

public class ProcessingForgeObject
extends GameObject
implements SettlementWorkstationObject {
    public ObjectDamagedTextureArray texture;

    public ProcessingForgeObject() {
        super(new Rectangle(32, 32));
        this.setItemCategory("objects", "craftingstations");
        this.setCraftingCategory("craftingstations");
        this.isLightTransparent = true;
        this.roomProperties.add("metalwork");
        this.lightHue = 50.0f;
        this.lightSat = 0.2f;
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
        this.replaceCategories.add("workstation");
        this.canReplaceCategories.add("workstation");
        this.canReplaceCategories.add("wall");
        this.canReplaceCategories.add("furniture");
    }

    @Override
    public int getLightLevel(Level level, int layerID, int tileX, int tileY) {
        ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (forgeObjectEntity != null && forgeObjectEntity.isFuelRunning()) {
            return 100;
        }
        return 0;
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        ProcessingForgeObjectEntity forgeObjectEntity;
        super.tickEffect(level, layerID, tileX, tileY);
        if (GameRandom.globalRandom.nextInt(10) == 0 && (forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY)) != null && forgeObjectEntity.isFuelRunning()) {
            int startHeight = 16 + GameRandom.globalRandom.nextInt(16);
            level.entityManager.addParticle(tileX * 32 + GameRandom.globalRandom.getIntBetween(8, 24), tileY * 32 + 32, Particle.GType.COSMETIC).smokeColor().heightMoves(startHeight, startHeight + 20).lifeTime(1000);
        }
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/forge");
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation % 2 == 0) {
            return new Rectangle(x * 32 + 2, y * 32 + 6, 28, 20);
        }
        return new Rectangle(x * 32 + 6, y * 32 + 2, 20, 28);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        TextureDrawOptionsEnd flame;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        boolean isFueled = false;
        ProcessingForgeObjectEntity objectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (objectEntity != null) {
            isFueled = objectEntity.isFuelRunning();
        }
        int spriteHeight = texture.getHeight() - 32;
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(rotation % 4, 0, 32, spriteHeight).light(light).pos(drawX, drawY - (spriteHeight - 32));
        if (isFueled && rotation == 2) {
            int spriteX = (int)(level.getWorldEntity().getWorldTime() % 1200L / 300L);
            flame = texture.initDraw().sprite(spriteX, spriteHeight / 32, 32).light(light).pos(drawX, drawY);
        } else {
            flame = null;
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
                if (flame != null) {
                    flame.draw();
                }
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        int spriteHeight = texture.getHeight() - 32;
        texture.initDraw().sprite(rotation % 4, 0, 32, spriteHeight).alpha(alpha).draw(drawX, drawY - (spriteHeight - 32));
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new ProcessingForgeObjectEntity(level, x, y);
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
            CraftingStationContainer.openAndSendContainer(ContainerRegistry.FUELED_PROCESSING_STATION_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "forgetip"));
        return tooltips;
    }

    public ProcessingForgeObjectEntity getForgeObjectEntity(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity instanceof ProcessingForgeObjectEntity) {
            return (ProcessingForgeObjectEntity)objectEntity;
        }
        return null;
    }

    @Override
    public Stream<Recipe> streamSettlementRecipes(Level level, int tileX, int tileY) {
        ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (forgeObjectEntity != null) {
            return Recipes.streamRecipes(forgeObjectEntity.techs);
        }
        return Stream.empty();
    }

    @Override
    public boolean isProcessingInventory(Level level, int tileX, int tileY) {
        return true;
    }

    @Override
    public boolean canCurrentlyCraft(Level level, int tileX, int tileY, Recipe recipe) {
        ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (forgeObjectEntity != null) {
            return forgeObjectEntity.getExpectedResults().crafts < 10 && (forgeObjectEntity.isFuelRunning() || forgeObjectEntity.canUseFuel());
        }
        return false;
    }

    @Override
    public int getMaxCraftsAtOnce(Level level, int tileX, int tileY, Recipe recipe) {
        return 5;
    }

    @Override
    public InventoryRange getProcessingInputRange(Level level, int tileX, int tileY) {
        ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (forgeObjectEntity != null) {
            return forgeObjectEntity.getInputInventoryRange();
        }
        return null;
    }

    @Override
    public InventoryRange getProcessingOutputRange(Level level, int tileX, int tileY) {
        ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (forgeObjectEntity != null) {
            return forgeObjectEntity.getOutputInventoryRange();
        }
        return null;
    }

    @Override
    public ArrayList<InventoryItem> getCurrentAndFutureProcessingOutputs(Level level, int tileX, int tileY) {
        ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (forgeObjectEntity != null) {
            return forgeObjectEntity.getCurrentAndExpectedResults().items;
        }
        return new ArrayList<InventoryItem>();
    }

    @Override
    public SettlementRequestOptions getFuelRequestOptions(Level level, int tileX, int tileY) {
        return new SettlementRequestOptions(5, 10){

            @Override
            public SettlementStorageRecordsRegionData getRequestStorageData(SettlementStorageRecords records) {
                return records.getIndex(SettlementStorageGlobalIngredientIDIndex.class).getGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredientID("anylog"));
            }
        };
    }

    @Override
    public InventoryRange getFuelInventoryRange(Level level, int tileX, int tileY) {
        Inventory inventory;
        ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (forgeObjectEntity != null && (inventory = forgeObjectEntity.getInventory()) != null && forgeObjectEntity.fuelSlots > 0) {
            return new InventoryRange(inventory, 0, forgeObjectEntity.fuelSlots - 1);
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
}

