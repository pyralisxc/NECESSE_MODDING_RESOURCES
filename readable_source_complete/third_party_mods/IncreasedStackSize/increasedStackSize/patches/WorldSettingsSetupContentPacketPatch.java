/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.world.WorldSettings
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodExit
 */
package increasedStackSize.patches;

import increasedStackSize.IncreasedStackSize;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.PacketWriter;
import necesse.engine.world.WorldSettings;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=WorldSettings.class, name="setupContentPacket", arguments={PacketWriter.class})
public class WorldSettingsSetupContentPacketPatch {
    @Advice.OnMethodExit
    static void onExit(@Advice.Argument(value=0, readOnly=false) PacketWriter writer) {
        writer.putNextInt(IncreasedStackSize.stackSizeMultiplier);
    }
}

