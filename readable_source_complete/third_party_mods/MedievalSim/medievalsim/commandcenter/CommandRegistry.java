/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.PermissionLevel
 */
package medievalsim.commandcenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import medievalsim.commandcenter.AdminCommand;
import medievalsim.commandcenter.CommandCategory;
import necesse.engine.commands.PermissionLevel;

public class CommandRegistry {
    private static final Map<String, AdminCommand> commands = new ConcurrentHashMap<String, AdminCommand>();
    private static final Map<CommandCategory, List<AdminCommand>> commandsByCategory = new ConcurrentHashMap<CommandCategory, List<AdminCommand>>();

    public static void register(AdminCommand command) {
        commands.put(command.getId(), command);
        commandsByCategory.computeIfAbsent(command.getCategory(), k -> new ArrayList()).add(command);
    }

    public static AdminCommand getCommand(String id) {
        return commands.get(id);
    }

    public static Collection<AdminCommand> getAllCommands() {
        return commands.values();
    }

    public static List<AdminCommand> getCommandsByCategory(CommandCategory category) {
        return commandsByCategory.getOrDefault((Object)category, Collections.emptyList());
    }

    public static List<AdminCommand> getCommandsByPermission(PermissionLevel level) {
        return commands.values().stream().filter(cmd -> cmd.getRequiredPermission().getLevel() <= level.getLevel()).collect(Collectors.toList());
    }

    public static List<AdminCommand> searchCommands(String query) {
        String lowerQuery = query.toLowerCase();
        return commands.values().stream().filter(cmd -> cmd.getDisplayName().toLowerCase().contains(lowerQuery) || cmd.getDescription().toLowerCase().contains(lowerQuery)).collect(Collectors.toList());
    }

    public static Set<CommandCategory> getActiveCategories() {
        return commandsByCategory.keySet();
    }

    public static void clear() {
        commands.clear();
        commandsByCategory.clear();
    }
}

