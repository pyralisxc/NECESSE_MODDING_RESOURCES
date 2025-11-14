/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.PacketReader
 *  necesse.engine.world.WorldSettings
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodExit
 */
package increasedStackSize.patches;

import increasedStackSize.IncreasedStackSize;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.PacketReader;
import necesse.engine.world.WorldSettings;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=WorldSettings.class, name="applyContentPacket", arguments={PacketReader.class})
public class WorldSettingsApplyContentPacketPatch {
    @Advice.OnMethodExit
    static void onExit(@Advice.Argument(value=0) PacketReader reader) {
        int newStackSize = reader.getNextInt();
        IncreasedStackSize.setStackSizeMultiplier(newStackSize, true);
    }
}

