/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.client.ClientClient
 *  necesse.engine.network.packet.PacketRequestPlayerData
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.ParticleTypeSwitcher
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 *  necesse.entity.particle.Particle$GType
 *  necesse.level.maps.Level
 */
package aphorea.buffs;

import aphorea.utils.AphColors;
import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.level.maps.Level;

public class VenomExtractBuff
extends Buff {
    public static ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});

    public VenomExtractBuff() {
        this.isImportant = true;
        this.canCancel = false;
        this.isVisible = true;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.MAX_RESILIENCE, (Object)Float.valueOf(-100.0f));
        buff.addModifier(BuffModifiers.RESILIENCE_REGEN, (Object)Float.valueOf(-100.0f));
        buff.addModifier(BuffModifiers.HEALTH_REGEN, (Object)Float.valueOf(-100.0f));
        buff.addModifier(BuffModifiers.COMBAT_HEALTH_REGEN, (Object)Float.valueOf(-100.0f));
        buff.addModifier(BuffModifiers.BLINDNESS, (Object)Float.valueOf(0.25f));
    }

    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        if (buff.owner.getHealth() > 1) {
            buff.owner.setHealth(1);
            if (buff.owner.isPlayer) {
                ServerClient serverClient = ((PlayerMob)buff.owner).getServerClient();
                buff.owner.getServer().network.sendToClientsAtEntireLevel((Packet)new VenomExtractBuffPacket(serverClient.slot), buff.owner.getLevel());
            }
        }
    }

    public void updateLocalDisplayName() {
        super.updateLocalDisplayName();
        this.displayName = this.isVisible ? new StaticMessage(Localization.translate((String)"buff", (String)this.getLocalizationKey()) + "\n" + Localization.translate((String)"itemtooltip", (String)(this.getLocalizationKey() + "desc"))) : new StaticMessage(this.getStringID());
    }

    public static class VenomExtractBuffPacket
    extends Packet {
        public final int slot;

        public VenomExtractBuffPacket(byte[] data) {
            super(data);
            PacketReader reader = new PacketReader((Packet)this);
            this.slot = reader.getNextByteUnsigned();
        }

        public VenomExtractBuffPacket(int slot) {
            this.slot = slot;
            PacketWriter writer = new PacketWriter((Packet)this);
            writer.putNextByteUnsigned(slot);
        }

        public void processClient(NetworkPacket packet, Client client) {
            if (client.getLevel() != null) {
                ClientClient target = client.getClient(this.slot);
                if (target != null && target.isSamePlace(client.getLevel())) {
                    VenomExtractBuffPacket.applyToPlayer(target.playerMob.getLevel(), (Mob)target.playerMob);
                } else {
                    client.network.sendPacket((Packet)new PacketRequestPlayerData(this.slot));
                }
            }
        }

        public static void applyToPlayer(Level level, Mob mob) {
            mob.setHealth(1);
            if (level != null && level.isClient()) {
                for (int i = 0; i < 40; ++i) {
                    int angle = (int)(360.0f + GameRandom.globalRandom.nextFloat() * 360.0f);
                    float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                    float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                    mob.getLevel().entityManager.addParticle((Entity)mob, particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8f).color((Color)GameRandom.globalRandom.getOneOf((Object[])new Color[]{AphColors.green, AphColors.blood})).heightMoves(20.0f, 0.0f).lifeTime(500);
                }
            }
        }
    }
}

