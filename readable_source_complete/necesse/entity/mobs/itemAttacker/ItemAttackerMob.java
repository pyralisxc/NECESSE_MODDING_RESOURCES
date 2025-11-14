/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.itemAttacker;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.ItemCooldown;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ServerFollowersManager;
import necesse.entity.projectile.Projectile;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemAttackerWeaponItem;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public abstract class ItemAttackerMob
extends AttackAnimMob {
    protected InventoryItem attackingItem;
    public boolean isInteractAttack;
    public int animAttack;
    private int animAttackTotal;
    private long animAttackCooldown;
    protected int inaccuracySeedCounter = 1;
    public int beforeAttackDir = -1;
    private final HashMap<String, ItemCooldown> itemCooldowns = new HashMap();
    protected AttackHandler attackHandler;
    public final HashSet<Projectile> boomerangs = new HashSet();
    public int summonFocusUniqueID = -1;
    public ServerFollowersManager serverFollowersManager;
    public final ShowItemAttackMobAbility showItemAttackMobAbility;
    public final BooleanMobAbility stopAttackAbility;
    private final IntMobAbility setSummonFocusUniqueID;

    public ItemAttackerMob(int health) {
        super(health);
        this.isUsingItemsForDamage = true;
        this.showItemAttackMobAbility = this.registerAbility(new ShowItemAttackMobAbility(this));
        this.stopAttackAbility = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                ItemAttackerMob.this.stopAttack(value);
            }
        });
        this.setSummonFocusUniqueID = this.registerAbility(new IntMobAbility(){

            @Override
            protected void run(int value) {
                ItemAttackerMob.this.summonFocusUniqueID = value;
            }
        });
        this.serverFollowersManager = new ServerFollowersManager(this, this.setSummonFocusUniqueID);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.isAttacking && this.attackingItem != null);
        if (this.isAttacking && this.attackingItem != null) {
            this.attackingItem.addPacketContent(writer);
            writer.putNextFloat(this.attackDir.x);
            writer.putNextFloat(this.attackDir.y);
            writer.putNextLong(this.attackTime);
            writer.putNextShortUnsigned(this.attackSeed);
        }
        this.serverFollowersManager.setupSpawnPacket(writer);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.isAttacking = reader.getNextBoolean();
        if (this.isAttacking) {
            this.attackingItem = InventoryItem.fromContentPacket(reader);
            float attackDirX = reader.getNextFloat();
            float attackDirY = reader.getNextFloat();
            this.attackDir = new Point2D.Float(attackDirX, attackDirY);
            this.attackTime = reader.getNextLong();
            this.attackSeed = reader.getNextShortUnsigned();
            if (this.attackingItem != null) {
                this.animAttackTotal = this.attackingItem.item.getAnimAttacks(this.attackingItem);
                this.animAttack = 1;
                this.attackAnimTime = this.attackingItem.item.getAttackAnimTime(this.attackingItem, this);
                this.animAttackCooldown = this.attackAnimTime / this.animAttackTotal;
                this.attackCooldown = Math.max(this.attackingItem.item.getAttackCooldownTime(this.attackingItem, this), this.attackAnimTime);
                int itemCooldown = this.attackingItem.item.getItemCooldownTime(this.attackingItem, this);
                if (itemCooldown > 0) {
                    this.startItemCooldown(this.attackingItem.item, this.attackTime, itemCooldown);
                }
            }
        }
        this.serverFollowersManager.applySpawnPacket(reader);
    }

    @Override
    public void tickMovement(float delta) {
        if (this.attackHandler != null) {
            this.attackHandler.tickUpdate(delta);
        }
        super.tickMovement(delta);
    }

    @Override
    public void clientTick() {
        InventoryItem invItem;
        ItemAttackSlot slot;
        super.clientTick();
        this.boomerangs.removeIf(Entity::removed);
        if (this.isAttacking && this.buffManager.getModifier(BuffModifiers.INTIMIDATED).booleanValue()) {
            this.stopAttack(true);
        }
        if (this.isAttacking || this.onCooldown) {
            this.getAttackAnimProgress();
        }
        if ((slot = this.getCurrentSelectedAttackSlot()) != null && (invItem = slot.getItem()) != null && invItem.item instanceof ItemAttackerWeaponItem && this.itemAttackerHoldsItem(invItem)) {
            ((ItemAttackerWeaponItem)((Object)invItem.item)).itemAttackerTickHolding(invItem, this);
        }
    }

    @Override
    public void serverTick() {
        InventoryItem invItem;
        ItemAttackSlot slot;
        super.serverTick();
        this.serverFollowersManager.serverTick();
        this.boomerangs.removeIf(Entity::removed);
        if (this.isAttacking && this.buffManager.getModifier(BuffModifiers.INTIMIDATED).booleanValue()) {
            this.stopAttack(true);
        }
        if (this.isAttacking || this.onCooldown) {
            this.getAttackAnimProgress();
        }
        if ((slot = this.getCurrentSelectedAttackSlot()) != null && (invItem = slot.getItem()) != null && invItem.item instanceof ItemAttackerWeaponItem && this.itemAttackerHoldsItem(invItem)) {
            ((ItemAttackerWeaponItem)((Object)invItem.item)).itemAttackerTickHolding(invItem, this);
        }
    }

    public int getSummonFocusUniqueID() {
        if (this.isServer()) {
            return this.serverFollowersManager.summonFocusMob == null ? -1 : this.serverFollowersManager.summonFocusMob.getUniqueID();
        }
        return this.summonFocusUniqueID;
    }

    public Mob getSummonFocusMob() {
        if (this.isServer()) {
            return this.serverFollowersManager.summonFocusMob;
        }
        return this.summonFocusUniqueID == -1 ? null : GameUtils.getLevelMob(this.summonFocusUniqueID, this.getLevel(), false);
    }

    public int getBoomerangsUsage() {
        return (int)Math.ceil(this.boomerangs.stream().reduce(Float.valueOf(0.0f), (last, projectile) -> Float.valueOf(last.floatValue() + projectile.getBoomerangUsage()), Float::sum).floatValue());
    }

    public void forceEndAttack() {
        if (this.attackingItem != null && this.beforeAttackDir != -1 && !this.attackingItem.item.changesDir()) {
            this.setDir(this.beforeAttackDir);
            this.beforeAttackDir = -1;
        }
        this.isAttacking = false;
        this.canAttack();
    }

    @Override
    public float getAttackAnimProgress() {
        float progress = (float)(this.getTime() - this.attackTime) / (float)this.attackAnimTime;
        if (progress >= 1.0f) {
            this.forceEndAttack();
        }
        return Math.min(1.0f, progress);
    }

    public long getNextAnimAttackCooldown() {
        return this.attackTime + (long)this.animAttack * this.animAttackCooldown - this.getTime();
    }

    public boolean canAnimAttackAgain(InventoryItem attackItem) {
        return this.isAttacking && this.animAttack < this.animAttackTotal && this.attackingItem != null && attackItem != null && attackItem.item.getStringID().equals(this.attackingItem.item.getStringID());
    }

    @Override
    public int getCurrentAttackHeight() {
        int height = 14;
        Mob mount = this.getMount();
        if (mount != null) {
            height -= mount.getBobbing(mount.getX(), mount.getY());
            if (mount.getLevel() != null) {
                height -= mount.getLevel().getTile(mount.getTileX(), mount.getTileY()).getMobSinkingAmount(mount);
            }
        } else {
            height -= this.getBobbing(this.getX(), this.getY());
            if (this.getLevel() != null) {
                height -= this.getLevel().getTile(this.getTileX(), this.getTileY()).getMobSinkingAmount(this);
                MaskShaderOptions swimShader = this.getSwimMaskShaderOptions(this.inLiquidFloat(this.getX(), this.getY()));
                if (swimShader != null) {
                    height -= swimShader.drawYOffset;
                }
            }
        }
        return height;
    }

    public HumanDrawOptions setupAttackDraw(HumanDrawOptions humanOptions) {
        float progress = this.getAttackAnimProgress();
        if (this.isAttacking && this.attackingItem != null && this.attackDir != null) {
            humanOptions.itemAttack(this.attackingItem, this.isPlayer ? (PlayerMob)this : null, progress, this.attackDir.x, this.attackDir.y);
        } else {
            InventoryItem invItem;
            ItemAttackSlot slot = this.getCurrentSelectedAttackSlot();
            if (slot != null && (invItem = slot.getItem()) != null && invItem.item instanceof ItemAttackerWeaponItem && this.itemAttackerHoldsItem(invItem) && invItem.item.holdsItem(invItem, null)) {
                humanOptions.holdItem(invItem);
            }
        }
        return humanOptions;
    }

    public boolean itemAttackerHoldsItem(InventoryItem item) {
        return true;
    }

    @Override
    public float getAttackingMovementModifier() {
        if (this.attackingItem != null) {
            return this.attackingItem.item.getFinalAttackMovementMod(this.attackingItem, this);
        }
        return super.getAttackingMovementModifier();
    }

    public GNDItemMap runItemAttack(InventoryItem item, int targetX, int targetY, int seed, int animAttack, ItemAttackSlot slot, GNDItemMap attackMap) {
        if (attackMap == null) {
            attackMap = new GNDItemMap();
            item.item.setupAttackMapContent(attackMap, this.getLevel(), targetX, targetY, this, seed, item);
        }
        int attackHeight = this.getCurrentAttackHeight();
        this.animAttackTotal = item.item.getAnimAttacks(item);
        this.animAttack = animAttack;
        this.attackAnimTime = item.item.getAttackAnimTime(item, this);
        this.animAttackCooldown = this.attackAnimTime / this.animAttackTotal;
        ++this.inaccuracySeedCounter;
        InventoryItem resultItem = item.item.onAttack(this.getLevel(), targetX, targetY, this, attackHeight, item, slot, animAttack, seed, attackMap);
        if (slot != null) {
            slot.setItem(resultItem);
        }
        if (item.item.shouldRunOnAttackedBuffEvent(this.getLevel(), targetX, targetY, this, item, slot, animAttack, seed, attackMap)) {
            for (ActiveBuff b : this.buffManager.getArrayBuffs()) {
                b.onItemAttacked(targetX, targetY, this, attackHeight, item, slot, animAttack, attackMap);
            }
        }
        if (animAttack == 0) {
            this.showItemAttack(item, targetX, targetY, animAttack, seed, attackMap);
        }
        ++this.animAttack;
        return attackMap;
    }

    public final GNDItemMap runItemAttack(InventoryItem item, int targetX, int targetY, int animAttack, ItemAttackSlot slot, GNDItemMap attackMap) {
        return this.runItemAttack(item, targetX, targetY, Item.getRandomAttackSeed(GameRandom.globalRandom), animAttack, slot, attackMap);
    }

    protected GNDItemMap runItemLevelInteract(ItemInteractAction interactItem, InventoryItem item, int targetX, int targetY, int seed, ItemAttackSlot slot, GNDItemMap interactMap) {
        if (interactMap == null) {
            interactMap = new GNDItemMap();
            interactItem.setupLevelInteractMapContent(interactMap, this.getLevel(), targetX, targetY, this, item);
        }
        int attackHeight = this.getCurrentAttackHeight();
        this.animAttackTotal = 1;
        this.animAttack = 0;
        this.attackAnimTime = interactItem.getLevelInteractAttackAnimTime(item, this);
        this.animAttackCooldown = this.attackAnimTime;
        ++this.inaccuracySeedCounter;
        InventoryItem resultItem = interactItem.onLevelInteract(this.getLevel(), targetX, targetY, this, attackHeight, item, slot, seed, interactMap);
        if (slot != null) {
            slot.setItem(resultItem);
        }
        this.showItemLevelInteract(interactItem, item, targetX, targetY, seed, interactMap);
        ++this.animAttack;
        return interactMap;
    }

    protected final GNDItemMap runItemLevelInteract(ItemInteractAction interactItem, InventoryItem item, int targetX, int targetY, ItemAttackSlot slot, GNDItemMap interactMap) {
        return this.runItemLevelInteract(interactItem, item, targetX, targetY, Item.getRandomAttackSeed(GameRandom.globalRandom), slot, interactMap);
    }

    protected GNDItemMap runItemMobInteract(ItemInteractAction interactItem, InventoryItem item, int targetX, int targetY, Mob targetMob, int seed, ItemAttackSlot slot, GNDItemMap interactMap) {
        if (interactMap == null) {
            interactMap = new GNDItemMap();
            interactItem.setupMobInteractMapContent(interactMap, this.getLevel(), targetMob, this, item);
        }
        int attackHeight = this.getCurrentAttackHeight();
        this.animAttackTotal = 1;
        this.animAttack = 0;
        this.attackAnimTime = interactItem.getMobInteractAnimTime(item, this);
        this.animAttackCooldown = this.attackAnimTime;
        ++this.inaccuracySeedCounter;
        InventoryItem resultItem = interactItem.onMobInteract(this.getLevel(), targetMob, this, attackHeight, item, slot, seed, interactMap);
        if (slot != null) {
            slot.setItem(resultItem);
        }
        this.showItemMobInteract(interactItem, item, targetX, targetY, targetMob, seed, interactMap);
        ++this.animAttack;
        return interactMap;
    }

    protected final GNDItemMap runItemMobInteract(ItemInteractAction interactItem, InventoryItem item, Mob targetMob, int seed, ItemAttackSlot slot, GNDItemMap interactMap) {
        Rectangle selectionBox = targetMob.getSelectBox();
        int centerX = selectionBox.x + selectionBox.width / 2;
        int centerY = selectionBox.y + selectionBox.height / 2;
        return this.runItemMobInteract(interactItem, item, centerX, centerY, targetMob, seed, slot, interactMap);
    }

    protected final GNDItemMap runItemMobInteract(ItemInteractAction interactItem, InventoryItem item, Mob targetMob, ItemAttackSlot slot, GNDItemMap interactMap) {
        return this.runItemMobInteract(interactItem, item, targetMob, Item.getRandomAttackSeed(GameRandom.globalRandom), slot, interactMap);
    }

    public void showItemAttack(InventoryItem item, int targetX, int targetY, int animAttack, int seed, GNDItemMap attackMap) {
        if (attackMap == null) {
            attackMap = new GNDItemMap();
            item.item.setupAttackMapContent(attackMap, this.getLevel(), targetX, targetY, this, seed, item);
        }
        int attackHeight = this.getCurrentAttackHeight();
        item.item.showAttack(this.getLevel(), targetX, targetY, this, attackHeight, item, animAttack, seed, attackMap);
        this.attackingItem = item;
        this.attackDir = GameMath.normalize((float)targetX - this.x, (float)targetY - this.y);
        this.beforeAttackDir = this.getDir();
        if (item.item.showAttackAllDirections(this, item)) {
            this.setFacingDir(targetX - this.getX(), targetY - this.getY());
        } else if (targetX > this.getX()) {
            this.setDir(1);
        } else {
            this.setDir(3);
        }
        this.isAttacking = true;
        this.isInteractAttack = false;
        this.onCooldown = true;
        this.attackTime = this.getTime();
        this.attackSeed = seed;
        this.attackAnimTime = item.item.getAttackAnimTime(item, this);
        this.attackCooldown = Math.max(this.attackingItem.item.getAttackCooldownTime(item, this), this.attackAnimTime);
        int itemCooldown = item.item.getItemCooldownTime(item, this);
        if (itemCooldown > 0) {
            this.startItemCooldown(item.item, this.attackTime, itemCooldown);
        }
    }

    public void showItemLevelInteract(ItemInteractAction interactItem, InventoryItem item, int targetX, int targetY, int seed, GNDItemMap interactMap) {
        if (interactMap == null) {
            interactMap = new GNDItemMap();
            interactItem.setupLevelInteractMapContent(interactMap, this.getLevel(), targetX, targetY, this, item);
        }
        int attackHeight = this.getCurrentAttackHeight();
        interactItem.showLevelInteract(this.getLevel(), targetX, targetY, this, attackHeight, item, seed, interactMap);
        this.attackingItem = item;
        this.attackDir = GameMath.normalize((float)targetX - this.x, (float)targetY - this.y);
        this.beforeAttackDir = this.getDir();
        if (item.item.showAttackAllDirections(this, item)) {
            this.setFacingDir(targetX - this.getX(), targetY - this.getY());
        } else if (targetX > this.getX()) {
            this.setDir(1);
        } else {
            this.setDir(3);
        }
        this.isAttacking = true;
        this.isInteractAttack = true;
        this.onCooldown = true;
        this.attackTime = this.getTime();
        this.attackSeed = seed;
        this.attackAnimTime = interactItem.getLevelInteractAttackAnimTime(item, this);
        this.attackCooldown = Math.max(interactItem.getLevelInteractCooldownTime(item, this), this.attackAnimTime);
        int itemCooldown = item.item.getItemCooldownTime(item, this);
        if (itemCooldown > 0) {
            this.startItemCooldown(item.item, this.attackTime, itemCooldown);
        }
    }

    public void showItemMobInteract(ItemInteractAction interactItem, InventoryItem item, int targetX, int targetY, Mob targetMob, int seed, GNDItemMap interactMap) {
        if (interactMap == null) {
            interactMap = new GNDItemMap();
            interactItem.setupMobInteractMapContent(interactMap, this.getLevel(), targetMob, this, item);
        }
        int attackHeight = this.getCurrentAttackHeight();
        interactItem.showMobInteract(this.getLevel(), targetMob, this, attackHeight, item, seed, interactMap);
        this.attackingItem = item;
        this.attackDir = GameMath.normalize((float)targetX - this.x, (float)targetY - this.y);
        this.beforeAttackDir = this.getDir();
        if (item.item.showAttackAllDirections(this, item)) {
            this.setFacingDir(targetX - this.getX(), targetY - this.getY());
        } else if (targetX > this.getX()) {
            this.setDir(1);
        } else {
            this.setDir(3);
        }
        this.isAttacking = true;
        this.isInteractAttack = true;
        this.onCooldown = true;
        this.attackTime = this.getTime();
        this.attackSeed = seed;
        this.attackAnimTime = interactItem.getMobInteractAnimTime(item, this);
        this.attackCooldown = Math.max(interactItem.getMobInteractCooldownTime(item, this), this.attackAnimTime);
        int itemCooldown = item.item.getItemCooldownTime(item, this);
        if (itemCooldown > 0) {
            this.startItemCooldown(item.item, this.attackTime, itemCooldown);
        }
    }

    public void stopAttack(boolean endAttackHandler) {
        this.forceEndAttack();
        if (endAttackHandler) {
            this.endAttackHandler(false);
        }
        this.onCooldown = false;
        this.attackTime = 0L;
    }

    public void stopAttack() {
        this.stopAttack(true);
    }

    public void startItemCooldown(Item item, long startTime, int cooldown) {
        ItemCooldown newCooldown = new ItemCooldown(startTime, cooldown);
        this.itemCooldowns.compute(item.getStringID(), (stringID, last) -> {
            if (last == null) {
                return newCooldown;
            }
            long currentTime = this.getWorldEntity().getTime();
            if (newCooldown.getTimeRemaining(currentTime) >= last.getTimeRemaining(currentTime)) {
                return newCooldown;
            }
            return last;
        });
    }

    public void startItemCooldown(Item item, int cooldown) {
        this.startItemCooldown(item, this.getWorldEntity().getTime(), cooldown);
    }

    public boolean isItemOnCooldown(Item item) {
        ItemCooldown itemCooldown = this.getItemCooldown(item);
        return itemCooldown != null && itemCooldown.getTimeRemaining(this.getWorldEntity().getTime()) > 0;
    }

    public ItemCooldown getItemCooldown(Item item) {
        return this.itemCooldowns.get(item.getStringID());
    }

    public AttackHandler getAttackHandler() {
        return this.attackHandler;
    }

    public boolean isAttackHandlerFrom(InventoryItem item, ItemAttackSlot slot) {
        return this.attackHandler != null && this.attackHandler.isFrom(item, slot);
    }

    public void startAttackHandler(AttackHandler handler) {
        if (this.attackHandler == handler) {
            return;
        }
        if (this.attackHandler != null) {
            this.attackHandler.onEndAttack(false);
        }
        this.attackHandler = handler;
    }

    public void endAttackHandler(boolean bySelf) {
        if (this.attackHandler != null) {
            this.attackHandler.onEndAttack(bySelf);
        }
        this.attackHandler = null;
    }

    public void showAttackAndSendAttacker(InventoryItem item, int targetX, int targetY, int animAttack, int seed, GNDItemMap attackMap) {
        this.showItemAttackMobAbility.runAndSend(item, targetX, targetY, animAttack, seed, attackMap);
    }

    public final GNDItemMap showAttackAndSendAttacker(InventoryItem item, int targetX, int targetY, int animAttack, int seed) {
        GNDItemMap attackMap = new GNDItemMap();
        item.item.setupAttackMapContent(attackMap, this.getLevel(), targetX, targetY, this, seed, item);
        this.showAttackAndSendAttacker(item, targetX, targetY, animAttack, seed, attackMap);
        return attackMap;
    }

    public void doAndSendStopAttackAttacker(boolean endAttackHandler) {
        this.stopAttackAbility.runAndSend(endAttackHandler);
    }

    public final void addAndSendAttackerProjectile(Projectile projectile) {
        this.addAndSendAttackerProjectile(projectile, null);
    }

    public final void addAndSendAttackerProjectile(Projectile projectile, int moveDist) {
        this.addAndSendAttackerProjectile(projectile, () -> {
            if (moveDist != 0) {
                projectile.moveDist(moveDist);
            }
        });
    }

    public final void addAndSendAttackerProjectile(Projectile projectile, int moveDist, float angleOffset) {
        this.addAndSendAttackerProjectile(projectile, () -> {
            if (moveDist != 0) {
                projectile.moveDist(moveDist);
            }
            if (angleOffset != 0.0f) {
                projectile.setAngle(projectile.getAngle() + angleOffset);
            }
        });
    }

    public void addAndSendAttackerProjectile(Projectile projectile, Runnable runAfterAdded) {
        this.getLevel().entityManager.projectiles.addHidden(projectile);
        if (runAfterAdded != null) {
            runAfterAdded.run();
        }
        if (this.isServer()) {
            this.getServer().network.sendToClientsWithEntity(new PacketSpawnProjectile(projectile), projectile);
        }
    }

    public void addAndSendAttackerLevelEvent(LevelEvent event) {
        this.getLevel().entityManager.events.add(event);
    }

    public void sendAttackerPacket(RegionPositionGetter entity, Packet packet) {
        if (this.isServer()) {
            this.getServer().network.sendToClientsWithEntity(packet, entity);
        }
    }

    @Override
    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        super.remove(knockbackX, knockbackY, attacker, isDeath);
        AttackHandler attackHandler = this.attackHandler;
        this.attackHandler = null;
        if (attackHandler != null) {
            attackHandler.onEndAttack(false);
        }
        if (this.isServer()) {
            this.serverFollowersManager.clearFollowers();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        AttackHandler attackHandler = this.attackHandler;
        this.attackHandler = null;
        if (attackHandler != null) {
            attackHandler.onEndAttack(false);
        }
    }

    public abstract ItemAttackSlot getCurrentSelectedAttackSlot();

    public abstract boolean hasValidSummonItem(Item var1, CheckSlotType var2);

    public float getWeaponSkillPercent(InventoryItem item) {
        return 1.0f;
    }

    public int getInaccuracySeed(InventoryItem item) {
        return (int)((long)this.getUniqueID() * (this.getTime() / 1000L) * (long)this.inaccuracySeedCounter);
    }

    public static class ShowItemAttackMobAbility
    extends MobAbility {
        public final ItemAttackerMob attackerMob;

        public ShowItemAttackMobAbility(ItemAttackerMob attackerMob) {
            this.attackerMob = attackerMob;
        }

        public void runAndSend(InventoryItem item, int targetX, int targetY, int animAttack, int seed, GNDItemMap attackMap) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            item.addPacketContent(writer);
            writer.putNextInt(targetX);
            writer.putNextInt(targetY);
            writer.putNextShortUnsigned(seed);
            writer.putNextShortUnsigned(animAttack);
            attackMap.writePacket(writer);
            this.runAndSendAbility(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            InventoryItem item = InventoryItem.fromContentPacket(reader);
            int targetX = reader.getNextInt();
            int targetY = reader.getNextInt();
            int seed = reader.getNextShortUnsigned();
            int animAttack = reader.getNextShortUnsigned();
            GNDItemMap attackMap = new GNDItemMap(reader);
            this.attackerMob.showItemAttack(item, targetX, targetY, animAttack, seed, attackMap);
        }
    }
}

