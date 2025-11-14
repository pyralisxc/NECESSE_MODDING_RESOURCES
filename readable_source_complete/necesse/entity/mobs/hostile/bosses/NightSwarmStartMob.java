/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.util.List;
import necesse.engine.GameDifficulty;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundManager;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;

public class NightSwarmStartMob
extends FlyingBossMob {
    private int eventUniqueID;
    private boolean canTakeDamage = false;

    public NightSwarmStartMob() {
        super((Integer)NightSwarmLevelEvent.BAT_MAX_HEALTH.get(GameDifficulty.CLASSIC) * NightSwarmLevelEvent.START_BAT_COUNT);
        this.shouldSave = false;
    }

    @Override
    public void init() {
        super.init();
        if (!this.isClient()) {
            NightSwarmLevelEvent event = new NightSwarmLevelEvent(this, this.x, this.y);
            this.getLevel().entityManager.events.add(event);
            this.eventUniqueID = event.getUniqueID();
        }
    }

    @Override
    public LootTable getLootTable() {
        return NightSwarmLevelEvent.lootTable;
    }

    @Override
    public LootTable getPrivateLootTable() {
        return NightSwarmLevelEvent.privateLootTable;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isClientPlayerNearby()) {
            SoundManager.setMusic(MusicRegistry.TheSwarmoftheNight, SoundManager.MusicPriority.EVENT, 1.5f);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        LevelEvent event = this.getLevel().entityManager.events.get(this.eventUniqueID, false);
        if (!(event instanceof NightSwarmLevelEvent)) {
            this.remove();
        }
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    public void updateHealth(int currentHealth, int maxHealth) {
        this.canTakeDamage = true;
        this.setMaxHealth(maxHealth);
        this.setHealthHidden(currentHealth);
        this.canTakeDamage = false;
    }

    @Override
    public boolean isHealthBarVisible() {
        return false;
    }

    @Override
    public boolean canTakeDamage() {
        return this.canTakeDamage;
    }

    @Override
    public boolean countDamageDealt() {
        return false;
    }
}

