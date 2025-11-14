/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.Packet
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.network.packet.PacketShowAttack
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.util.GameMath
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.MousePositionAttackHandler
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawables.SortedDrawable
 *  necesse.gfx.ui.HUD
 *  necesse.inventory.InventoryItem
 *  necesse.level.maps.hudManager.HudDrawElement
 *  necesse.level.maps.regionSystem.RegionPositionGetter
 */
package aphorea.items.tools.weapons.melee.rapier.logic;

import aphorea.items.tools.weapons.melee.rapier.AphRapierToolItem;
import aphorea.items.tools.weapons.melee.rapier.logic.RapierDashLevelEvent;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MousePositionAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.ui.HUD;
import necesse.inventory.InventoryItem;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class RapierDashAttackHandler
extends MousePositionAttackHandler {
    public int chargeTime;
    public AphRapierToolItem rapierItem;
    public long startTime;
    public InventoryItem item;
    public int seed;
    public Color particleColors;
    public boolean endedByInteract;
    protected HudDrawElement hudDrawElement;

    public RapierDashAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, AphRapierToolItem rapierItem, int chargeTime, Color particleColors, int seed) {
        super(attackerMob, slot, 20);
        this.item = item;
        this.rapierItem = rapierItem;
        this.chargeTime = chargeTime;
        this.particleColors = particleColors;
        this.seed = seed;
        this.startTime = attackerMob.getWorldEntity().getLocalTime();
    }

    public long getTimeSinceStart() {
        return this.attackerMob.getWorldEntity().getLocalTime() - this.startTime;
    }

    public float getChargePercent() {
        return (float)this.getTimeSinceStart() / (float)this.chargeTime;
    }

    public void onUpdate() {
        float chargePercent;
        super.onUpdate();
        if (this.attackerMob.isClient() && this.hudDrawElement == null) {
            this.hudDrawElement = this.attackerMob.getLevel().hudManager.addElement(new HudDrawElement(){

                public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                    if (RapierDashAttackHandler.this.attackerMob.getAttackHandler() != RapierDashAttackHandler.this) {
                        this.remove();
                    } else {
                        float distance = RapierDashAttackHandler.this.getChargeDistance(RapierDashAttackHandler.this.getChargePercent());
                        if (distance > 0.0f) {
                            Point2D.Float dir = GameMath.normalize((float)((float)RapierDashAttackHandler.this.lastX - RapierDashAttackHandler.this.attackerMob.x), (float)((float)RapierDashAttackHandler.this.lastY - RapierDashAttackHandler.this.attackerMob.y));
                            final DrawOptions drawOptions = HUD.getArrowHitboxIndicator((float)RapierDashAttackHandler.this.attackerMob.x, (float)RapierDashAttackHandler.this.attackerMob.y, (float)dir.x, (float)dir.y, (int)((int)distance), (int)50, (Color)new Color(0, 0, 0, 0), (Color)new Color(220, 255, 255, 100), (Color)new Color(0, 0, 0, 100), (GameCamera)camera);
                            list.add(new SortedDrawable(){

                                public int getPriority() {
                                    return 1000;
                                }

                                public void draw(TickManager tickManager) {
                                    drawOptions.draw();
                                }
                            });
                        }
                    }
                }
            });
        }
        if ((chargePercent = this.getChargePercent()) >= 1.0f) {
            this.attackerMob.endAttackHandler(true);
            return;
        }
        InventoryItem showItem = this.item.copy();
        showItem.getGndData().setFloat("chargePercent", chargePercent);
        showItem.getGndData().setBoolean("charging", true);
        GNDItemMap attackMap = new GNDItemMap();
        this.attackerMob.showItemAttack(showItem, this.lastX, this.lastY, 0, this.seed, attackMap);
        if (this.attackerMob.isServer()) {
            if (this.attackerMob.isPlayer) {
                PlayerMob player = (PlayerMob)this.attackerMob;
                ServerClient client = player.getServerClient();
                this.attackerMob.getServer().network.sendToClientsWithEntityExcept((Packet)new PacketShowAttack(player, showItem, this.lastX, this.lastY, 0, this.seed, attackMap), (RegionPositionGetter)this.attackerMob, client);
            } else {
                this.attackerMob.showItemAttackMobAbility.runAndSend(showItem, this.lastX, this.lastY, 0, this.seed, attackMap);
            }
        }
    }

    public void onMouseInteracted(int levelX, int levelY) {
        this.endedByInteract = true;
        this.attackerMob.endAttackHandler(false);
    }

    public void onControllerInteracted(float aimX, float aimY) {
        this.endedByInteract = true;
        this.attackerMob.endAttackHandler(false);
    }

    public void onEndAttack(boolean bySelf) {
        float chargePercent = this.getChargePercent();
        if (!this.endedByInteract && chargePercent >= 0.5f) {
            if (this.attackerMob.isPlayer) {
                ((PlayerMob)this.attackerMob).constantAttack = true;
            }
            InventoryItem attackItem = this.item.copy();
            attackItem.getGndData().setFloat("chargePercent", chargePercent);
            this.attackerMob.showAttackAndSendAttacker(attackItem, this.lastX, this.lastY, 0, this.seed);
            Point2D.Float dir = GameMath.normalize((float)((float)this.lastX - this.attackerMob.x), (float)((float)this.lastY - this.attackerMob.y));
            chargePercent = Math.min(chargePercent, 1.0f);
            RapierDashLevelEvent event = new RapierDashLevelEvent((Mob)this.attackerMob, this.seed, dir.x, dir.y, this.getChargeDistance(chargePercent), (int)(200.0f * chargePercent), this.rapierItem.getAttackDamage(this.item).modDamage(this.rapierItem.getDashDamageMultiplier(this.item)));
            this.attackerMob.addAndSendAttackerLevelEvent((LevelEvent)event);
        }
        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }
    }

    public float getChargeDistance(float chargePercent) {
        return (chargePercent = Math.min(chargePercent, 1.0f)) > 0.5f ? (chargePercent - 0.5f) * 2.0f * (float)this.rapierItem.dashRange.getValue(this.rapierItem.getUpgradeTier(this.item)).intValue() : 0.0f;
    }
}

