/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.save.CharacterSave;
import necesse.entity.mobs.PlayerMob;

public class CharacterSaveNetworkData {
    public final Packet playerData;
    public final boolean cheatsEnabled;
    public final boolean creativeEnabled;
    public final Packet characterStatsData;
    public final long timePlayed;
    public final String playerName;

    public CharacterSaveNetworkData(CharacterSave character) {
        this.playerData = new Packet();
        character.player.setupLoadedCharacterPacket(new PacketWriter(this.playerData));
        this.cheatsEnabled = character.cheatsEnabled;
        this.creativeEnabled = character.creativeEnabled;
        if (character.characterStats != null) {
            this.characterStatsData = new Packet();
            character.characterStats.setupContentPacket(new PacketWriter(this.characterStatsData));
        } else {
            this.characterStatsData = null;
        }
        this.timePlayed = character.timePlayed;
        this.playerName = character.player.playerName;
    }

    public CharacterSaveNetworkData(PacketReader reader) {
        this.playerData = reader.getNextContentPacket();
        this.cheatsEnabled = reader.getNextBoolean();
        this.creativeEnabled = reader.getNextBoolean();
        this.characterStatsData = reader.getNextBoolean() ? reader.getNextContentPacket() : null;
        this.timePlayed = reader.getNextLong();
        this.playerName = reader.getNextString();
    }

    public void write(PacketWriter writer) {
        writer.putNextContentPacket(this.playerData);
        writer.putNextBoolean(this.cheatsEnabled);
        writer.putNextBoolean(this.creativeEnabled);
        writer.putNextBoolean(this.characterStatsData != null);
        if (this.characterStatsData != null) {
            writer.putNextContentPacket(this.characterStatsData);
        }
        writer.putNextLong(this.timePlayed);
        writer.putNextString(this.playerName);
    }

    public void applyToPlayer(PlayerMob player) {
        player.applyLoadedCharacterPacket(new PacketReader(this.playerData));
        player.playerName = this.playerName;
    }

    public boolean applyToStats(PlayerStats stats) {
        if (this.characterStatsData != null) {
            stats.applyContentPacket(new PacketReader(this.characterStatsData));
            return true;
        }
        return false;
    }
}

