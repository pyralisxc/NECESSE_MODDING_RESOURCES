/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import java.util.Arrays;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;

public abstract class ItemSearchTester {
    public final String string;

    private ItemSearchTester(String string) {
        this.string = string;
    }

    public abstract boolean matches(InventoryItem var1, PlayerMob var2, GameBlackboard var3);

    public static ItemSearchTester constructSearchTester(String search) {
        if (search == null || search.isEmpty()) {
            return new ItemSearchTester(search){

                @Override
                public boolean matches(InventoryItem item, PlayerMob perspective, GameBlackboard tooltipBlackboard) {
                    return true;
                }
            };
        }
        final String[] split = search.toLowerCase().split("[|]");
        return new ItemSearchTester(search){

            @Override
            public boolean matches(InventoryItem item, PlayerMob perspective, GameBlackboard tooltipBlackboard) {
                return Arrays.stream(split).anyMatch(str -> {
                    boolean searchTooltip = false;
                    if (str.startsWith("@")) {
                        searchTooltip = true;
                        str = str.substring(1);
                    }
                    return item.item.matchesSearch(item, perspective, (String)str, searchTooltip ? tooltipBlackboard : null);
                });
            }
        };
    }
}

