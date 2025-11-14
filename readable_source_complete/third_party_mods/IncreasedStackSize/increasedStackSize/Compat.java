/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GlobalData
 */
package increasedStackSize;

import increasedStackSize.IncreasedStackSize;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Objects;
import necesse.engine.GlobalData;

public class Compat {
    public static void backwardCompatLoad() {
        String filename = GlobalData.rootPath() + "settings/increasedStackSize/settings.cfg";
        File file = new File(filename);
        if (!file.exists()) {
            return;
        }
        try {
            String line;
            InputStreamReader isr = new InputStreamReader(Files.newInputStream(file.toPath(), new OpenOption[0]), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                String[] temp;
                if (line.isEmpty() || !Objects.equals((temp = line.split("="))[0], "multiplier")) continue;
                IncreasedStackSize.setStackSizeMultiplier(Integer.parseInt(temp[1]), true);
            }
            br.close();
            isr.close();
            boolean bl = file.delete();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

