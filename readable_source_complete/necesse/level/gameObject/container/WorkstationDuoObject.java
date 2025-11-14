/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.gameObject.container.CraftingStationObject;
import necesse.level.gameObject.container.CraftingStationUpgrade;
import necesse.level.gameObject.container.WorkstationDuo2Object;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;

public class WorkstationDuoObject
extends CraftingStationObject {
    public ObjectDamagedTextureArray texture;
    protected int counterID;

    protected WorkstationDuoObject() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(132, 91, 25);
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "workstation");
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/workstationduo");
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(0, 1, 1, 2, rotation, true, this.counterID, this.getID());
    }

    @Override
    public ArrayList<ObjectPlaceOption> getPlaceOptions(Level level, int levelX, int levelY, PlayerMob playerMob, int playerDir, boolean offsetMultiTile) {
        return super.getPlaceOptions(level, levelX, levelY, playerMob, Math.floorMod(playerDir - 1, 4), offsetMultiTile);
    }

    @Override
    public CraftingStationUpgrade getStationUpgrade() {
        return new CraftingStationUpgrade(ObjectRegistry.getObject("demonicworkstationduo"), new Ingredient("demonicbar", 5));
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 4, y * 32, 24, 26);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32 + 6, y * 32 + 6, 26, 20);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 4, y * 32 + 4, 24, 28);
        }
        return new Rectangle(x * 32, y * 32 + 6, 26, 20);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final DrawOptionsList options = new DrawOptionsList();
        if (rotation == 0) {
            options.add(texture.initDraw().sprite(0, 2, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 1) {
            options.add(texture.initDraw().sprite(0, 5, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(0, 6, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 2) {
            options.add(texture.initDraw().sprite(1, 0, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(1, 1, 32).light(light).pos(drawX, drawY));
        } else {
            options.add(texture.initDraw().sprite(1, 3, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(1, 4, 32).light(light).pos(drawX, drawY));
        }
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
        if (rotation == 0) {
            texture.initDraw().sprite(0, 2, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(0, 0, 32).alpha(alpha).draw(drawX, drawY - 64);
            texture.initDraw().sprite(0, 1, 32).alpha(alpha).draw(drawX, drawY - 32);
        } else if (rotation == 1) {
            texture.initDraw().sprite(0, 5, 32).alpha(alpha).draw(drawX, drawY - 32);
            texture.initDraw().sprite(0, 6, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(1, 5, 32).alpha(alpha).draw(drawX + 32, drawY - 32);
            texture.initDraw().sprite(1, 6, 32).alpha(alpha).draw(drawX + 32, drawY);
        } else if (rotation == 2) {
            texture.initDraw().sprite(1, 0, 32).alpha(alpha).draw(drawX, drawY - 32);
            texture.initDraw().sprite(1, 1, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(1, 2, 32).alpha(alpha).draw(drawX, drawY + 32);
        } else {
            texture.initDraw().sprite(1, 3, 32).alpha(alpha).draw(drawX, drawY - 32);
            texture.initDraw().sprite(1, 4, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(0, 3, 32).alpha(alpha).draw(drawX - 32, drawY - 32);
            texture.initDraw().sprite(0, 4, 32).alpha(alpha).draw(drawX - 32, drawY);
        }
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        if (level.isClient() && level.getClient().getPlayer() == player) {
            level.getClient().tutorial.usedWorkstation();
        }
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.WORKSTATION};
    }

    public static int[] registerWorkstation() {
        int i2;
        WorkstationDuoObject o1 = new WorkstationDuoObject();
        WorkstationDuo2Object o2 = new WorkstationDuo2Object();
        int i1 = ObjectRegistry.registerObject("workstationduo", o1, 10.0f, true);
        o1.counterID = i2 = ObjectRegistry.registerObject("workstationduo2", o2, 0.0f, false);
        o2.counterID = i1;
        return new int[]{i1, i2};
    }

    @Override
    protected SoundSettings getInteractSoundOpen() {
        return SoundSettingsRegistry.workstationOpen;
    }
}

