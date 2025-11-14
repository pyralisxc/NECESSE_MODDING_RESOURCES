/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.save.LoadData
 *  necesse.entity.mobs.PlayerMob
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$This
 */
package aphorea.patches.playerdata;

import aphorea.data.AphPlayerData;
import aphorea.data.AphPlayerDataList;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.save.LoadData;
import necesse.entity.mobs.PlayerMob;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=PlayerMob.class, name="applyLoadData", arguments={LoadData.class})
public class LoadPlayerData {
    @Advice.OnMethodExit
    static void onExit(@Advice.This PlayerMob playerMob, @Advice.Argument(value=0) LoadData loadData) {
        AphPlayerData player = AphPlayerDataList.getCurrentPlayer(playerMob);
        player.loadData(loadData);
    }
}

