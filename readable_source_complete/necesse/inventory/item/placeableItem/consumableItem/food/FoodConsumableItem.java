/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.food;

import java.awt.Color;
import java.awt.geom.Line2D;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketMobBuff;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.HungerMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.FoodBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemUsed;
import necesse.inventory.item.placeableItem.consumableItem.AdventurePartyConsumableItem;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.settler.FoodQuality;

public class FoodConsumableItem
extends ConsumableItem
implements AdventurePartyConsumableItem {
    public FoodQuality quality;
    public int nutrition;
    public boolean isDebuff;
    protected String cropTextureName;
    public int duration;
    public boolean drinkSound;
    public ModifierValue<?>[] modifiers;
    public GameTexture buffTexture;

    public FoodConsumableItem(int stackSize, Item.Rarity rarity, FoodQuality quality, int nutrition, int buffSecondsDuration, boolean drinkSound, ModifierValue<?> ... modifiers) {
        super(stackSize, true);
        this.duration = buffSecondsDuration;
        this.rarity = rarity;
        this.quality = quality;
        this.nutrition = nutrition;
        this.drinkSound = drinkSound;
        this.modifiers = modifiers;
        if (quality != null) {
            this.setItemCategory(quality.masterCategoryTree);
        } else {
            this.setItemCategory("consumable", "food");
        }
        this.keyWords.add("food");
        this.dropDecayTimeMillis = 1800000L;
        this.dropsAsMatDeathPenalty = false;
    }

    public FoodConsumableItem(int stackSize, Item.Rarity rarity, FoodQuality quality, int nutrition, int buffSecondsDuration, ModifierValue<?> ... modifiers) {
        this(stackSize, rarity, quality, nutrition, buffSecondsDuration, false, modifiers);
    }

    @Override
    public void registerItemCategory() {
        if (this.quality != null) {
            this.setItemCategory(this.quality.masterCategoryTree);
        } else {
            this.setItemCategory("consumable", "food");
        }
        super.registerItemCategory();
        if (this.quality != null) {
            ItemCategory.foodQualityManager.setItemCategory((Item)this, this.quality.foodCategory);
        }
        if (this.quality != null) {
            ItemCategory.craftingManager.setItemCategory((Item)this, this.quality.craftingCategory);
        }
    }

    @Override
    public boolean getConstantUse(InventoryItem item) {
        return true;
    }

    public FoodConsumableItem cropTexture(String textureName) {
        this.cropTextureName = textureName;
        return this;
    }

    public FoodConsumableItem debuff() {
        this.isDebuff = true;
        return this;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        GameTexture mask = GameTexture.fromFile("buffs/mask", true);
        GameTexture base = GameTexture.fromFile(this.isDebuff ? "buffs/negative" : "buffs/positive", true);
        this.buffTexture = new GameTexture("buffs/food " + this.getStringID(), base.getWidth(), base.getHeight());
        int itemXOffset = (this.buffTexture.getWidth() - this.itemTexture.getWidth()) / 2;
        int itemYOffset = (this.buffTexture.getHeight() - this.itemTexture.getHeight()) / 2;
        for (int x = 0; x < this.buffTexture.getWidth(); ++x) {
            for (int y = 0; y < this.buffTexture.getHeight(); ++y) {
                Color maskedCol = MergeFunction.GLBLEND.merge(this.itemTexture.getColor(x + itemXOffset, y + itemYOffset), mask.getColor(x, y));
                this.buffTexture.setPixel(x, y, MergeFunction.NORMAL.merge(base.getColor(x, y), maskedCol));
            }
        }
        this.buffTexture.makeFinal();
        this.itemTexture.makeFinal();
    }

    @Override
    protected void loadItemTextures() {
        this.itemTexture = this.cropTextureName != null ? new GameTexture(GameTexture.fromFile("objects/" + this.cropTextureName), 0, 0, 32) : GameTexture.fromFile("items/" + this.getStringID(), true);
    }

    @Override
    public boolean shouldSendToOtherClients(Level level, int x, int y, PlayerMob player, InventoryItem item, String error, GNDItemMap mapContent) {
        return error == null;
    }

    @Override
    public void onOtherPlayerPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent) {
        this.playConsumeSound(level, player, item);
    }

    public String canConsume(Level level, PlayerMob player, InventoryItem item) {
        if (!FoodConsumableItem.canEatFood(player, this)) {
            return "alreadyconsumed";
        }
        return null;
    }

    public boolean consume(Level level, HungerMob hungerMob, InventoryItem item) {
        boolean hungerEnabled;
        boolean bl = hungerEnabled = level.getWorldSettings() == null || level.getWorldSettings().playerHunger();
        if (this.isDebuff && hungerMob.getHungerLevel() >= 1.0f && hungerEnabled) {
            return false;
        }
        if (hungerMob.useFoodItem(this, true)) {
            if (this.isSingleUse(null)) {
                item.setAmount(item.getAmount() - 1);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canAndShouldPartyConsume(Level level, HumanMob mob, ServerClient partyClient, InventoryItem item, String purpose) {
        return mob.hungerLevel <= 0.3f;
    }

    @Override
    public InventoryItem onPartyConsume(Level level, HumanMob mob, ServerClient partyClient, InventoryItem item, String purpose) {
        mob.useFoodItem(this, true);
        InventoryItem out = item.copy();
        if (this.isSingleUse(null)) {
            item.setAmount(item.getAmount() - 1);
        }
        return out;
    }

    public void giveFoodBuff(Mob mob) {
        ActiveBuff ab = FoodConsumableItem.giveFoodBuff(mob, this);
        if (mob.isServer()) {
            mob.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobBuff(mob.getUniqueID(), ab, false), mob);
        }
    }

    public void playConsumeSound(Level level, ItemAttackerMob attackerMob, InventoryItem item) {
        SoundManager.playSound(this.drinkSound ? GameResources.drink : GameResources.eat, (SoundEffect)SoundEffect.effect(attackerMob));
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (this.consume(level, player, item) && !level.isServer()) {
            this.playConsumeSound(level, player, item);
        }
        return item;
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        return this.canConsume(level, player, item);
    }

    @Override
    public ComparableSequence<Integer> getInventoryPriority(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose) {
        ComparableSequence<Integer> last = super.getInventoryPriority(level, player, inventory, inventorySlot, item, purpose);
        if (purpose.equals("usebuffpotion") || purpose.equals("usehealthpotion") || purpose.equals("eatfood")) {
            return last.beforeBy(this.isDebuff ? 100 : -this.quality.happinessIncrease);
        }
        return last;
    }

    @Override
    public ItemUsed eatFood(Level level, PlayerMob player, int seed, InventoryItem item) {
        boolean hungerEnabled;
        boolean bl = hungerEnabled = level.getWorldSettings() == null || level.getWorldSettings().playerHunger();
        if (player.hungerLevel >= 1.0f && hungerEnabled) {
            return new ItemUsed(false, item);
        }
        if (!hungerEnabled && !FoodConsumableItem.doesMobHaveEmptyFoodSlot(player)) {
            return new ItemUsed(false, item);
        }
        String error = this.canPlace(level, 0, 0, player, null, item, null);
        if (error == null) {
            return new ItemUsed(true, this.onPlace(level, 0, 0, player, seed, item, null));
        }
        return new ItemUsed(false, this.onAttemptPlace(level, 0, 0, player, item, null, error));
    }

    @Override
    public ItemUsed useBuffPotion(Level level, PlayerMob player, int seed, InventoryItem item) {
        if (this.isDebuff) {
            return new ItemUsed(false, item);
        }
        if (FoodConsumableItem.doesMobHaveFoodBuff(player, this)) {
            return new ItemUsed(false, item);
        }
        if (!FoodConsumableItem.doesMobHaveEmptyFoodSlot(player)) {
            return new ItemUsed(false, item);
        }
        String error = this.canPlace(level, 0, 0, player, null, item, null);
        if (error == null) {
            return new ItemUsed(true, this.onPlace(level, 0, 0, player, seed, item, null));
        }
        return new ItemUsed(false, this.onAttemptPlace(level, 0, 0, player, item, null, error));
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "consumetip"));
        if (perspective == null || perspective.getWorldSettings() == null || perspective.getWorldSettings().playerHunger()) {
            tooltips.add(Localization.translate("itemtooltip", "nutritiontip", "value", (Object)this.nutrition));
        }
        if (this.quality != null) {
            tooltips.add(Localization.translate("itemtooltip", "foodqualitytip", "quality", this.quality.displayName.translate()));
        }
        tooltips.add(FoodConsumableItem.getBuffDurationMessage(this.duration));
        for (ModifierValue<?> modifier : this.modifiers) {
            ModifierTooltip modifierTooltip = modifier.getTooltip();
            tooltips.add(modifierTooltip.toTooltip(true));
        }
        return tooltips;
    }

    public static boolean doesMobHaveAnyFoodBuff(Mob mob) {
        for (FoodBuff foodBuff : BuffRegistry.FOOD_BUFFS) {
            if (!mob.buffManager.hasBuff(foodBuff)) continue;
            return true;
        }
        return false;
    }

    public static boolean doesMobHaveFoodBuff(Mob mob, FoodConsumableItem item) {
        for (FoodBuff foodBuff : BuffRegistry.FOOD_BUFFS) {
            FoodConsumableItem buffItem;
            ActiveBuff buff = mob.buffManager.getBuff(foodBuff);
            if (buff == null || (buffItem = FoodBuff.getFoodItem(buff)) == null || buffItem.getID() != item.getID()) continue;
            return true;
        }
        return false;
    }

    public static boolean doesMobHaveEmptyFoodSlot(Mob mob) {
        int totalFoundBuffs = 0;
        for (FoodBuff foodBuff : BuffRegistry.FOOD_BUFFS) {
            if (!mob.buffManager.hasBuff(foodBuff)) continue;
            ++totalFoundBuffs;
        }
        int maxFoodBuffs = GameMath.limit(mob.buffManager.getModifier(BuffModifiers.MAX_FOOD_BUFFS), 1, 10);
        return totalFoundBuffs < maxFoodBuffs;
    }

    public static boolean canEatFood(HungerMob hungerMob, FoodConsumableItem item) {
        boolean hungerEnabled;
        Mob mob = (Mob)((Object)hungerMob);
        boolean bl = hungerEnabled = mob.getWorldSettings() == null || mob.getWorldSettings().playerHunger();
        if (item.isDebuff) {
            if (!hungerEnabled) {
                return false;
            }
            return hungerMob.getHungerLevel() < 1.0f;
        }
        if (hungerEnabled && hungerMob.getHungerLevel() < 1.0f) {
            return true;
        }
        for (FoodBuff foodBuff : BuffRegistry.FOOD_BUFFS) {
            FoodConsumableItem buffItem;
            ActiveBuff buff = mob.buffManager.getBuff(foodBuff);
            if (buff == null || (buffItem = FoodBuff.getFoodItem(buff)) == null || buffItem.getID() != item.getID()) continue;
            return false;
        }
        return true;
    }

    public static ActiveBuff giveFoodBuff(Mob mob, FoodConsumableItem item) {
        ActiveBuff buff;
        if (item.isDebuff) {
            ActiveBuff ab = new ActiveBuff((Buff)BuffRegistry.FOOD_DEBUFF, mob, (float)item.duration, null);
            FoodBuff.setFoodItem(ab, item);
            mob.buffManager.addBuff(ab, false, true);
            return ab;
        }
        int maxFoodBuffs = GameMath.limit(mob.buffManager.getModifier(BuffModifiers.MAX_FOOD_BUFFS), 1, 10);
        FoodBuff buffToUse = null;
        int bestFoundEatenIndex = 0;
        int totalFoundBuffs = 0;
        for (FoodBuff foodBuff : BuffRegistry.FOOD_BUFFS) {
            buff = mob.buffManager.getBuff(foodBuff);
            if (buff == null) continue;
            ++totalFoundBuffs;
            bestFoundEatenIndex = Math.max(bestFoundEatenIndex, buff.getGndData().getInt("eatenIndex"));
        }
        if (totalFoundBuffs < maxFoodBuffs) {
            for (FoodBuff foodBuff : BuffRegistry.FOOD_BUFFS) {
                buff = mob.buffManager.getBuff(foodBuff);
                if (buff != null) continue;
                buffToUse = foodBuff;
                break;
            }
        }
        if (buffToUse == null) {
            int bestQualityHappinessIncrease = Integer.MAX_VALUE;
            int lastEatenIndex = Integer.MIN_VALUE;
            for (FoodBuff foodBuff : BuffRegistry.FOOD_BUFFS) {
                int eatenIndex;
                ActiveBuff activeBuff = mob.buffManager.getBuff(foodBuff);
                if (activeBuff == null) continue;
                FoodConsumableItem lastItem = FoodBuff.getFoodItem(activeBuff);
                if (lastItem == null) {
                    buffToUse = foodBuff;
                    break;
                }
                if (lastItem.quality.happinessIncrease < bestQualityHappinessIncrease) {
                    bestQualityHappinessIncrease = lastItem.quality.happinessIncrease;
                    lastEatenIndex = activeBuff.getGndData().getInt("eatenIndex");
                    buffToUse = foodBuff;
                    continue;
                }
                if (lastItem.quality.happinessIncrease != bestQualityHappinessIncrease || (eatenIndex = activeBuff.getGndData().getInt("eatenIndex")) >= lastEatenIndex) continue;
                buffToUse = foodBuff;
                lastEatenIndex = eatenIndex;
            }
        }
        ActiveBuff ab = new ActiveBuff(buffToUse, mob, (float)item.duration, null);
        FoodBuff.setFoodItem(ab, item);
        ab.getGndData().setInt("eatenIndex", bestFoundEatenIndex + 1);
        mob.buffManager.addBuff(ab, false, true);
        return ab;
    }

    public static void clearFoodBuffsAboveCount(Mob mob, int maxFoodBuffs) {
        int totalFoundBuffs = 0;
        for (FoodBuff foodBuff : BuffRegistry.FOOD_BUFFS) {
            if (!mob.buffManager.hasBuff(foodBuff)) continue;
            ++totalFoundBuffs;
        }
        while (totalFoundBuffs > maxFoodBuffs) {
            int lastEatenIndex = Integer.MAX_VALUE;
            FoodBuff buffToRemove = null;
            for (FoodBuff foodBuff : BuffRegistry.FOOD_BUFFS) {
                int eatenIndex;
                ActiveBuff activeBuff = mob.buffManager.getBuff(foodBuff);
                if (activeBuff == null || (eatenIndex = activeBuff.getGndData().getInt("eatenIndex")) >= lastEatenIndex) continue;
                lastEatenIndex = eatenIndex;
                buffToRemove = foodBuff;
            }
            if (buffToRemove != null) {
                mob.buffManager.removeBuff(buffToRemove, mob.isServer());
            }
            --totalFoundBuffs;
        }
    }
}

