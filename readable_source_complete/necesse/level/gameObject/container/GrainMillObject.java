/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.GrainMillObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.ProcessingTechInventoryObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.GrainMillExtraObject;
import necesse.level.gameObject.container.GrainMillObject2;
import necesse.level.gameObject.container.GrainMillObject3;
import necesse.level.gameObject.container.GrainMillObject4;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

public class GrainMillObject
extends GrainMillExtraObject
implements SettlementWorkstationObject {
    public ObjectDamagedTextureArray texture;
    public GameTexture bladeTexture;
    protected int counterIDTopRight;
    protected int counterIDBotLeft;
    protected int counterIDBotRight;

    protected GrainMillObject() {
    }

    @Override
    protected Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 5, y * 32 + 12, 27, 20);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32, y * 32 + 12, 27, 20);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32, y * 32, 27, 22);
        }
        return new Rectangle(x * 32 + 5, y * 32, 27, 22);
    }

    @Override
    protected void setCounterIDs(int id1, int id2, int id3, int id4) {
        this.counterIDTopRight = id2;
        this.counterIDBotLeft = id3;
        this.counterIDBotRight = id4;
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(0, 0, 2, 2, rotation, true, this.getID(), this.counterIDTopRight, this.counterIDBotLeft, this.counterIDBotRight);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/grainmill");
        this.bladeTexture = GameTexture.fromFile("objects/grainmillblade");
    }

    public GrainMillObjectEntity getGrainMillObjectEntity(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity instanceof GrainMillObjectEntity) {
            return (GrainMillObjectEntity)objectEntity;
        }
        return null;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        byte rotation;
        GameLight light = level.getLightLevel(tileX, tileY);
        float angle = 0.0f;
        GrainMillObjectEntity grainMill = this.getGrainMillObjectEntity(level, tileX, tileY);
        if (grainMill != null) {
            angle = grainMill.bladeRotation;
        }
        if ((rotation = level.getObjectRotation(tileX, tileY)) == 1) {
            --tileX;
        } else if (rotation == 2) {
            --tileX;
            --tileY;
        } else if (rotation == 3) {
            --tileY;
        }
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final TextureDrawOptionsEnd options = texture.initDraw().light(light).pos(drawX, drawY - (texture.getHeight() - 64));
        final TextureDrawOptionsEnd bladeOptions = this.bladeTexture.initDraw().light(light).rotate(angle, this.bladeTexture.getWidth() / 2, this.bladeTexture.getHeight() / 2).posMiddle(drawX + texture.getWidth() / 2, drawY - 16);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 40;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
                bladeOptions.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        if (rotation == 1) {
            --tileX;
        } else if (rotation == 2) {
            --tileX;
            --tileY;
        } else if (rotation == 3) {
            --tileY;
        }
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        texture.initDraw().alpha(alpha).draw(drawX, drawY - (texture.getHeight() - 64));
        this.bladeTexture.initDraw().alpha(alpha).posMiddle(drawX + texture.getWidth() / 2, drawY - 16).draw();
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
            return processingOE.getExpectedResults().crafts < 40;
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

    public static int[] registerGrainMill() {
        GrainMillObject o1 = new GrainMillObject();
        int id1 = ObjectRegistry.registerObject("grainmill", o1, 10.0f, true);
        GrainMillObject2 o2 = new GrainMillObject2();
        int id2 = ObjectRegistry.registerObject("grainmill2", o2, 0.0f, false);
        GrainMillObject3 o3 = new GrainMillObject3();
        int id3 = ObjectRegistry.registerObject("grainmill3", o3, 0.0f, false);
        GrainMillObject4 o4 = new GrainMillObject4();
        int id4 = ObjectRegistry.registerObject("grainmill4", o4, 0.0f, false);
        o1.setCounterIDs(id1, id2, id3, id4);
        o2.setCounterIDs(id1, id2, id3, id4);
        o3.setCounterIDs(id1, id2, id3, id4);
        o4.setCounterIDs(id1, id2, id3, id4);
        return new int[]{id1, id2, id3, id4};
    }
}

