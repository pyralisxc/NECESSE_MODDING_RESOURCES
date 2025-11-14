/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.ChatCommand;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.clientCommands.BoolClientCommand;
import necesse.engine.commands.clientCommands.DebugClientClientCommand;
import necesse.engine.commands.clientCommands.DebugWorldPresetsClientClientCommand;
import necesse.engine.commands.clientCommands.DeleteMapDataClientClientCommand;
import necesse.engine.commands.clientCommands.HelpClientCommand;
import necesse.engine.commands.clientCommands.MaxFPSClientCommand;
import necesse.engine.commands.clientCommands.ObjectHitboxesClientCommand;
import necesse.engine.commands.clientCommands.PanningCameraClientCommand;
import necesse.engine.commands.clientCommands.PlayCreditsClientCommand;
import necesse.engine.commands.clientCommands.ReloadClientCommand;
import necesse.engine.commands.clientCommands.StopCreditsClientCommand;
import necesse.engine.commands.clientCommands.VoidClientCommand;
import necesse.engine.commands.clientCommands.ZoomClientCommand;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.commands.serverCommands.AllowCheatsServerCommand;
import necesse.engine.commands.serverCommands.ArmorSetServerCommand;
import necesse.engine.commands.serverCommands.BanServerCommand;
import necesse.engine.commands.serverCommands.BansServerCommand;
import necesse.engine.commands.serverCommands.BuffServerCommand;
import necesse.engine.commands.serverCommands.ChangeNameServerCommand;
import necesse.engine.commands.serverCommands.ClearAreaServerCommand;
import necesse.engine.commands.serverCommands.ClearBuffServerCommand;
import necesse.engine.commands.serverCommands.ClearConnectionServerCommand;
import necesse.engine.commands.serverCommands.ClearDropsServerCommand;
import necesse.engine.commands.serverCommands.ClearEventsServerCommand;
import necesse.engine.commands.serverCommands.ClearMobsServerCommand;
import necesse.engine.commands.serverCommands.ClearTeamServerCommand;
import necesse.engine.commands.serverCommands.ClearallServerCommand;
import necesse.engine.commands.serverCommands.CompleteIncursionServerCommand;
import necesse.engine.commands.serverCommands.CopyInventoryServerCommand;
import necesse.engine.commands.serverCommands.CopyItemCommand;
import necesse.engine.commands.serverCommands.CreateTeamServerCommand;
import necesse.engine.commands.serverCommands.CreativeModeServerCommand;
import necesse.engine.commands.serverCommands.DeathPenaltyServerCommand;
import necesse.engine.commands.serverCommands.DebugLoadingPerformanceCommand;
import necesse.engine.commands.serverCommands.DeletePlayerServerCommand;
import necesse.engine.commands.serverCommands.DieServerCommand;
import necesse.engine.commands.serverCommands.DifficultyServerCommand;
import necesse.engine.commands.serverCommands.EnchantServerCommand;
import necesse.engine.commands.serverCommands.EndRaidCommand;
import necesse.engine.commands.serverCommands.GetTeamServerCommand;
import necesse.engine.commands.serverCommands.GiveServerCommand;
import necesse.engine.commands.serverCommands.HealServerCommand;
import necesse.engine.commands.serverCommands.HealthServerCommand;
import necesse.engine.commands.serverCommands.HelpServerCommand;
import necesse.engine.commands.serverCommands.HungerServerCommand;
import necesse.engine.commands.serverCommands.InvincibilityServerCommand;
import necesse.engine.commands.serverCommands.InviteTeamServerCommand;
import necesse.engine.commands.serverCommands.ItemGNDCommand;
import necesse.engine.commands.serverCommands.KickServerCommand;
import necesse.engine.commands.serverCommands.LeaveTeamServerCommand;
import necesse.engine.commands.serverCommands.LevelsServerCommand;
import necesse.engine.commands.serverCommands.MOTDServerCommand;
import necesse.engine.commands.serverCommands.ManaServerCommand;
import necesse.engine.commands.serverCommands.MaxHealthServerCommand;
import necesse.engine.commands.serverCommands.MaxLatencyServerCommand;
import necesse.engine.commands.serverCommands.MaxManaServerCommand;
import necesse.engine.commands.serverCommands.MeServerCommand;
import necesse.engine.commands.serverCommands.MowServerCommand;
import necesse.engine.commands.serverCommands.MyPermissionsServerCommand;
import necesse.engine.commands.serverCommands.NetworkServerCommand;
import necesse.engine.commands.serverCommands.PasswordServerCommand;
import necesse.engine.commands.serverCommands.PauseWhenEmptyServerCommand;
import necesse.engine.commands.serverCommands.PerformanceServerCommand;
import necesse.engine.commands.serverCommands.PermissionsServerCommand;
import necesse.engine.commands.serverCommands.PlayerNamesServerCommand;
import necesse.engine.commands.serverCommands.PlayersServerCommand;
import necesse.engine.commands.serverCommands.PlaytimeServerCommand;
import necesse.engine.commands.serverCommands.PrintServerCommand;
import necesse.engine.commands.serverCommands.RaidsServerCommand;
import necesse.engine.commands.serverCommands.RainServerCommand;
import necesse.engine.commands.serverCommands.RegenServerCommand;
import necesse.engine.commands.serverCommands.ResetHealthUpgradesServerCommand;
import necesse.engine.commands.serverCommands.ResilienceServerCommand;
import necesse.engine.commands.serverCommands.SaveServerCommand;
import necesse.engine.commands.serverCommands.SayServerCommand;
import necesse.engine.commands.serverCommands.SetDimensionServerCommand;
import necesse.engine.commands.serverCommands.SetIslandServerCommand;
import necesse.engine.commands.serverCommands.SetLanguageServerCommand;
import necesse.engine.commands.serverCommands.SetLevelServerCommand;
import necesse.engine.commands.serverCommands.SetPositionServerCommand;
import necesse.engine.commands.serverCommands.SetRaidDifficultyCommand;
import necesse.engine.commands.serverCommands.SetRaidTierCommand;
import necesse.engine.commands.serverCommands.SetTeamOwnerServerCommand;
import necesse.engine.commands.serverCommands.SetTeamServerCommand;
import necesse.engine.commands.serverCommands.SettingsServerCommand;
import necesse.engine.commands.serverCommands.SettlementsServerCommand;
import necesse.engine.commands.serverCommands.SpawnSettlerServerCommand;
import necesse.engine.commands.serverCommands.SpawnVisitorServerCommand;
import necesse.engine.commands.serverCommands.StartRaidAttackCommand;
import necesse.engine.commands.serverCommands.StartRaidCommand;
import necesse.engine.commands.serverCommands.StaticDamageServerCommand;
import necesse.engine.commands.serverCommands.StopHelpServerCommand;
import necesse.engine.commands.serverCommands.StopServerCommand;
import necesse.engine.commands.serverCommands.TeleportServerCommand;
import necesse.engine.commands.serverCommands.TimeServerCommand;
import necesse.engine.commands.serverCommands.UnbanServerCommand;
import necesse.engine.commands.serverCommands.UpgradeItemServerCommand;
import necesse.engine.commands.serverCommands.WhisperHelpServerCommand;
import necesse.engine.commands.serverCommands.WhisperServerCommand;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketCmdAutocomplete;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.platforms.PlatformManager;
import necesse.gfx.GameColor;

public class CommandsManager {
    private static boolean registryOpen = true;
    private static boolean registeredCore = false;
    private static final List<ChatCommand> serverCommands = new ArrayList<ChatCommand>();
    private static final List<ChatCommand> clientCommands = new ArrayList<ChatCommand>();
    public final Server server;
    public final Client client;
    private String lastCheatCommand;

    public static void registerCoreCommands() {
        if (!registryOpen) {
            throw new IllegalStateException("Command registration is closed");
        }
        if (registeredCore) {
            throw new IllegalStateException("Cannot register core commands twice");
        }
        registeredCore = true;
        CommandsManager.registerServerCommand(new StopHelpServerCommand());
        CommandsManager.registerServerCommand(new StopServerCommand("quit"));
        CommandsManager.registerServerCommand(new StopServerCommand("stop"));
        CommandsManager.registerServerCommand(new StopServerCommand("exit"));
        CommandsManager.registerServerCommand(new HelpServerCommand());
        CommandsManager.registerServerCommand(new PlaytimeServerCommand());
        CommandsManager.registerServerCommand(new MeServerCommand());
        CommandsManager.registerServerCommand(new WhisperHelpServerCommand());
        CommandsManager.registerServerCommand(new WhisperServerCommand("whisper"));
        CommandsManager.registerServerCommand(new WhisperServerCommand("w"));
        CommandsManager.registerServerCommand(new WhisperServerCommand("pm"));
        CommandsManager.registerServerCommand(new NetworkServerCommand());
        CommandsManager.registerServerCommand(new PlayersServerCommand());
        CommandsManager.registerServerCommand(new PlayerNamesServerCommand());
        CommandsManager.registerServerCommand(new LevelsServerCommand());
        CommandsManager.registerServerCommand(new SettlementsServerCommand());
        CommandsManager.registerServerCommand(new SaveServerCommand());
        CommandsManager.registerServerCommand(new MowServerCommand());
        CommandsManager.registerServerCommand(new TimeServerCommand());
        CommandsManager.registerServerCommand(new ClearallServerCommand());
        CommandsManager.registerServerCommand(new ClearMobsServerCommand());
        CommandsManager.registerServerCommand(new ClearDropsServerCommand());
        CommandsManager.registerServerCommand(new ClearEventsServerCommand());
        CommandsManager.registerServerCommand(new TeleportServerCommand("tp"));
        CommandsManager.registerServerCommand(new KickServerCommand());
        CommandsManager.registerServerCommand(new SayServerCommand());
        CommandsManager.registerServerCommand(new PrintServerCommand());
        CommandsManager.registerServerCommand(new GiveServerCommand());
        CommandsManager.registerServerCommand(new BuffServerCommand());
        CommandsManager.registerServerCommand(new ClearBuffServerCommand());
        CommandsManager.registerServerCommand(new SetIslandServerCommand());
        CommandsManager.registerServerCommand(new SetDimensionServerCommand());
        CommandsManager.registerServerCommand(new SetPositionServerCommand());
        CommandsManager.registerServerCommand(new SetLevelServerCommand());
        CommandsManager.registerServerCommand(new HealthServerCommand("hp"));
        CommandsManager.registerServerCommand(new ResilienceServerCommand("resilience"));
        CommandsManager.registerServerCommand(new MaxHealthServerCommand());
        CommandsManager.registerServerCommand(new ManaServerCommand());
        CommandsManager.registerServerCommand(new MaxManaServerCommand());
        CommandsManager.registerServerCommand(new HungerServerCommand());
        CommandsManager.registerServerCommand(new DeletePlayerServerCommand());
        CommandsManager.registerServerCommand(new SettingsServerCommand());
        CommandsManager.registerServerCommand(new DifficultyServerCommand());
        CommandsManager.registerServerCommand(new DeathPenaltyServerCommand());
        CommandsManager.registerServerCommand(new RaidsServerCommand());
        CommandsManager.registerServerCommand(new PauseWhenEmptyServerCommand());
        CommandsManager.registerServerCommand(new MaxLatencyServerCommand());
        CommandsManager.registerServerCommand(new PasswordServerCommand());
        CommandsManager.registerServerCommand(new PermissionsServerCommand());
        CommandsManager.registerServerCommand(new MyPermissionsServerCommand());
        CommandsManager.registerServerCommand(new BanServerCommand());
        CommandsManager.registerServerCommand(new UnbanServerCommand());
        CommandsManager.registerServerCommand(new BansServerCommand());
        CommandsManager.registerServerCommand(new RegenServerCommand());
        CommandsManager.registerServerCommand(new RainServerCommand());
        CommandsManager.registerServerCommand(new EnchantServerCommand());
        CommandsManager.registerServerCommand(new AllowCheatsServerCommand());
        CommandsManager.registerServerCommand(new SetLanguageServerCommand());
        CommandsManager.registerServerCommand(new DieServerCommand());
        CommandsManager.registerServerCommand(new ItemGNDCommand());
        CommandsManager.registerServerCommand(new CopyItemCommand());
        CommandsManager.registerServerCommand(new HealServerCommand());
        CommandsManager.registerServerCommand(new CopyInventoryServerCommand());
        CommandsManager.registerServerCommand(new PerformanceServerCommand());
        CommandsManager.registerServerCommand(new GetTeamServerCommand());
        CommandsManager.registerServerCommand(new ClearTeamServerCommand());
        CommandsManager.registerServerCommand(new SetTeamServerCommand());
        CommandsManager.registerServerCommand(new SetTeamOwnerServerCommand());
        CommandsManager.registerServerCommand(new CreateTeamServerCommand());
        CommandsManager.registerServerCommand(new LeaveTeamServerCommand());
        CommandsManager.registerServerCommand(new InviteTeamServerCommand());
        CommandsManager.registerServerCommand(new MOTDServerCommand());
        CommandsManager.registerServerCommand(new ChangeNameServerCommand());
        CommandsManager.registerServerCommand(new StartRaidCommand());
        CommandsManager.registerServerCommand(new StartRaidAttackCommand());
        CommandsManager.registerServerCommand(new EndRaidCommand());
        CommandsManager.registerServerCommand(new SetRaidTierCommand());
        CommandsManager.registerServerCommand(new SetRaidDifficultyCommand());
        CommandsManager.registerServerCommand(new InvincibilityServerCommand());
        CommandsManager.registerServerCommand(new SpawnSettlerServerCommand());
        CommandsManager.registerServerCommand(new SpawnVisitorServerCommand());
        CommandsManager.registerServerCommand(new UpgradeItemServerCommand());
        CommandsManager.registerServerCommand(new CreativeModeServerCommand());
        CommandsManager.registerServerCommand(new ArmorSetServerCommand());
        CommandsManager.registerClientCommand(new HelpClientCommand());
        CommandsManager.registerClientCommand(new ReloadClientCommand());
        CommandsManager.registerClientCommand(new PanningCameraClientCommand());
        CommandsManager.registerClientCommand(new ZoomClientCommand());
        CommandsManager.registerClientCommand(new ObjectHitboxesClientCommand());
        CommandsManager.registerClientCommand(new VoidClientCommand("reloadlang", "Reloads language files", PermissionLevel.USER, (c, l) -> {
            Localization.reloadLanguageFiles();
            c.chat.addMessage("Reloaded language files");
        }));
        CommandsManager.registerClientCommand(new PlayCreditsClientCommand());
        CommandsManager.registerClientCommand(new StopCreditsClientCommand());
        if (GlobalData.isDevMode()) {
            CommandsManager.registerClientCommand(BoolClientCommand.create("alwaysrain", "Toggles show rain", PermissionLevel.OWNER, Settings.class, "alwaysRain", null));
            CommandsManager.registerClientCommand(BoolClientCommand.create("alwayslight", "Toggles always light", PermissionLevel.OWNER, Settings.class, "alwaysLight", null));
            CommandsManager.registerClientCommand(new MaxFPSClientCommand());
            CommandsManager.registerServerCommand(new ClearConnectionServerCommand());
            CommandsManager.registerServerCommand(new StaticDamageServerCommand());
            CommandsManager.registerServerCommand(new DebugLoadingPerformanceCommand("serverloadingperformance"));
            CommandsManager.registerClientCommand(new DebugLoadingPerformanceCommand("clientloadingperformance"));
            CommandsManager.registerClientCommand(new DeleteMapDataClientClientCommand());
            CommandsManager.registerServerCommand(new DebugWorldPresetsClientClientCommand());
            CommandsManager.registerServerCommand(new ClearAreaServerCommand());
            CommandsManager.registerServerCommand(new ResetHealthUpgradesServerCommand());
            CommandsManager.registerServerCommand(new CompleteIncursionServerCommand());
        }
        CommandsManager.registerClientCommand(BoolClientCommand.create("hidechat", "Toggles showing chat when not typing", PermissionLevel.USER, Settings.class, "hideChat", null));
        CommandsManager.registerClientCommand(new DebugClientClientCommand());
        PlatformManager.getPlatform().registerPlatformCommands();
    }

    public static <T extends ChatCommand> T registerServerCommand(T command) {
        if (!registryOpen) {
            throw new IllegalStateException("Command registration is closed");
        }
        serverCommands.add(command);
        return command;
    }

    public static <T extends ChatCommand> T registerClientCommand(T command) {
        if (!registryOpen) {
            throw new IllegalStateException("Command registration is closed");
        }
        clientCommands.add(command);
        return command;
    }

    public static void closeRegistry() {
        registryOpen = false;
    }

    public CommandsManager(Server server) {
        this.server = server;
        this.client = null;
        this.lastCheatCommand = null;
    }

    public CommandsManager(Client client) {
        this.client = client;
        this.server = null;
    }

    public List<ChatCommand> getServerCommands() {
        return serverCommands;
    }

    public List<ChatCommand> getClientCommands() {
        return clientCommands;
    }

    public List<ChatCommand> getCommands() {
        ArrayList<ChatCommand> out = new ArrayList<ChatCommand>(this.getServerCommands());
        if (this.client != null) {
            out.addAll(this.getClientCommands());
        }
        return out;
    }

    public boolean runServerCommand(ParsedCommand command, ServerClient serverClient) {
        ArrayList<String> args = new ArrayList<String>(Arrays.asList(command.args));
        CommandLog firstLogs = null;
        for (ChatCommand cmd : serverCommands) {
            if (cmd.onlyForHelp() || !cmd.name.equalsIgnoreCase(command.commandName)) continue;
            CommandLog logs = new CommandLog(this.client, serverClient);
            if (firstLogs == null) {
                firstLogs = logs;
            }
            if (cmd.havePermissions(this.client, this.server, serverClient)) {
                String error = null;
                if (cmd.isCheat() && !this.server.world.settings.cheatsAllowedOrHidden()) {
                    if (CommandsManager.getPermissionLevel(this.client, serverClient).getLevel() < PermissionLevel.OWNER.getLevel()) {
                        error = GameColor.RED.getColorCode() + "Cheats are not allowed in this world.";
                    } else {
                        error = GameColor.CYAN.getColorCode() + "Running this command will disable achievements. Run it again to accept this.";
                        String cmdJoin = command.commandName + " " + String.join((CharSequence)" ", args);
                        if (cmdJoin.equals(this.lastCheatCommand)) {
                            error = null;
                            this.lastCheatCommand = null;
                            this.server.world.settings.enableCheats();
                        } else {
                            this.lastCheatCommand = cmdJoin;
                        }
                    }
                }
                if (error != null) {
                    logs.add(error);
                    continue;
                }
                try {
                    if (!cmd.run(this.client, this.server, serverClient, args, logs)) continue;
                    logs.printLog();
                    return true;
                }
                catch (Exception e) {
                    logs.add(GameColor.RED.getColorCode() + "Error executing command " + command.commandName);
                    e.printStackTrace();
                    continue;
                }
            }
            logs.add(GameColor.RED.getColorCode() + "You do not have permissions for that command.");
        }
        if (firstLogs != null) {
            firstLogs.printLog();
            return true;
        }
        this.lastCheatCommand = null;
        CommandLog.print(null, serverClient, "Could not find command: \"" + command.commandName + "\", use command \"help\".");
        return false;
    }

    public boolean runClientCommand(ParsedCommand command) {
        if (this.client == null) {
            throw new IllegalStateException("Cannot run client commands on server");
        }
        ArrayList<String> args = new ArrayList<String>(Arrays.asList(command.args));
        CommandLog firstLogs = null;
        for (ChatCommand cmd : clientCommands) {
            if (cmd.onlyForHelp() || !cmd.name.equalsIgnoreCase(command.commandName)) continue;
            CommandLog logs = new CommandLog(this.client, null);
            if (firstLogs == null) {
                firstLogs = logs;
            }
            if (cmd.havePermissions(this.client, this.server, null)) {
                try {
                    if (!cmd.run(this.client, this.server, null, args, logs)) continue;
                    logs.printLog();
                    return true;
                }
                catch (Exception e) {
                    logs.add(GameColor.RED.getColorCode() + "Error executing command " + command.commandName);
                    e.printStackTrace();
                    continue;
                }
            }
            logs.add(GameColor.RED.getColorCode() + "You do not have permissions for that command.");
        }
        if (firstLogs != null) {
            firstLogs.printLog();
            return true;
        }
        return false;
    }

    public List<AutoComplete> autocomplete(ParsedCommand command, ServerClient serverClient) {
        List<AutoComplete> out;
        if (command.args.length == 0) {
            return ParameterHandler.autocompleteFromList(this.getCommands(), cmd -> !cmd.onlyForHelp() && cmd.havePermissions(this.client, this.server, serverClient), cmd -> cmd.name, new CmdArgument(null, command.commandName, 1));
        }
        for (ChatCommand cmd2 : clientCommands) {
            if (cmd2.onlyForHelp() || !cmd2.havePermissions(this.client, this.server, serverClient) || !cmd2.name.equalsIgnoreCase(command.commandName) || (out = this.clearDuplicates(cmd2.autocomplete(this.client, this.server, serverClient, command.args))).isEmpty()) continue;
            return out;
        }
        for (ChatCommand cmd2 : serverCommands) {
            if (cmd2.onlyForHelp() || !cmd2.havePermissions(this.client, this.server, serverClient) || !cmd2.name.equalsIgnoreCase(command.commandName)) continue;
            if (this.client != null && cmd2.autocompleteOnServer()) {
                this.client.network.sendPacket(new PacketCmdAutocomplete(command.fullCommand));
                return Collections.emptyList();
            }
            out = this.clearDuplicates(cmd2.autocomplete(this.client, this.server, serverClient, command.args));
            if (out.isEmpty()) continue;
            return out;
        }
        return Collections.emptyList();
    }

    protected List<AutoComplete> clearDuplicates(List<AutoComplete> out) {
        ListIterator<AutoComplete> li = out.listIterator();
        while (li.hasNext()) {
            AutoComplete next = li.next();
            boolean remove = false;
            for (AutoComplete other : out) {
                if (next == other || !next.equals(other)) continue;
                remove = true;
                break;
            }
            if (!remove) continue;
            li.remove();
        }
        return out;
    }

    public String getCurrentUsage(ParsedCommand command, ServerClient serverClient) {
        if (command.args.length == 0) {
            return null;
        }
        for (ChatCommand cmd : clientCommands) {
            if (cmd.onlyForHelp() || !cmd.havePermissions(this.client, this.server, serverClient) || !cmd.name.equalsIgnoreCase(command.commandName)) continue;
            return cmd.getCurrentUsage(this.client, this.server, serverClient, command.args);
        }
        for (ChatCommand cmd : serverCommands) {
            if (cmd.onlyForHelp() || !cmd.havePermissions(this.client, this.server, serverClient) || !cmd.name.equalsIgnoreCase(command.commandName)) continue;
            return cmd.getCurrentUsage(this.client, this.server, serverClient, command.args);
        }
        return null;
    }

    public static PermissionLevel getPermissionLevel(Client client, ServerClient serverClient) {
        if (client != null) {
            return client.getPermissionLevel();
        }
        if (serverClient != null) {
            return serverClient.getPermissionLevel();
        }
        return PermissionLevel.SERVER;
    }
}

