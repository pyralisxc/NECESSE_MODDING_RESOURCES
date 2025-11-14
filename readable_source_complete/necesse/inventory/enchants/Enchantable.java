/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.enchants;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.ItemEnchantment;

public interface Enchantable<T extends ItemEnchantment> {
    public void setEnchantment(InventoryItem var1, int var2);

    public int getEnchantmentID(InventoryItem var1);

    public void clearEnchantment(InventoryItem var1);

    public T getEnchantment(InventoryItem var1);

    public boolean isValidEnchantment(InventoryItem var1, ItemEnchantment var2);

    public Set<Integer> getValidEnchantmentIDs(InventoryItem var1);

    public T getRandomEnchantment(GameRandom var1, InventoryItem var2);

    public int getEnchantCost(InventoryItem var1);

    default public int getRandomEnchantmentID(GameRandom random, InventoryItem item) {
        T out = this.getRandomEnchantment(random, item);
        if (out == null) {
            return 0;
        }
        return out.getID();
    }

    default public int getFinalEnchantCost(InventoryItem item) {
        return (int)((float)this.getEnchantCost(item) * ((ItemEnchantment)this.getEnchantment(item)).getEnchantCostMod());
    }

    default public int getRandomEnchantCost(InventoryItem item, GameRandom random, int happiness) {
        return HumanShop.getRandomHappinessMiddlePrice(random, happiness, this.getFinalEnchantCost(item), 6, 3);
    }

    default public String getEnchantName(InventoryItem item) {
        return ((ItemEnchantment)this.getEnchantment(item)).getDisplayName();
    }

    default public void addRandomEnchantment(InventoryItem item, GameRandom random) {
        this.setEnchantment(item, this.getRandomEnchantmentID(random, item));
    }

    default public void addRandomEnchantment(InventoryItem item) {
        this.setEnchantment(item, this.getRandomEnchantment(item));
    }

    default public int getRandomEnchantment(InventoryItem item) {
        return this.getRandomEnchantmentID(GameRandom.globalRandom, item);
    }

    default public GameTooltips getEnchantmentTooltips(InventoryItem item) {
        return ((ItemEnchantment)this.getEnchantment(item)).getTooltips();
    }

    public static <T extends ItemEnchantment> T getRandomEnchantment(GameRandom random, Set<Integer> setIDs, Predicate<Integer> filterIDs, Class<T> expectedClass, T defaultReturn) {
        List excluded = setIDs.stream().filter(filterIDs).collect(Collectors.toList());
        if (excluded.isEmpty()) {
            return null;
        }
        int id = (Integer)excluded.get(random.nextInt(excluded.size()));
        return EnchantmentRegistry.getEnchantment(id, expectedClass, defaultReturn);
    }

    public static <T extends ItemEnchantment> T getRandomEnchantment(GameRandom random, Set<Integer> setIDs, int excludeID, Class<T> expectedClass) {
        return Enchantable.getRandomEnchantment(random, setIDs, id -> id != excludeID, expectedClass, null);
    }
}

