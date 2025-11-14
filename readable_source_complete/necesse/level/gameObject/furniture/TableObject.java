/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.level.gameObject.TableObjectInterface;
import necesse.level.gameObject.furniture.FurnitureObject;

public class TableObject
extends FurnitureObject
implements TableObjectInterface {
    public TableObject(Rectangle collision, Color mapColor) {
        super(collision);
        this.mapColor = mapColor;
        this.furnitureType = "table";
    }
}

