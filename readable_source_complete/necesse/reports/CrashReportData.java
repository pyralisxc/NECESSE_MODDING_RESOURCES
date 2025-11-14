/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.Version
 */
package necesse.reports;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import necesse.engine.GameSystemInfo;
import necesse.engine.GlobalData;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.window.WindowManager;
import necesse.level.maps.Level;
import necesse.reports.BasicsData;
import org.lwjgl.Version;

public class CrashReportData
extends BasicsData {
    public final List<Throwable> errors;

    public CrashReportData(List<Throwable> errors, Client client, Server server, String state) {
        super(state);
        this.errors = errors;
        this.data.put("system_user_name", this.getString(() -> System.getProperty("user.name")));
        this.data.put("system_os", this.getString(GameSystemInfo::getOSName));
        this.data.put("system_os_arch", this.getString(() -> System.getProperty("os.arch")));
        this.data.put("system_cpu", this.getString(GameSystemInfo::getCPUName));
        this.data.put("system_memory_total", this.getString(GameSystemInfo::getTotalMemoryString));
        this.data.put("system_memory_used", this.getString(GameSystemInfo::getUsedMemoryString));
        this.data.put("system_java_path", this.getString(() -> System.getProperty("java.home")));
        this.data.put("system_java_version", this.getString(() -> System.getProperty("java.version")));
        this.addList("system_java_total_arguments", "system_java_argument", () -> ManagementFactory.getRuntimeMXBean().getInputArguments(), this.data, s -> s);
        this.data.put("system_lwjgl_version", this.getString(Version::getVersion));
        this.data.put("system_appdata_path", this.getString(GlobalData::appDataPath));
        this.data.put("system_working_dir", this.getString(() -> System.getProperty("user.dir")));
        this.data.put("system_natives_path", this.getString(() -> {
            String nativesPath = System.getProperty("org.lwjgl.librarypath");
            if (nativesPath == null) {
                nativesPath = "INTERNAL";
            }
            return nativesPath;
        }));
        this.data.put("system_jvm_memory_max", this.getString(() -> GameUtils.getByteString(Runtime.getRuntime().maxMemory())));
        this.data.put("system_jvm_memory_used", this.getString(() -> {
            long total = Runtime.getRuntime().totalMemory();
            long used = total - Runtime.getRuntime().freeMemory();
            double usedPercent = (double)used / (double)Runtime.getRuntime().totalMemory();
            return GameUtils.getByteString(used) + " / " + GameUtils.getByteString(total) + " (" + GameMath.toDecimals(usedPercent * 100.0, 2) + "%)";
        }));
        if (WindowManager.getWindow() != null) {
            this.data.put("system_graphics_card", this.getString(GameSystemInfo::getGraphicsCard));
            this.data.put("system_opengl_version", this.getString(GameSystemInfo::getOpenGLVersion));
            this.addList("system_total_displays", "system_display", GameSystemInfo::getDisplays, this.data, d -> d);
            this.data.put("system_fbo", this.getString(WindowManager.getWindow()::getFBOCapabilities));
        }
        this.addList("total_errors", "error", () -> errors, this.data, e -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(outputStream));
            return outputStream.toString().trim();
        });
        if (server != null) {
            this.data.put("running_server", "true");
            this.data.put("server_network", this.getString(server.network::getDebugString));
            this.data.put("server_network_in", this.getString(() -> server.packetManager.getAverageIn() + "/s (" + server.packetManager.getAverageInPackets() + "), Total: " + server.packetManager.getTotalIn() + " (" + server.packetManager.getTotalInPackets() + ")"));
            this.data.put("server_network_out", this.getString(() -> server.packetManager.getAverageOut() + "/s (" + server.packetManager.getAverageOutPackets() + "), Total: " + server.packetManager.getTotalOut() + " (" + server.packetManager.getTotalOutPackets() + ")"));
            if (server.world != null) {
                this.data.put("server_save_name", this.getString(() -> server.world.filePath));
                if (server.world.worldEntity != null) {
                    this.data.put("server_world_time", this.getString(() -> server.world.worldEntity.getDayTimeInt() + ", day " + server.world.worldEntity.getDay()));
                    this.data.put("server_game_time", this.getString(() -> server.world.worldEntity.getTime()));
                    this.data.put("server_local_time", this.getString(() -> server.world.worldEntity.getLocalTime()));
                }
            }
            this.data.put("server_ticks", this.getString(() -> server.tickManager().getTotalTicks()));
            this.addStream("total_server_players", "server_player", server::streamClients, this.data, c -> "Slot " + c.slot + ": " + c.getName() + " at " + c.getLevelIdentifier());
            this.addList("total_server_levels", "server_level", () -> server.world.levelManager.getLoadedLevels(), this.data, l -> l.getIdentifier() + ", Size: " + l.tileWidth + "x" + l.tileHeight + ", Biome: " + l.baseBiome.getStringID() + ", Entities: " + l.entityManager.getSize() + ", Mobs: " + l.entityManager.mobs.count() + ", Projectiles: " + l.entityManager.projectiles.count() + ", Pickups: " + l.entityManager.pickups.count());
        }
        if (client != null) {
            this.data.put("running_client", "true");
            this.data.put("client_network", this.getString(client.network::getDebugString));
            this.data.put("client_network_in", this.getString(() -> client.packetManager.getAverageIn() + "/s (" + client.packetManager.getAverageInPackets() + "), Total: " + client.packetManager.getTotalIn() + " (" + client.packetManager.getTotalInPackets() + ")"));
            this.data.put("client_network_out", this.getString(() -> client.packetManager.getAverageOut() + "/s (" + client.packetManager.getAverageOutPackets() + "), Total: " + client.packetManager.getTotalOut() + " (" + client.packetManager.getTotalOutPackets() + ")"));
            if (client.worldEntity != null) {
                this.data.put("client_world_time", this.getString(() -> client.worldEntity.getDayTimeInt() + ", day " + client.worldEntity.getDay()));
                this.data.put("client_game_time", this.getString(() -> client.worldEntity.getTime()));
                this.data.put("client_local_time", this.getString(() -> client.worldEntity.getLocalTime()));
            }
            this.data.put("client_slot", this.getString(() -> client.getSlot() + "/" + client.getSlots()));
            Level level = client.getLevel();
            if (level != null) {
                this.data.put("client_level", this.getString(() -> level.getIdentifier() + ", Size: " + level.tileWidth + "x" + level.tileHeight + ", Biome: " + level.baseBiome.getStringID() + ", Entities: " + level.entityManager.getSize() + ", Mobs: " + level.entityManager.mobs.count() + ", Projectiles: " + level.entityManager.projectiles.count() + ", Pickups: " + level.entityManager.pickups.count()));
            }
        }
        this.addList("total_log_errors", "log_error", () -> this.getErrors, this.data, e -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(outputStream));
            return outputStream.toString().trim();
        });
    }

    public void printFullReport(PrintStream ps, File file) {
        ps.println("--- Necesse crash log ---");
        ps.println("Generated on: " + (String)this.data.get("generated_on"));
        if (file != null) {
            ps.println("At " + file.getAbsolutePath());
        }
        ps.println("Game state: " + (String)this.data.get("game_state"));
        ps.println("Game version: " + (String)this.data.get("game_version"));
        ps.println("Game language: " + (String)this.data.get("game_language"));
        ps.println("Steam build: " + (String)this.data.get("steam_build"));
        ps.println("Steam name: " + (String)this.data.get("steam_name"));
        ps.println("Authentication: " + (String)this.data.get("authentication"));
        if (!((String)this.data.get("launch_parameters")).isEmpty()) {
            ps.println("Launch parameters: " + (String)this.data.get("launch_parameters"));
        }
        this.printList(ps, "total_loaded_mods", "loaded_mod", total -> "Found " + total + " loaded mods:");
        this.printList(ps, "total_found_mods", "found_mod", total -> "Found " + total + " not enabled mods:");
        ps.println();
        String[] exceptions = this.getList("total_errors", "error");
        ps.println("Exceptions:");
        for (String exception : exceptions) {
            ps.println(exception);
        }
        ps.println();
        ps.println("Username: " + (String)this.data.get("system_user_name"));
        ps.println("OS: " + (String)this.data.get("system_os"));
        ps.println("OS arch: " + (String)this.data.get("system_os_arch"));
        ps.println("CPU: " + (String)this.data.get("system_cpu"));
        ps.println("Memory total: " + (String)this.data.get("system_memory_total"));
        ps.println("Memory used: " + (String)this.data.get("system_memory_used"));
        ps.println("Java path: " + (String)this.data.get("system_java_path"));
        ps.println("Java version: " + (String)this.data.get("system_java_version"));
        this.printList(ps, "system_java_total_arguments", "system_java_argument", total -> "JVM arguments:");
        ps.println("LWJGL version: " + (String)this.data.get("system_lwjgl_version"));
        ps.println("AppData path: " + (String)this.data.get("system_appdata_path"));
        ps.println("Working dir: " + (String)this.data.get("system_working_dir"));
        ps.println("Natives path: " + (String)this.data.get("system_natives_path"));
        ps.println("JVM memory max: " + (String)this.data.get("system_jvm_memory_max"));
        ps.println("JVM memory used: " + (String)this.data.get("system_jvm_memory_used"));
        ps.println();
        if (this.data.get("system_graphics_card") != null) {
            ps.println("Graphics card: " + (String)this.data.get("system_graphics_card"));
            ps.println("OpenGL version: " + (String)this.data.get("system_opengl_version"));
            this.printList(ps, "system_total_displays", "system_display", total -> "Found " + total + " displays:");
            ps.println("FBO: " + (String)this.data.get("system_fbo"));
        }
        ps.println();
        if (this.data.get("running_server") != null) {
            ps.println("Running server: Yes");
            ps.println("Server network: " + (String)this.data.get("server_network"));
            ps.println("Server network in: " + (String)this.data.get("server_network_in"));
            ps.println("Server network out: " + (String)this.data.get("server_network_out"));
            ps.println("Server save name: " + (String)this.data.get("server_save_name"));
            ps.println("Server world time: " + (String)this.data.get("server_world_time"));
            ps.println("Server game time: " + (String)this.data.get("server_game_time"));
            ps.println("Server local time: " + (String)this.data.get("server_local_time"));
            ps.println("Server ticks: " + (String)this.data.get("server_ticks"));
            this.printList(ps, "total_server_players", "server_player", total -> "Players online: " + total);
            this.printList(ps, "total_server_levels", "server_level", total -> "Loaded levels: " + total);
        } else {
            ps.println("Running server: No");
        }
        ps.println();
        if (this.data.get("running_client") != null) {
            ps.println("Running client: Yes");
            ps.println("Client network: " + (String)this.data.get("client_network"));
            ps.println("Client network in: " + (String)this.data.get("client_network_in"));
            ps.println("Client network out: " + (String)this.data.get("client_network_out"));
            ps.println("Client world time: " + (String)this.data.get("client_world_time"));
            ps.println("Client game time: " + (String)this.data.get("client_game_time"));
            ps.println("Client local time: " + (String)this.data.get("client_local_time"));
            ps.println("Client slot: " + (String)this.data.get("client_slot"));
            ps.println("Client level: " + (String)this.data.get("client_level"));
        } else {
            ps.println("Running client: No");
        }
        String[] logErrors = this.getList("total_log_errors", "log_error");
        if (logErrors.length > 0) {
            ps.println();
            ps.println("Log exceptions:");
            for (String e : logErrors) {
                ps.println(e);
            }
        }
    }

    public String getFullReport(File file) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        this.printFullReport(ps, file);
        return os.toString();
    }

    private void printList(PrintStream ps, String totalKey, String keyPrefix, Function<Integer, String> introTotalDisplay) {
        this.printList(ps, totalKey, keyPrefix, introTotalDisplay, (i, v) -> "\t" + v);
    }

    private void printList(PrintStream ps, String totalKey, String keyPrefix, Function<Integer, String> introTotalDisplay, BiFunction<Integer, String, String> valueDisplay) {
        String[] list = this.getList(totalKey, keyPrefix);
        if (introTotalDisplay != null) {
            ps.println(introTotalDisplay.apply(list.length));
        }
        for (int i = 0; i < list.length; ++i) {
            String v = (String)this.data.get(keyPrefix + i);
            ps.println(valueDisplay.apply(i, v));
        }
    }

    private String[] getList(String totalKey, String keyPrefix) {
        String totalStr = (String)this.data.get(totalKey);
        if (totalStr != null) {
            try {
                int total = Integer.parseInt(totalStr);
                String[] list = new String[total];
                for (int i = 0; i < total; ++i) {
                    list[i] = (String)this.data.get(keyPrefix + i);
                }
                return list;
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return new String[0];
    }
}

