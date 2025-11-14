/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;

public class MobHealthScaling {
    private final Mob owner;
    private final float playerPercentStart;
    private final float playerPercentDec;
    private final float partyPercentStart;
    private final float partyPercentDec;
    private int startMaxHealth;
    private int healthIncrease;
    private int lastPlayers;
    private int lastPartyMembers;
    public static final int PLAYER_AFFECTS_HEALTH_WITHIN_TILE_RANGE = 50;

    public MobHealthScaling(Mob owner, float playerPercentStart, float playerPercentDecreasePer, float partyPercentStart, float partyPercentDecreasePer) {
        this.owner = owner;
        this.startMaxHealth = owner.getMaxHealthFlat();
        this.playerPercentStart = playerPercentStart;
        this.playerPercentDec = playerPercentDecreasePer;
        this.partyPercentStart = partyPercentStart;
        this.partyPercentDec = partyPercentDecreasePer;
    }

    public MobHealthScaling(Mob owner) {
        this(owner, 0.8f, 0.04f, 0.2f, 0.02f);
    }

    public void serverTick() {
        Level level = this.owner.getLevel();
        if (this.owner == null || level == null) {
            return;
        }
        if (!this.owner.isServer()) {
            return;
        }
        PlayerCount nextCount = MobHealthScaling.getPlayerCount(this.owner);
        if (this.lastPlayers < nextCount.players || this.lastPartyMembers != nextCount.partyMembers || this.startMaxHealth != this.owner.getMaxHealthFlat() || nextCount.players != this.lastPlayers && this.owner.getHealth() >= this.owner.getMaxHealth() && !this.owner.isInCombat()) {
            this.lastPlayers = this.owner.getHealth() < this.owner.getMaxHealth() ? Math.max(this.lastPlayers, nextCount.players) : nextCount.players;
            this.lastPartyMembers = nextCount.partyMembers;
            this.startMaxHealth = this.owner.getMaxHealthFlat();
            this.updateHealthIncrease(true);
            this.owner.sendHealthPacket(false);
        }
    }

    public void setupHealthPacket(PacketWriter writer, boolean isFull) {
        writer.putNextByteUnsigned(this.lastPlayers);
        writer.putNextShortUnsigned(this.lastPartyMembers);
    }

    public void applyHealthPacket(PacketReader reader, boolean isFull) {
        this.lastPlayers = reader.getNextByteUnsigned();
        this.lastPartyMembers = reader.getNextShortUnsigned();
        this.updateHealthIncrease(true);
    }

    public void updatedMaxHealth() {
        this.startMaxHealth = this.owner.getMaxHealthFlat();
        this.updateHealthIncrease(false);
    }

    private void updateHealthIncrease(boolean updateHealthPercent) {
        float percHealth = this.owner.getHealthPercent();
        this.healthIncrease = Math.round((float)this.startMaxHealth * (GameUtils.getMultiplayerScaling(this.lastPlayers, this.playerPercentStart, this.playerPercentDec) - 1.0f + (GameUtils.getMultiplayerScaling(this.lastPartyMembers + 1, Integer.MAX_VALUE, this.partyPercentStart, this.partyPercentDec) - 1.0f)));
        if (updateHealthPercent) {
            int health = (int)((float)this.owner.getMaxHealth() * percHealth);
            this.owner.setHealthHidden(health);
        }
    }

    public int getHealthIncrease() {
        return this.healthIncrease;
    }

    public static PlayerCount getPlayerCount(Mob owner) {
        return MobHealthScaling.getPlayerCount(owner.getLevel(), owner.getX(), owner.getY());
    }

    public static PlayerCount getPlayerCount(Level level, float levelX, float levelY) {
        List players = level.entityManager.players.streamAreaTileRange((int)levelX, (int)levelY, 50).filter(p -> !p.removed()).filter(player -> player.getDiagonalMoveDistance(levelX, levelY) <= 1600.0f).collect(Collectors.toList());
        int nextPlayers = players.size();
        int nextPartyMembers = (int)players.stream().filter(PlayerMob::isServerClient).flatMap(p -> p.getServerClient().adventureParty.getMobs().stream()).filter(m -> m.getLevel().isSamePlace(level)).filter(m -> m.getDiagonalMoveDistance(levelX, levelY) <= 1600.0f).count();
        return new PlayerCount(nextPlayers, nextPartyMembers);
    }

    public static class PlayerCount {
        public int players;
        public int partyMembers;

        public PlayerCount(int players, int partyMembers) {
            this.players = players;
            this.partyMembers = partyMembers;
        }
    }
}

