/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop;

import java.util.function.BiPredicate;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.ShopManager;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class BuyingShopItem
implements IDDataContainer {
    public final IDData idData = new IDData();
    protected ShopManager manager;
    protected Item item;
    protected BuyShopPriceGenerator priceGenerator;
    public BiPredicate<GameRandom, ServerClient> requirement;

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    @Override
    public final int getID() {
        return this.idData.getID();
    }

    @Override
    public String getStringID() {
        return this.idData.getStringID();
    }

    public BuyingShopItem(String customItemStringID) {
        this.item = ItemRegistry.getItem(customItemStringID);
    }

    public BuyingShopItem() {
        this(null);
    }

    public void onRegistered(ShopManager manager) {
        if (this.manager != null) {
            throw new IllegalStateException("BuyingShopItem is already registered to a registry");
        }
        this.manager = manager;
    }

    public void onRegistryClosed() {
        Item item;
        if (this.item == null && (item = ItemRegistry.getItem(this.getStringID())) != null) {
            this.item = item;
        }
        if (this.item == null) {
            throw new NullPointerException("BuyingShopItem item cannot be null");
        }
        if (this.priceGenerator == null) {
            throw new NullPointerException("BuyingShopItem price cannot be null");
        }
    }

    public void init(Level level, int uniqueSeed) {
    }

    public BuyingShopItem setPrice(BuyShopPriceGenerator priceGenerator) {
        this.priceGenerator = priceGenerator;
        return this;
    }

    public BuyingShopItem setRandomPrice(int minPrice, int maxPrice) {
        return this.setPrice((random, client, mob) -> random.getIntBetween(minPrice, maxPrice));
    }

    public BuyingShopItem setStaticPrice(int price) {
        return this.setPrice((random, client, mob) -> price);
    }

    public BuyingShopItem setPriceBasedOnHappiness(int bestPrice, int worstPrice, int randomRange) {
        return this.setPrice((random, client, mob) -> {
            float happinessPercent = GameMath.limit((float)mob.getShopHappiness() / 100.0f, 0.0f, 1.0f);
            int price = GameMath.lerp(happinessPercent, worstPrice, bestPrice);
            if (randomRange > 0) {
                price = GameMath.limit(random.getIntOffset(price, randomRange / 2), worstPrice, bestPrice);
            }
            return price;
        });
    }

    public BuyingShopItem setPriceBasedOnHappiness(int bestPrice, int worstPrice) {
        return this.setPriceBasedOnHappiness(bestPrice, worstPrice, 0);
    }

    public BuyingShopItem setRequirement(BiPredicate<GameRandom, ServerClient> isAvailable) {
        this.requirement = isAvailable;
        return this;
    }

    public BuyingShopItem addRequirement(BiPredicate<GameRandom, ServerClient> isAvailable) {
        this.requirement = this.requirement == null ? isAvailable : this.requirement.and(isAvailable);
        return this;
    }

    public BuyingShopItem addRandomAvailableRequirement(float chance) {
        return this.addRequirement((random, client) -> random.getChance(chance));
    }

    public BuyingShopItem addKilledMobRequirement(String mobStringID) {
        return this.addRequirement((random, client) -> client.characterStats().mob_kills.getKills(mobStringID) > 0);
    }

    public BuyingShopItem addKilledAllMobsRequirement(String ... mobStringIDs) {
        return this.addRequirement((random, client) -> {
            for (String mobStringID : mobStringIDs) {
                if (client.characterStats().mob_kills.getKills(mobStringID) > 0) continue;
                return false;
            }
            return true;
        });
    }

    public BuyingShopItem addKilledEitherMobsRequirement(String ... mobStringIDs) {
        return this.addRequirement((random, client) -> {
            for (String mobStringID : mobStringIDs) {
                if (client.characterStats().mob_kills.getKills(mobStringID) <= 0) continue;
                return true;
            }
            return false;
        });
    }

    public boolean isAvailable(GameRandom random, ServerClient client) {
        return this.requirement == null || this.requirement.test(random, client);
    }

    public int generatePrice(GameRandom random, ServerClient client, HumanShop mob) {
        return this.priceGenerator.generatePrice(random, client, mob);
    }

    @FunctionalInterface
    public static interface BuyShopPriceGenerator {
        public int generatePrice(GameRandom var1, ServerClient var2, HumanShop var3);
    }
}

