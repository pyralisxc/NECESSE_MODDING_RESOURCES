/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.level.gameObject.TreeObject
 */
package aphorea.objects;

import java.awt.Color;
import necesse.level.gameObject.TreeObject;

public class AphTreeObject
extends TreeObject {
    public AphTreeObject(String textureName, String logStringID, String saplingStringID, Color mapColor, int leavesCenterWidth, int leavesMinHeight, int leavesMaxHeight, String leavesTextureName) {
        super(textureName, logStringID, saplingStringID, mapColor, leavesCenterWidth, leavesMinHeight, leavesMaxHeight, leavesTextureName);
    }

    public AphTreeObject(String textureName, String logStringID, String saplingStringID, Color mapColor, int leavesCenterWidth, int leavesMinHeight, int leavesMaxHeight, String leavesTextureName, int lightLevel, float lightHue, float lightSat) {
        this(textureName, logStringID, saplingStringID, mapColor, leavesCenterWidth, leavesMinHeight, leavesMaxHeight, leavesTextureName);
        this.lightLevel = lightLevel;
        this.lightHue = lightHue;
        this.lightSat = lightSat;
    }
}

