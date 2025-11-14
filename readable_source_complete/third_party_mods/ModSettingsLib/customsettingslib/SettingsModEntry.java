/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModEntry
 *  necesse.engine.registries.PacketRegistry
 */
package customsettingslib;

import customsettingslib.packets.PacketReadServerSettings;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.PacketRegistry;

@ModEntry
public class SettingsModEntry {
    public void init() {
        PacketRegistry.registerPacket(PacketReadServerSettings.class);
    }
}

