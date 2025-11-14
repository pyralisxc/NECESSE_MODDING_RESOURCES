/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem;

public enum ToolType {
    UNBREAKABLE,
    NONE,
    ALL,
    AXE,
    PICKAXE,
    SHOVEL;


    public boolean canDealDamageTo(ToolType toolType) {
        return toolType != UNBREAKABLE && (toolType == this || this == ALL || toolType == ALL);
    }
}

