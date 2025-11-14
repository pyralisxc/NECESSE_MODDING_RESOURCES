/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.platform.win32.Advapi32Util
 *  com.sun.jna.platform.win32.WinReg
 *  com.sun.jna.platform.win32.WinReg$HKEY
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWVidMode
 *  org.lwjgl.glfw.GLFWVidMode$Buffer
 *  org.lwjgl.system.Configuration
 *  org.lwjgl.system.Platform
 *  oshi.hardware.Display
 *  oshi.hardware.HardwareAbstractionLayer
 *  oshi.util.EdidUtil
 */
package necesse.engine.platforms.sharedOnPC.window;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import necesse.engine.GameLaunch;
import necesse.engine.GameLog;
import necesse.engine.GameSystemInfo;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.platforms.Platform;
import necesse.engine.platforms.PlatformManager;
import necesse.engine.platforms.sharedOnPC.window.GLFWGameError;
import necesse.engine.platforms.sharedOnPC.window.GLFWGameWindow;
import necesse.engine.window.GameWindow;
import necesse.engine.window.GameWindowCreationException;
import necesse.engine.window.WindowManager;
import necesse.reports.NoticeJFrame;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.Platform;
import oshi.hardware.Display;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.util.EdidUtil;

public class GLFWWindowManager
extends WindowManager {
    private boolean glfwLoaded = false;
    private GLFWErrorCallback errorCallback = null;
    private WindowError windowError;
    private boolean monitorNeedsHDRHack = false;

    private void readMonitorHDRStatusOnWindows() {
        this.monitorNeedsHDRHack = false;
        try {
            String[] keys;
            if (PlatformManager.getPlatform().getOperatingSystemFamily() != Platform.OperatingSystemFamily.Windows) {
                return;
            }
            HardwareAbstractionLayer hardware = GameSystemInfo.getHardware();
            if (hardware.getGraphicsCards().stream().noneMatch(x -> x.getVendor().toLowerCase().contains("nvidia"))) {
                return;
            }
            try {
                keys = Advapi32Util.registryGetKeys((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)"SYSTEM\\CurrentControlSet\\Control\\GraphicsDrivers\\MonitorDataStore\\");
            }
            catch (Exception e) {
                return;
            }
            List displays = hardware.getDisplays();
            for (Display display : displays) {
                byte[] edid = display.getEdid();
                String temp = String.format("%8s%8s", Integer.toBinaryString(edid[8] & 0xFF), Integer.toBinaryString(edid[9] & 0xFF)).replace(' ', '0');
                String manId = String.format("%s%s%s", Character.valueOf((char)(64 + Integer.parseInt(temp.substring(1, 6), 2))), Character.valueOf((char)(64 + Integer.parseInt(temp.substring(6, 11), 2))), Character.valueOf((char)(64 + Integer.parseInt(temp.substring(11, 16), 2)))).replace("@", "");
                String productId = EdidUtil.getProductID((byte[])edid).toUpperCase();
                String path = Arrays.stream(keys).filter(x -> x.startsWith(manId + productId)).findFirst().orElse(null);
                if (path == null) continue;
                path = "SYSTEM\\CurrentControlSet\\Control\\GraphicsDrivers\\MonitorDataStore\\" + path;
                try {
                    if (Advapi32Util.registryGetIntValue((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)path, (String)"HDREnabled") > 0) {
                        this.monitorNeedsHDRHack = true;
                    }
                }
                catch (Exception e) {
                    try {
                        if (Advapi32Util.registryGetIntValue((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)path, (String)"AdvancedColorEnabled") > 0) {
                            this.monitorNeedsHDRHack = true;
                        }
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                if (!this.monitorNeedsHDRHack) continue;
                GameLog.out.println("Detected HDR monitor running on Nvidia GPU. Game will use fullscreen instead of borderless and perform HDR hack. This is due to a bug with OpenGL and Nvidia on Windows.");
                return;
            }
        }
        catch (Exception e) {
            this.monitorNeedsHDRHack = false;
        }
    }

    @Override
    public long[] getMonitors() {
        PointerBuffer pointers = GLFW.glfwGetMonitors();
        if (pointers == null) {
            return new long[0];
        }
        long primary = GLFW.glfwGetPrimaryMonitor();
        long[] monitors = new long[pointers.remaining()];
        int i = 0;
        if (primary != 0L && monitors.length > 0) {
            monitors[i++] = primary;
        }
        while (pointers.hasRemaining() && i < monitors.length) {
            long next = pointers.get();
            if (next == primary) continue;
            monitors[i++] = next;
        }
        return monitors;
    }

    @Override
    public boolean initialize() {
        boolean debugStartup = GameLaunch.launchOptions.containsKey("debugstartup");
        if (Platform.get() == Platform.MACOSX) {
            JFrame jFrame = new JFrame();
            jFrame.dispose();
            Configuration.GLFW_LIBRARY_NAME.set((Object)"glfw_async");
        }
        this.errorCallback = new GLFWErrorCallback(){

            public void invoke(int error, long description) {
                GLFWGameError.throwError(error, description);
            }
        };
        if (debugStartup) {
            System.out.println("STARTUP: Constructed GLFW error callback");
        }
        this.errorCallback.set();
        if (debugStartup) {
            System.out.println("STARTUP: Set GLFW error callback");
        }
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        if (debugStartup) {
            System.out.println("STARTUP: Initialized GLFW");
        }
        this.glfwLoaded = true;
        this.windowError = GLFWGameError.tryGLFWError(new GLFWGameError.Supplier<WindowError>(new int[]{65542}){

            @Override
            public WindowError run() {
                try {
                    GLFWGameWindow window = new GLFWGameWindow();
                    window.createWindow(false);
                    window.requestFocus();
                    return new WindowError(window);
                }
                catch (GameWindowCreationException e) {
                    return new WindowError(Localization.translate("misc", "openglfailed"));
                }
            }

            @Override
            public WindowError onCatch(GLFWGameError error) {
                error.print(System.err);
                return new WindowError(Localization.translate("misc", "openglfailed"));
            }
        });
        if (this.windowError.error != null) {
            if (debugStartup) {
                System.out.println("STARTUP: Window creation error: " + this.windowError.error);
            }
            NoticeJFrame noticeFrame = new NoticeJFrame(400, this.windowError.error);
            noticeFrame.setVisible(true);
            noticeFrame.requestFocus();
            return false;
        }
        if (debugStartup) {
            System.out.println("STARTUP: Successfully created window");
        }
        currentWindow = this.windowError.window;
        GameSystemInfo.printGraphicsInfo(GameLog.file);
        if (debugStartup) {
            System.out.println("STARTUP: Printed graphics info");
        }
        this.readMonitorHDRStatusOnWindows();
        return super.initialize();
    }

    @Override
    public GameWindow createNewWindow(boolean setAsCurrent) {
        GLFWGameWindow newWindow = new GLFWGameWindow();
        if (setAsCurrent) {
            currentWindow = newWindow;
        }
        return newWindow;
    }

    @Override
    public void dispose() {
        if (this.glfwLoaded) {
            GLFW.glfwTerminate();
        }
        if (this.errorCallback != null) {
            this.errorCallback.free();
        }
    }

    @Override
    public Dimension[] getVideoModes(long monitor) {
        GLFWVidMode.Buffer modes = GLFW.glfwGetVideoModes((long)monitor);
        if (modes == null) {
            return new Dimension[]{new Dimension(WindowManager.getWindow().getWidth(), WindowManager.getWindow().getHeight())};
        }
        ArrayList<Dimension> sizes = new ArrayList<Dimension>();
        for (GLFWVidMode mode : modes) {
            Dimension dim = new Dimension(mode.width(), mode.height());
            if (sizes.contains(dim)) continue;
            sizes.add(dim);
        }
        sizes.sort((p1, p2) -> p1.height == p2.height ? Integer.compare(p1.width, p2.width) : Integer.compare(p1.height, p2.height));
        return sizes.toArray(new Dimension[0]);
    }

    @Override
    public Dimension getVideoMode(long monitor) {
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode((long)monitor);
        if (vidMode == null) {
            throw new NullPointerException("Could not find VidMode for monitor: " + monitor);
        }
        return new Dimension(vidMode.width(), vidMode.height());
    }

    @Override
    public GameMessage getBorderlessDisplayMessage() {
        if (this.doesMonitorNeedHDRHack()) {
            return new LocalMessage("settingsui", "hdrhackwarning");
        }
        return null;
    }

    @Override
    public boolean doesMonitorNeedHDRHack() {
        return this.monitorNeedsHDRHack;
    }

    private static class WindowError {
        public final GameWindow window;
        public final String error;

        public WindowError(GameWindow window) {
            this.window = window;
            this.error = null;
        }

        public WindowError(String error) {
            this.window = null;
            this.error = error;
        }
    }
}

