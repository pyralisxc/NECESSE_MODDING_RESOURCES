/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelBuffManager;

import java.util.Collections;
import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierManager;
import necesse.engine.modifiers.ModifierValue;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.ActiveLevelBuff;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class LevelBuffManager
extends ModifierManager<ActiveLevelBuff> {
    public final Level level;
    private int updateTimer;
    private boolean updateModifiers;

    public LevelBuffManager(Level level) {
        super(LevelModifiers.LIST);
        this.level = level;
        super.updateModifiers();
    }

    public void clientTick() {
        boolean update = false;
        if (this.updateModifiers || ++this.updateTimer > 20) {
            update = true;
        }
        if (update) {
            this.updateModifiers();
        }
    }

    public void serverTick() {
        boolean update = false;
        if (this.updateModifiers || ++this.updateTimer > 20) {
            update = true;
        }
        if (update) {
            this.updateModifiers();
        }
    }

    @Override
    protected void updateModifiers() {
        this.updateTimer = 0;
        this.updateModifiers = false;
        super.updateModifiers();
    }

    public void updateBuffs() {
        this.updateModifiers = true;
    }

    public void forceUpdateBuffs() {
        this.updateModifiers();
    }

    @Override
    protected Iterable<? extends ActiveLevelBuff> getModifierContainers() {
        return Collections.emptyList();
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return this.level.getDefaultLevelModifiers();
    }
}

