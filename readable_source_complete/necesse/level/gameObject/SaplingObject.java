/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SaplingObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.ObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SaplingObject
extends GameObject {
    public GameTexture texture;
    protected String textureName;
    public String[] validTiles;
    public String resultObjectStringID;
    public int minGrowTimeInSeconds;
    public int maxGrowTimeInSeconds;
    protected final GameRandom drawRandom;
    public boolean addAnySaplingIngredient;

    public SaplingObject(String textureName, String resultObjectStringID, int minGrowTimeInSeconds, int maxGrowTimeInSeconds, boolean addAnySaplingIngredient, String ... additionalValidTiles) {
        super(new Rectangle(0, 0));
        this.textureName = textureName;
        this.resultObjectStringID = resultObjectStringID;
        this.minGrowTimeInSeconds = minGrowTimeInSeconds;
        this.maxGrowTimeInSeconds = maxGrowTimeInSeconds;
        this.addAnySaplingIngredient = addAnySaplingIngredient;
        this.validTiles = GameUtils.concat(additionalValidTiles, new String[]{"grasstile", "overgrowngrasstile", "swampgrasstile", "overgrownswampgrasstile", "plainsgrasstile", "overgrownplainsgrasstile", "dirttile", "farmland", "snowtile"});
        this.setItemCategory("objects", "saplings");
        this.mapColor = new Color(16, 147, 30);
        this.displayMapTooltip = true;
        this.objectHealth = 1;
        this.stackSize = 500;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.drawRandom = new GameRandom();
        this.replaceCategories.add("sapling");
        this.canReplaceCategories.add("sapling");
        this.canReplaceCategories.add("tree");
        this.canReplaceCategories.add("bush");
        this.replaceRotations = false;
        this.setItemCategory("objects", "landscaping", "plants");
    }

    public SaplingObject overrideValidTiles(String ... validTiles) {
        this.validTiles = validTiles;
        return this;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/" + this.textureName);
    }

    @Override
    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameResources.grass, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        boolean mirror;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.setSeed(SaplingObject.getTileSeed(tileX, tileY));
            mirror = this.drawRandom.nextBoolean();
        }
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(0, 0, 32).mirror(mirror, false).light(light).pos(drawX, drawY - 8);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        boolean mirror;
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.setSeed(SaplingObject.getTileSeed(tileX, tileY));
            mirror = this.drawRandom.nextBoolean();
        }
        this.texture.initDraw().sprite(0, 0, 32).mirror(mirror, false).alpha(alpha).draw(drawX, drawY - 8);
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String error = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (error != null) {
            return error;
        }
        boolean valid = false;
        for (String tile : this.validTiles) {
            if (level.getTileID(x, y) != TileRegistry.getTileID(tile)) continue;
            valid = true;
            break;
        }
        if (!valid) {
            return "invalidtile";
        }
        return null;
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        if (super.isValid(level, layerID, x, y)) {
            for (String tile : this.validTiles) {
                if (level.getTileID(x, y) != TileRegistry.getTileID(tile)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new SaplingObjectEntity(level, x, y, this.resultObjectStringID, this.minGrowTimeInSeconds, this.maxGrowTimeInSeconds);
    }

    @Override
    public Item generateNewObjectItem() {
        Item item = super.generateNewObjectItem();
        ((ObjectItem)item).setTranslatedTypeName(new LocalMessage("itemcategory", "saplings"));
        if (this.addAnySaplingIngredient) {
            item.addGlobalIngredient("anysapling");
        }
        item.addGlobalIngredient("anycompostable");
        return item;
    }
}

