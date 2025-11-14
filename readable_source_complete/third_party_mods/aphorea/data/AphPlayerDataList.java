/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.PlayerMob
 */
package aphorea.data;

import aphorea.data.AphPlayerData;
import java.util.ArrayList;
import necesse.entity.mobs.PlayerMob;

public class AphPlayerDataList {
    public static ArrayList<AphPlayerData> players = new ArrayList();

    public static AphPlayerData getCurrentPlayer(String playerName) {
        AphPlayerData playerData = players.stream().filter(p -> p.playerName.equals(playerName)).findFirst().orElse(null);
        if (playerData == null) {
            playerData = AphPlayerDataList.initPlayer(playerName);
        }
        return playerData;
    }

    public static AphPlayerData getCurrentPlayer(PlayerMob player) {
        return AphPlayerDataList.getCurrentPlayer(player.playerName);
    }

    public static AphPlayerData initPlayer(String playerName) {
        AphPlayerData playerData = new AphPlayerData(playerName);
        players.add(playerData);
        return playerData;
    }

    public static AphPlayerData initPlayer(PlayerMob player) {
        return AphPlayerDataList.getCurrentPlayer(player.playerName);
    }
}

