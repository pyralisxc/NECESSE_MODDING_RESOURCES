/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.mountItem;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.function.Supplier;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketMobMount;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.MountFollowingMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.ItemCombineResult;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.ContainerTransferResult;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class MountItem
extends Item {
    public String mobStringID;
    public boolean singleUse;
    public boolean setMounterPos = true;

    public MountItem(String mobStringID) {
        super(1);
        this.keyWords.add("mount");
        this.setItemCategory("misc", "mounts");
        this.mobStringID = mobStringID;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 30000;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "mountslot"));
        if (this.singleUse) {
            tooltips.add(Localization.translate("itemtooltip", "singleuse"));
        } else {
            tooltips.add(Localization.translate("itemtooltip", "infiniteuse"));
        }
        tooltips.add(Localization.translate("itemtooltip", "summonmounttip", "mob", MobRegistry.getDisplayName(MobRegistry.getMobID(this.mobStringID))));
        return tooltips;
    }

    @Override
    public String getInventoryRightClickControlTip(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        if (slotIndex == container.CLIENT_MOUNT_SLOT) {
            return Localization.translate("controls", "removetip");
        }
        return Localization.translate("controls", "equiptip");
    }

    @Override
    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            if (slotIndex == container.CLIENT_MOUNT_SLOT) {
                ContainerTransferResult result = container.transferToSlots(slot, Arrays.asList(new SlotIndexRange(container.CLIENT_HOTBAR_START, container.CLIENT_HOTBAR_END), new SlotIndexRange(container.CLIENT_INVENTORY_START, container.CLIENT_INVENTORY_END)));
                return new ContainerActionResult(146355839, result.error);
            }
            ItemCombineResult result = container.getSlot(container.CLIENT_MOUNT_SLOT).swapItems(slot);
            if (result.success) {
                return new ContainerActionResult(1755925617);
            }
            return new ContainerActionResult(151661709, result.error);
        };
    }

    @Override
    public boolean getConstantUse(InventoryItem item) {
        return false;
    }

    public String canUseMount(InventoryItem item, PlayerMob player, Level level) {
        Mob lastMount = player.getMount();
        if (lastMount != null) {
            GameMessage dismountError = lastMount.getMountDismountError(player, item);
            return dismountError == null ? null : dismountError.translate();
        }
        Mob m = MobRegistry.getMob(this.mobStringID, level);
        if (m.collidesWith(level, player.getX(), player.getY())) {
            return Localization.translate("misc", "cannotusemounthere", "mount", this.getDisplayName(item));
        }
        return null;
    }

    public InventoryItem useMount(ServerClient client, float playerX, float playerY, InventoryItem item, Level level) {
        PlayerMob player = client.playerMob;
        Mob lastMount = player.getMount();
        if (lastMount != null) {
            player.dx = lastMount.dx;
            player.dy = lastMount.dy;
        }
        if (lastMount != null) {
            if (lastMount.getStringID().equals(this.mobStringID)) {
                player.buffManager.removeBuff("summonedmount", true);
            } else {
                player.dismount();
                level.getServer().network.sendToClientsWithEntity(new PacketMobMount(client.slot, -1, true, playerX, playerY), player);
            }
        } else {
            Mob m = MobRegistry.getMob(this.mobStringID, level);
            FollowPosition followPosition = this.getFollowPosition(item, player);
            client.playerMob.serverFollowersManager.addFollower("summonedmount", m, followPosition, "summonedmount", 1.0f, 1, null, false);
            Point2D.Float spawnPos = this.getMountSpawnPos(m, client, playerX, playerY, item, level);
            m.setPos(spawnPos.x, spawnPos.y, true);
            boolean mount = player.mount(m, this.setMounterPos);
            if (mount) {
                m.dx = player.dx;
                m.dy = player.dy;
                if (m instanceof MountFollowingMob) {
                    ((MountFollowingMob)m).removeWhenNotInInventoryItem = item.item;
                }
                this.beforeSpawn(m, item, player);
                level.entityManager.addMob(m, spawnPos.x, spawnPos.y);
                level.getServer().network.sendToClientsWithEntity(new PacketMobMount(client.slot, m.getUniqueID(), this.setMounterPos, playerX, playerY), player);
            }
        }
        if (this.singleUse) {
            item.setAmount(item.getAmount() - 1);
        }
        return item;
    }

    public Point2D.Float getMountSpawnPos(Mob mount, ServerClient client, float playerX, float playerY, InventoryItem item, Level level) {
        return new Point2D.Float(client.playerMob.x, client.playerMob.y);
    }

    public FollowPosition getFollowPosition(InventoryItem item, PlayerMob player) {
        return FollowPosition.WALK_CLOSE;
    }

    protected void beforeSpawn(Mob mob, InventoryItem item, PlayerMob player) {
    }

    @Override
    public void setupAttackMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        super.setupAttackMapContent(map, level, x, y, attackerMob, seed, item);
        map.setFloat("attackerX", attackerMob.x);
        map.setFloat("attackerY", attackerMob.y);
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        if (!attackerMob.isPlayer) {
            return "";
        }
        return this.canUseMount(item, (PlayerMob)attackerMob, level);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        float attackerX = mapContent.getFloat("attackerX");
        float attackerY = mapContent.getFloat("attackerY");
        if (level.isServer() && attackerMob.isPlayer) {
            ServerClient client = ((PlayerMob)attackerMob).getServerClient();
            double allowed = client.playerMob.allowServerMovement(level.getServer(), client, attackerX, attackerY);
            if (allowed <= 0.0) {
                attackerMob.setPos(attackerX, attackerY, false);
            } else {
                GameLog.warn.println(client.getName() + " attempted to use mount from wrong position, snapping back " + allowed);
                level.getServer().network.sendToClientsWithEntity(new PacketPlayerMovement(client, false), client.playerMob);
            }
            return this.useMount(client, client.playerMob.x, client.playerMob.y, item, level);
        }
        if (this.singleUse) {
            item.setAmount(item.getAmount() - 1);
        }
        return item;
    }

    @Override
    public float getSinkingRate(ItemPickupEntity entity, float currentSinking) {
        return super.getSinkingRate(entity, currentSinking) / 5.0f;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "mount");
    }
}

