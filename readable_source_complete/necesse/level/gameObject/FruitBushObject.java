/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketHitObject;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FruitGrowerObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsPositionMod;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.HarvestFruitLevelJob;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.light.GameLight;

public class FruitBushObject
extends GameObject {
    public int weaveTime = 250;
    public float weaveAmount = 0.08f;
    public float weaveHeight = 1.0f;
    public float waveHeightOffset = 0.0f;
    public float minGrowTimeSeconds;
    public float maxGrowTimeSeconds;
    public float fruitPerStage;
    public int maxStage;
    public String seedStringID;
    public String fruitStringID;
    protected String textureName;
    protected GameTexture[][] textures;
    protected final GameRandom drawRandom;

    public FruitBushObject(String textureName, String seedStringID, float minGrowTimeSeconds, float maxGrowTimeSeconds, String fruitStringID, float fruitPerStage, int maxStage, Color mapColor) {
        this.textureName = textureName;
        this.seedStringID = seedStringID;
        this.minGrowTimeSeconds = minGrowTimeSeconds;
        this.maxGrowTimeSeconds = maxGrowTimeSeconds;
        this.fruitStringID = fruitStringID;
        this.fruitPerStage = fruitPerStage;
        this.maxStage = maxStage;
        this.mapColor = mapColor;
        this.displayMapTooltip = true;
        this.drawDamage = false;
        this.objectHealth = 1;
        this.toolType = ToolType.ALL;
        this.drawRandom = new GameRandom();
        this.attackThrough = true;
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
        this.replaceCategories.add("bush");
        this.replaceRotations = false;
        this.setItemCategory("objects", "landscaping", "plants");
    }

    public FruitBushObject(String textureName, String seedStringID, float minGrowTimeSeconds, float maxGrowTimeSeconds, String fruitStringID, float fruitPerStage, int maxStage) {
        this(textureName, seedStringID, minGrowTimeSeconds, maxGrowTimeSeconds, fruitStringID, fruitPerStage, maxStage, new Color(86, 69, 40));
    }

    @Override
    public void tick(Mob mob, Level level, int x, int y) {
        super.tick(mob, level, x, y);
        if (Settings.wavyGrass && mob.getFlyingHeight() < 10 && (mob.dx != 0.0f || mob.dy != 0.0f)) {
            level.makeGrassWeave(x, y, this.weaveTime, false);
        }
    }

    @Override
    public List<LevelJob> getLevelJobs(Level level, int tileX, int tileY) {
        if (this.getFruitStage(level, tileX, tileY) > 0) {
            return Collections.singletonList(new HarvestFruitLevelJob(tileX, tileY));
        }
        return super.getLevelJobs(level, tileX, tileY);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        GameTexture texture = GameTexture.fromFile("objects/" + this.textureName);
        this.textures = new GameTexture[texture.getWidth() / 64][texture.getHeight() / 64];
        for (int i = 0; i < this.textures.length; ++i) {
            for (int j = 0; j < this.textures[i].length; ++j) {
                this.textures[i][j] = new GameTexture(texture, 64 * i, 64 * j, 64, 64);
            }
        }
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        LootTable lootTable = new LootTable();
        if (this.seedStringID != null) {
            lootTable.items.add(new LootItem(this.seedStringID).preventLootMultiplier());
        }
        return lootTable;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        boolean mirror;
        double yGaussian;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        float alpha = 1.0f;
        if (perspective != null && !Settings.hideUI && !Settings.hideCursor) {
            Rectangle alphaRec = new Rectangle(tileX * 32 - 12, tileY * 32 - 24, 56, 32);
            if (perspective.getCollision().intersects(alphaRec)) {
                alpha = 0.5f;
            } else if (alphaRec.contains(camera.getX() + WindowManager.getWindow().mousePos().sceneX, camera.getY() + WindowManager.getWindow().mousePos().sceneY)) {
                alpha = 0.5f;
            }
        }
        int spriteX = 0;
        if (this.textures.length > 1 && level.getTileID(tileX, tileY) == TileRegistry.snowID) {
            spriteX = 1;
        }
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            yGaussian = this.drawRandom.seeded(FruitBushObject.getTileSeed(tileX, tileY, 0)).nextFloat() * 2.0f - 1.0f;
            this.drawRandom.setSeed(FruitBushObject.getTileSeed(tileX, tileY));
            mirror = this.drawRandom.nextBoolean();
        }
        Consumer<TextureDrawOptionsPositionMod> waveChange = GameResources.waveShader.setupGrassWaveMod(level, tileX, tileY, 400L, this.weaveAmount, 2, this.drawRandom, FruitBushObject.getTileSeed(tileX, tileY, 0), mirror, 2.0f);
        int spriteY = Math.min(this.getFruitStage(level, tileX, tileY), this.textures[spriteX].length - 1);
        GameTexture texture = this.textures[spriteX][spriteY];
        int offset = 28 + (int)(yGaussian * 4.0);
        final TextureDrawOptionsEnd options = ((TextureDrawOptionsEnd)texture.initDraw().alpha(alpha).light(light).mirror(mirror, false).addPositionMod((Consumer)waveChange)).pos(drawX - 32 + 16, drawY - texture.getHeight() + offset);
        final int sortY = offset - 8;
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return sortY;
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
        double yGaussian;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int spriteX = 0;
        if (this.textures.length > 1 && level.getTileID(tileX, tileY) == TileRegistry.snowID) {
            spriteX = 1;
        }
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            yGaussian = this.drawRandom.seeded(FruitBushObject.getTileSeed(tileX, tileY, 0)).nextFloat() * 2.0f - 1.0f;
            this.drawRandom.setSeed(FruitBushObject.getTileSeed(tileX, tileY));
            mirror = this.drawRandom.nextBoolean();
        }
        int spriteY = Math.min(0, this.textures[spriteX].length - 1);
        GameTexture texture = this.textures[spriteX][spriteY];
        int offset = 28 + (int)(yGaussian * 4.0);
        texture.initDraw().alpha(alpha).light(light).mirror(mirror, false).alpha(alpha).draw(drawX - 32 + 16, drawY - texture.getHeight() + offset);
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage, Attacker attacker) {
        level.getServer().network.sendToClientsWithTile(new PacketHitObject(level, x, y, this, damage), level, x, y);
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage) {
        super.attackThrough(level, x, y, damage);
        level.makeGrassWeave(x, y, 800, false);
    }

    @Override
    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(SoundSettingsRegistry.leavesBreakAction, SoundEffect.effect(x * 32 + 16, y * 32 + 16));
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        if (this.getFruitStage(level, x, y) > 0) {
            return Localization.translate("controls", "harvesttip");
        }
        return null;
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        this.harvest(level, x, y, player);
    }

    public void harvest(Level level, int x, int y, Mob mob) {
        FruitGrowerObjectEntity ent = this.getFruitObjectEntity(level, x, y);
        if (ent != null) {
            ent.harvest(mob);
        }
        if (!level.isServer() && !level.isGrassWeaving(x, y)) {
            SoundManager.playSound(SoundSettingsRegistry.leavesBreakAction, SoundEffect.effect(x * 32 + 16, y * 32 + 16));
            level.makeGrassWeave(x, y, this.weaveTime, false);
        }
    }

    public FruitGrowerObjectEntity getFruitObjectEntity(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity instanceof FruitGrowerObjectEntity) {
            return (FruitGrowerObjectEntity)objectEntity;
        }
        return null;
    }

    public int getFruitStage(Level level, int tileX, int tileY) {
        FruitGrowerObjectEntity ent = this.getFruitObjectEntity(level, tileX, tileY);
        if (ent != null) {
            return ent.getStage();
        }
        return 0;
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new FruitGrowerObjectEntity(level, x, y, (int)(this.minGrowTimeSeconds * 1000.0f), (int)(this.maxGrowTimeSeconds * 1000.0f), this.maxStage, this.fruitStringID, this.fruitPerStage);
    }
}

