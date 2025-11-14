/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands;

import necesse.engine.commands.ChatCommand;
import necesse.engine.commands.PermissionLevel;

public abstract class ManualChatCommand
extends ChatCommand {
    public final String usage;
    public final String action;

    public ManualChatCommand(String name, String usage, String action, PermissionLevel permissionLevel) {
        super(name, permissionLevel);
        this.usage = usage;
        this.action = action;
    }

    @Override
    public String getUsage() {
        return this.usage;
    }

    @Override
    public String getAction() {
        return this.action;
    }
}

