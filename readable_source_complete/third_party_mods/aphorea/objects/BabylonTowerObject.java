/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.Settings
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.journal.JournalChallenge
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.registries.JournalChallengeRegistry
 *  necesse.engine.registries.ObjectRegistry
 *  necesse.engine.util.GameUtils
 *  necesse.engine.window.WindowManager
 *  necesse.entity.TileEntity
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.objectEntity.ObjectEntity
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.inventory.item.Item
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.toolItem.ToolType
 *  necesse.level.gameObject.GameObject
 *  necesse.level.gameObject.StaticMultiObject
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.objects;

import aphorea.journal.ActivateBabylonTowerJournalChallenge;
import aphorea.mobs.bosses.BabylonTowerMob;
import aphorea.utils.AphColors;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameUtils;
import necesse.engine.window.WindowManager;
import necesse.entity.TileEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BabylonTowerObject
extends StaticMultiObject {
    protected int yOffset = -3;

    public BabylonTowerObject(int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, "babylontower");
        this.stackSize = 1;
        this.rarity = Item.Rarity.LEGENDARY;
        this.mapColor = AphColors.spinel;
        this.objectHealth = Integer.MAX_VALUE;
        this.toolType = ToolType.UNBREAKABLE;
        this.isLightTransparent = false;
        this.hoverHitbox = new Rectangle(0, 0, 32, 32);
        this.setItemCategory(new String[]{"objects", "misc"});
        this.setCraftingCategory(new String[]{"objects", "misc"});
    }

    public static void registerObject() {
        int[] ids = new int[6];
        Rectangle collision = new Rectangle(0, 0, 96, 64);
        ids[0] = ObjectRegistry.registerObject((String)"babylontower", (GameObject)new BabylonTowerObject(0, 0, 3, 2, ids, collision), (float)0.0f, (boolean)false);
        ids[1] = ObjectRegistry.registerObject((String)"babylontower2", (GameObject)new BabylonTowerObject(1, 0, 3, 2, ids, collision), (float)0.0f, (boolean)false);
        ids[2] = ObjectRegistry.registerObject((String)"babylontower3", (GameObject)new BabylonTowerObject(2, 0, 3, 2, ids, collision), (float)0.0f, (boolean)false);
        ids[3] = ObjectRegistry.registerObject((String)"babylontower4", (GameObject)new BabylonTowerObject(0, 1, 3, 2, ids, collision), (float)0.0f, (boolean)false);
        ids[4] = ObjectRegistry.registerObject((String)"babylontower5", (GameObject)new BabylonTowerObject(1, 1, 3, 2, ids, collision), (float)0.0f, (boolean)false);
        ids[5] = ObjectRegistry.registerObject((String)"babylontower6", (GameObject)new BabylonTowerObject(2, 1, 3, 2, ids, collision), (float)0.0f, (boolean)false);
    }

    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        super.tickEffect(level, layerID, tileX, tileY);
        if (this.isActive(level, tileX, tileY)) {
            level.lightManager.refreshParticleLightFloat((float)(tileX * 32 - 16), (float)(tileY * 32 - 16), AphColors.spinel, 1.0f, 200);
        }
    }

    public boolean isActive(Level level, int tileX, int tileY) {
        GameObject object = level.getObject(tileX - this.multiX, tileY - this.multiY);
        return object != null && object.getCurrentObjectEntity(level, tileX - this.multiX, tileY - this.multiY) instanceof BabylonTowerObjectEntity;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.multiY == 0) {
            DrawOptions[] options;
            float alpha = 1.0f;
            if (perspective != null && !Settings.hideUI && !Settings.hideCursor) {
                Rectangle alphaRec = new Rectangle(tileX * 32 - 32, tileY * 32 - 128, 96, 128);
                if (perspective.getCollision().intersects(alphaRec)) {
                    alpha = 0.5f;
                } else if (alphaRec.contains(camera.getX() + WindowManager.getWindow().mousePos().sceneX, camera.getY() + WindowManager.getWindow().mousePos().sceneY)) {
                    alpha = 0.5f;
                }
            }
            GameTexture texture = this.texture.getDamagedTexture((GameObject)this, level, tileX, tileY);
            for (final DrawOptions drawOptions : options = this.getMultiTextureDrawOptionsCustom(texture, level, tileX, tileY, camera, alpha)) {
                list.add(new LevelSortedDrawable((GameObject)this, tileX, tileY){

                    public int getSortY() {
                        return 16;
                    }

                    public void draw(TickManager tickManager) {
                        drawOptions.draw();
                    }
                });
            }
        }
    }

    protected DrawOptions[] getMultiTextureDrawOptionsCustom(GameTexture texture, Level level, int tileX, int tileY, GameCamera camera, float alpha) {
        return this.getMultiTextureDrawOptionsCustom(new GameSprite(texture), level, tileX, tileY, camera, alpha);
    }

    protected DrawOptions[] getMultiTextureDrawOptionsCustom(GameSprite sprite, Level level, int tileX, int tileY, GameCamera camera, float alpha) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int startX = this.multiX * 32;
        ArrayList<TextureDrawOptionsEnd> drawOptions = new ArrayList<TextureDrawOptionsEnd>();
        int parts = this.getParts(level, tileX, tileY);
        int startHeight = drawY - 80 - 20 * parts + 64;
        drawOptions.add(sprite.initDrawSection(startX, startX + 32, 0, 40, false).alpha(alpha).size(32, 40).light(light).pos(drawX, startHeight));
        if (this.isActive(level, tileX, tileY)) {
            drawOptions.add(sprite.initDrawSection(startX, startX + 32, 100, 140, false).alpha(alpha).size(32, 40).light(light).pos(drawX, startHeight + 40));
        } else {
            drawOptions.add(sprite.initDrawSection(startX, startX + 32, 40, 80, false).alpha(alpha).size(32, 40).light(light).pos(drawX, startHeight + 40));
        }
        for (int i = 0; i < parts; ++i) {
            drawOptions.add(sprite.initDrawSection(startX, startX + 32, 160, 180, false).alpha(alpha).size(32, 20).light(light).pos(drawX, startHeight + 80 + 20 * i));
        }
        return drawOptions.toArray(new DrawOptions[0]);
    }

    public int getParts(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = this.getCurrentObjectEntity(level, tileX - this.multiX, tileY - this.multiY);
        if (objectEntity instanceof BabylonTowerObjectEntity) {
            BabylonTowerObjectEntity babylonTowerObjectEntity = (BabylonTowerObjectEntity)objectEntity;
            return babylonTowerObjectEntity.getMob() == null ? 4 : babylonTowerObjectEntity.getMob().getParts();
        }
        return 4;
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return !this.isActive(level, x, y);
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        if (this.isActive(level, x, y)) {
            return null;
        }
        return Localization.translate((String)"controls", (String)"activatetip");
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        Item item = ItemRegistry.getItem((String)"lifespinel");
        if (!player.isItemOnCooldown(item) && player.getInv().removeItems(item, 1, false, false, false, false, "use") > 0) {
            JournalChallenge challenge;
            level.entityManager.objectEntities.add((TileEntity)new BabylonTowerObjectEntity(level, x - this.multiX, y - this.multiY));
            if (player.isServer() && (challenge = JournalChallengeRegistry.getChallenge((String)"activatebabylontower")) instanceof ActivateBabylonTowerJournalChallenge) {
                ((ActivateBabylonTowerJournalChallenge)challenge).onBabylonTowerActivated(player.getServerClient());
                GameUtils.streamServerClients((Level)level).filter(m -> m.playerMob != player && m.playerMob.getDistance((float)x, (float)y) <= (float)BabylonTowerMob.BOSS_AREA_RADIUS).forEach(serverClient -> ((ActivateBabylonTowerJournalChallenge)challenge).onBabylonTowerActivated((ServerClient)serverClient));
            }
        }
    }

    public List<Rectangle> getProjectileCollisions(Level level, int x, int y, int rotation) {
        return this.isActive(level, x, y) ? Collections.emptyList() : super.getProjectileCollisions(level, x, y, rotation);
    }

    public static class BabylonTowerObjectEntity
    extends ObjectEntity {
        private int bossID = -1;

        public BabylonTowerObjectEntity(Level level, int x, int y) {
            super(level, "babylontower", x, y);
        }

        public float getMobX() {
            return this.tileX * 32 + 48;
        }

        public float getMobY() {
            return this.tileY * 32 + 32;
        }

        public void setupContentPacket(PacketWriter writer) {
            if (this.bossID == -1) {
                this.generateMobID();
            }
            writer.putNextInt(this.bossID);
        }

        public void applyContentPacket(PacketReader reader) {
            this.bossID = reader.getNextInt();
        }

        public void clientTick() {
            super.clientTick();
            BabylonTowerMob m = this.getMob();
            if (m != null) {
                m.keepAlive(this);
            }
            this.checkLeave(false);
        }

        public void serverTick() {
            super.serverTick();
            BabylonTowerMob m = this.getMob();
            if (m == null) {
                m = this.generateMobID();
                this.markDirty();
            }
            m.keepAlive(this);
            this.checkLeave(true);
        }

        public void checkLeave(boolean heal) {
            if (this.getMob() == null || this.getMob().removed()) {
                return;
            }
            boolean noPlayersNearby = this.getLevel().entityManager.players.streamArea(this.getMobX(), this.getMobY(), BabylonTowerMob.BOSS_AREA_RADIUS).noneMatch(p -> p.getDistance(this.getMobX(), this.getMobY()) < (float)BabylonTowerMob.BOSS_AREA_RADIUS);
            if (noPlayersNearby) {
                if (this.getMob().getHealthPercent() == 1.0f) {
                    this.getMob().remove();
                    this.remove();
                } else if (heal) {
                    this.getMob().setHealth((int)((float)this.getMob().getHealth() + (float)this.getMob().getMaxHealth() * 0.01f));
                }
            }
        }

        private BabylonTowerMob generateMobID() {
            BabylonTowerMob lastMob = this.getMob();
            if (lastMob != null) {
                lastMob.remove();
            }
            BabylonTowerMob m = new BabylonTowerMob();
            this.getLevel().entityManager.addMob((Mob)m, this.getMobX(), this.getMobY());
            this.bossID = m.getUniqueID();
            return m;
        }

        private BabylonTowerMob getMob() {
            if (this.bossID == -1) {
                return null;
            }
            Mob m = (Mob)this.getLevel().entityManager.mobs.get(this.bossID, false);
            return m != null ? (BabylonTowerMob)m : null;
        }

        public void remove() {
            super.remove();
            BabylonTowerMob m = this.getMob();
            if (m != null) {
                m.remove();
            }
        }
    }
}

