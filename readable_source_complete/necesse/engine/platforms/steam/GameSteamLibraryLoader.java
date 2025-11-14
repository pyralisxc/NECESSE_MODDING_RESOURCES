/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamLibraryLoader
 *  org.lwjgl.system.Configuration
 *  org.lwjgl.system.Library
 *  org.lwjgl.system.Platform
 *  org.lwjgl.system.Platform$Architecture
 */
package necesse.engine.platforms.steam;

import com.codedisaster.steamworks.SteamLibraryLoader;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.Library;
import org.lwjgl.system.Platform;

public class GameSteamLibraryLoader
implements SteamLibraryLoader {
    public void setLibraryPath(String libraryPath) {
        System.setProperty("org.lwjgl.librarypath", libraryPath);
    }

    public boolean loadLibrary(String libraryName) {
        boolean debugLWJGLAlreadyEnabled;
        Platform os = Platform.get();
        Platform.Architecture arch = Platform.getArchitecture();
        if (os == Platform.WINDOWS && arch == Platform.Architecture.X64) {
            libraryName = libraryName + "64";
        }
        if (!(debugLWJGLAlreadyEnabled = ((Boolean)Configuration.DEBUG_LOADER.get((Object)false)).booleanValue())) {
            try {
                boolean previousDebugValue = (Boolean)Configuration.DEBUG.get((Object)false);
                boolean previousDebugLoaderValue = (Boolean)Configuration.DEBUG_LOADER.get((Object)false);
                Object previousDebugStream = Configuration.DEBUG_STREAM.get();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream((OutputStream)out, true, StandardCharsets.UTF_8.name());
                Configuration.DEBUG.set((Object)true);
                Configuration.DEBUG_LOADER.set((Object)true);
                Configuration.DEBUG_STREAM.set((Object)printStream);
                try {
                    Library.loadSystem((String)"com.codedisaster.steamworks", (String)libraryName);
                }
                catch (Throwable t) {
                    throw new RuntimeException("Failed to load Steam library: " + libraryName + " \nLWJGL debug output:\n" + out.toString(StandardCharsets.UTF_8.name()), t);
                }
                Configuration.DEBUG.set((Object)previousDebugValue);
                Configuration.DEBUG_LOADER.set((Object)previousDebugLoaderValue);
                if (previousDebugStream != null) {
                    Configuration.DEBUG_STREAM.set(previousDebugStream);
                }
                printStream.close();
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Library.loadSystem((String)"com.codedisaster.steamworks", (String)libraryName);
        }
        catch (Throwable t) {
            throw new RuntimeException("Failed to load Steam library: " + libraryName, t);
        }
        return true;
    }
}

