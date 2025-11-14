/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.mapData;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;

public class DiscoveredMapData {
    private int tileWidth;
    private int tileHeight;
    private int[] colors;
    private String base64Colors;

    public DiscoveredMapData(int tileWidth, int tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.colors = new int[tileWidth * tileHeight];
    }

    public int getIndex(int mapRegionTileX, int mapRegionTileY, int tileWidth) {
        return mapRegionTileX + mapRegionTileY * tileWidth;
    }

    public int getIndex(int mapRegionTileX, int mapRegionTileY) {
        return this.getIndex(mapRegionTileX, mapRegionTileY, this.tileWidth);
    }

    public int getTileWidth() {
        return this.tileWidth;
    }

    public int getTileHeight() {
        return this.tileHeight;
    }

    public void makeFinal() {
        if (this.colors == null) {
            return;
        }
        try {
            this.base64Colors = DiscoveredMapData.toBase64(this.colors);
            this.colors = null;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isFinal() {
        return this.colors == null;
    }

    public boolean ensureNotFinal() {
        if (this.colors != null) {
            return false;
        }
        try {
            this.colors = DiscoveredMapData.toColors(this.base64Colors, this.tileWidth, this.tileHeight);
        }
        catch (IOException | DataFormatException e) {
            e.printStackTrace();
            this.colors = new int[this.tileWidth * this.tileHeight];
        }
        this.base64Colors = null;
        return true;
    }

    public SaveData getSaveData(String name) throws IOException {
        SaveData saveData = new SaveData(name);
        saveData.addInt("width", this.tileWidth);
        saveData.addInt("height", this.tileHeight);
        if (this.base64Colors != null) {
            saveData.addSafeString("colors", this.base64Colors);
        } else {
            saveData.addSafeString("colors", DiscoveredMapData.toBase64(this.colors));
        }
        return saveData;
    }

    public void saveToFileSystem(File file, int mapRegionX, int mapRegionY) throws IOException {
        this.getSaveData(mapRegionX + "x" + mapRegionY).saveScript(file);
    }

    public void applySaveData(LoadData save, boolean allowSizeChange) throws DataFormatException, IOException {
        if (!save.isArray()) {
            return;
        }
        int nextTileWidth = save.getInt("width", -1, false);
        int nextTileHeight = save.getInt("height", -1, false);
        if (!(allowSizeChange || nextTileWidth == this.tileWidth && nextTileHeight == this.tileHeight)) {
            throw new LoadDataException("Map region size changed while not being allowed to: " + this.tileWidth + "x" + this.tileHeight + " -> " + nextTileWidth + "x" + nextTileHeight);
        }
        this.tileWidth = nextTileWidth;
        this.tileHeight = nextTileHeight;
        if (this.tileWidth < 0 || this.tileHeight < 0) {
            throw new LoadDataException("Invalid map region size: " + this.tileWidth + "x" + this.tileHeight);
        }
        String colorsString = save.getSafeString("colors", null, false);
        if (colorsString == null) {
            throw new LoadDataException("Missing map region colors");
        }
        if (this.colors == null) {
            this.base64Colors = colorsString;
        } else {
            this.colors = DiscoveredMapData.toColors(colorsString, this.tileWidth, this.tileHeight);
        }
    }

    public void writePacketData(PacketWriter writer) throws IOException, DataFormatException {
        byte[] compressedData = this.colors != null ? DiscoveredMapData.toCompressedData(this.colors) : DiscoveredMapData.toCompressedData(this.base64Colors);
        writer.putNextShortUnsigned(this.tileWidth);
        writer.putNextShortUnsigned(this.tileHeight);
        writer.putNextInt(compressedData.length);
        writer.putNextBytes(compressedData);
    }

    public boolean applyPacketData(PacketReader reader) throws DataFormatException, IOException {
        int tileWidth = reader.getNextShortUnsigned();
        int tileHeight = reader.getNextShortUnsigned();
        int compressedDataLength = reader.getNextInt();
        byte[] compressedData = reader.getNextBytes(compressedDataLength);
        int[] colors = DiscoveredMapData.toColors(compressedData, tileWidth, tileHeight);
        return this.combine(colors, tileWidth, tileHeight);
    }

    public static String toBase64(int[] colors) throws IOException {
        return GameUtils.toBase64(DiscoveredMapData.toCompressedData(colors));
    }

    public static byte[] toCompressedData(int[] colors) throws IOException {
        byte[] data = new byte[colors.length * 3];
        for (int i = 0; i < colors.length; ++i) {
            int rgb = colors[i];
            data[i * 3] = (byte)(rgb >> 16 & 0xFF);
            data[i * 3 + 1] = (byte)(rgb >> 8 & 0xFF);
            data[i * 3 + 2] = (byte)(rgb & 0xFF);
        }
        return GameUtils.compressData(data);
    }

    public static byte[] toCompressedData(String base64String) throws DataFormatException {
        return GameUtils.fromBase64(base64String);
    }

    public static int[] toColors(String base64String, int expectedWidth, int expectedHeight) throws DataFormatException, IOException {
        return DiscoveredMapData.toColors(DiscoveredMapData.toCompressedData(base64String), expectedWidth, expectedHeight);
    }

    public static int[] toColors(byte[] compressedData, int expectedWidth, int expectedHeight) throws DataFormatException, IOException {
        int[] colors;
        byte[] rgbColors = GameUtils.decompressData(compressedData);
        if (rgbColors.length != (colors = new int[expectedWidth * expectedHeight]).length * 3) {
            throw new DataFormatException("Invalid map region data length: " + rgbColors.length + " != " + colors.length * 3);
        }
        for (int i = 0; i < colors.length; ++i) {
            int r = rgbColors[i * 3] & 0xFF;
            int g = rgbColors[i * 3 + 1] & 0xFF;
            int b = rgbColors[i * 3 + 2] & 0xFF;
            colors[i] = r << 16 | g << 8 | b;
        }
        return colors;
    }

    public boolean setRGB(int mapRegionTileX, int mapRegionTileY, int rgb) {
        this.ensureNotFinal();
        int index = this.getIndex(mapRegionTileX, mapRegionTileY);
        int lastColor = this.colors[index];
        if (lastColor == rgb) {
            return false;
        }
        this.colors[index] = rgb;
        return true;
    }

    public int getRGB(int mapRegionTileX, int mapRegionTileY) {
        this.ensureNotFinal();
        return this.colors[this.getIndex(mapRegionTileX, mapRegionTileY)];
    }

    public boolean combine(int[] colors, int tileWidth, int tileHeight) {
        if (colors.length != tileWidth * tileHeight) {
            System.err.println("Colors size did not match: " + tileWidth + "x" + tileHeight + ", " + colors.length);
            return false;
        }
        if (tileWidth > 256 || tileHeight > 256) {
            System.err.println("Map region size is too big: " + tileWidth + "x" + tileHeight);
            return false;
        }
        this.ensureNotFinal();
        if (tileWidth > this.tileWidth || tileHeight > this.tileHeight) {
            int oldTileWidth = this.tileWidth;
            int oldTileHeight = this.tileHeight;
            int[] oldColors = this.colors;
            this.tileWidth = Math.max(this.tileWidth, tileWidth);
            this.tileHeight = Math.max(this.tileHeight, tileHeight);
            this.colors = new int[this.tileWidth * this.tileHeight];
            for (int x = 0; x < oldTileWidth; ++x) {
                for (int y = 0; y < oldTileHeight; ++y) {
                    this.colors[x + y * this.tileWidth] = oldColors[x + y * oldTileWidth];
                }
            }
        }
        boolean out = false;
        if (tileWidth == this.tileWidth && tileHeight == this.tileHeight) {
            for (int i = 0; i < this.colors.length; ++i) {
                int otherColor;
                int tileColor = this.colors[i];
                if (tileColor != 0 || (otherColor = colors[i]) == 0) continue;
                this.colors[i] = otherColor;
                out = true;
            }
        } else {
            for (int x = 0; x < tileWidth; ++x) {
                for (int y = 0; y < tileHeight; ++y) {
                    int otherColor;
                    int tileColor = this.colors[x + y * this.tileWidth];
                    if (tileColor != 0 || (otherColor = colors[x + y * tileWidth]) == 0) continue;
                    this.colors[x + y * this.tileWidth] = otherColor;
                    out = true;
                }
            }
        }
        return out;
    }

    public boolean combine(DiscoveredMapData other) {
        return this.combine(other.colors, other.tileWidth, other.tileHeight);
    }
}

