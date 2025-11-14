/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.window.WindowManager;
import necesse.engine.world.GameClock;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.RegionType;

public class RockObject
extends GameObject {
    protected String rockTextureName;
    String droppedStone;
    protected int minStoneAmount;
    protected int maxStoneAmount;
    protected int placedStoneAmount;
    protected ObjectDamagedTextureArray rockTextures;
    private final GameRandom oreTextureRandom;

    public RockObject(String rockTexture, Color rockColor, String droppedStone, int minStoneAmount, int maxStoneAmount, int placedStoneAmount, String ... category) {
        super(new Rectangle(0, 0, 32, 32));
        this.rockTextureName = rockTexture;
        this.mapColor = rockColor;
        this.droppedStone = droppedStone;
        this.regionType = RegionType.WALL;
        this.minStoneAmount = minStoneAmount;
        this.maxStoneAmount = maxStoneAmount;
        this.placedStoneAmount = placedStoneAmount;
        this.oreTextureRandom = new GameRandom();
        this.isRock = true;
        this.stackSize = 500;
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "landscaping", "rocksandores");
            this.setCraftingCategory("objects", "landscaping", "rocksandores");
        }
        this.replaceCategories.add("wall");
        this.canReplaceCategories.add("wall");
        this.canReplaceCategories.add("door");
        this.canReplaceCategories.add("fence");
        this.canReplaceCategories.add("fencegate");
        this.replaceRotations = false;
    }

    public RockObject(String rockTexture, Color rockColor, String droppedStone, String ... category) {
        this(rockTexture, rockColor, droppedStone, 3, 5, 4, category);
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        if (this.droppedStone != null && this.maxStoneAmount > 0) {
            return new LootTable(LootItem.between(this.droppedStone, this.minStoneAmount, this.maxStoneAmount).splitItems(5));
        }
        return new LootTable();
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        this.addRockDrawables(list, level, tileX, tileY, this.rockTextures.getDamagedTexture(this, level, tileX, tileY), null, 0L, tickManager, camera, perspective);
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        LinkedList<LevelSortedDrawable> list = new LinkedList<LevelSortedDrawable>();
        this.addRockDrawables(list, level, tileX, tileY, this.rockTextures.getDamagedTexture(0.0f), null, 0L, Float.valueOf(alpha), null, camera, player);
        list.forEach(e -> e.draw(null));
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.rockTextures = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.rockTextureName);
    }

    @Override
    public GameTexture generateItemTexture() {
        return new GameTexture(GameTexture.fromFile("items/" + this.rockTextureName));
    }

    public final void addRockDrawables(List<LevelSortedDrawable> list, Level level, int tileX, int tileY, GameTexture rockTexture, GameTexture oreTexture, long oreHash, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        this.addRockDrawables(list, level, tileX, tileY, rockTexture, oreTexture, oreHash, null, tickManager, camera, perspective);
    }

    public final void addRockDrawables(List<LevelSortedDrawable> list, Level level, int tileX, int tileY, GameTexture rockTexture, GameTexture oreTexture, long oreHash, Float fullAlpha, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Performance.record((PerformanceTimerManager)tickManager, "rockSetup", () -> {
            int r1;
            GameObject[] objAdj = level.getAdjacentObjects(tileX, tileY);
            boolean[] adj = new boolean[objAdj.length];
            for (int i = 0; i < adj.length; ++i) {
                adj[i] = objAdj[i].isRock;
            }
            float alpha = fullAlpha == null ? 1.0f : fullAlpha.floatValue();
            GameLight[] lights = level.getRelative(tileX, tileY, Level.adjacentGettersWithCenter, level::getLightLevel, GameLight[]::new);
            float topAlpha = alpha;
            if (fullAlpha == null && perspective != null && !Settings.hideUI && !Settings.hideCursor) {
                Rectangle alphaRec = new Rectangle(tileX * 32 - 16, tileY * 32 - 32, 64, 48);
                if (perspective.getCollision().intersects(alphaRec)) {
                    topAlpha = 0.5f;
                } else if (alphaRec.contains(camera.getX() + WindowManager.getWindow().mousePos().sceneX, camera.getY() + WindowManager.getWindow().mousePos().sceneY)) {
                    topAlpha = 0.5f;
                }
            }
            int randomWidth = rockTexture.getWidth() / 32;
            GameRandom gameRandom = this.oreTextureRandom;
            synchronized (gameRandom) {
                this.oreTextureRandom.setSeed(RockObject.getTileSeed(tileX, tileY) * 4621L);
                r1 = this.oreTextureRandom.nextInt(randomWidth) * 2;
            }
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            int topLeft = 0;
            int top = 1;
            int topRight = 2;
            int left = 3;
            int right = 4;
            int botLeft = 5;
            int bot = 6;
            int botRight = 7;
            final SharedTextureDrawOptions rockDraws = new SharedTextureDrawOptions(rockTexture);
            final SharedTextureDrawOptions oreDraws = oreTexture == null ? null : new SharedTextureDrawOptions(oreTexture);
            boolean hasSpelunker = false;
            GameLight[] oreLights = null;
            if (oreDraws != null) {
                hasSpelunker = perspective != null && perspective.buffManager.getModifier(BuffModifiers.SPELUNKER) != false;
                oreLights = Arrays.copyOf(lights, lights.length);
                if (this.isIncursionExtractionObject && level.isIncursionLevel) {
                    float anim = GameUtils.getAnimFloatContinuous(Math.abs(level.getTime() + 2500L * oreHash), 2500);
                    for (int i = 0; i < oreLights.length; ++i) {
                        oreLights[i] = lights[i].minLevelCopy(GameMath.lerp(anim, 80, 100));
                    }
                }
            }
            if (!adj[top]) {
                if (adj[left]) {
                    RockObject.addRockDraw(rockDraws, r1, 5, lights, topAlpha, drawX, drawY - 16, 0, -1);
                    RockObject.addOreDraw(oreDraws, r1, 5, oreLights, hasSpelunker, oreHash, level, topAlpha, drawX, drawY - 16, 0, -1);
                } else {
                    RockObject.addRockDraw(rockDraws, r1, 0, lights, topAlpha, drawX, drawY - 16, 0, -1);
                    RockObject.addOreDraw(oreDraws, r1, 0, oreLights, hasSpelunker, oreHash, level, topAlpha, drawX, drawY - 16, 0, -1);
                }
                if (adj[right]) {
                    RockObject.addRockDraw(rockDraws, r1 + 1, 5, lights, topAlpha, drawX + 16, drawY - 16, 1, -1);
                    RockObject.addOreDraw(oreDraws, r1 + 1, 5, oreLights, hasSpelunker, oreHash, level, topAlpha, drawX + 16, drawY - 16, 1, -1);
                } else {
                    RockObject.addRockDraw(rockDraws, r1 + 1, 0, lights, topAlpha, drawX + 16, drawY - 16, 1, -1);
                    RockObject.addOreDraw(oreDraws, r1 + 1, 0, oreLights, hasSpelunker, oreHash, level, topAlpha, drawX + 16, drawY - 16, 1, -1);
                }
            } else {
                if (adj[left]) {
                    if (adj[topLeft]) {
                        RockObject.addRockDraw(rockDraws, r1, 6, lights, alpha, drawX, drawY - 16, 0, -1);
                        if (objAdj[top].isOre) {
                            RockObject.addOreDraw(oreDraws, r1, 6, oreLights, hasSpelunker, oreHash, level, alpha, drawX, drawY - 16, 0, -1);
                        } else if (objAdj[topLeft].isOre) {
                            RockObject.addOreDraw(oreDraws, r1 + 1, 12, oreLights, hasSpelunker, oreHash, level, alpha, drawX, drawY - 16, 0, -1);
                        } else {
                            RockObject.addOreDraw(oreDraws, r1, 5, oreLights, hasSpelunker, oreHash, level, alpha, drawX, drawY - 16, 0, -1);
                        }
                    } else {
                        RockObject.addRockDraw(rockDraws, r1, 12, lights, alpha, drawX, drawY - 16, 0, -1);
                        RockObject.addOreDraw(oreDraws, r1, 12, oreLights, hasSpelunker, oreHash, level, alpha, drawX, drawY - 16, 0, -1);
                    }
                } else if (adj[topLeft]) {
                    RockObject.addRockDraw(rockDraws, r1, 11, lights, alpha, drawX, drawY - 16, 0, -1);
                    RockObject.addOreDraw(oreDraws, r1, 11, oreLights, hasSpelunker, oreHash, level, alpha, drawX, drawY - 16, 0, -1);
                } else {
                    RockObject.addRockDraw(rockDraws, r1, 1, lights, alpha, drawX, drawY - 16, 0, -1);
                    RockObject.addOreDraw(oreDraws, r1, 1, oreLights, hasSpelunker, oreHash, level, alpha, drawX, drawY - 16, 0, -1);
                }
                if (adj[right]) {
                    if (adj[topRight]) {
                        RockObject.addRockDraw(rockDraws, r1 + 1, 6, lights, alpha, drawX + 16, drawY - 16, 1, -1);
                        if (objAdj[top].isOre) {
                            RockObject.addOreDraw(oreDraws, r1 + 1, 6, oreLights, hasSpelunker, oreHash, level, alpha, drawX + 16, drawY - 16, 1, -1);
                        } else if (objAdj[topRight].isOre) {
                            RockObject.addOreDraw(oreDraws, r1, 12, oreLights, hasSpelunker, oreHash, level, alpha, drawX + 16, drawY - 16, 1, -1);
                        } else {
                            RockObject.addOreDraw(oreDraws, r1 + 1, 5, oreLights, hasSpelunker, oreHash, level, alpha, drawX + 16, drawY - 16, 1, -1);
                        }
                    } else {
                        RockObject.addRockDraw(rockDraws, r1 + 1, 12, lights, alpha, drawX + 16, drawY - 16, 1, -1);
                        RockObject.addOreDraw(oreDraws, r1 + 1, 12, oreLights, hasSpelunker, oreHash, level, alpha, drawX + 16, drawY - 16, 1, -1);
                    }
                } else if (adj[topRight]) {
                    RockObject.addRockDraw(rockDraws, r1 + 1, 11, lights, alpha, drawX + 16, drawY - 16, 1, -1);
                    RockObject.addOreDraw(oreDraws, r1 + 1, 11, oreLights, hasSpelunker, oreHash, level, alpha, drawX + 16, drawY - 16, 1, -1);
                } else {
                    RockObject.addRockDraw(rockDraws, r1 + 1, 1, lights, alpha, drawX + 16, drawY - 16, 1, -1);
                    RockObject.addOreDraw(oreDraws, r1 + 1, 1, oreLights, hasSpelunker, oreHash, level, alpha, drawX + 16, drawY - 16, 1, -1);
                }
            }
            if (adj[bot]) {
                if (adj[left]) {
                    if (adj[botLeft]) {
                        RockObject.addRockDraw(rockDraws, r1, 7, lights, alpha, drawX, drawY, 0, 0);
                        RockObject.addOreDraw(oreDraws, r1, 7, oreLights, hasSpelunker, oreHash, level, alpha, drawX, drawY, 0, 0);
                    } else {
                        RockObject.addRockDraw(rockDraws, r1, 10, lights, alpha, drawX, drawY, 0, 0);
                        RockObject.addOreDraw(oreDraws, r1, 10, oreLights, hasSpelunker, oreHash, level, alpha, drawX, drawY, 0, 0);
                    }
                } else if (adj[botLeft]) {
                    RockObject.addRockDraw(rockDraws, r1, 2, lights, alpha, drawX, drawY, 0, 0);
                    RockObject.addOreDraw(oreDraws, r1, 2, oreLights, hasSpelunker, oreHash, level, alpha, drawX, drawY, 0, 0);
                } else {
                    RockObject.addRockDraw(rockDraws, r1, 2, lights, alpha, drawX, drawY, 0, 0);
                    RockObject.addOreDraw(oreDraws, r1, 2, oreLights, hasSpelunker, oreHash, level, alpha, drawX, drawY, 0, 0);
                }
                if (adj[right]) {
                    if (adj[botRight]) {
                        RockObject.addRockDraw(rockDraws, r1 + 1, 7, lights, alpha, drawX + 16, drawY, 1, 0);
                        RockObject.addOreDraw(oreDraws, r1 + 1, 7, oreLights, hasSpelunker, oreHash, level, alpha, drawX + 16, drawY, 1, 0);
                    } else {
                        RockObject.addRockDraw(rockDraws, r1 + 1, 10, lights, alpha, drawX + 16, drawY, 1, 0);
                        RockObject.addOreDraw(oreDraws, r1 + 1, 10, oreLights, hasSpelunker, oreHash, level, alpha, drawX + 16, drawY, 1, 0);
                    }
                } else if (adj[botRight]) {
                    RockObject.addRockDraw(rockDraws, r1 + 1, 2, lights, alpha, drawX + 16, drawY, 1, 0);
                    RockObject.addOreDraw(oreDraws, r1 + 1, 2, oreLights, hasSpelunker, oreHash, level, alpha, drawX + 16, drawY, 1, 0);
                } else {
                    RockObject.addRockDraw(rockDraws, r1 + 1, 2, lights, alpha, drawX + 16, drawY, 1, 0);
                    RockObject.addOreDraw(oreDraws, r1 + 1, 2, oreLights, hasSpelunker, oreHash, level, alpha, drawX + 16, drawY, 1, 0);
                }
            } else {
                if (adj[left]) {
                    RockObject.addRockDraw(rockDraws, r1, 8, lights, alpha, drawX, drawY, 0, 0);
                    RockObject.addRockDraw(rockDraws, r1, 9, lights, alpha, drawX, drawY + 16, 0, 1);
                    RockObject.addOreDraw(oreDraws, r1, 8, oreLights, hasSpelunker, oreHash, level, alpha, drawX, drawY, 0, 0);
                    RockObject.addOreDraw(oreDraws, r1, 9, oreLights, hasSpelunker, oreHash, level, alpha, drawX, drawY + 16, 0, 1);
                } else {
                    RockObject.addRockDraw(rockDraws, r1, 3, lights, alpha, drawX, drawY, 0, 0);
                    RockObject.addRockDraw(rockDraws, r1, 4, lights, alpha, drawX, drawY + 16, 0, 1);
                    RockObject.addOreDraw(oreDraws, r1, 3, oreLights, hasSpelunker, oreHash, level, alpha, drawX, drawY, 0, 0);
                    RockObject.addOreDraw(oreDraws, r1, 4, oreLights, hasSpelunker, oreHash, level, alpha, drawX, drawY + 16, 0, 1);
                }
                if (adj[right]) {
                    RockObject.addRockDraw(rockDraws, r1 + 1, 8, lights, alpha, drawX + 16, drawY, 1, 0);
                    RockObject.addRockDraw(rockDraws, r1 + 1, 9, lights, alpha, drawX + 16, drawY + 16, 1, 1);
                    RockObject.addOreDraw(oreDraws, r1 + 1, 8, oreLights, hasSpelunker, oreHash, level, alpha, drawX + 16, drawY, 1, 0);
                    RockObject.addOreDraw(oreDraws, r1 + 1, 9, oreLights, hasSpelunker, oreHash, level, alpha, drawX + 16, drawY + 16, 1, 1);
                } else {
                    RockObject.addRockDraw(rockDraws, r1 + 1, 3, lights, alpha, drawX + 16, drawY, 1, 0);
                    RockObject.addRockDraw(rockDraws, r1 + 1, 4, lights, alpha, drawX + 16, drawY + 16, 1, 1);
                    RockObject.addOreDraw(oreDraws, r1 + 1, 3, oreLights, hasSpelunker, oreHash, level, alpha, drawX + 16, drawY, 1, 0);
                    RockObject.addOreDraw(oreDraws, r1 + 1, 4, oreLights, hasSpelunker, oreHash, level, alpha, drawX + 16, drawY + 16, 1, 1);
                }
            }
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return 16;
                }

                @Override
                public void draw(TickManager tickManager) {
                    Performance.record((PerformanceTimerManager)tickManager, "rockDraw", () -> {
                        rockDraws.draw();
                        if (oreDraws != null) {
                            oreDraws.draw();
                        }
                    });
                }
            });
        });
    }

    private static float[] getAdvancedLight(GameLight[] lights, float alpha, int spriteCoordX, int spriteCoordY) {
        GameLight l3;
        GameLight l2;
        GameLight l1;
        GameLight l0;
        GameLight centerLight = lights[4];
        if (spriteCoordX == 0) {
            if (spriteCoordY == -1) {
                l0 = centerLight.average(lights[0], lights[1], lights[3]);
                l1 = centerLight.mix(lights[1]);
                l2 = centerLight;
                l3 = centerLight.mix(lights[3]);
            } else if (spriteCoordY == 0) {
                l0 = centerLight.mix(lights[3]);
                l1 = centerLight;
                l2 = centerLight.mix(lights[7]);
                l3 = centerLight.average(lights[6], lights[7], lights[3]);
            } else {
                centerLight = lights[7];
                l0 = centerLight.average(lights[3], lights[4], lights[6]);
                l1 = centerLight.mix(lights[4]);
                l2 = centerLight;
                l3 = centerLight.mix(lights[6]);
            }
        } else if (spriteCoordY == -1) {
            l0 = centerLight.mix(lights[1]);
            l1 = centerLight.average(lights[2], lights[1], lights[5]);
            l2 = centerLight.mix(lights[5]);
            l3 = centerLight;
        } else if (spriteCoordY == 0) {
            l0 = centerLight;
            l1 = centerLight.mix(lights[5]);
            l2 = centerLight.average(lights[8], lights[7], lights[5]);
            l3 = centerLight.mix(lights[7]);
        } else {
            centerLight = lights[7];
            l0 = centerLight.mix(lights[4]);
            l1 = centerLight.average(lights[5], lights[4], lights[8]);
            l2 = centerLight.mix(lights[8]);
            l3 = centerLight;
        }
        return l0.getAdvColor(l1, l2, l3, alpha);
    }

    private static void addRockDraw(SharedTextureDrawOptions list, int spriteX, int spriteY, GameLight[] lights, float alpha, int drawX, int drawY, int rockCoordX, int rockCoordY) {
        if (Settings.smoothLighting) {
            list.addSprite(spriteX, spriteY, 16).advColor(RockObject.getAdvancedLight(lights, alpha, rockCoordX, rockCoordY)).pos(drawX, drawY);
        } else {
            list.addSprite(spriteX, spriteY, 16).light(lights[4]).alpha(alpha).pos(drawX, drawY);
        }
    }

    private static void addOreDraw(SharedTextureDrawOptions list, int spriteX, int spriteY, GameLight[] lights, boolean hasSpelunker, long colorHash, GameClock gameClock, float alpha, int drawX, int drawY, int rockCoordX, int rockCoordY) {
        if (list != null) {
            if (hasSpelunker) {
                list.addSprite(spriteX, spriteY, 16).spelunkerLight(lights[4], hasSpelunker, colorHash, gameClock).alpha(alpha).pos(drawX, drawY);
            } else if (Settings.smoothLighting) {
                list.addSprite(spriteX, spriteY, 16).advColor(RockObject.getAdvancedLight(lights, alpha, rockCoordX, rockCoordY)).pos(drawX, drawY);
            } else {
                list.addSprite(spriteX, spriteY, 16).light(lights[4]).alpha(alpha).pos(drawX, drawY);
            }
        }
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (level.getObject((int)x, (int)(y - 1)).isRock) {
            return new Rectangle(x * 32, y * 32, 32, 32);
        }
        return new Rectangle(x * 32, y * 32 + 4, 32, 28);
    }

    @Override
    protected ObjectHoverHitbox getHoverHitbox(Level level, int layerID, int tileX, int tileY) {
        if (level.getObject((int)tileX, (int)(tileY - 1)).isRock) {
            return new ObjectHoverHitbox(layerID, tileX, tileY, 0, -16, 32, 48);
        }
        return new ObjectHoverHitbox(layerID, tileX, tileY, 0, -10, 32, 42);
    }
}

