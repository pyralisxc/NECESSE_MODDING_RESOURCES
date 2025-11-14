/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import necesse.engine.commands.serverCommands.setupCommand.CharacterBuild;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;

public class SummonBossBuild
extends CharacterBuild {
    public String mobStringID;
    public int tileRange;

    public SummonBossBuild(String mobStringID, int tileRange) {
        this.mobStringID = mobStringID;
        this.tileRange = tileRange;
    }

    public SummonBossBuild(String mobStringID) {
        this(mobStringID, 30);
    }

    @Override
    public void apply(ServerClient client) {
        float angle = GameRandom.globalRandom.nextInt(360);
        float nx = (float)Math.cos(Math.toRadians(angle));
        float ny = (float)Math.sin(Math.toRadians(angle));
        float distance = this.tileRange * 32;
        Mob mob = MobRegistry.getMob(this.mobStringID, client.getLevel());
        client.getLevel().entityManager.addMob(mob, client.playerMob.getX() + (int)(nx * distance), client.playerMob.getY() + (int)(ny * distance));
        client.getServer().network.sendToClientsWithEntity(new PacketChatMessage(new LocalMessage("misc", "bosssummon", "name", mob.getLocalization())), mob);
    }
}

