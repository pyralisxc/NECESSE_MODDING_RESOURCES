/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.registries.ObjectRegistry
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.DeathMessageTable
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 *  necesse.gfx.gameTexture.GameTextureSection
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.ChanceLootItem
 *  necesse.level.gameObject.GameObject
 *  necesse.level.gameTile.GrassTile$CanPlacePredicate
 *  necesse.level.gameTile.TerrainSplatterTile
 *  necesse.level.maps.Level
 *  necesse.level.maps.regionSystem.SimulatePriorityList
 */
package aphorea.tiles;

import aphorea.utils.AphColors;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GrassTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SimulatePriorityList;

public class InfectedGrassTile
extends TerrainSplatterTile {
    public static double growChance = GameMath.getAverageSuccessRuns((double)7000.0);
    public static double spreadChance = GameMath.getAverageSuccessRuns((double)850.0);
    private final GameRandom drawRandom;
    private static final Map<Integer, Long> lastHit = new HashMap<Integer, Long>();
    private static final Map<Integer, Integer> consecutiveHits = new HashMap<Integer, Integer>();
    public static Attacker INFECED_GRASS_ATTACKER = new Attacker(){

        public GameMessage getAttackerName() {
            return new StaticMessage("Infected Grass at day");
        }

        public DeathMessageTable getDeathMessages() {
            return new DeathMessageTable().add(new GameMessage[]{new LocalMessage("deaths", "default")});
        }

        public Mob getFirstAttackOwner() {
            return null;
        }
    };
    public static Map<Integer, Long> playersMessageTime = new HashMap<Integer, Long>();

    public InfectedGrassTile() {
        super(false, "infectedgrass");
        this.mapColor = AphColors.infected;
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
        this.isOrganic = true;
    }

    public LootTable getLootTable(Level level, int tileX, int tileY) {
        return new LootTable(new LootItemInterface[]{new ChanceLootItem(0.04f, "infectedgrassseed")});
    }

    public void addSimulateLogic(Level level, int x, int y, long ticks, SimulatePriorityList list, boolean sendChanges) {
        InfectedGrassTile.addSimulateGrow(level, x, y, growChance, ticks, "infectedgrass", list, sendChanges);
    }

    public static void addSimulateGrow(Level level, int tileX, int tileY, double growChance, long ticks, String growObjectID, SimulatePriorityList list, boolean sendChanges) {
        InfectedGrassTile.addSimulateGrow(level, tileX, tileY, growChance, ticks, growObjectID, (object, l, x, y, r) -> object.canPlace(l, x, y, r, false) == null, list, sendChanges);
    }

    public static void addSimulateGrow(Level level, int tileX, int tileY, double growChance, long ticks, String growObjectID, GrassTile.CanPlacePredicate canPlace, SimulatePriorityList list, boolean sendChanges) {
        GameObject obj;
        double runs;
        long remainingTicks;
        if (level.getObjectID(tileX, tileY) == 0 && (remainingTicks = (long)((double)ticks - (runs = Math.max(1.0, GameMath.getRunsForSuccess((double)growChance, (double)GameRandom.globalRandom.nextDouble()))))) > 0L && canPlace.check(obj = ObjectRegistry.getObject((int)ObjectRegistry.getObjectID((String)growObjectID)), level, tileX, tileY, 0)) {
            list.add(tileX, tileY, remainingTicks, () -> {
                if (canPlace.check(obj, level, tileX, tileY, 0)) {
                    obj.placeObject(level, tileX, tileY, 0, false);
                    level.objectLayer.setIsPlayerPlaced(tileX, tileY, false);
                    if (sendChanges) {
                        level.sendObjectUpdatePacket(tileX, tileY);
                    }
                }
            });
        }
    }

    public double spreadToDirtChance() {
        return spreadChance;
    }

    public void tick(Level level, int x, int y) {
        GameObject grass;
        if (level.isServer() && level.getObjectID(x, y) == 0 && GameRandom.globalRandom.getChance(growChance) && (grass = ObjectRegistry.getObject((int)ObjectRegistry.getObjectID((String)"infectedgrass"))).canPlace(level, x, y, 0, false) == null) {
            grass.placeObject(level, x, y, 0, false);
            level.objectLayer.setIsPlayerPlaced(x, y, false);
            level.sendObjectUpdatePacket(x, y);
        }
    }

    public void tick(Mob mob, Level level, int x, int y) {
        if (!level.getWorldEntity().isNight() && !level.isCave && level.isServer() && !mob.isHostile) {
            long currentTime;
            long now;
            PlayerMob player;
            long messageTime;
            if (mob.isPlayer && (messageTime = playersMessageTime.getOrDefault((player = (PlayerMob)mob).getUniqueID(), 0L).longValue()) + 5000L < (now = player.getTime())) {
                playersMessageTime.put(player.getUniqueID(), now);
                player.getServerClient().sendChatMessage((GameMessage)new LocalMessage("message", "infectedfieldsday"));
            }
            float damageMultiplier = 0.0f;
            long lastHitTime = lastHit.getOrDefault(mob.getID(), 0L);
            if (lastHitTime + 200L < (currentTime = level.getTime())) {
                int consecutiveHitsCount = consecutiveHits.getOrDefault(mob.getID(), 0);
                consecutiveHitsCount = lastHitTime + 300L > currentTime ? ++consecutiveHitsCount : 0;
                damageMultiplier = consecutiveHitsCount;
                lastHit.put(mob.getID(), currentTime);
                consecutiveHits.put(mob.getID(), consecutiveHitsCount);
            }
            if (damageMultiplier != 0.0f) {
                float damage = (level.isCave ? 10.0f : 5.0f) * damageMultiplier;
                mob.isServerHit(new GameDamage(DamageTypeRegistry.TRUE, damage), 0.0f, 0.0f, 0.0f, INFECED_GRASS_ATTACKER);
            }
        }
    }

    public void tickEffect(Level level, int x, int y) {
        super.tickEffect(level, x, y);
        if (GameRandom.globalRandom.getChance(0.05f) && !level.getWorldEntity().isNight() && !level.isCave && !level.getObject(x, y).drawsFullTile() && level.getLightLevel(x, y).getLevel() > 0.0f) {
            int posX = x * 32 + GameRandom.globalRandom.nextInt(32);
            int posY = y * 32 + GameRandom.globalRandom.nextInt(32);
            boolean mirror = GameRandom.globalRandom.nextBoolean();
            level.entityManager.addParticle((float)posX, (float)(posY + 30), Particle.GType.COSMETIC).sprite(GameResources.fogParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 32, 16)).fadesAlpha(0.4f, 0.4f).color(AphColors.spinel).alpha(0.4f).size((options, lifeTime, timeAlive, lifePercent) -> {}).height(30.0f).dontRotate().movesConstant(GameRandom.globalRandom.getFloatBetween(2.0f, 5.0f) * ((Float)GameRandom.globalRandom.getOneOf((Object[])new Float[]{Float.valueOf(1.0f), Float.valueOf(-1.0f)})).floatValue(), 0.0f).modify((options, lifeTime, timeAlive, lifePercent) -> options.mirror(mirror, false)).lifeTime(3000);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        int tile;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            tile = this.drawRandom.seeded(InfectedGrassTile.getTileSeed((int)tileX, (int)tileY)).nextInt(terrainTexture.getHeight() / 32);
        }
        return new Point(0, tile);
    }

    public int getTerrainPriority() {
        return 100;
    }
}

