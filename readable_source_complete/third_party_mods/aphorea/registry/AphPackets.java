/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.PacketRegistry
 */
package aphorea.registry;

import aphorea.buffs.VenomExtractBuff;
import aphorea.items.tools.weapons.melee.sword.TheNarcissist;
import aphorea.mobs.friendly.WildPhosphorSlime;
import aphorea.packets.AphAreaShowPacket;
import aphorea.packets.AphCustomPushPacket;
import aphorea.packets.AphRemoveObjectEntity;
import aphorea.packets.AphRuneOfUnstableGelSlimePacket;
import aphorea.packets.AphRunesInjectorAbilityPacket;
import necesse.engine.registries.PacketRegistry;

public class AphPackets {
    public static void registerCore() {
        PacketRegistry.registerPacket(AphCustomPushPacket.class);
        PacketRegistry.registerPacket(AphRunesInjectorAbilityPacket.class);
        PacketRegistry.registerPacket(AphRuneOfUnstableGelSlimePacket.class);
        AphPackets.clientOnly();
    }

    public static void clientOnly() {
        PacketRegistry.registerPacket(AphAreaShowPacket.class);
        PacketRegistry.registerPacket(VenomExtractBuff.VenomExtractBuffPacket.class);
        PacketRegistry.registerPacket(WildPhosphorSlime.PhosphorSlimeParticlesPacket.class);
        PacketRegistry.registerPacket(AphRemoveObjectEntity.class);
        PacketRegistry.registerPacket(TheNarcissist.NarcissistHitMob.class);
    }
}

