/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 */
package medievalsim.commandcenter;

import medievalsim.commandcenter.CommandCategory;
import medievalsim.commandcenter.CommandResult;
import medievalsim.commandcenter.WorldType;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public abstract class AdminCommand {
    private final String id;
    private final String displayName;
    private final String description;
    private final PermissionLevel requiredPermission;
    private final CommandCategory category;
    private final boolean requiresConfirmation;
    private final WorldType worldType;

    protected AdminCommand(Builder builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.requiredPermission = builder.requiredPermission;
        this.category = builder.category;
        this.requiresConfirmation = builder.requiresConfirmation;
        this.worldType = builder.worldType;
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDescription() {
        return this.description;
    }

    public PermissionLevel getRequiredPermission() {
        return this.requiredPermission;
    }

    public CommandCategory getCategory() {
        return this.category;
    }

    public boolean requiresConfirmation() {
        return this.requiresConfirmation;
    }

    public WorldType getWorldType() {
        return this.worldType;
    }

    public abstract CommandResult execute(Client var1, Server var2, ServerClient var3, Object[] var4);

    public boolean hasPermission(ServerClient executor) {
        if (executor == null) {
            return false;
        }
        PermissionLevel level = executor.getPermissionLevel();
        return level != null && level.getLevel() >= this.requiredPermission.getLevel();
    }

    public boolean isAvailableInWorld(Server server) {
        if (server == null || server.world == null || server.world.settings == null) {
            return true;
        }
        boolean isCreative = server.world.settings.creativeMode;
        switch (this.worldType) {
            case SURVIVAL_ONLY: 
            case REQUIRES_SURVIVAL: {
                return !isCreative;
            }
            case CREATIVE_ONLY: {
                return isCreative;
            }
        }
        return true;
    }

    public static class Builder {
        private String id;
        private String displayName;
        private String description = "";
        private PermissionLevel requiredPermission = PermissionLevel.ADMIN;
        private CommandCategory category = CommandCategory.OTHER;
        private boolean requiresConfirmation = false;
        private WorldType worldType = WorldType.ANY;

        public Builder(String id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder permission(PermissionLevel level) {
            this.requiredPermission = level;
            return this;
        }

        public Builder category(CommandCategory category) {
            this.category = category;
            return this;
        }

        public Builder requiresConfirmation(boolean requires) {
            this.requiresConfirmation = requires;
            return this;
        }

        public Builder requiresConfirmation() {
            return this.requiresConfirmation(true);
        }

        public Builder worldType(WorldType type) {
            this.worldType = type;
            return this;
        }

        public Builder build() {
            return this;
        }
    }
}

