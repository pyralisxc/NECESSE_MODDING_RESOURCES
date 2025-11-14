/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventoryItem;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.ItemCostList;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanAngerTargetAINode;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.BuyableAnimalMob;
import necesse.entity.mobs.friendly.ClientInteractMob;
import necesse.entity.mobs.friendly.FriendlyMob;
import necesse.entity.mobs.friendly.InteractingClients;
import necesse.entity.mobs.friendly.RopeClearerMob;
import necesse.entity.trails.RopeTrail;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.mob.BuyAnimalContainer;
import necesse.inventory.item.miscItem.RopeItem;
import necesse.level.maps.Level;

public class FriendlyRopableMob
extends FriendlyMob
implements BuyableAnimalMob,
ClientInteractMob {
    protected RopeTrail ropeTrail;
    private final LevelMob<Mob> ropeMob = new LevelMob<Mob>(){

        @Override
        public void onMobChanged(Mob oldMob, Mob newMob) {
            FriendlyRopableMob.this.respawnRopeTrail(newMob);
            if (newMob != null) {
                FriendlyRopableMob.this.lastClearRopeCheckTime = newMob.getTime();
            }
        }

        @Override
        public Level onMobChangedLevel(Mob mob, Level currentLevel) {
            if (currentLevel.isServer()) {
                currentLevel.entityManager.changeMobLevel(FriendlyRopableMob.this, mob.getLevel(), mob.getX(), mob.getY(), true);
                FriendlyRopableMob.this.respawnRopeTrail(mob);
                return mob.getLevel();
            }
            return super.onMobChangedLevel(mob, currentLevel);
        }

        @Override
        public void onMobRemoved(Mob mob) {
            super.onMobRemoved(mob);
            if (FriendlyRopableMob.this.isServer() && FriendlyRopableMob.this.removeIfRoperRemoved && !mob.hasDied()) {
                FriendlyRopableMob.this.roperRemoved = true;
                FriendlyRopableMob.this.remove();
            }
        }
    };
    protected InventoryItem ropeItem;
    protected int ropeRange = 100;
    protected float ropeOverDistanceBuffer;
    public boolean removeIfRoperRemoved;
    public boolean shouldEscape;
    protected boolean roperRemoved;
    public ItemCostList buyPrice;
    protected long lastClearRopeCheckTime;
    public final InteractingClients interactingClients = new InteractingClients(this){

        @Override
        public synchronized void refresh(ServerClient client) {
            super.refresh(client);
            Mob roper = FriendlyRopableMob.this.getRopeMob();
            if (roper instanceof ClientInteractMob) {
                ((ClientInteractMob)((Object)roper)).refreshInteracting(client);
            }
        }

        @Override
        public synchronized void remove(ServerClient client) {
            super.remove(client);
            Mob roper = FriendlyRopableMob.this.getRopeMob();
            if (roper instanceof ClientInteractMob) {
                ((ClientInteractMob)((Object)roper)).removeInteracting(client);
            }
        }
    };

    public FriendlyRopableMob(int health) {
        super(health);
    }

    protected int getRopeRange() {
        return this.ropeRange;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("ropeMobUniqueID", this.ropeMob.uniqueID);
        if (this.ropeItem != null) {
            SaveData ropeItem = new SaveData("ropeItem");
            this.ropeItem.addSaveData(ropeItem);
            save.addSaveData(ropeItem);
            save.addInt("ropeRange", this.ropeRange);
            save.addBoolean("removeIfRoperRemoved", this.removeIfRoperRemoved);
        }
        if (this.buyPrice != null) {
            this.buyPrice.addSaveData(save, "buyPrice");
        }
        save.addLong("lastClearRopeCheckTime", this.lastClearRopeCheckTime);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.ropeMob.uniqueID = save.getInt("ropeMobUniqueID", -1);
        this.respawnRopeTrail(this.getRopeMob());
        LoadData ropeItem = save.getFirstLoadDataByName("ropeItem");
        if (ropeItem != null) {
            this.ropeItem = InventoryItem.fromLoadData(ropeItem);
        }
        this.ropeRange = save.getInt("ropeRange", this.ropeRange, false);
        this.removeIfRoperRemoved = save.getBoolean("removeIfRoperRemoved", this.removeIfRoperRemoved, false);
        this.buyPrice = ItemCostList.fromLoadData(save, "buyPrice", false);
        this.lastClearRopeCheckTime = save.getLong("lastClearRopeCheckTime", this.lastClearRopeCheckTime, false);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        if (this.buyPrice != null) {
            writer.putNextBoolean(true);
            this.buyPrice.writePacketData(writer);
        } else {
            writer.putNextBoolean(false);
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.buyPrice = reader.getNextBoolean() ? new ItemCostList(reader) : null;
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextInt(this.ropeMob.uniqueID);
        InventoryItem.addPacketContent(this.ropeItem, writer);
        if (this.ropeItem != null) {
            writer.putNextShortUnsigned(this.ropeRange);
        }
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.ropeMob.uniqueID = reader.getNextInt();
        this.ropeItem = InventoryItem.fromContentPacket(reader);
        if (this.ropeItem != null) {
            this.ropeRange = reader.getNextShortUnsigned();
        }
        this.ropeMob.get(this.getLevel());
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        Mob mob = this.ropeMob.get(this.getLevel());
        if (mob != null) {
            int ropeDistance;
            float distance = this.getDistance(mob);
            if (distance > (float)(ropeDistance = this.getRopeRange())) {
                if (!this.isClient() && distance > (float)(ropeDistance + 50)) {
                    this.ropeOverDistanceBuffer += delta;
                    if (this.ropeOverDistanceBuffer >= 2000.0f) {
                        FollowerAINode.teleportCloseTo(this, mob, 1);
                        this.sendMovementPacket(true);
                        this.ropeOverDistanceBuffer = 0.0f;
                    }
                } else {
                    this.ropeOverDistanceBuffer = 0.0f;
                }
                float distanceX = mob.getX() - this.getX();
                float distanceY = mob.getY() - this.getY();
                Point2D.Float normalize = GameMath.normalize(distanceX, distanceY);
                this.dx = normalize.x * distance / 2.0f;
                this.dy = normalize.y * distance / 2.0f;
                this.setFacingDir(distanceX, distanceY);
            }
        } else if (this.ropeMob.uniqueID != -1) {
            boolean dropRope = !this.removeIfRoperRemoved || !this.roperRemoved;
            this.removeRope(dropRope);
        }
        if (this.ropeTrail != null) {
            this.ropeTrail.update(0.0f, 0.0f);
        }
    }

    @Override
    public PathDoorOption getPathDoorOption() {
        if (this.getLevel() != null) {
            return this.getLevel().regionManager.CANNOT_PASS_DOORS_OPTIONS;
        }
        return null;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        Mob mob = this.ropeMob.get(this.getLevel());
        if (mob != null && this.ropeItem != null) {
            ActiveBuff ab = new ActiveBuff(BuffRegistry.HOLD_ITEM_BUFF, mob, 1.0f, null);
            ab.getGndData().setItem("holdItem", (GNDItem)new GNDItemInventoryItem(this.ropeItem));
            mob.buffManager.addBuff(ab, false);
        }
        if (mob == null && this.ropeTrail != null) {
            this.ropeTrail.remove();
            this.ropeTrail = null;
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        Mob mob = this.ropeMob.get(this.getLevel());
        if (mob instanceof RopeClearerMob) {
            if (((RopeClearerMob)((Object)mob)).shouldClearAfterTime(this.lastClearRopeCheckTime)) {
                ((RopeClearerMob)((Object)mob)).clearRopeFromMob(this);
                mob = this.ropeMob.get(this.getLevel());
            } else {
                this.lastClearRopeCheckTime = this.getTime();
            }
        }
        if (mob != null && this.ropeItem != null) {
            ActiveBuff ab = new ActiveBuff(BuffRegistry.HOLD_ITEM_BUFF, mob, 1.0f, null);
            ab.getGndData().setItem("holdItem", (GNDItem)new GNDItemInventoryItem(this.ropeItem));
            mob.buffManager.addBuff(ab, false);
        }
        if (mob == null && this.ropeItem != null) {
            boolean consumeRope = true;
            if (this.ropeItem.item instanceof RopeItem) {
                consumeRope = ((RopeItem)this.ropeItem.item).consumesRope();
            }
            if (consumeRope) {
                this.getLevel().entityManager.pickups.add(this.ropeItem.copy().getPickupEntity(this.getLevel(), this.getX(), this.getY()));
            }
            this.ropeItem = null;
            this.sendMovementPacket(false);
        }
    }

    @Override
    protected void doWasHitLogic(MobWasHitEvent event) {
        super.doWasHitLogic(event);
        if (this.buyPrice != null && !this.shouldEscape) {
            HumanAngerTargetAINode.addNearbyHumansAnger(this, event.attacker, m -> !m.isSettler(), false);
        }
    }

    @Override
    public boolean canBeTargeted(Mob attacker, NetworkClient attackerClient) {
        Mob roper = this.ropeMob.get(this.getLevel());
        if (roper != null && !roper.isPlayer && !roper.canBeTargeted(attacker, attackerClient)) {
            return false;
        }
        return super.canBeTargeted(attacker, attackerClient);
    }

    @Override
    public void interact(PlayerMob player) {
        super.interact(player);
        if (this.ropeMob.uniqueID != -1 && this.ropeMob.uniqueID == player.getUniqueID()) {
            this.removeRope(true);
        } else if (this.buyPrice != null && this.isServer() && player.isServerClient()) {
            ServerClient client = player.getServerClient();
            PacketOpenContainer container = PacketOpenContainer.Mob(ContainerRegistry.BUY_ANIMAL_CONTAINER, this, BuyAnimalContainer.getContainerContent(this.buyPrice));
            ContainerRegistry.openAndSendContainer(client, container);
        }
    }

    @Override
    public boolean canInteract(Mob mob) {
        if (this.ropeMob.uniqueID != -1 && this.ropeMob.uniqueID == mob.getUniqueID()) {
            return true;
        }
        return this.buyPrice != null;
    }

    @Override
    protected String getInteractTip(PlayerMob perspective, boolean debug) {
        if (this.ropeMob.uniqueID != -1 && this.ropeMob.uniqueID == perspective.getUniqueID()) {
            return Localization.translate("controls", "removeropetip");
        }
        return Localization.translate("ui", "buyanimal", "animal", this.getDisplayName());
    }

    @Override
    public InteractingClients getInteractingClients() {
        return this.interactingClients;
    }

    public void setDefaultBuyPrice(GameRandom random) {
        this.buyPrice = new ItemCostList();
        this.buyPrice.addItem("coin", random.getIntBetween(300, 400));
    }

    @Override
    public void onBought(PlayerMob from) {
        InventoryItem ropeItem = this.ropeItem;
        this.removeRope(false);
        if (ropeItem == null) {
            ropeItem = new InventoryItem("rope");
        }
        this.onRope(from.getUniqueID(), ropeItem);
        this.buyPrice = null;
        this.shouldEscape = false;
        this.canDespawn = false;
        this.markDirty();
    }

    @Override
    public boolean shouldSave() {
        if (!super.shouldSave()) {
            return false;
        }
        if (this.shouldEscape) {
            return false;
        }
        if (this.removeIfRoperRemoved) {
            Mob roper = this.ropeMob.get(this.getLevel());
            return roper == null || roper.shouldSave();
        }
        return true;
    }

    public void removeRope(boolean dropRope) {
        this.ropeMob.uniqueID = -1;
        this.respawnRopeTrail(this.getRopeMob());
        if (this.ropeItem != null && this.isServer() && dropRope) {
            boolean consumeRope = true;
            if (this.ropeItem.item instanceof RopeItem) {
                consumeRope = ((RopeItem)this.ropeItem.item).consumesRope();
            }
            if (consumeRope) {
                this.getLevel().entityManager.pickups.add(this.ropeItem.copy().getPickupEntity(this.getLevel(), this.getX(), this.getY()));
            }
        }
        this.ropeItem = null;
        this.sendMovementPacket(false);
    }

    public boolean canRope(int fromUniqueID, InventoryItem item) {
        return item.item instanceof RopeItem && !this.isRoped() && this.buyPrice == null;
    }

    public InventoryItem onRope(int fromUniqueID, InventoryItem item) {
        boolean consumeItem = true;
        if (item.item instanceof RopeItem) {
            consumeItem = ((RopeItem)item.item).consumesRope();
        }
        this.startRope(fromUniqueID, item.copy(1));
        if (consumeItem) {
            item.setAmount(item.getAmount() - 1);
        }
        return item;
    }

    public void startRope(int roperUniqueID, InventoryItem item) {
        this.ropeMob.uniqueID = roperUniqueID;
        this.ropeItem = item;
        this.ropeRange = item.item instanceof RopeItem ? ((RopeItem)item.item).getRopeRange(item) : GameRandom.globalRandom.getIntBetween(75, 150);
        this.removeIfRoperRemoved = false;
        this.sendMovementPacket(false);
        this.respawnRopeTrail(this.getRopeMob());
    }

    public void respawnRopeTrail(Mob mob) {
        if (this.ropeTrail != null) {
            this.ropeTrail.remove();
        }
        this.ropeTrail = null;
        if (mob != null) {
            this.ropeTrail = this.getNewRopeTrail(mob, this.ropeItem);
            this.getLevel().entityManager.addTrail(this.ropeTrail);
        }
    }

    protected RopeTrail getNewRopeTrail(Mob ropeMob, InventoryItem item) {
        if (item != null && item.item instanceof RopeItem) {
            return new RopeTrail(this.getLevel(), this, ropeMob, 0.0f, 0.0f, ((RopeItem)item.item).getRopeColor(item));
        }
        return new RopeTrail(this.getLevel(), this, ropeMob, 0.0f, 0.0f);
    }

    public Mob getRopeMob() {
        return this.ropeMob.get(this.getLevel());
    }

    protected boolean isBeingDragged() {
        Mob mob = this.getRopeMob();
        return mob != null && this.getDistance(mob) > (float)this.getRopeRange();
    }

    public boolean isRoped() {
        return this.ropeMob.uniqueID != -1;
    }

    @Override
    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        super.remove(knockbackX, knockbackY, attacker, isDeath);
        if (this.ropeTrail != null) {
            this.ropeTrail.remove();
        }
        this.ropeTrail = null;
        if (!(this.ropeItem == null || !this.isServer() || this.removeIfRoperRemoved && this.roperRemoved)) {
            boolean consumeRope = true;
            if (this.ropeItem.item instanceof RopeItem) {
                consumeRope = ((RopeItem)this.ropeItem.item).consumesRope();
            }
            if (consumeRope) {
                this.getLevel().entityManager.pickups.add(this.ropeItem.copy().getPickupEntity(this.getLevel(), this.getX(), this.getY()));
            }
        }
    }
}

