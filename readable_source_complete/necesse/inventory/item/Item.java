/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import necesse.engine.GlobalData;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ItemDropperHandler;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.IntersectionPoint;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.mobs.ItemCooldown;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.pickup.PickupEntity;
import necesse.gfx.GameColor;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.HumanAttackDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.forms.presets.sidebar.SidebarForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.PlaceableItemInterface;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemCategoryManager;
import necesse.inventory.item.ItemUsed;
import necesse.inventory.item.TickItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.matItem.MatItem;
import necesse.inventory.item.mountItem.MountItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.inventory.item.placeableItem.objectItem.SeedObjectItem;
import necesse.inventory.item.questItem.QuestItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.inventory.recipe.GlobalIngredient;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.IngredientCounter;
import necesse.inventory.recipe.IngredientUser;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.CollisionPoint;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.light.GameLight;

public class Item {
    public static float GLOBAL_SPOIL_TIME_MODIFIER = 1.0f;
    public static final FontOptions tipFontOptions = new FontOptions(12).color(220, 220, 220).outline();
    public final IDData idData = new IDData();
    private final boolean isPlaceable;
    private final boolean isTickItem;
    protected boolean showAttackAllDirections;
    public final Type type;
    protected boolean isPotion;
    protected boolean dropsAsMatDeathPenalty = false;
    protected long dropDecayTimeMillis;
    protected int spoilDurationSeconds;
    protected int incinerationTimeMillis = 3000;
    protected Rarity rarity = Rarity.NORMAL;
    protected int stackSize;
    protected float defaultLootTier = 0.0f;
    protected int initialSettlementRecipeCount = -1;
    private final ArrayList<Integer> globalIngredients = new ArrayList();
    private HashMap<ItemCategoryManager, String[]> itemCategoryTree = new HashMap();
    protected GameTexture itemTexture;
    protected GameTexture holdTexture;
    protected GameTexture attackTexture;
    protected GameLight worldDrawLight;
    protected int worldDrawSize;
    protected int attackXOffset;
    protected int attackYOffset;
    protected float hungerUsage;
    @Deprecated
    protected int animSpeed;
    @Deprecated
    protected int itemCooldown;
    @Deprecated
    protected int cooldown;
    protected IntUpgradeValue attackAnimTime;
    protected IntUpgradeValue attackCooldownTime;
    protected IntUpgradeValue itemCooldownTime;
    protected int animAttacks;
    protected boolean changeDir;
    protected ArrayList<String> keyWords;

    private static Type findType(Item item) {
        for (Type value : Type.values()) {
            if (!value.subClass.isInstance(item)) continue;
            return value;
        }
        return Type.MISC;
    }

    public final int getID() {
        return this.idData.getID();
    }

    public String getStringID() {
        return this.idData.getStringID();
    }

    public Item(int stackSize) {
        this.itemCategoryTree.put(ItemCategory.masterManager, new String[]{"misc"});
        this.itemCategoryTree.put(ItemCategory.craftingManager, new String[]{"misc"});
        this.worldDrawSize = 24;
        this.attackXOffset = 8;
        this.attackYOffset = 16;
        this.hungerUsage = 0.0f;
        this.animSpeed = -1;
        this.itemCooldown = -1;
        this.cooldown = -1;
        this.attackAnimTime = new IntUpgradeValue(true, 200, 0.0f);
        this.attackCooldownTime = new IntUpgradeValue(true, 0, 0.0f);
        this.itemCooldownTime = new IntUpgradeValue(true, 0, 0.0f);
        this.animAttacks = 1;
        this.changeDir = false;
        this.keyWords = new ArrayList();
        if (ItemRegistry.instance.isClosed()) {
            throw new RegistryClosedException("Cannot construct Item objects when item registry is closed, since they are a static registered objects. Use ItemRegistry.getItem(...) to get items.");
        }
        this.isPlaceable = this instanceof PlaceableItemInterface;
        this.isTickItem = this instanceof TickItem;
        this.type = Item.findType(this);
        this.stackSize = stackSize;
        this.init();
    }

    protected ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.addAll(this.getDisplayNameTooltips(item, perspective, blackboard));
        tooltips.addAll(this.getDebugTooltips(item, perspective, blackboard));
        tooltips.addAll(this.getCraftingMatTooltips(item, perspective, blackboard));
        return tooltips;
    }

    protected ListGameTooltips getDisplayNameTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(new StringTooltips(this.getDisplayName(item), this.getRarityColor(item)));
        return tooltips;
    }

    protected ListGameTooltips getDebugTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        if (GlobalData.debugActive()) {
            tooltips.add(this.getStringID() + " (" + this.getID() + ")");
            tooltips.add("Value: " + this.getBrokerValue(item));
            tooltips.add("Category: " + GameUtils.join(ItemCategory.masterManager.getItemsCategory(this).getStringIDTree(false), "."));
            LoadedMod mod = ItemRegistry.getItemMod(this.getID());
            if (mod != null) {
                tooltips.add("Mod: " + mod.name);
            }
        }
        return tooltips;
    }

    protected ListGameTooltips getCraftingMatTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        HashSet<Tech> techs = Recipes.getCraftingMatTechs(this.getID());
        LinkedList<GameMessage> craftingMatMessages = new LinkedList<GameMessage>();
        for (Tech tech : techs) {
            if (tech.craftingMatTip == null || !craftingMatMessages.stream().noneMatch(m -> m.isSame(tech.craftingMatTip))) continue;
            craftingMatMessages.add(tech.craftingMatTip);
        }
        Iterator<Object> iterator = this.getGlobalIngredients().iterator();
        while (iterator.hasNext()) {
            int globalIngredientID = (Integer)iterator.next();
            GlobalIngredient globalIngredient = GlobalIngredientRegistry.getGlobalIngredient(globalIngredientID);
            if (globalIngredient.craftingMatTip == null || !craftingMatMessages.stream().noneMatch(m -> m.isSame(globalIngredient.craftingMatTip))) continue;
            craftingMatMessages.add(globalIngredient.craftingMatTip);
        }
        for (GameMessage message : craftingMatMessages) {
            tooltips.add(message.translate());
        }
        return tooltips;
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        return this.getBaseTooltips(item, perspective, blackboard);
    }

    @Deprecated
    public void init() {
    }

    public void onItemRegistryClosed() {
        if (this.animSpeed != -1 && this.attackAnimTime.isEmpty()) {
            this.attackAnimTime.setBaseValue(this.animSpeed);
        }
        if (this.cooldown != -1 && this.attackCooldownTime.isEmpty()) {
            this.attackCooldownTime.setBaseValue(this.cooldown);
        }
        if (this.itemCooldown != -1 && this.itemCooldownTime.isEmpty()) {
            this.itemCooldownTime.setBaseValue(this.itemCooldown);
        }
    }

    public GameMessage getNewLocalization() {
        return new LocalMessage("item", this.getStringID());
    }

    public GameMessage getLocalization(InventoryItem item) {
        String name;
        GNDItem gndItem;
        GNDItemMap gndData = item.getGndData();
        if (gndData.hasKey("name") && !GNDItem.isDefault(gndItem = gndData.getItem("name")) && !(name = gndItem.toString()).isEmpty()) {
            return new StaticMessage(name);
        }
        return ItemRegistry.getLocalization(this.getID());
    }

    public final String getDisplayName(InventoryItem item) {
        return this.getLocalization(item).translate();
    }

    public String getTranslatedTypeName() {
        return "";
    }

    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        float percentCooldown;
        this.drawIcon(item, perspective, x, y, 32, null);
        if (inInventory && perspective != null && (percentCooldown = this.getItemCooldownPercent(item, perspective)) > 0.0f) {
            int size = 34;
            int pixels = GameMath.limit((int)(percentCooldown * (float)size), 1, size);
            Renderer.initQuadDraw(size, pixels).color(0.0f, 0.0f, 0.0f, 0.5f).draw(x - 1, y + Math.abs(pixels - size) - 1);
        }
    }

    public float getItemCooldownPercent(InventoryItem item, PlayerMob perspective) {
        ItemCooldown cooldown = perspective.getItemCooldown(this);
        if (cooldown != null) {
            return cooldown.getPercentRemaining(perspective.getWorldEntity().getTime());
        }
        return 0.0f;
    }

    public void drawIcon(InventoryItem item, PlayerMob perspective, int x, int y, int size, Color color) {
        color = color != null ? MergeFunction.MULTIPLY.merge(color, this.getDrawColor(item, perspective)) : this.getDrawColor(item, perspective);
        this.getItemSprite(item, perspective).initDraw().color(color).size(size).draw(x, y);
    }

    @Deprecated
    public void drawIcon(InventoryItem item, PlayerMob perspective, int x, int y, int size) {
        this.drawIcon(item, perspective, x, y, size, null);
    }

    public GameLight getWorldDrawLight() {
        return this.worldDrawLight;
    }

    public int getWorldDrawSize(InventoryItem item, PlayerMob perspective) {
        return this.worldDrawSize;
    }

    public DrawOptions getWorldDrawOptions(InventoryItem item, PlayerMob perspective, int x, int y, GameLight light, float sinking) {
        return this.getWorldDrawOptions(item, perspective, x, y, light, sinking, this.getWorldDrawSize(item, perspective));
    }

    public DrawOptions getWorldDrawOptions(InventoryItem item, PlayerMob perspective, int x, int y, GameLight light, float sinking, int size) {
        return this.getWorldDrawOptions(item, perspective, this.getWorldItemSprite(item, perspective), x, y, light, sinking, size);
    }

    protected final DrawOptions getWorldDrawOptions(InventoryItem item, PlayerMob perspective, GameSprite sprite, int x, int y, GameLight light, float sinking, int size) {
        Color col = this.getDrawColor(item, perspective);
        GameLight wdLight = this.getWorldDrawLight();
        TextureDrawOptionsEnd drawOptions = sprite.initDrawSection(0, sprite.spriteWidth, 0, sprite.spriteHeight - (int)(sinking * (float)sprite.spriteHeight)).colorLight(col, wdLight != null ? wdLight : light).size(size);
        int sinkingSize = (int)(sinking * (float)drawOptions.getHeight());
        int width = drawOptions.getWidth();
        int height = drawOptions.getHeight();
        drawOptions = drawOptions.size(drawOptions.getWidth(), drawOptions.getHeight() - sinkingSize);
        return drawOptions.pos(x - width / 2, y - height + sinkingSize, true);
    }

    public DrawOptions getWorldShadowDrawOptions(InventoryItem item, PlayerMob perspective, int x, int y, GameLight light, float sinking) {
        return this.getWorldShadowDrawOptions(item, perspective, x, y, light, sinking, this.getWorldDrawSize(item, perspective));
    }

    public DrawOptions getWorldShadowDrawOptions(InventoryItem item, PlayerMob perspective, int x, int y, GameLight light, float sinking, int size) {
        GameSprite sprite = this.getWorldShadowSprite(item, perspective);
        float spriteSize = (float)Math.max(sprite.width, sprite.height) / 24.0f;
        float shadowSize = 1.0f;
        if (sinking >= 0.75f) {
            shadowSize = Math.abs((sinking - 0.75f) * 4.0f - 1.0f);
        }
        return sprite.initDraw().size((int)((float)size * spriteSize * shadowSize)).light(light).posMiddle(x, y, true);
    }

    public GameSprite getWorldShadowSprite(InventoryItem item, PlayerMob perspective) {
        return new GameSprite(GameResources.item_shadow);
    }

    public GameSprite getWorldItemSprite(InventoryItem item, PlayerMob perspective) {
        return this.getItemSprite(item, perspective);
    }

    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        return new GameSprite(this.itemTexture);
    }

    public Color getDrawColor(InventoryItem item, PlayerMob player) {
        return new Color(255, 255, 255);
    }

    public HumanAttackDrawOptions getAttackDrawOptions(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int mobDir, float attackDirX, float attackDirY, float attackProgress, GameSprite armSprite) {
        ItemAttackDrawOptions drawOptions = this.setupAttackDrawOptions(item, level, player, headItem, chestItem, feetItem, mobDir, attackDirX, attackDirY, attackProgress, armSprite);
        this.setDrawAttackRotation(item, drawOptions, attackDirX, attackDirY, attackProgress);
        return drawOptions;
    }

    public ItemAttackDrawOptions setupAttackDrawOptions(InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, GameSprite armSprite, GameSprite armorSprite, Color chestCol, Color itemColor) {
        ItemAttackDrawOptions options = ItemAttackDrawOptions.start(mobDir);
        this.setupItemSpriteAttackDrawOptions(options, item, player, mobDir, attackDirX, attackDirY, attackProgress, itemColor);
        if (armSprite != null) {
            options.armSprite(armSprite);
        }
        if (armorSprite != null) {
            options.armorSprite(armorSprite);
            options.armorColor(chestCol);
        }
        if (!this.animDrawBehindHand(item)) {
            options.itemAfterHand();
        }
        return options;
    }

    public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions options, InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, Color itemColor) {
        ItemAttackDrawOptions.AttackItemSprite itemSprite = options.itemSprite(this.getAttackSprite(item, player));
        itemSprite.itemRotatePoint(this.attackXOffset, this.attackYOffset);
        if (itemColor != null) {
            itemSprite.itemColor(itemColor);
        }
        return itemSprite.itemEnd();
    }

    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        if (this.attackTexture != null) {
            return new GameSprite(this.attackTexture);
        }
        return new GameSprite(this.getItemSprite(item, player), 24);
    }

    public ItemAttackDrawOptions setupAttackDrawOptions(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int mobDir, float attackDirX, float attackDirY, float attackProgress, GameSprite armSprite) {
        GameSprite chestSprite = null;
        Color chestCol = null;
        if (chestItem != null && chestItem.item.isArmorItem() && chestItem.item instanceof ChestArmorItem) {
            chestSprite = ((ChestArmorItem)chestItem.item).getAttackArmSprite(chestItem, level, player, headItem, chestItem, feetItem);
            chestCol = chestItem.item.getDrawColor(chestItem, player);
        }
        return this.setupAttackDrawOptions(item, player, mobDir, attackDirX, attackDirY, attackProgress, armSprite, chestSprite, chestCol, this.getDrawColor(item, player));
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY);
    }

    public boolean holdsItem(InventoryItem item, PlayerMob player) {
        return this.holdTexture != null;
    }

    public DrawOptions getHoldItemDrawOptions(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        TextureDrawOptionsEnd options;
        int xOffset = 0;
        int yOffset = 0;
        if (this.holdTexture.getHeight() / 128 == 4) {
            width *= 2;
            height *= 2;
            xOffset = -32;
            yOffset = -32;
            options = this.holdTexture.initDraw().sprite(spriteX, spriteY, 128);
            if (mask != null) {
                options.addShaderState(mask.addMaskOffset(0, yOffset));
            }
        } else {
            options = this.holdTexture.initDraw().sprite(spriteX, spriteY, 64);
        }
        options = options.light(light).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask);
        return options.pos(drawX + xOffset, drawY + yOffset);
    }

    public boolean holdItemInFrontOfArms(InventoryItem item, PlayerMob player, int spriteX, int spriteY, int drawX, int drawY, int width, int height, boolean mirrorX, boolean mirrorY, GameLight light, float alpha, MaskShaderOptions mask) {
        return false;
    }

    protected void loadItemTextures() {
        this.itemTexture = GameTexture.fromFile("items/" + this.getStringID());
    }

    protected void loadHoldTextures() {
        try {
            this.holdTexture = GameTexture.fromFileRaw("player/holditems/" + this.getStringID());
        }
        catch (FileNotFoundException e) {
            this.holdTexture = null;
        }
    }

    protected void loadAttackTexture() {
        try {
            this.attackTexture = GameTexture.fromFileRaw("player/weapons/" + this.getStringID());
        }
        catch (FileNotFoundException e) {
            this.attackTexture = null;
        }
    }

    public void loadTextures() {
        this.loadItemTextures();
        this.loadHoldTextures();
        this.loadAttackTexture();
    }

    public float getBrokerValue(InventoryItem item) {
        return ItemRegistry.getBrokerValue(this.getID());
    }

    public int getStackSize() {
        return this.stackSize;
    }

    public int getInitialSettlementRecipeCount() {
        return this.initialSettlementRecipeCount;
    }

    public boolean isToolItem() {
        return this.type == Type.TOOL;
    }

    public boolean isPlaceable() {
        return this.isPlaceable;
    }

    public boolean isTickItem() {
        return this.isTickItem;
    }

    public PlaceableItemInterface getPlaceable() {
        if (this.isPlaceable()) {
            return (PlaceableItemInterface)((Object)this);
        }
        return null;
    }

    public boolean isArmorItem() {
        return this.type == Type.ARMOR;
    }

    public boolean isMountItem() {
        return this.type == Type.MOUNT;
    }

    public boolean isTrinketItem() {
        return this.type == Type.TRINKET;
    }

    public boolean isFoodItem() {
        return this.type == Type.FOOD;
    }

    public boolean isEnchantable(InventoryItem item) {
        return false;
    }

    public String getIsEnchantableError(InventoryItem item) {
        return null;
    }

    public int getSpriteRes() {
        return 32;
    }

    public boolean getConstantUse(InventoryItem item) {
        return true;
    }

    public boolean animDrawBehindHand(InventoryItem item) {
        return false;
    }

    public float zoomAmount() {
        return 0.0f;
    }

    public Rarity getRarity(InventoryItem item) {
        return this.rarity;
    }

    public GameColor getRarityColor(InventoryItem item) {
        Rarity rarity = this.getRarity(item);
        return rarity == null ? GameColor.NO_COLOR : rarity.color;
    }

    public float getHungerUsage(InventoryItem item, PlayerMob player) {
        return this.hungerUsage;
    }

    public int getUpgradeLevel(InventoryItem item) {
        return item.getGndData().getInt("upgradeLevel");
    }

    public float getUpgradeTier(InventoryItem item) {
        return (float)this.getUpgradeLevel(item) / 100.0f;
    }

    public void setUpgradeLevel(InventoryItem item, int upgradeLevel) {
        item.getGndData().setInt("upgradeLevel", upgradeLevel);
    }

    public void setUpgradeTier(InventoryItem item, float tier) {
        this.setUpgradeLevel(item, (int)(tier * 100.0f));
    }

    public int getFlatAttackAnimTime(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("attackAnimTime") ? gndData.getInt("attackAnimTime") : this.attackAnimTime.getValue(this.getUpgradeTier(item)).intValue();
    }

    public int getFlatAttackCooldownTime(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("attackCooldownTime") ? gndData.getInt("attackCooldownTime") : this.attackCooldownTime.getValue(this.getUpgradeTier(item)).intValue();
    }

    public int getFlatItemCooldownTime(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("itemCooldownTime") ? gndData.getInt("itemCooldownTime") : this.itemCooldownTime.getValue(this.getUpgradeTier(item)).intValue();
    }

    public int getAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return Math.round((float)this.getFlatAttackAnimTime(item) * (1.0f / this.getAttackSpeedModifier(item, attackerMob)));
    }

    public int getAttackCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return Math.round((float)this.getFlatAttackCooldownTime(item) * (1.0f / this.getAttackSpeedModifier(item, attackerMob)));
    }

    public int getItemCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return this.getFlatItemCooldownTime(item);
    }

    public float getAttackSpeedModifier(InventoryItem item, ItemAttackerMob attackerMob) {
        float mod;
        GNDItemMap gndData = item.getGndData();
        if (gndData.hasKey("attackSpeedMod") && (mod = gndData.getFloat("attackSpeedMod")) > 0.0f) {
            return mod;
        }
        return 1.0f;
    }

    public double toAttacksPerSecond(int animSpeed) {
        double attacksPerSecond = 1.0 / ((double)animSpeed / 1000.0);
        return GameMath.toDecimals(attacksPerSecond, 1);
    }

    public int getAnimAttacks(InventoryItem item) {
        return Math.max(1, this.animAttacks);
    }

    public boolean changesDir() {
        return this.changeDir;
    }

    public void tickPickupEntity(ItemPickupEntity entity) {
        if (entity.isClient()) {
            this.refreshLight(entity.getLevel(), entity.x, entity.y, entity.item, false);
        }
    }

    public void refreshLight(Level level, float x, float y, InventoryItem item, boolean isHolding) {
    }

    public float getSinkingRate(ItemPickupEntity entity, float currentSinking) {
        if (!entity.getLevel().inLiquid(entity.getX(), entity.getY())) {
            return 0.0f;
        }
        return entity.getLevel().getTile(entity.getTileX(), entity.getTileY()).getItemSinkingRate(currentSinking);
    }

    public float getMaxSinking(ItemPickupEntity entity) {
        if (!entity.getLevel().inLiquid(entity.getX(), entity.getY())) {
            return 0.0f;
        }
        return entity.getLevel().getTile(entity.getTileX(), entity.getTileY()).getItemMaxSinking();
    }

    public int getIncinerationRate() {
        return this.incinerationTimeMillis;
    }

    public boolean showWires() {
        return false;
    }

    public SidebarForm getSidebar(InventoryItem item) {
        return null;
    }

    public boolean isGlobalIngredient(int globalIngredientID) {
        return this.globalIngredients.contains(globalIngredientID);
    }

    public boolean isGlobalIngredient(String globalIngredientStringID) {
        return this.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredientID(globalIngredientStringID));
    }

    public boolean isGlobalIngredient(GlobalIngredient globalIngredient) {
        return this.isGlobalIngredient(globalIngredient.getID());
    }

    public Item addGlobalIngredient(String ... globalIngredientStringIDs) {
        for (String stringID : globalIngredientStringIDs) {
            this.addGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient(stringID));
        }
        return this;
    }

    private void addGlobalIngredient(GlobalIngredient ... globalIngredients) {
        for (GlobalIngredient globalIngredient : globalIngredients) {
            if (this.globalIngredients.contains(globalIngredient.getID())) {
                return;
            }
            this.globalIngredients.add(globalIngredient.getID());
            if (!this.idData.isSet()) continue;
            globalIngredient.registerItemID(this.getID());
        }
    }

    public ArrayList<Integer> getGlobalIngredients() {
        return this.globalIngredients;
    }

    public Item setItemCategory(ItemCategoryManager manager, String ... categoryTree) {
        if (this.idData.isSet()) {
            manager.setItemCategory(this, categoryTree);
        } else {
            this.itemCategoryTree.put(manager, categoryTree);
        }
        return this;
    }

    public Item setItemCategory(String ... categoryTree) {
        return this.setItemCategory(ItemCategory.masterManager, categoryTree);
    }

    public void registerItemCategory() {
        if (this.itemCategoryTree != null && this.idData.isSet()) {
            for (Map.Entry<ItemCategoryManager, String[]> e : this.itemCategoryTree.entrySet()) {
                String[] value = e.getValue();
                if (value == null) continue;
                e.getKey().setItemCategory(this, value);
            }
            this.itemCategoryTree = null;
        }
    }

    public void setupAttackMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
    }

    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return null;
    }

    public void onServerCanAttackFailed(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, String error, boolean ranAnyway) {
    }

    public Point getControllerAttackLevelPos(Level level, float aimDirX, float aimDirY, PlayerMob player, InventoryItem item) {
        return new Point((int)(player.x + aimDirX * 100.0f), (int)(player.y + aimDirY * 100.0f));
    }

    public void drawHUDItemSelected(GameCamera camera, Level level, PlayerMob player, InventoryItem item) {
    }

    public void drawControllerAimPos(GameCamera camera, Level level, PlayerMob player, InventoryItem item) {
        float aimX = ControllerInput.getAimX();
        float aimY = ControllerInput.getAimY();
        if (aimX != 0.0f || aimY != 0.0f) {
            Point levelPos = this.getControllerAttackLevelPos(level, aimX, aimY, player, item);
            int drawX = camera.getDrawX(levelPos.x);
            int drawY = camera.getDrawY(levelPos.y);
            int playerDrawX = camera.getDrawX(player.x);
            int playerDrawY = camera.getDrawY(player.y);
            this.drawControllerAimInsideWindow(level.getWorldEntity().getLocalTime(), drawX, drawY, playerDrawX, playerDrawY);
        }
    }

    public void drawControllerAimInsideWindow(long currentTime, int drawX, int drawY, int playerDrawX, int playerDrawY) {
        float scaleY;
        float scaleX;
        boolean scaleHitboxes;
        int hitBoxPadding = 10;
        GameWindow window = WindowManager.getWindow();
        boolean bl = scaleHitboxes = window.getSceneWidth() != window.getHudWidth() || window.getSceneHeight() != window.getHudHeight();
        if (scaleHitboxes) {
            scaleX = (float)window.getSceneWidth() / (float)window.getHudWidth();
            scaleY = (float)window.getSceneHeight() / (float)window.getHudHeight();
        } else {
            scaleX = 1.0f;
            scaleY = 1.0f;
        }
        List hitBoxes = GlobalData.getCurrentState().streamHudHitboxes().map(r -> {
            if (scaleHitboxes) {
                int newX = (int)((float)r.x * scaleX);
                int newWidth = (int)((float)r.width * scaleX);
                int newY = (int)((float)r.y * scaleY);
                int newHeight = (int)((float)r.height * scaleY);
                return new Rectangle(newX - hitBoxPadding, newY - hitBoxPadding, newWidth + hitBoxPadding * 2, newHeight + hitBoxPadding * 2);
            }
            return new Rectangle(r.x - hitBoxPadding, r.y - hitBoxPadding, r.width + hitBoxPadding * 2, r.height + hitBoxPadding * 2);
        }).filter(r -> !r.isEmpty()).collect(Collectors.toList());
        int edgePadding = 20;
        Rectangle box = new Rectangle(edgePadding, edgePadding, window.getSceneWidth() - edgePadding * 2, window.getSceneHeight() - edgePadding * 2 - 20);
        boolean drawArrow = false;
        if (!box.contains(drawX, drawY)) {
            Line2D.Float line = new Line2D.Float(playerDrawX, playerDrawY, drawX, drawY);
            IntersectionPoint<Rectangle> collisionPoint = CollisionPoint.getClosestCollision(Collections.singletonList(box), (Line2D)line, true);
            if (collisionPoint != null) {
                drawArrow = true;
                drawX = (int)collisionPoint.x;
                drawY = (int)collisionPoint.y;
            }
        }
        for (Rectangle hitBox : hitBoxes) {
            Line2D.Float line;
            IntersectionPoint collisionPoint;
            if (!hitBox.contains(drawX, drawY) || (collisionPoint = CollisionPoint.getClosestCollision(hitBoxes, (Line2D)(line = new Line2D.Float(playerDrawX, playerDrawY, drawX, drawY)), false)) == null) continue;
            drawArrow = true;
            drawX = (int)collisionPoint.x;
            drawY = (int)collisionPoint.y;
            break;
        }
        if (drawArrow) {
            Point2D.Float dir = GameMath.normalize(drawX - playerDrawX, drawY - playerDrawY);
            float angle = GameMath.getAngle(dir);
            GameResources.aimArrow.initDraw().color(new Color(250, 50, 50)).rotate(angle + 90.0f).posMiddle(drawX, drawY).draw();
        } else {
            GameResources.aim.initDraw().color(new Color(250, 50, 50)).rotate((float)currentTime / 5.0f).posMiddle(drawX, drawY).draw();
        }
    }

    public static int getRandomAttackSeed(GameRandom random) {
        return random.nextInt(65535);
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return item;
    }

    public boolean shouldRunOnAttackedBuffEvent(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return true;
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
    }

    public boolean showAttackAllDirections(ItemAttackerMob attackerMob, InventoryItem item) {
        return this.showAttackAllDirections;
    }

    public float getFinalAttackMovementMod(InventoryItem item, ItemAttackerMob attackerMob) {
        float mod = this.getAttackMovementMod(item);
        float remaining = 1.0f - mod;
        return mod + remaining * Math.abs(attackerMob.buffManager.getModifier(BuffModifiers.ATTACK_MOVEMENT_MOD).floatValue() - 1.0f);
    }

    public float getAttackMovementMod(InventoryItem item) {
        return 1.0f;
    }

    public void tickHolding(InventoryItem item, PlayerMob player) {
        if (player != null && player.isClient()) {
            this.refreshLight(player.getLevel(), player.x, player.y, item, true);
        }
    }

    public ItemUsed useHealthPotion(Level level, PlayerMob player, int seed, InventoryItem item) {
        return new ItemUsed(false, item);
    }

    public ItemUsed useManaPotion(Level level, PlayerMob player, int seed, InventoryItem item) {
        return new ItemUsed(false, item);
    }

    public ItemUsed eatFood(Level level, PlayerMob player, int seed, InventoryItem item) {
        return new ItemUsed(false, item);
    }

    public ItemUsed useBuffPotion(Level level, PlayerMob player, int seed, InventoryItem item) {
        return new ItemUsed(false, item);
    }

    public String getInventoryRightClickControlTip(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return null;
    }

    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return null;
    }

    public boolean isPotion() {
        return this.isPotion;
    }

    public boolean onMouseHoverMob(InventoryItem item, GameCamera camera, PlayerMob perspective, Mob mob, boolean isDebug) {
        return false;
    }

    public boolean onMouseHoverPickup(InventoryItem item, GameCamera camera, PlayerMob perspective, PickupEntity pickupEntity, boolean isDebug) {
        return false;
    }

    public void onMouseHoverTile(InventoryItem item, GameCamera camera, PlayerMob perspective, int mouseX, int mouseY, TilePosition pos, boolean isDebug) {
    }

    public int compareTo(Item him) {
        ItemCategory himCategory;
        ItemCategory meCategory = ItemCategory.masterManager.getItemsCategory(this);
        int compare = meCategory.compareTo(himCategory = ItemCategory.masterManager.getItemsCategory(him));
        if (compare == 0) {
            if (this.getID() != him.getID()) {
                String meName = ItemRegistry.getDisplayName(this.getID());
                String himName = ItemRegistry.getDisplayName(him.getID());
                if (meName == null && himName == null) {
                    return 0;
                }
                if (meName == null) {
                    return 1;
                }
                if (himName == null) {
                    return -1;
                }
                compare = meName.compareTo(himName);
            }
            return compare;
        }
        return compare;
    }

    public int compareTo(InventoryItem me, InventoryItem him) {
        int compare = me.item.compareTo(him.item);
        if (compare == 0) {
            return this.compareToSameItem(me, him);
        }
        return compare;
    }

    public int compareToSameItem(InventoryItem me, InventoryItem them) {
        return me.item.getDisplayName(me).compareTo(them.item.getDisplayName(them));
    }

    public ItemPickupEntity getPickupEntity(Level level, InventoryItem item, float x, float y, float dx, float dy) {
        return new ItemPickupEntity(level, item, x, y, dx, dy);
    }

    public boolean isSameItem(Level level, InventoryItem me, InventoryItem them, String purpose) {
        return this == them.item;
    }

    public boolean isSameGNDData(Level level, InventoryItem me, InventoryItem them, String purpose) {
        return true;
    }

    public boolean canCombineItem(Level level, PlayerMob player, InventoryItem me, InventoryItem them, String purpose) {
        if (them == null) {
            return false;
        }
        return this.isSameItem(level, me, them, purpose);
    }

    public boolean ignoreCombineStackLimit(Level level, PlayerMob player, InventoryItem me, InventoryItem them, String purpose) {
        return false;
    }

    @Deprecated
    public boolean onCombine(Level level, PlayerMob player, InventoryItem me, InventoryItem other, int maxStackSize, int amount, boolean combineIsNew, String purpose) {
        return this.onCombine(level, player, null, -1, me, other, maxStackSize, amount, combineIsNew, purpose, null);
    }

    public boolean onCombine(Level level, PlayerMob player, Inventory myInventory, int mySlot, InventoryItem me, InventoryItem other, int maxStackSize, int amount, boolean combineIsNew, String purpose, InventoryAddConsumer addConsumer) {
        WorldSettings worldSettings;
        if ((amount = Math.min(amount, maxStackSize - me.getAmount())) <= 0) {
            return false;
        }
        int startAmount = me.getAmount();
        long mySpoilTime = 0L;
        long otherSpoilTime = 0L;
        if (this.shouldSpoilTick(me) && level != null && ((worldSettings = level.getWorldSettings()) == null || worldSettings.survivalMode)) {
            mySpoilTime = this.getAndUpdateCurrentSpoilTime(me, level, 1.0f);
        }
        if (other.item.shouldSpoilTick(other) && level != null && ((worldSettings = level.getWorldSettings()) == null || worldSettings.survivalMode)) {
            otherSpoilTime = other.item.getAndUpdateCurrentSpoilTime(other, level, 1.0f);
        }
        me.setAmount(me.getAmount() + amount);
        if (addConsumer != null) {
            addConsumer.add(myInventory, mySlot, amount);
        }
        if (combineIsNew) {
            me.setNew(me.isNew() || other.isNew());
        } else {
            other.setNew(me.isNew());
        }
        other.setAmount(other.getAmount() - amount);
        if (otherSpoilTime != mySpoilTime) {
            int endAmount = me.getAmount();
            if (mySpoilTime != 0L && otherSpoilTime != 0L) {
                double beforeAmountPercent = (double)startAmount / (double)endAmount;
                long combinedSpoilTime = GameMath.lerp(beforeAmountPercent, otherSpoilTime, mySpoilTime);
                this.setSpoilTime(me, combinedSpoilTime);
            } else if (otherSpoilTime != 0L) {
                this.setSpoilTime(me, otherSpoilTime);
            }
        }
        return true;
    }

    public ComparableSequence<Integer> getInventoryPriority(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose) {
        return new ComparableSequence<Integer>(inventorySlot);
    }

    public ComparableSequence<Integer> getInventoryAddPriority(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, InventoryItem input, String purpose) {
        return new ComparableSequence<Integer>(inventorySlot);
    }

    public int getInventoryAmount(Level level, PlayerMob player, InventoryItem item, Item requestItem, String purpose) {
        if (requestItem == this) {
            return item.getAmount();
        }
        return 0;
    }

    public int getInventoryAmount(Level level, PlayerMob player, InventoryItem item, Type requestType, String purpose) {
        if (requestType == this.type) {
            return item.getAmount();
        }
        return 0;
    }

    public void countIngredientAmount(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose, IngredientCounter handler) {
        handler.handle(inventory, inventorySlot, item);
    }

    public void useIngredientAmount(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose, IngredientUser handler) {
        handler.handle(inventory, inventorySlot, item, () -> {
            if (inventory != null) {
                inventory.markDirty(inventorySlot);
            }
        });
    }

    public Item getInventoryFirstItem(Level level, PlayerMob player, InventoryItem item, Item[] requestItems, String purpose) {
        for (Item requestItem : requestItems) {
            if (requestItem != this) continue;
            return this;
        }
        return null;
    }

    public Item getInventoryFirstItem(Level level, PlayerMob player, InventoryItem item, Type requestType, String purpose) {
        if (requestType == this.type) {
            return this;
        }
        return null;
    }

    public boolean inventoryAddItem(Level level, PlayerMob player, InventoryItem me, InventoryItem input, String purpose, boolean isValid, int stackLimit, boolean combineIsNew) {
        return this.inventoryAddItem(level, player, null, -1, me, input, purpose, isValid, stackLimit, combineIsNew, null);
    }

    public boolean inventoryAddItem(Level level, PlayerMob player, Inventory myInventory, int mySlot, InventoryItem me, InventoryItem input, String purpose, boolean isValid, int stackLimit, boolean combineIsNew, InventoryAddConsumer addConsumer) {
        if (isValid && this.canCombineItem(level, player, me, input, purpose)) {
            return this.onCombine(level, player, myInventory, mySlot, me, input, this.getStackSize(), input.getAmount(), combineIsNew, purpose, addConsumer);
        }
        return false;
    }

    public int inventoryCanAddItem(Level level, PlayerMob player, InventoryItem item, InventoryItem input, String purpose, boolean isValid, int stackLimit) {
        if (isValid && this.canCombineItem(level, player, item, input, purpose)) {
            return Math.max(0, stackLimit - item.getAmount());
        }
        return 0;
    }

    public int removeInventoryAmount(Level level, PlayerMob player, InventoryItem item, Item requestItem, int amount, String purpose) {
        if (requestItem == this) {
            int removedAmount = Math.min(item.getAmount(), amount);
            item.setAmount(item.getAmount() - removedAmount);
            return removedAmount;
        }
        return 0;
    }

    public int removeInventoryAmount(Level level, PlayerMob player, InventoryItem item, Type requestType, int amount, String purpose) {
        if (requestType == this.type) {
            int removedAmount = Math.min(item.getAmount(), amount);
            item.setAmount(item.getAmount() - removedAmount);
            return removedAmount;
        }
        return 0;
    }

    public int removeInventoryAmount(Level level, PlayerMob player, final InventoryItem item, Inventory inventory, int inventorySlot, Ingredient ingredient, int amount, Collection<InventoryItemsRemoved> collect) {
        if (ingredient.matchesItem(this)) {
            final int removedAmount = Math.min(item.getAmount(), amount);
            item.setAmount(item.getAmount() - removedAmount);
            if (removedAmount > 0 && collect != null) {
                collect.add(new InventoryItemsRemoved(inventory, inventorySlot, item, removedAmount){

                    @Override
                    public void revert() {
                        item.setAmount(item.getAmount() + removedAmount);
                        if (this.inventory != null && this.inventorySlot != -1) {
                            this.inventory.setItem(this.inventorySlot, item);
                        }
                    }
                });
            }
            return removedAmount;
        }
        return 0;
    }

    public boolean dropAsMatDeathPenalty(PlayerInventorySlot slot, boolean slotIsLocked, InventoryItem item, ItemDropperHandler dropper) {
        if (this.dropsAsMatDeathPenalty) {
            dropper.dropItem(item, slot, slotIsLocked);
            return true;
        }
        return false;
    }

    public long getDropDecayTime(InventoryItem item) {
        return this.dropDecayTimeMillis;
    }

    public Item spoilDuration(int minutes) {
        this.spoilDurationSeconds = minutes * 60;
        return this;
    }

    public int getStartSpoilSeconds(InventoryItem item) {
        return this.spoilDurationSeconds;
    }

    public boolean shouldSpoilTick(InventoryItem item) {
        return this.getStartSpoilSeconds(item) > 0;
    }

    public long getAndUpdateCurrentSpoilTime(InventoryItem item, GameClock clock, float spoilRateModifier) {
        if (item != null) {
            spoilRateModifier = Math.max(0.0f, spoilRateModifier * GLOBAL_SPOIL_TIME_MODIFIER);
            float lastRateModifier = this.getCurrentSpoilRateModifier(item);
            long spoilTime = item.getGndData().getLong("spoilTime");
            if (spoilTime == 0L) {
                spoilTime = spoilRateModifier <= 0.0f ? (long)(-this.getStartSpoilSeconds(item)) * 1000L : clock.getWorldTime() + (long)((double)((long)this.getStartSpoilSeconds(item) * 1000L) * (1.0 / (double)spoilRateModifier));
            } else if (lastRateModifier != spoilRateModifier) {
                if (lastRateModifier <= 0.0f) {
                    long spoilTimeRemaining = spoilTime > 0L ? Math.max(0L, spoilTime - clock.getWorldTime()) : -spoilTime;
                    spoilTime = clock.getWorldTime() + (long)((double)spoilTimeRemaining * (1.0 / (double)spoilRateModifier));
                } else if (spoilRateModifier <= 0.0f) {
                    long spoilTimeRemaining = spoilTime - clock.getWorldTime();
                    spoilTime = (long)(-((double)spoilTimeRemaining * (double)lastRateModifier));
                } else {
                    long spoilTimeRemaining = Math.max(0L, spoilTime - clock.getWorldTime());
                    long fullSpoilTimeRemaining = (long)((double)spoilTimeRemaining * (double)lastRateModifier);
                    long convertedSpoilTimeRemaining = (long)((double)fullSpoilTimeRemaining * (1.0 / (double)spoilRateModifier));
                    spoilTime = clock.getWorldTime() + convertedSpoilTimeRemaining;
                }
            }
            item.getGndData().setFloat("spoilModifier", spoilRateModifier);
            this.setSpoilTime(item, spoilTime);
            return spoilTime;
        }
        return 0L;
    }

    public long getCurrentSpoilTime(InventoryItem item) {
        long spoilTime = 0L;
        if (item != null) {
            spoilTime = item.getGndData().getLong("spoilTime");
            if (this.getCurrentSpoilRateModifier(item) > 0.0f) {
                spoilTime = Math.max(0L, spoilTime);
            }
        }
        return spoilTime;
    }

    public float getCurrentSpoilRateModifier(InventoryItem item) {
        return item == null ? 0.0f : Math.max(0.0f, item.getGndData().getFloat("spoilModifier"));
    }

    public void setSpoilTime(InventoryItem item, long spoilTime) {
        item.getGndData().setLong("spoilTime", spoilTime);
    }

    public long tickSpoilTime(InventoryItem item, GameClock clock, float modifier, Consumer<InventoryItem> setItem) {
        long spoilsIn;
        long spoilTime = this.getAndUpdateCurrentSpoilTime(item, clock, modifier);
        if (spoilTime > 0L && clock != null && (spoilsIn = spoilTime - clock.getWorldTime()) <= 0L && setItem != null) {
            InventoryItem spoiledItem = this.getSpoiledItem(item);
            setItem.accept(spoiledItem);
            return 0L;
        }
        return spoilTime;
    }

    public InventoryItem getSpoiledItem(InventoryItem item) {
        return new InventoryItem("spoiledfood", item.getAmount());
    }

    public InventoryItem getDefaultItem(PlayerMob player, int amount) {
        InventoryItem inventoryItem = new InventoryItem(this, amount);
        if (this.defaultLootTier != 0.0f) {
            this.setUpgradeTier(inventoryItem, this.defaultLootTier);
        }
        return inventoryItem;
    }

    public void addDefaultItems(List<InventoryItem> list, PlayerMob player) {
        list.add(this.getDefaultItem(player, 1));
    }

    public InventoryItem getDefaultLootItem(GameRandom random, int amount) {
        InventoryItem inventoryItem = new InventoryItem(this, amount);
        if (this.defaultLootTier != 0.0f) {
            this.setUpgradeTier(inventoryItem, this.defaultLootTier);
        }
        return inventoryItem;
    }

    public boolean shouldShowInItemList() {
        return true;
    }

    public boolean matchesSearch(InventoryItem item, PlayerMob perspective, String search, GameBlackboard tooltipBlackboard) {
        if (search == null || search.isEmpty()) {
            return true;
        }
        search = search.toLowerCase();
        if (this.getStringID().toLowerCase().contains(search)) {
            return true;
        }
        if (item.getItemDisplayName().toLowerCase().contains(search)) {
            return true;
        }
        ItemCategory category = ItemCategory.getItemsCategory(this);
        while (category != null) {
            if (category.stringID.toLowerCase().contains(search)) {
                return true;
            }
            if (category.displayName.translate().toLowerCase().contains(search)) {
                return true;
            }
            category = category.parent;
        }
        for (Integer globalIngredientID : this.getGlobalIngredients()) {
            GlobalIngredient globalIngredient = GlobalIngredientRegistry.getGlobalIngredient(globalIngredientID);
            if (globalIngredient.getStringID().toLowerCase().contains(search)) {
                return true;
            }
            if (!globalIngredient.displayName.translate().toLowerCase().contains(search)) continue;
            return true;
        }
        if (tooltipBlackboard != null && item.getTooltip(perspective, tooltipBlackboard).matchesSearch(search)) {
            return true;
        }
        for (String str : this.keyWords) {
            if (!str.toLowerCase().contains(search)) continue;
            return true;
        }
        return false;
    }

    public static enum Type {
        MAT(MatItem.class),
        TOOL(ToolItem.class),
        ARMOR(ArmorItem.class),
        TRINKET(TrinketItem.class),
        MOUNT(MountItem.class),
        ARROW(ArrowItem.class),
        BULLET(BulletItem.class),
        SEED(SeedObjectItem.class),
        BAIT(BaitItem.class),
        FOOD(FoodConsumableItem.class),
        QUEST(QuestItem.class),
        MISC(Item.class);

        final Class<? extends Item> subClass;

        private Type(Class<? extends Item> subClass) {
            this.subClass = subClass;
        }
    }

    public static enum Rarity {
        NORMAL(GameColor.ITEM_NORMAL, 350, 40),
        COMMON(GameColor.ITEM_COMMON, 250, 300),
        UNCOMMON(GameColor.ITEM_UNCOMMON, 230, 280),
        RARE(GameColor.ITEM_RARE, 300, 350),
        EPIC(GameColor.ITEM_EPIC, 260, 310),
        LEGENDARY(GameColor.ITEM_LEGENDARY, 280, 330),
        QUEST(GameColor.ITEM_QUEST, 330, 20),
        UNIQUE(GameColor.ITEM_UNIQUE, 220, 280);

        public final GameColor color;
        public final int outlineMinHue;
        public final int outlineMaxHue;

        private Rarity(GameColor color, int outlineMinHue, int outlineMaxHue) {
            this.color = color;
            this.outlineMinHue = outlineMinHue;
            this.outlineMaxHue = outlineMaxHue;
        }

        public Rarity getNext(Rarity limit) {
            int limitOrdinal;
            int n = limitOrdinal = limit == null ? Rarity.values().length - 1 : limit.ordinal();
            if (this.ordinal() > limitOrdinal) {
                return this;
            }
            int nextOrdinal = this.ordinal() + 1;
            if (nextOrdinal >= limitOrdinal) {
                return Rarity.values()[limitOrdinal];
            }
            return Rarity.values()[nextOrdinal];
        }
    }
}

