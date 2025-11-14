/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.DifficultyBasedGetter;
import necesse.engine.GameDifficulty;
import necesse.engine.world.WorldSettings;
import necesse.entity.mobs.Mob;

public class MobDifficultyChanges {
    private final Mob mob;
    private HashMap<GameDifficulty, HashMap<String, MobDifficultyChange>> difficulties = new HashMap();
    private boolean locked;
    private GameDifficulty lastDifficulty = GameDifficulty.CLASSIC;
    protected int mobStartHealth;

    public MobDifficultyChanges(Mob mob) {
        this.mob = mob;
    }

    public void init() {
        WorldSettings settings = this.mob.getWorldSettings();
        if (settings != null) {
            this.lastDifficulty = settings.difficulty;
        }
        this.forceRunChanges(this.lastDifficulty);
        this.locked = true;
    }

    public void tick() {
        WorldSettings worldSettings = this.mob.getWorldSettings();
        if (worldSettings != null && this.lastDifficulty != worldSettings.difficulty) {
            this.forceRunChanges(worldSettings.difficulty);
            this.lastDifficulty = worldSettings.difficulty;
        }
    }

    protected void forceRunChanges(GameDifficulty newDifficulty) {
        HashMap<String, MobDifficultyChange> changes = this.difficulties.get((Object)newDifficulty);
        if (changes != null) {
            for (MobDifficultyChange change : changes.values()) {
                change.run(!this.locked, this.lastDifficulty);
            }
        }
    }

    public boolean setChange(GameDifficulty difficulty, String changeUniqueID, MobDifficultyChange runnable) {
        if (this.locked) {
            throw new IllegalStateException("Mob difficulty changes must be registered in construction");
        }
        AtomicBoolean removedPrevious = new AtomicBoolean();
        this.difficulties.compute(difficulty, (key, changes) -> {
            if (changes == null) {
                changes = new HashMap<String, MobDifficultyChange>();
            }
            removedPrevious.set(changes.put(changeUniqueID, runnable) != null);
            return changes;
        });
        return removedPrevious.get();
    }

    public boolean removeChange(GameDifficulty difficulty, String changeUniqueID) {
        if (this.locked) {
            throw new IllegalStateException("Mob difficulty changes must be registered in construction");
        }
        HashMap<String, MobDifficultyChange> changes = this.difficulties.get((Object)difficulty);
        if (changes != null) {
            MobDifficultyChange remove = changes.remove(changeUniqueID);
            if (changes.isEmpty()) {
                this.difficulties.remove((Object)difficulty);
            }
            return remove != null;
        }
        return false;
    }

    public boolean clearChange(GameDifficulty difficulty) {
        if (this.locked) {
            throw new IllegalStateException("Mob difficulty changes must be registered in construction");
        }
        return this.difficulties.remove((Object)difficulty) != null;
    }

    public boolean hasChange(GameDifficulty difficulty, String changeUniqueID) {
        HashMap<String, MobDifficultyChange> changes = this.difficulties.get((Object)difficulty);
        if (changes != null) {
            return changes.get(changeUniqueID) != null;
        }
        return false;
    }

    public boolean hasChanges(GameDifficulty difficulty) {
        HashMap<String, MobDifficultyChange> changes = this.difficulties.get((Object)difficulty);
        return changes != null && !changes.isEmpty();
    }

    public boolean setServerChange(GameDifficulty difficulty, String changeUniqueID, MobDifficultyChange runnable) {
        return this.setChange(difficulty, changeUniqueID, (init, lastDifficulty) -> {
            if (this.mob.isServer() || this.mob.getLevel() != null && !this.mob.getLevel().isLoadingComplete()) {
                runnable.run(init, lastDifficulty);
            }
        });
    }

    public boolean setClientChange(GameDifficulty difficulty, String changeUniqueID, MobDifficultyChange runnable) {
        return this.setChange(difficulty, changeUniqueID, (init, lastDifficulty) -> {
            if (!this.mob.isClient()) {
                return;
            }
            runnable.run(init, lastDifficulty);
        });
    }

    public void setMaxHealth(GameDifficulty difficulty, int maxHealth) {
        if (!this.mob.isClient()) {
            if (difficulty == GameDifficulty.CLASSIC) {
                if (!this.locked) {
                    this.mob.setMaxHealth(maxHealth);
                    this.mob.setHealth(maxHealth);
                }
            } else if (!this.hasChange(GameDifficulty.CLASSIC, "serverMaxHealth")) {
                this.setMaxHealth(GameDifficulty.CLASSIC, this.mob.getMaxHealthFlat());
            }
        }
        this.mobStartHealth = this.mob.getMaxHealthFlat();
        this.setServerChange(difficulty, "serverMaxHealth", (init, lastDifficulty) -> {
            int currentMaxHealth;
            if (init && lastDifficulty != GameDifficulty.CLASSIC) {
                this.mobStartHealth = this.mob.getMaxHealthFlat();
            }
            if (this.mobStartHealth == (currentMaxHealth = this.mob.getMaxHealthFlat()) && currentMaxHealth != maxHealth) {
                float currentPercent = this.mob.getHealthPercent();
                this.mob.setMaxHealth(maxHealth);
                this.mob.setHealth(Math.max(1, (int)((float)this.mob.getMaxHealth() * currentPercent)));
                if (!init) {
                    this.mob.sendHealthPacket(true);
                }
                this.mobStartHealth = maxHealth;
            }
        });
    }

    public void setMaxHealth(int casualMaxHealth, int adventureMaxHealth, int normalMaxHealth, int hardMaxHealth, int brutalMaxHealth) {
        this.setMaxHealth(GameDifficulty.CASUAL, casualMaxHealth);
        this.setMaxHealth(GameDifficulty.ADVENTURE, adventureMaxHealth);
        this.setMaxHealth(GameDifficulty.CLASSIC, normalMaxHealth);
        this.setMaxHealth(GameDifficulty.HARD, hardMaxHealth);
        this.setMaxHealth(GameDifficulty.BRUTAL, brutalMaxHealth);
    }

    public void setMaxHealth(DifficultyBasedGetter<Integer> difficultyGetter) {
        this.setMaxHealth(GameDifficulty.CASUAL, difficultyGetter.get(GameDifficulty.CASUAL));
        this.setMaxHealth(GameDifficulty.ADVENTURE, difficultyGetter.get(GameDifficulty.ADVENTURE));
        this.setMaxHealth(GameDifficulty.CLASSIC, difficultyGetter.get(GameDifficulty.CLASSIC));
        this.setMaxHealth(GameDifficulty.HARD, difficultyGetter.get(GameDifficulty.HARD));
        this.setMaxHealth(GameDifficulty.BRUTAL, difficultyGetter.get(GameDifficulty.BRUTAL));
    }

    @FunctionalInterface
    public static interface MobDifficultyChange {
        public void run(boolean var1, GameDifficulty var2);
    }
}

