/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.ChatCommand
 *  necesse.engine.commands.CommandsManager
 *  necesse.engine.modLoader.annotations.ModEntry
 */
package increasedStackSize;

import increasedStackSize.Compat;
import increasedStackSize.commands.ChangeStackSizeCommand;
import increasedStackSize.commands.GetStackSizeCommand;
import increasedStackSize.commands.StackSizeCommand;
import necesse.engine.commands.ChatCommand;
import necesse.engine.commands.CommandsManager;
import necesse.engine.modLoader.annotations.ModEntry;

@ModEntry
public class IncreasedStackSize {
    public static int stackSizeMultiplier = 5;
    public static int newStackSizeMultiplier = 5;

    public static void setStackSizeMultiplier(int value, boolean overwrite) {
        int newValue = value <= 0 ? 1 : value;
        if (overwrite) {
            stackSizeMultiplier = newValue;
        }
        newStackSizeMultiplier = newValue;
    }

    public void preInit() {
        Compat.backwardCompatLoad();
    }

    public void init() {
    }

    public void initResources() {
    }

    public void postInit() {
        CommandsManager.registerServerCommand((ChatCommand)new ChangeStackSizeCommand());
        CommandsManager.registerServerCommand((ChatCommand)new GetStackSizeCommand());
        CommandsManager.registerServerCommand((ChatCommand)new StackSizeCommand());
    }
}

