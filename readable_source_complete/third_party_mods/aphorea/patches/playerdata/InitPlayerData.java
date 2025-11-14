/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.client.ClientClient
 *  necesse.entity.mobs.PlayerMob
 *  net.bytebuddy.asm.Advice$FieldValue
 *  net.bytebuddy.asm.Advice$OnMethodExit
 */
package aphorea.patches.playerdata;

import aphorea.data.AphPlayerDataList;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.entity.mobs.PlayerMob;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=ClientClient.class, name="applySpawned", arguments={int.class})
public class InitPlayerData {
    @Advice.OnMethodExit
    public static void onExit(@Advice.FieldValue(value="client") Client client) {
        PlayerMob player;
        if (client.getPlayer() != null && (player = client.getPlayer()) != null) {
            AphPlayerDataList.initPlayer(player);
        }
    }
}

