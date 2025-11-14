/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.CompostBinObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.ProcessingTechInventoryObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.light.GameLight;

public class CompostBinObject
extends GameObject
implements SettlementWorkstationObject {
    public ObjectDamagedTextureArray texture;

    public CompostBinObject() {
        super(new Rectangle(4, 6, 24, 20));
        this.setItemCategory("objects", "craftingstations");
        this.setCraftingCategory("craftingstations");
        this.mapColor = new Color(111, 78, 24);
        this.displayMapTooltip = true;
        this.toolType = ToolType.ALL;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
        this.replaceCategories.add("workstation");
        this.canReplaceCategories.add("workstation");
        this.canReplaceCategories.add("wall");
        this.canReplaceCategories.add("furniture");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/compostbin");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(rotation % texture.getWidth() / 32, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - (texture.getHeight() - 32));
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        texture.initDraw().sprite(rotation % texture.getWidth() / 32, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - (texture.getHeight() - 32));
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
            OEInventoryContainer.openAndSendContainer(ContainerRegistry.PROCESSING_INVENTORY_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new CompostBinObjectEntity(level, x, y);
    }

    public ProcessingTechInventoryObjectEntity getProcessingObjectEntity(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity instanceof ProcessingTechInventoryObjectEntity) {
            return (ProcessingTechInventoryObjectEntity)objectEntity;
        }
        return null;
    }

    @Override
    public Stream<Recipe> streamSettlementRecipes(Level level, int tileX, int tileY) {
        ProcessingTechInventoryObjectEntity processingOE = this.getProcessingObjectEntity(level, tileX, tileY);
        if (processingOE != null) {
            return Recipes.streamRecipes(processingOE.techs);
        }
        return Stream.empty();
    }

    @Override
    public boolean isProcessingInventory(Level level, int tileX, int tileY) {
        return true;
    }

    @Override
    public boolean canCurrentlyCraft(Level level, int tileX, int tileY, Recipe recipe) {
        ProcessingTechInventoryObjectEntity processingOE = this.getProcessingObjectEntity(level, tileX, tileY);
        if (processingOE != null) {
            return processingOE.getExpectedResults().crafts < 15;
        }
        return false;
    }

    @Override
    public int getMaxCraftsAtOnce(Level level, int tileX, int tileY, Recipe recipe) {
        return 5;
    }

    @Override
    public InventoryRange getProcessingInputRange(Level level, int tileX, int tileY) {
        ProcessingTechInventoryObjectEntity processingOE = this.getProcessingObjectEntity(level, tileX, tileY);
        if (processingOE != null) {
            return processingOE.getInputInventoryRange();
        }
        return null;
    }

    @Override
    public InventoryRange getProcessingOutputRange(Level level, int tileX, int tileY) {
        ProcessingTechInventoryObjectEntity processingOE = this.getProcessingObjectEntity(level, tileX, tileY);
        if (processingOE != null) {
            return processingOE.getOutputInventoryRange();
        }
        return null;
    }

    @Override
    public ArrayList<InventoryItem> getCurrentAndFutureProcessingOutputs(Level level, int tileX, int tileY) {
        ProcessingTechInventoryObjectEntity processingOE = this.getProcessingObjectEntity(level, tileX, tileY);
        if (processingOE != null) {
            return processingOE.getCurrentAndExpectedResults().items;
        }
        return new ArrayList<InventoryItem>();
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "compostbintip"));
        return tooltips;
    }

    @Override
    protected boolean shouldPlayInteractSound(Level level, int tileX, int tileY) {
        return true;
    }
}

