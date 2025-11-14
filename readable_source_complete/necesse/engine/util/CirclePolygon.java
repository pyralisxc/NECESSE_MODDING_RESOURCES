/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Polygon;

public class CirclePolygon
extends Polygon {
    public CirclePolygon(int centerX, int centerY, int xRadius, int yRadius, int numSides) {
        super(CirclePolygon.getXCoords(centerX, xRadius, numSides), CirclePolygon.getYCoords(centerY, yRadius, numSides), numSides);
    }

    private static int[] getXCoords(int centerX, int radius, int numSides) {
        int[] xCoords = new int[numSides];
        for (int i = 0; i < numSides; ++i) {
            double angle = Math.PI * 2 * (double)i / (double)numSides;
            xCoords[i] = centerX + (int)((double)radius * Math.cos(angle));
        }
        return xCoords;
    }

    private static int[] getYCoords(int centerY, int radius, int numSides) {
        int[] yCoords = new int[numSides];
        for (int i = 0; i < numSides; ++i) {
            double angle = Math.PI * 2 * (double)i / (double)numSides;
            yCoords[i] = centerY + (int)((double)radius * Math.sin(angle));
        }
        return yCoords;
    }
}

