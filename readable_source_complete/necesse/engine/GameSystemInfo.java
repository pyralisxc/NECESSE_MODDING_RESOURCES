/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.Version
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.system.Platform
 *  oshi.SystemInfo
 *  oshi.hardware.Display
 *  oshi.hardware.HardwareAbstractionLayer
 *  oshi.software.os.OperatingSystem
 *  oshi.util.EdidUtil
 */
package necesse.engine;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.GlobalData;
import necesse.engine.util.GameUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.Platform;
import oshi.SystemInfo;
import oshi.hardware.Display;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import oshi.util.EdidUtil;

public class GameSystemInfo {
    private static boolean initialized;
    private static SystemInfo info;
    private static HardwareAbstractionLayer hardware;
    private static OperatingSystem os;
    private static String graphicsCardString;
    private static String openGLVersionString;

    private static void initialize() {
        try {
            info = new SystemInfo();
        }
        catch (Error | Exception e) {
            System.err.println("Could not initialize system info:");
            e.printStackTrace();
        }
        if (info != null) {
            try {
                hardware = info.getHardware();
            }
            catch (Error | Exception e) {
                System.err.println("Could not initialize system hardware:");
                e.printStackTrace();
            }
            try {
                os = info.getOperatingSystem();
            }
            catch (Error | Exception e) {
                System.err.println("Could not initialize system OS:");
                e.printStackTrace();
            }
        }
        initialized = true;
    }

    public static SystemInfo getInfo() {
        if (!initialized) {
            GameSystemInfo.initialize();
        }
        return info;
    }

    public static HardwareAbstractionLayer getHardware() {
        if (!initialized) {
            GameSystemInfo.initialize();
        }
        return hardware;
    }

    public static OperatingSystem getOS() {
        if (!initialized) {
            GameSystemInfo.initialize();
        }
        return os;
    }

    public static void printSystemInfo(PrintStream printStream) {
        try {
            printStream.println("SYSTEM INFO:");
            GameSystemInfo.printSystemInfo(printStream, "\t");
        }
        catch (Error | Exception e) {
            printStream.println("Error getting system info:");
            e.printStackTrace(printStream);
        }
    }

    public static void printSystemInfo(PrintStream printStream, String prefix) {
        printStream.println(prefix + "Username: " + System.getProperty("user.name"));
        printStream.println(prefix + "OS: " + GameSystemInfo.getOSName());
        printStream.println(prefix + "CPU: " + GameSystemInfo.getCPUName());
        printStream.println(prefix + "OS arch: " + System.getProperty("os.arch"));
        printStream.println(prefix + "Memory total: " + GameSystemInfo.getTotalMemoryString());
        printStream.println(prefix + "Memory max: " + GameSystemInfo.getSafeString(() -> GameUtils.getByteString(Runtime.getRuntime().maxMemory())));
        printStream.println(prefix + "Java path: " + System.getProperty("java.home"));
        printStream.println(prefix + "Java version: " + System.getProperty("java.version"));
        try {
            printStream.println(prefix + "JVM arguments:");
            for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                printStream.println(prefix + "\t" + arg);
            }
        }
        catch (Exception e) {
            printStream.println(prefix + "\tERR: " + e.getMessage());
        }
        printStream.println(prefix + "LWJGL version: " + Version.getVersion());
        printStream.println(prefix + "Root path: " + GlobalData.rootPath());
        printStream.println(prefix + "AppData path: " + GlobalData.appDataPath());
        printStream.println(prefix + "Working dir: " + System.getProperty("user.dir"));
        String nativesPath = System.getProperty("org.lwjgl.librarypath");
        if (nativesPath == null) {
            nativesPath = "INTERNAL";
        }
        printStream.println(prefix + "Natives path: " + nativesPath);
    }

    public static void printGraphicsInfo(PrintStream printStream) {
        try {
            printStream.println("GRAPHICS INFO:");
            GameSystemInfo.printGraphicsInfo(printStream, "\t");
        }
        catch (Error | Exception e) {
            printStream.println("Error getting system info:");
            e.printStackTrace(printStream);
        }
    }

    public static void printGraphicsInfo(PrintStream printStream, String prefix) {
        printStream.println(prefix + "Graphics card: " + GameSystemInfo.getGraphicsCard());
        printStream.println(prefix + "OpenGL version: " + GameSystemInfo.getOpenGLVersion());
        try {
            ArrayList<String> displays = GameSystemInfo.getDisplays();
            for (int i = 0; i < displays.size(); ++i) {
                String str = displays.get(i);
                printStream.println(prefix + "Display " + i + ": " + str);
            }
        }
        catch (Error | Exception throwable) {
            // empty catch block
        }
    }

    public static String getGraphicsCard() {
        return GameSystemInfo.getSafeString(() -> {
            if (graphicsCardString == null) {
                if (GLFW.glfwGetCurrentContext() == 0L) {
                    return "NO_CONTEXT";
                }
                graphicsCardString = GL11.glGetString((int)7936) + ", " + GL11.glGetString((int)7937);
            }
            return graphicsCardString;
        });
    }

    public static String getOpenGLVersion() {
        return GameSystemInfo.getSafeString(() -> {
            if (openGLVersionString == null) {
                if (GLFW.glfwGetCurrentContext() == 0L) {
                    return "NO_CONTEXT";
                }
                openGLVersionString = GL11.glGetString((int)7938);
            }
            return openGLVersionString;
        });
    }

    public static ArrayList<String> getDisplays() {
        if (Platform.get() == Platform.MACOSX) {
            return new ArrayList<String>(Collections.singletonList("MacOS display"));
        }
        HardwareAbstractionLayer hardware = GameSystemInfo.getHardware();
        if (hardware == null) {
            return new ArrayList<String>(Collections.singletonList("NULL HARDWARE"));
        }
        List displays = hardware.getDisplays();
        ArrayList<String> out = new ArrayList<String>(displays.size());
        for (Display display : displays) {
            byte[][] desc;
            byte[] edid = display.getEdid();
            for (byte[] d : desc = EdidUtil.getDescriptors((byte[])edid)) {
                if (EdidUtil.getDescriptorType((byte[])d) != 252) continue;
                out.add(GameSystemInfo.getSafeString(() -> EdidUtil.getDescriptorText((byte[])d) + " (" + EdidUtil.getManufacturerID((byte[])edid) + ", " + EdidUtil.getProductID((byte[])edid) + ")"));
            }
        }
        return out;
    }

    public static String getOSName() {
        return GameSystemInfo.getSafeString(() -> GameSystemInfo.getOS().toString());
    }

    public static String getCPUName() {
        return GameSystemInfo.getSafeString(() -> GameSystemInfo.getHardware().getProcessor().getProcessorIdentifier().getName());
    }

    public static long getTotalMemory() {
        return GameSystemInfo.getHardware().getMemory().getTotal();
    }

    public static long getAvailableMemory() {
        return GameSystemInfo.getHardware().getMemory().getAvailable();
    }

    public static long getUsedMemory() {
        return GameSystemInfo.getTotalMemory() - GameSystemInfo.getAvailableMemory();
    }

    public static String getTotalMemoryString() {
        return GameSystemInfo.getSafeString(() -> GameUtils.getByteString(GameSystemInfo.getTotalMemory()));
    }

    public static String getAvailableMemoryString() {
        return GameSystemInfo.getSafeString(() -> GameUtils.getByteString(GameSystemInfo.getAvailableMemory()));
    }

    public static String getUsedMemoryString() {
        return GameSystemInfo.getSafeString(() -> GameUtils.getByteString(GameSystemInfo.getTotalMemory() - GameSystemInfo.getAvailableMemory()));
    }

    private static String getSafeString(Supplier<String> supplier) {
        try {
            return supplier.get();
        }
        catch (Error | Exception e) {
            return "ERR: " + e.getClass().getSimpleName();
        }
    }
}

