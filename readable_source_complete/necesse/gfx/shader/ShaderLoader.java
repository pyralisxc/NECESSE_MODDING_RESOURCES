/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL20
 */
package necesse.gfx.shader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.gfx.res.ResourceEncoder;
import org.lwjgl.opengl.GL20;

public class ShaderLoader {
    public static int loadFragmentShader(String file) {
        return ShaderLoader.loadShader(file, 35632);
    }

    public static int loadVertexShader(String file) {
        return ShaderLoader.loadShader(file, 35633);
    }

    public static int loadShader(String file, int shaderType) {
        StringBuilder shaderSource = new StringBuilder();
        int shaderID = GL20.glCreateShader((int)shaderType);
        try {
            String line;
            InputStream is;
            if (shaderType == 35633) {
                file = "shaders/vertex/" + file + ".glsl";
            } else if (shaderType == 35632) {
                file = "shaders/fragment/" + file + ".glsl";
            }
            boolean foundInFile = false;
            File outsideFile = new File(GlobalData.rootPath() + "res/" + file);
            if (outsideFile.exists()) {
                is = new FileInputStream(outsideFile);
                foundInFile = true;
            } else {
                try {
                    is = ResourceEncoder.getResourceInputStream(file);
                    foundInFile = true;
                }
                catch (FileNotFoundException e) {
                    is = new FileInputStream(outsideFile);
                }
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            br.close();
            is.close();
            if (!foundInFile && !GlobalData.isDevMode()) {
                GameLog.warn.println(file + " was not found in resource file.");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        GL20.glShaderSource((int)shaderID, (CharSequence)shaderSource);
        GL20.glCompileShader((int)shaderID);
        if (GL20.glGetShaderi((int)shaderID, (int)35713) == 0) {
            System.err.println(file + " shader was not compiled correctly.");
            System.err.println(GL20.glGetShaderInfoLog((int)shaderID, (int)GL20.glGetShaderi((int)shaderID, (int)35716)));
        }
        return shaderID;
    }
}

