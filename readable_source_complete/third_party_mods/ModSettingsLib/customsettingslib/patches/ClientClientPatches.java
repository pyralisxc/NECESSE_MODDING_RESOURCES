/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.Packet
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.client.ClientClient
 *  necesse.entity.mobs.PlayerMob
 *  net.bytebuddy.asm.Advice$FieldValue
 *  net.bytebuddy.asm.Advice$OnMethodExit
 */
package customsettingslib.patches;

import customsettingslib.packets.PacketReadServerSettings;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.entity.mobs.PlayerMob;
import net.bytebuddy.asm.Advice;

public class ClientClientPatches {

    @ModMethodPatch(target=ClientClient.class, name="applySpawned", arguments={int.class})
    public static class applySpawned {
        @Advice.OnMethodExit
        public static void onExit(@Advice.FieldValue(value="client") Client client) {
            PlayerMob player;
            if (client.getPlayer() != null && (player = client.getPlayer()) != null) {
                client.network.sendPacket((Packet)new PacketReadServerSettings(true));
            }
        }
    }
}

