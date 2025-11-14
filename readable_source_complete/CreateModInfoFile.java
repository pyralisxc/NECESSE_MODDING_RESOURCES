/*
 * Decompiled with CFR 0.152.
 */
import java.io.File;
import java.util.HashMap;
import necesse.engine.GameLaunch;
import necesse.engine.modLoader.ModInfoFile;
import necesse.engine.util.GameUtils;

public class CreateModInfoFile {
    public static void main(String[] args) {
        String path;
        HashMap<String, String> options = GameLaunch.parseLaunchOptions(args);
        String fullLaunchParameters = GameUtils.join(GameLaunch.quoteArgs(args), " ");
        if (args.length > 0) {
            System.out.println("Launched CreateModInfoFile with arguments: " + fullLaunchParameters);
        }
        if ((path = options.remove("file")) == null) {
            path = "mod.info";
        }
        File file = new File(path);
        System.out.println("Creating mod info file at " + file.getAbsolutePath());
        ModInfoFile.saveModInfoFile(file, options);
    }
}

