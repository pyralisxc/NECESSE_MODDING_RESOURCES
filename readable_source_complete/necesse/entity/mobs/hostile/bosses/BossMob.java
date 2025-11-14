/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.level.maps.Level;

public class BossMob
extends HostileMob {
    public static int BOSS_MULTIPLAYER_RESPAWN_TIME = 20000;
    public static int BOSS_SINGLEPLAYER_RESPAWN_TIME = 6000;

    public static int getBossRespawnTime(Mob mob) {
        Level level = mob.getLevel();
        if (level.isServer() && level.getServer().getPlayersOnline() <= 1) {
            return BOSS_SINGLEPLAYER_RESPAWN_TIME;
        }
        if (level.isClient() && level.getClient().streamClients().count() <= 1L) {
            return BOSS_SINGLEPLAYER_RESPAWN_TIME;
        }
        return BOSS_MULTIPLAYER_RESPAWN_TIME;
    }

    public BossMob(int health) {
        super(health);
        this.canDespawn = false;
        this.shouldSave = false;
    }

    @Override
    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        return false;
    }

    @Override
    public int getRespawnTime() {
        return BossMob.getBossRespawnTime(this);
    }
}

