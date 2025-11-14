/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.MobHealthChangeListenerEntityComponent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.MobBuffsEntityComponent;

public class DoubleBossChanceLevelEvent
extends LevelEvent
implements MobHealthChangeListenerEntityComponent,
MobBuffsEntityComponent {
    public static ArrayList<SpawnBossFunction> possibleBossSpawns = new ArrayList();
    public HashSet<Integer> triggeredOnBossUniqueIDs = new HashSet();

    public DoubleBossChanceLevelEvent() {
        super(true);
        this.shouldSave = true;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        if (!this.triggeredOnBossUniqueIDs.isEmpty()) {
            save.addIntCollection("triggeredOnBossUniqueIDs", this.triggeredOnBossUniqueIDs);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.triggeredOnBossUniqueIDs = new HashSet<Integer>(save.getIntCollection("triggeredOnBossUniqueIDs", new ArrayList<Integer>(), false));
    }

    @Override
    public void clientTick() {
        PlayerMob player;
        super.clientTick();
        if (this.isClient() && (player = this.getClient().getPlayer()) != null && player.buffManager.hasBuff(BuffRegistry.BOSS_NEARBY)) {
            player.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SECOND_BOSS_WATCHING, (Mob)player, 1.0f, null), false);
        }
    }

    @Override
    public Stream<ModifierValue<?>> getLevelModifiers(Mob mob) {
        if (mob.isSecondaryIncursionBoss) {
            return Stream.of(new ModifierValue<Float>(BuffModifiers.MAX_HEALTH, Float.valueOf(-0.5f)));
        }
        return Stream.empty();
    }

    @Override
    public void onLevelMobHealthChanged(Mob mob, int beforeHealth, int health, float knockbackX, float knockbackY, Attacker attacker) {
        float healthPercent;
        if (!mob.isClient() && mob.isBoss() && !mob.isSecondaryIncursionBoss() && mob.dropsLoot() && !this.triggeredOnBossUniqueIDs.contains(mob.getUniqueID()) && (healthPercent = (float)health / (float)mob.getMaxHealth()) <= 0.25f) {
            SpawnBossFunction spawner = GameRandom.globalRandom.getOneOf(possibleBossSpawns);
            spawner.spawnBoss(mob);
            this.triggeredOnBossUniqueIDs.add(mob.getUniqueID());
        }
    }

    protected static Point getRandomNearbyPosition(Mob mob, float distance) {
        int angle = GameRandom.globalRandom.nextInt(360);
        Point2D.Float dir = GameMath.getAngleDir(angle);
        int newX = (int)((float)mob.getX() + dir.x * distance);
        int newY = (int)((float)mob.getY() + dir.y * distance);
        return new Point(newX, newY);
    }

    static {
        possibleBossSpawns.add(new SimpleSpawnBossFunction("reaper"));
        possibleBossSpawns.add(new SimpleSpawnBossFunction("cryoqueen"));
        possibleBossSpawns.add(new SimpleSpawnBossFunction("pestwarden"));
        possibleBossSpawns.add(new SimpleSpawnBossFunction("sageandgrit"));
        possibleBossSpawns.add(new SimpleSpawnBossFunction("motherslime"));
        possibleBossSpawns.add(new SimpleSpawnBossFunction("nightswarm"));
        possibleBossSpawns.add(new SimpleSpawnBossFunction("spiderempress"));
        possibleBossSpawns.add(new SimpleSpawnBossFunction("sunlightchampion"));
        possibleBossSpawns.add(new SimpleSpawnBossFunction("moonlightdancer"));
        possibleBossSpawns.add(new SimpleSpawnBossFunction("crystaldragon"));
    }

    public static interface SpawnBossFunction {
        public void spawnBoss(Mob var1);
    }

    public static class SimpleSpawnBossFunction
    implements SpawnBossFunction {
        public String bossStringID;

        public SimpleSpawnBossFunction(String bossStringID) {
            this.bossStringID = bossStringID;
        }

        @Override
        public void spawnBoss(Mob originalBoss) {
            Level level = originalBoss.getLevel();
            Mob mob = MobRegistry.getMob(this.bossStringID, level);
            mob.isSecondaryIncursionBoss = true;
            mob.buffManager.forceUpdateBuffs();
            mob.onSpawned(originalBoss.getX(), originalBoss.getY());
            level.entityManager.mobs.add(mob);
        }
    }
}

