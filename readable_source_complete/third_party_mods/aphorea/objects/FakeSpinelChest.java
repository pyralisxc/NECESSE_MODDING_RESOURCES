/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.MobRegistry
 *  necesse.engine.registries.ObjectRegistry
 *  necesse.engine.world.GameClock
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.pickup.ItemPickupEntity
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.SharedTextureDrawOptions
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTooltips.GameTooltips
 *  necesse.level.gameObject.GameObject
 *  necesse.level.gameObject.ObjectDamagedTextureArray
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.objects;

import aphorea.utils.AphColors;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.world.GameClock;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FakeSpinelChest
extends GameObject {
    ObjectDamagedTextureArray texture;

    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, (String)"objects/spinelchest");
    }

    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        return rotation % 2 == 0 ? new Rectangle(x * 32 + 3, y * 32 + 6, 26, 20) : new Rectangle(x * 32 + 6, y * 32 + 4, 20, 24);
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        ObjectDamagedTextureArray usedTexture = this.texture;
        GameTexture texture = usedTexture.getDamagedTexture((GameObject)this, level, tileX, tileY);
        final SharedTextureDrawOptions draws = new SharedTextureDrawOptions(texture);
        int rotation = level.getObjectRotation(tileX, tileY) % (texture.getWidth() / 32);
        boolean treasureHunter = perspective != null && (Boolean)perspective.buffManager.getModifier(BuffModifiers.TREASURE_HUNTER) != false;
        draws.addSprite(rotation, 0, 32, texture.getHeight()).spelunkerLight(light, treasureHunter, (long)this.getID(), (GameClock)level).pos(drawX, drawY - texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                draws.draw();
            }
        });
    }

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        texture.initDraw().sprite(rotation %= texture.getWidth() / 32, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate((String)"controls", (String)"opentip");
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        this.turnIntoMimic(level, 0, x, y);
    }

    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        this.turnIntoMimic(level, layerID, x, y);
    }

    public void turnIntoMimic(Level level, int layerID, int tileX, int tileY) {
        if (level.isServer()) {
            Mob mob = MobRegistry.getMob((String)"spinelmimic", (Level)level);
            mob.setDir((int)level.objectLayer.getObjectRotation(layerID, tileX, tileY));
            level.entityManager.addMob(mob, (float)(tileX * 32 + 16), (float)(tileY * 32 + 16));
        }
        level.objectLayer.setObject(layerID, tileX, tileY, 0);
    }

    public Color getMapColor(Level level, int tileX, int tileY) {
        return AphColors.spinel;
    }

    public GameTooltips getMapTooltips(Level level, int x, int y) {
        return ObjectRegistry.getObject((String)"spinelchest").getMapTooltips(level, x, y);
    }
}

