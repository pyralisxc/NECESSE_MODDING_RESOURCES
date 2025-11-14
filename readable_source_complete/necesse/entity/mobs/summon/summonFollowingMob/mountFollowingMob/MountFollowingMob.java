/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketMobMount;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SameNearSoundCooldown;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.summonFollowingMob.SummonedFollowingMob;

public class MountFollowingMob
extends SummonedFollowingMob {
    public MountFollowingMob(int health) {
        super(health);
    }

    @Override
    public void init() {
        this.shouldPlayAmbience = false;
        if (this.isClient()) {
            this.playAmbientSound();
        }
        super.init();
    }

    @Override
    public void playAmbientSound() {
        SoundSettings ambientSound = this.getAmbientSound();
        if (ambientSound != null) {
            ambientSound.cooldown(new SameNearSoundCooldown(200, 96));
            SoundManager.playSound(ambientSound, this);
        }
    }

    @Override
    public void interact(PlayerMob player) {
        super.interact(player);
        if (this.isServer() && player.isServerClient()) {
            ServerClient client = player.getServerClient();
            if (player.getUniqueID() == this.rider) {
                player.dismount();
                client.getServer().network.sendToClientsWithEntity(new PacketMobMount(client.slot, -1, false, player.x, player.y), player);
            } else if (client.playerMob.serverFollowersManager.isFollower(this)) {
                if (player.mount(this, false)) {
                    client.getServer().network.sendToClientsWithEntity(new PacketMobMount(client.slot, this.getUniqueID(), false, player.x, player.y), player);
                }
            } else {
                client.sendChatMessage(new LocalMessage("misc", "mountnotown"));
            }
        }
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return null;
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return null;
    }

    @Override
    public boolean canInteract(Mob mob) {
        return !this.isMounted() && mob.getUniqueID() == this.followingUniqueID || mob.getUniqueID() == this.rider;
    }

    @Override
    protected String getInteractTip(PlayerMob perspective, boolean debug) {
        if (this.isMounted()) {
            return null;
        }
        return Localization.translate("controls", "mounttip");
    }
}

