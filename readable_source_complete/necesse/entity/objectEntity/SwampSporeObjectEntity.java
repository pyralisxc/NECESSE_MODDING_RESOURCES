/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashSet;
import necesse.engine.GameTileRange;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.SwampSporeDummyMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SwampSporeObject;
import necesse.level.maps.Level;

public class SwampSporeObjectEntity
extends ObjectEntity {
    public static GameTileRange range = new GameTileRange(12, new Point[0]);
    private static long lastGameTick;
    private static final HashSet<Point> handledEffectTiles;
    private final ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC);
    private int dummyMobID = -1;
    private long lastBurstTime = 0L;

    public SwampSporeObjectEntity(Level level, int x, int y) {
        super(level, "swampspore", x, y);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        if (this.dummyMobID == -1) {
            this.generateMobID();
        }
        writer.putNextInt(this.dummyMobID);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.dummyMobID = reader.getNextInt();
    }

    @Override
    public void clientTick() {
        long currentTick;
        super.clientTick();
        this.tickBuffs();
        SwampSporeDummyMob m = this.getMob();
        if (m != null) {
            m.keepAlive(this);
        }
        if (lastGameTick != (currentTick = this.getLevel().tickManager().getTotalTicks())) {
            handledEffectTiles.clear();
            lastGameTick = currentTick;
        }
        for (Point tile : range.getValidTiles(this.tileX, this.tileY)) {
            if (handledEffectTiles.contains(tile)) continue;
            handledEffectTiles.add(tile);
            if (!GameRandom.globalRandom.getChance(0.02f) || !this.getLevel().entityManager.isParticlesAllowed(tile.x * 32 + 16, tile.y * 32 + 16) || this.getLevel().getObject(tile.x, tile.y).drawsFullTile() || this.getLevel().getLightLevel(tile.x, tile.y).getLevel() <= 0.0f) continue;
            int posX = tile.x * 32 - 16 + GameRandom.globalRandom.nextInt(32);
            int posY = tile.y * 32 - 16 + GameRandom.globalRandom.nextInt(32);
            final int spriteOffset = GameRandom.globalRandom.nextInt(2000);
            boolean mirror = GameRandom.globalRandom.nextBoolean();
            this.getLevel().entityManager.addTopParticle(posX, posY + 30, this.typeSwitcher.next()).sprite(new ParticleOption.SpriteSelector(){

                @Override
                public SharedTextureDrawOptions.Wrapper get(SharedTextureDrawOptions options, int lifeTime, int timeAlive, float lifePercent) {
                    int sprite = GameUtils.getAnim(SwampSporeObjectEntity.this.getLocalTime() + (long)spriteOffset, 6, 1000);
                    return options.add(GameResources.poisonCloudParticles.sprite(sprite, 0, 64, 64));
                }
            }).fadesAlpha(0.4f, 0.4f).color(new Color(105, 39, 110)).size((options, lifeTime, timeAlive, lifePercent) -> {}).height(30.0f).dontRotate().movesConstant(GameRandom.globalRandom.getFloatBetween(2.0f, 4.0f) * GameRandom.globalRandom.getOneOf(Float.valueOf(1.0f), Float.valueOf(-1.0f)).floatValue(), 0.0f).modify((options, lifeTime, timeAlive, lifePercent) -> options.mirror(mirror, false)).lifeTime(2000);
        }
        long currentTime = this.getTime() + GameObject.getTileSeed(this.tileX, this.tileY, 52);
        long burstTimeSinceTimeStart = currentTime / (long)SwampSporeObject.totalFrameTime;
        long lastBurstTime = burstTimeSinceTimeStart * (long)SwampSporeObject.totalFrameTime;
        long nextBurstTime = lastBurstTime + (long)SwampSporeObject.burstTime;
        if (this.lastBurstTime != lastBurstTime && nextBurstTime <= currentTime) {
            this.lastBurstTime = lastBurstTime;
            for (int i = 0; i < 20; ++i) {
                int posX = this.tileX * 32 + GameRandom.globalRandom.nextInt(32);
                int posY = this.tileY * 32 + GameRandom.globalRandom.nextInt(32);
                final int spriteOffset = GameRandom.globalRandom.nextInt(2000);
                boolean mirror = GameRandom.globalRandom.nextBoolean();
                float moveAngle = GameRandom.globalRandom.getFloatBetween(0.0f, 360.0f);
                Point2D.Float moveDir = GameMath.getAngleDir(moveAngle);
                this.getLevel().entityManager.addTopParticle(posX, posY + 10, this.typeSwitcher.next()).sprite(new ParticleOption.SpriteSelector(){

                    @Override
                    public SharedTextureDrawOptions.Wrapper get(SharedTextureDrawOptions options, int lifeTime, int timeAlive, float lifePercent) {
                        int sprite = GameUtils.getAnim(SwampSporeObjectEntity.this.getLocalTime() + (long)spriteOffset, 6, 1000);
                        return options.add(GameResources.poisonCloudParticles.sprite(sprite, 0, 64, 64));
                    }
                }).fadesAlpha(0.1f, 0.4f).color(new Color(144, 36, 151)).sizeFadesInAndOut(64, 64, 250, 0).height(30.0f).movesFriction(moveDir.x * GameRandom.globalRandom.nextFloat() * 70.0f, moveDir.y * GameRandom.globalRandom.nextFloat() * 70.0f, 0.2f).modify((options, lifeTime, timeAlive, lifePercent) -> options.mirror(mirror, false)).lifeTime(3000);
            }
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.tickBuffs();
        SwampSporeDummyMob m = this.getMob();
        if (m == null) {
            m = this.generateMobID();
            this.markDirty();
        }
        m.keepAlive(this);
    }

    private SwampSporeDummyMob generateMobID() {
        SwampSporeDummyMob lastMob = this.getMob();
        if (lastMob != null) {
            lastMob.remove();
        }
        SwampSporeDummyMob m = new SwampSporeDummyMob();
        this.getLevel().entityManager.addMob(m, this.tileX * 32 + 16, this.tileY * 32 + 16);
        this.dummyMobID = m.getUniqueID();
        return m;
    }

    private SwampSporeDummyMob getMob() {
        if (this.dummyMobID == -1) {
            return null;
        }
        Mob m = this.getLevel().entityManager.mobs.get(this.dummyMobID, false);
        if (m != null) {
            return (SwampSporeDummyMob)m;
        }
        return null;
    }

    @Override
    public void remove() {
        super.remove();
        SwampSporeDummyMob m = this.getMob();
        if (m != null) {
            m.remove();
        }
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        if (debug) {
            GameTooltipManager.addTooltip(new StringTooltips("MobID: " + this.dummyMobID), TooltipLocation.INTERACT_FOCUS);
        }
    }

    public boolean appliesBuffsToSettlers() {
        return true;
    }

    public void applyBuffs(Mob mob) {
        int cooldown = 100;
        mob.startGenericCooldown("swampspores", cooldown);
        ActiveBuff ab = new ActiveBuff(BuffRegistry.SWAMP_SPORES, mob, cooldown * 2, null);
        mob.buffManager.addBuff(ab, false);
    }

    public void tickBuffs() {
        if (this.getLevel().objectLayer.isPlayerPlaced(this.tileX, this.tileY)) {
            return;
        }
        this.getLevel().entityManager.players.streamInRegionsInTileRange(this.tileX * 32 + 16, this.tileY * 32 + 16, SwampSporeObjectEntity.range.maxRange + 2).filter(p -> range.isWithinRange(this.tileX, this.tileY, p.getTileX(), p.getTileY())).filter(p -> !p.isOnGenericCooldown("swampspores")).forEach(this::applyBuffs);
        if (this.appliesBuffsToSettlers()) {
            this.getLevel().entityManager.mobs.streamInRegionsInTileRange(this.tileX * 32 + 16, this.tileY * 32 + 16, SwampSporeObjectEntity.range.maxRange + 2).filter(m -> m.isHuman).filter(p -> range.isWithinRange(this.tileX, this.tileY, p.getTileX(), p.getTileY())).filter(p -> !p.isOnGenericCooldown("swampspores")).forEach(this::applyBuffs);
        }
    }

    static {
        handledEffectTiles = new HashSet();
    }
}

