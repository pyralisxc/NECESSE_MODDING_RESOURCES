/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.CameraShake
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.ObjectLayerRegistry
 *  necesse.engine.registries.ObjectRegistry
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.SoundPlayer
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.objectEntity.ObjectEntity
 *  necesse.entity.objectEntity.PortalObjectEntity
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.inventory.item.toolItem.ToolType
 *  necesse.level.gameObject.GameObject
 *  necesse.level.gameObject.StaticMultiObject
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.objects;

import aphorea.utils.AphColors;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import necesse.engine.CameraShake;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BabylonEntranceObject
extends StaticMultiObject {
    protected BabylonEntranceObject(int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, "babylonentrance");
        this.mapColor = AphColors.spinel_light;
        this.displayMapTooltip = true;
        this.toolType = ToolType.UNBREAKABLE;
        this.isLightTransparent = true;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        BabylonEntranceObjectEntity oe = this.getMultiTile(level, 0, tileX, tileY).getMasterLevelObject(level, 0, tileX, tileY).map(o -> (BabylonEntranceObjectEntity)o.getCurrentObjectEntity(BabylonEntranceObjectEntity.class)).orElse(null);
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture((GameObject)this, level, tileX, tileY);
        float animationProgress = oe == null ? 1.0f : oe.getRevealAnimationProgress();
        int tileProgress = (int)((float)this.multiWidth * (1.0f - animationProgress) * 32.0f);
        int offset = Math.max(tileProgress - this.multiX * 32, 0);
        int startX = this.multiX * 32 + offset;
        int endX = startX + 32 - offset;
        drawX += offset;
        if (endX > startX) {
            TextureDrawOptionsEnd options;
            int yOffset = texture.getHeight() - this.multiHeight * 32;
            if (this.multiY == 0) {
                options = texture.initDraw().section(startX, endX, 0, 32 + yOffset).light(light).pos(drawX, drawY - yOffset);
            } else {
                int startY = this.multiY * 32 + yOffset;
                options = texture.initDraw().section(startX, endX, startY, startY + 32).light(light).pos(drawX, drawY);
            }
            tileList.add(tm -> options.draw());
        }
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate((String)"controls", (String)"usetip");
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServer() && player.isServerClient()) {
            player.getServerClient().sendChatMessage("Maybe these stairs are an idea that will never become reality... Or maybe in the future they'll lead somewhere, but it's uncertain");
        }
        super.interact(level, x, y, player);
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return this.isMultiTileMaster() ? new BabylonEntranceObjectEntity(level, x, y) : super.getNewObjectEntity(level, x, y);
    }

    public static int[] registerObject() {
        int[] ids = new int[6];
        Rectangle collision = new Rectangle(96, 64);
        ids[0] = ObjectRegistry.registerObject((String)"babylonentrance", (GameObject)new BabylonEntranceObject(0, 0, 3, 2, ids, collision), (float)0.0f, (boolean)false);
        ids[1] = ObjectRegistry.registerObject((String)"babylonentrance2", (GameObject)new BabylonEntranceObject(1, 0, 3, 2, ids, collision), (float)0.0f, (boolean)false);
        ids[2] = ObjectRegistry.registerObject((String)"babylonentrance3", (GameObject)new BabylonEntranceObject(2, 0, 3, 2, ids, collision), (float)0.0f, (boolean)false);
        ids[3] = ObjectRegistry.registerObject((String)"babylonentrance4", (GameObject)new BabylonEntranceObject(0, 1, 3, 2, ids, collision), (float)0.0f, (boolean)false);
        ids[4] = ObjectRegistry.registerObject((String)"babylonentrance5", (GameObject)new BabylonEntranceObject(1, 1, 3, 2, ids, collision), (float)0.0f, (boolean)false);
        ids[5] = ObjectRegistry.registerObject((String)"babylonentrance6", (GameObject)new BabylonEntranceObject(2, 1, 3, 2, ids, collision), (float)0.0f, (boolean)false);
        return ids;
    }

    public List<Rectangle> getProjectileCollisions(Level level, int x, int y, int rotation) {
        return Collections.emptyList();
    }

    public static class BabylonEntranceObjectEntity
    extends PortalObjectEntity {
        private long revealAnimationStartTime;
        private int revealAnimationRunTime;

        public BabylonEntranceObjectEntity(Level level, int x, int y) {
            super(level, "babylonentrance", x, y, level.getIdentifier(), 50, 50);
        }

        public void init() {
            super.init();
        }

        public void use(Server server, ServerClient client) {
        }

        public void startRevealAnimation(int runTimeMilliseconds) {
            this.revealAnimationStartTime = this.getLocalTime();
            this.revealAnimationRunTime = runTimeMilliseconds;
        }

        public float getRevealAnimationProgress() {
            if (this.revealAnimationStartTime > 0L) {
                long timeSinceStart = this.getLocalTime() - this.revealAnimationStartTime;
                float out = (float)timeSinceStart / (float)this.revealAnimationRunTime;
                if (out >= 1.0f) {
                    this.revealAnimationStartTime = 0L;
                    return 1.0f;
                }
                return out;
            }
            return 1.0f;
        }
    }

    public static class BabylonEntranceEvent
    extends LevelEvent {
        public static int ANIMATION_TIME = 10000;
        public long startTime;
        public int tileX;
        public int tileY;
        protected SoundPlayer secondStageRumble;

        public BabylonEntranceEvent() {
        }

        public BabylonEntranceEvent(int tileX, int tileY) {
            this.tileX = tileX;
            this.tileY = tileY;
        }

        public void setupSpawnPacket(PacketWriter writer) {
            super.setupSpawnPacket(writer);
            writer.putNextInt(this.tileX);
            writer.putNextInt(this.tileY);
        }

        public void applySpawnPacket(PacketReader reader) {
            super.applySpawnPacket(reader);
            this.tileX = reader.getNextInt();
            this.tileY = reader.getNextInt();
        }

        public void init() {
            super.init();
            if (this.isServer()) {
                for (int x = this.tileX - 1; x <= this.tileX + 1; ++x) {
                    for (int y = this.tileY; y <= this.tileY + 1; ++y) {
                        for (int layer = 0; layer < ObjectLayerRegistry.getTotalLayers(); ++layer) {
                            this.level.entityManager.doObjectDamage(layer, x, y, 1000000, 1000000.0f, null, null);
                        }
                    }
                }
            }
            ObjectRegistry.getObject((String)"babylonentrance").placeObject(this.level, this.tileX - 1, this.tileY, 0, false);
            ObjectEntity entity = this.level.entityManager.getObjectEntity(this.tileX - 1, this.tileY);
            if (entity instanceof BabylonEntranceObjectEntity) {
                ((BabylonEntranceObjectEntity)entity).startRevealAnimation(ANIMATION_TIME);
            }
            this.startTime = this.level.getWorldEntity().getTime();
            if (this.isClient()) {
                CameraShake cameraShake = this.level.getClient().startCameraShake((float)(this.tileX * 32 + 16), (float)(this.tileY * 32 + 16), ANIMATION_TIME, 40, 5.0f, 5.0f, true);
                cameraShake.minDistance = 200;
                cameraShake.listenDistance = 2000;
            } else {
                this.over();
            }
        }

        public void clientTick() {
            super.clientTick();
            long timeProgress = this.level.getWorldEntity().getTime() - this.startTime;
            if (timeProgress > (long)ANIMATION_TIME) {
                this.over();
            } else {
                if (this.secondStageRumble == null || this.secondStageRumble.isDone()) {
                    this.secondStageRumble = SoundManager.playSound((GameSound)GameResources.rumble, (SoundEffect)SoundEffect.effect((float)(this.tileX * 32 + 16), (float)(this.tileY * 32 + 16)).volume(4.0f).falloffDistance(2000));
                }
                if (this.secondStageRumble != null) {
                    this.secondStageRumble.refreshLooping(1.0f);
                }
                float floatProgress = Math.abs(GameMath.limit((float)((float)timeProgress / (float)ANIMATION_TIME), (float)0.0f, (float)1.0f) - 1.0f);
                int pixels = (int)(floatProgress * 32.0f * 3.0f);
                for (int i = 0; i < 4; ++i) {
                    this.level.entityManager.addParticle((float)(this.tileX * 32 - 32 + pixels) + GameRandom.globalRandom.floatGaussian() * 5.0f, (float)(this.tileY * 32) + GameRandom.globalRandom.nextFloat() * 32.0f * 2.0f, Particle.GType.IMPORTANT_COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 3.0f, GameRandom.globalRandom.floatGaussian() * 3.0f).color(AphColors.spinel_darker).heightMoves(0.0f, GameRandom.globalRandom.getFloatBetween(20.0f, 30.0f)).lifeTime(1000);
                }
            }
        }

        public Point getSaveToRegionPos() {
            return new Point(this.level.regionManager.getRegionCoordByTile(this.tileX), this.level.regionManager.getRegionCoordByTile(this.tileY));
        }
    }
}

