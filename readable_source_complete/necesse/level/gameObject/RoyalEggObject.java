/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RoyalEggObject
extends GameObject {
    public GameTexture texture;

    public RoyalEggObject() {
        super(new Rectangle(4, 4, 24, 24));
        this.mapColor = new Color(156, 51, 39);
        this.displayMapTooltip = true;
        this.objectHealth = 1;
        this.attackThrough = true;
        this.isLightTransparent = true;
        this.toolType = ToolType.ALL;
        this.lightSat = 0.2f;
        this.lightLevel = 50;
        this.hoverHitbox = new Rectangle(0, -24, 32, 56);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("item", "royalegg");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/royalegg");
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        return new LootTable();
    }

    @Override
    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameResources.npcdeath, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16));
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage, Attacker attacker) {
        if (!level.objectLayer.isPlayerPlaced(x, y)) {
            super.attackThrough(level, x, y, damage, attacker);
        }
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage) {
        super.attackThrough(level, x, y, damage);
        if (damage.damage > 0.0f) {
            this.playDamageSound(level, x, y, true);
        }
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        JournalChallenge challenge;
        super.onDestroyed(level, layerID, x, y, attacker, client, itemsDropped);
        if (client != null && !(challenge = JournalChallengeRegistry.getChallenge(JournalChallengeRegistry.DESTROY_ROYAL_EGG_ID)).isCompleted(client) && challenge.isJournalEntryDiscovered(client)) {
            challenge.markCompleted(client);
            client.forceCombineNewStats();
        }
        this.spawnBoss(level, x, y);
    }

    public void spawnBoss(Level level, int tileX, int tileY) {
        if (level.isServer()) {
            System.out.println("Queen Spider has been spawned at " + level.getIdentifier() + ".");
            float angle = GameRandom.globalRandom.nextInt(360);
            float nx = (float)Math.cos(Math.toRadians(angle));
            float ny = (float)Math.sin(Math.toRadians(angle));
            float distance = 960.0f;
            Mob mob = MobRegistry.getMob("queenspider", level);
            level.entityManager.addMob(mob, tileX * 32 + 16 + (int)(nx * distance), tileY * 32 + 16 + (int)(ny * distance));
            level.getServer().network.sendToClientsWithEntity(new PacketChatMessage(new LocalMessage("misc", "bossawoke", "name", mob.getLocalization())), mob);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX) - (this.texture.getWidth() - 32) / 2;
        int drawY = camera.getTileDrawY(tileY) - (this.texture.getHeight() - 32);
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).pos(drawX, drawY);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX) - (this.texture.getWidth() - 32) / 2;
        int drawY = camera.getTileDrawY(tileY) - (this.texture.getHeight() - 32);
        this.texture.initDraw().light(light).alpha(alpha).draw(drawX, drawY);
    }

    @Override
    public void onMouseHover(Level level, int x, int y, GameCamera camera, PlayerMob perspective, boolean debug) {
        super.onMouseHover(level, x, y, camera, perspective, debug);
        GameTooltipManager.addTooltip(new StringTooltips(this.getDisplayName()), TooltipLocation.INTERACT_FOCUS);
    }
}

