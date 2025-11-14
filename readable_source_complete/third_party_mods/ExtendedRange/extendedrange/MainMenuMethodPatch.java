/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.state.MainMenu
 *  necesse.inventory.container.object.CraftingStationContainer
 *  net.bytebuddy.asm.Advice$OnMethodExit
 */
package extendedrange;

import extendedrange.Settings;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.state.MainMenu;
import necesse.inventory.container.object.CraftingStationContainer;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=MainMenu.class, name="init", arguments={})
public class MainMenuMethodPatch {
    @Advice.OnMethodExit
    static void onExit() {
        try {
            CraftingStationContainer.nearbyCraftTileRange = Settings.CraftingStationsRange;
        }
        catch (Exception e) {
            System.err.println("[Extended range mod] An error has occurred while restoring values after quitting a server.\nError:\n" + e);
        }
    }
}

