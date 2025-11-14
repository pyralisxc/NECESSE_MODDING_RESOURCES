/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWorldPosition;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.HomePortalMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.gameObject.RespawnObject;
import necesse.level.maps.Level;

public class PortalFlaskItem
extends ConsumableItem
implements ItemInteractAction {
    public PortalFlaskItem() {
        super(1, false);
        this.attackAnimTime.setBaseValue(500);
        this.rarity = Item.Rarity.RARE;
        this.itemCooldownTime.setBaseValue(2000);
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 30000;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (this.isSingleUse(player)) {
            item.setAmount(item.getAmount() - 1);
        }
        if (level.isServer()) {
            ServerClient client = player.getServerClient();
            client.validateSpawnPoint(true);
            Point offset = new Point(16, 16);
            Level newLevel = level.getServer().world.getLevel(client.spawnLevelIdentifier);
            if (!client.isDefaultSpawnPoint()) {
                offset = RespawnObject.calculateSpawnOffset(newLevel, client.spawnTile.x, client.spawnTile.y, client);
            }
            Point2D.Float placeDir = GameMath.normalize((float)x - player.x, (float)y - player.y);
            HomePortalMob portal1 = (HomePortalMob)MobRegistry.getMob("homeportal", level);
            HomePortalMob portal2 = (HomePortalMob)MobRegistry.getMob("homeportal", newLevel);
            for (MobWorldPosition portal : client.homePortals) {
                Mob mob = portal.getMob(client.getServer(), false);
                if (mob == null) continue;
                mob.remove(0.0f, 0.0f, null, true);
            }
            client.homePortals.clear();
            portal1.dx = placeDir.x * 50.0f;
            portal1.dy = placeDir.y * 50.0f;
            int portal1Y = (int)player.y - 4;
            if (level.collides((Shape)portal1.getCollision((int)player.x, portal1Y), portal1.getLevelCollisionFilter())) {
                portal1Y += 4;
            }
            level.entityManager.addMob(portal1, (int)player.x, portal1Y);
            Point2D.Float randomOffset = GameMath.getAngleDir(GameRandom.globalRandom.nextInt(360));
            newLevel.entityManager.addMob(portal2, (float)(client.spawnTile.x * 32 + offset.x) + randomOffset.x, (float)(client.spawnTile.y * 32 + offset.y) + randomOffset.y - 4.0f);
            client.homePortals.add(new MobWorldPosition(portal1));
            client.homePortals.add(new MobWorldPosition(portal2));
            portal1.setupPortal(client, portal2);
            portal2.setupPortal(client, portal1);
        }
        return item;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.magicbolt4, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.5f).pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.1f)));
        }
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        return null;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "homeportaltip"));
        tooltips.add(Localization.translate("itemtooltip", "homeportalcleartip"));
        if (!this.isSingleUse(perspective)) {
            tooltips.add(Localization.translate("itemtooltip", "infiniteuse"));
        }
        return tooltips;
    }

    @Override
    public ItemControllerInteract getControllerInteract(Level level, PlayerMob player, InventoryItem item, boolean beforeObjectInteract, int interactDir, LinkedList<Rectangle> mobInteractBoxes, LinkedList<Rectangle> tileInteractBoxes) {
        if (beforeObjectInteract && !this.overridesObjectInteract(level, player, item)) {
            return null;
        }
        return new ItemControllerInteract(player.getX(), player.getY()){

            @Override
            public DrawOptions getDrawOptions(GameCamera camera) {
                return null;
            }

            @Override
            public void onCurrentlyFocused(GameCamera camera) {
            }
        };
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return true;
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        if (level.isServer() && attackerMob.isPlayer) {
            ServerClient client = ((PlayerMob)attackerMob).getServerClient();
            for (MobWorldPosition portal : client.homePortals) {
                Mob mob = portal.getMob(client.getServer(), false);
                if (mob == null) continue;
                mob.remove(0.0f, 0.0f, null, true);
            }
            client.homePortals.clear();
        }
        return item;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "flask");
    }
}

