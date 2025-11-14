/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.CommandsManager;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public abstract class ChatCommand
implements Comparable<ChatCommand> {
    public final String name;
    public final PermissionLevel permissionLevel;

    public ChatCommand(String name, PermissionLevel permissionLevel) {
        this.name = name;
        this.permissionLevel = permissionLevel;
    }

    public String getName() {
        return this.name;
    }

    public String getFullUsage(boolean includeSlash) {
        return "Usage: " + (includeSlash ? "/" : "") + this.name + " " + this.getUsage();
    }

    public String getFullAction() {
        return "Action: " + this.getAction();
    }

    public abstract String getUsage();

    public abstract String getAction();

    public boolean isCheat() {
        return true;
    }

    public boolean bypassPermissions() {
        return false;
    }

    public boolean shouldBeListed() {
        return true;
    }

    public boolean onlyForHelp() {
        return false;
    }

    public abstract boolean run(Client var1, Server var2, ServerClient var3, ArrayList<String> var4, CommandLog var5);

    public abstract List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, String[] var4);

    public boolean autocompleteOnServer() {
        return false;
    }

    public abstract String getCurrentUsage(Client var1, Server var2, ServerClient var3, String[] var4);

    public boolean havePermissions(Client client, Server server, ServerClient serverClient) {
        return this.bypassPermissions() || this.permissionLevel.getLevel() <= CommandsManager.getPermissionLevel(client, serverClient).getLevel();
    }

    public String getFullHelp(boolean includeSlash) {
        return (includeSlash ? "/" : "") + this.name + " " + this.getUsage();
    }

    @Override
    public int compareTo(ChatCommand other) {
        return this.name.compareTo(other.name);
    }
}

