/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.NetworkClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelBuffManager.MobBuffsEntityComponent;

public class AlchemicalInterferenceModifierLevelEvent
extends LevelEvent
implements MobBuffsEntityComponent {
    public AlchemicalInterferenceModifierLevelEvent() {
        super(true);
        this.shouldSave = true;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.updateClientBuffs();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.updateClientBuffs();
    }

    public void updateClientBuffs() {
        GameUtils.streamNetworkClients(this.level).filter(NetworkClient::hasSpawned).forEach(client -> client.playerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.ALCHEMICAL_INTERFERENCE, (Mob)client.playerMob, 1, null), false));
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public Stream<ModifierValue<?>> getLevelModifiers(Mob mob) {
        boolean affectsMob = mob.isPlayer;
        if (!affectsMob && this.getLevel().buffManager.getModifier(LevelModifiers.MODIFIERS_AFFECT_ENEMIES).booleanValue()) {
            affectsMob = mob.isHostile;
        }
        if (affectsMob) {
            return Stream.of(new ModifierValue<Float>(BuffModifiers.POTION_DURATION, Float.valueOf(0.5f)));
        }
        return Stream.empty();
    }

    @Override
    public void over() {
        super.over();
        if (this.level.isServer()) {
            this.level.getServer().streamClients().filter(client -> client.isSamePlace(this.level) && client.hasSpawned()).forEach(serverClient -> {
                if (serverClient.playerMob.buffManager.hasBuff(BuffRegistry.ALCHEMICAL_INTERFERENCE)) {
                    serverClient.playerMob.buffManager.removeBuff(BuffRegistry.ALCHEMICAL_INTERFERENCE, true);
                }
            });
        }
    }
}

