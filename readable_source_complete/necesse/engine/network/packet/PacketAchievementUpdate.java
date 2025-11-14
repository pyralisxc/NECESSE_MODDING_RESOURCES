/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GlobalData;
import necesse.engine.achievements.Achievement;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.platforms.Platform;
import necesse.engine.playerStats.StatsProvider;

public class PacketAchievementUpdate
extends Packet {
    public final int slot;
    public final boolean submitCompleted;
    public final int achievementID;
    public final Packet achievementContent;

    public PacketAchievementUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.submitCompleted = reader.getNextBoolean();
        this.achievementID = reader.getNextShortUnsigned();
        this.achievementContent = reader.getNextContentPacket();
    }

    public PacketAchievementUpdate(ServerClient client, Achievement achievement, boolean submitCompleted) {
        this.slot = client.slot;
        this.submitCompleted = submitCompleted;
        this.achievementID = achievement.getDataID();
        this.achievementContent = new Packet();
        achievement.setupContentPacket(new PacketWriter(this.achievementContent));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.slot);
        writer.putNextBoolean(submitCompleted);
        writer.putNextShortUnsigned(this.achievementID);
        writer.putNextContentPacket(this.achievementContent);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ClientClient c;
        Achievement achievement = GlobalData.achievements().getAchievement(this.achievementID);
        if (this.submitCompleted && (c = client.getClient(this.slot)) != null) {
            client.chat.addMessage(Localization.translate("achievement", "chatcomplete", "player", c.getName(), "achievement", achievement.name.translate()));
        }
        if (this.slot == client.getSlot()) {
            achievement.applyContentPacket(new PacketReader(this.achievementContent));
            GlobalData.achievements().saveAchievementsFileSafe();
            if (this.submitCompleted) {
                StatsProvider statsProvider = Platform.getStatsProvider();
                statsProvider.setAchievement(achievement.stringID);
                statsProvider.forceStoreStatsAndAchievements();
            }
        }
    }
}

