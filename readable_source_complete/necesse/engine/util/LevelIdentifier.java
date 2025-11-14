/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Point;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.InvalidLevelIdentifierException;

public class LevelIdentifier {
    public static final Pattern levelIdentifierPattern = Pattern.compile("[a-z0-9-+]{1,50}");
    public static final Pattern islandStringPattern = Pattern.compile("(-?(?:\\d)+)x(-?(?:\\d)+)d(-?(?:\\d)+)");
    public static final LevelIdentifier SURFACE_IDENTIFIER = new LevelIdentifier("surface");
    public static final LevelIdentifier CAVE_IDENTIFIER = new LevelIdentifier("cave");
    public static final LevelIdentifier DEEP_CAVE_IDENTIFIER = new LevelIdentifier("deepcave");
    public static HashMap<String, Integer> IDENTIFIER_TO_DIMENSION = new HashMap();
    public final String stringID;
    private boolean calculatedIsland;
    private boolean isIslandPosition;
    private int islandX;
    private int islandY;
    private int dimension;
    private boolean calculatedOneWorldDimension;
    private Integer oneWorldDimension;

    public static String getIslandIdentifier(int islandX, int islandY, int dimension) {
        return islandX + "x" + islandY + "d" + dimension;
    }

    public LevelIdentifier(String stringID) {
        if (stringID == null || !levelIdentifierPattern.matcher(stringID).matches()) {
            throw new InvalidLevelIdentifierException("Invalid level identifier string \"" + stringID + "\". Must match regex: " + levelIdentifierPattern.pattern());
        }
        this.stringID = stringID;
    }

    public LevelIdentifier(int islandX, int islandY, int dimension) {
        this.islandX = islandX;
        this.islandY = islandY;
        this.dimension = dimension;
        this.stringID = LevelIdentifier.getIslandIdentifier(islandX, islandY, dimension);
        this.calculatedIsland = true;
        this.isIslandPosition = true;
    }

    public LevelIdentifier(Point island, int dimension) {
        this(island.x, island.y, dimension);
    }

    public LevelIdentifier(PacketReader reader) {
        if (reader.getNextBoolean()) {
            this.islandX = reader.getNextInt();
            this.islandY = reader.getNextInt();
            this.dimension = reader.getNextInt();
            this.stringID = LevelIdentifier.getIslandIdentifier(this.islandX, this.islandY, this.dimension);
            this.calculatedIsland = true;
            this.isIslandPosition = true;
        } else {
            this.stringID = reader.getNextString();
            this.calculatedIsland = true;
            this.isIslandPosition = false;
        }
    }

    public void writePacket(PacketWriter writer) {
        if (this.isIslandPosition()) {
            writer.putNextBoolean(true);
            writer.putNextInt(this.islandX);
            writer.putNextInt(this.islandY);
            writer.putNextInt(this.dimension);
        } else {
            writer.putNextBoolean(false);
            writer.putNextString(this.stringID);
        }
    }

    private void calculateIsland() {
        if (this.calculatedIsland) {
            return;
        }
        this.calculatedIsland = true;
        Matcher matcher = islandStringPattern.matcher(this.stringID);
        if (matcher.matches()) {
            this.isIslandPosition = true;
            this.islandX = Integer.parseInt(matcher.group(1));
            this.islandY = Integer.parseInt(matcher.group(2));
            this.dimension = Integer.parseInt(matcher.group(3));
        } else {
            this.isIslandPosition = false;
        }
    }

    public boolean isSurface() {
        return this.equals(SURFACE_IDENTIFIER);
    }

    public boolean isCave() {
        return this.equals(CAVE_IDENTIFIER);
    }

    public boolean isDeepCave() {
        return this.equals(DEEP_CAVE_IDENTIFIER);
    }

    public boolean isOneWorldDimension() {
        this.calculateOneWorldDimensionIfNeeded();
        return this.oneWorldDimension != null;
    }

    public int getOneWorldDimension() {
        this.calculateOneWorldDimensionIfNeeded();
        return this.oneWorldDimension;
    }

    private void calculateOneWorldDimensionIfNeeded() {
        if (this.calculatedOneWorldDimension) {
            return;
        }
        this.calculatedOneWorldDimension = true;
        this.oneWorldDimension = IDENTIFIER_TO_DIMENSION.get(this.stringID);
    }

    public boolean isSameOneWorldDimension(LevelIdentifier other) {
        if (!this.isOneWorldDimension() || !other.isOneWorldDimension()) {
            return false;
        }
        return other.getOneWorldDimension() == this.getOneWorldDimension();
    }

    public int getOneWorldDimensionDelta(LevelIdentifier other) {
        if (!this.isOneWorldDimension() || !other.isOneWorldDimension()) {
            return 0;
        }
        return other.getOneWorldDimension() - this.getOneWorldDimension();
    }

    public boolean isIslandPosition() {
        this.calculateIsland();
        return this.isIslandPosition;
    }

    public int getIslandX() {
        this.calculateIsland();
        return this.islandX;
    }

    public int getIslandY() {
        this.calculateIsland();
        return this.islandY;
    }

    public int getIslandDimension() {
        this.calculateIsland();
        return this.dimension;
    }

    public boolean equals(String stringID) {
        return this.stringID.equals(stringID);
    }

    public boolean equals(LevelIdentifier identifier) {
        if (identifier == this) {
            return true;
        }
        if (this.isIslandPosition() && identifier.isIslandPosition()) {
            return this.islandX == identifier.islandX && this.islandY == identifier.islandY && this.dimension == identifier.dimension;
        }
        return this.stringID.equals(identifier.stringID);
    }

    public boolean equals(int islandX, int islandY, int dimension) {
        if (!this.isIslandPosition()) {
            return false;
        }
        return this.islandX == islandX && this.islandY == islandY && this.dimension == dimension;
    }

    public boolean equals(Point island, int dimension) {
        return this.equals(island.x, island.y, dimension);
    }

    public boolean isSameIsland(LevelIdentifier other) {
        if (!other.isIslandPosition()) {
            return false;
        }
        return this.isSameIsland(other.getIslandX(), other.getIslandY());
    }

    public boolean isSameIsland(int islandX, int islandY) {
        if (!this.isIslandPosition()) {
            return false;
        }
        return this.islandX == islandX && this.islandY == islandY;
    }

    public boolean isSameIsland(Point island) {
        return this.isSameIsland(island.x, island.y);
    }

    public LocalMessage getDisplayName() {
        if (Localization.English.isMissing("level", this.stringID)) {
            return new LocalMessage("level", "unknown");
        }
        return new LocalMessage("level", this.stringID);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof LevelIdentifier) {
            return this.equals((LevelIdentifier)obj);
        }
        if (obj instanceof String) {
            return this.equals((String)obj);
        }
        return false;
    }

    public int hashCode() {
        return this.stringID.hashCode();
    }

    public String toString() {
        return this.stringID;
    }

    static {
        IDENTIFIER_TO_DIMENSION.put("surface", 0);
        IDENTIFIER_TO_DIMENSION.put("cave", -1);
        IDENTIFIER_TO_DIMENSION.put("deepcave", -2);
    }
}

