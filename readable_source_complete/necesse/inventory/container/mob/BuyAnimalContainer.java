/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.mob;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.ItemCostList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.BuyableAnimalMob;
import necesse.gfx.GameResources;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.mob.MobContainer;

public class BuyAnimalContainer
extends MobContainer {
    public BuyableAnimalMob buyableAnimalMob;
    public ItemCostList price;
    public final EmptyCustomAction buyAnimalAction;

    public BuyAnimalContainer(final NetworkClient client, int uniqueSeed, Mob mob, Packet content) {
        super(client, uniqueSeed, mob);
        PacketReader reader = new PacketReader(content);
        this.price = new ItemCostList(reader);
        if (mob instanceof BuyableAnimalMob) {
            this.buyableAnimalMob = (BuyableAnimalMob)((Object)mob);
        } else {
            System.err.println(mob + " is not a buyable animal");
        }
        this.buyAnimalAction = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (BuyAnimalContainer.this.canPayForAnimal()) {
                    BuyAnimalContainer.this.price.buy(client.playerMob, false, false);
                    if (client.isClient()) {
                        SoundManager.playSound(GameResources.coins, SoundEffect.globalEffect());
                    } else {
                        client.getServerClient().closeContainer(true);
                        BuyAnimalContainer.this.buyableAnimalMob.onBought(client.playerMob);
                    }
                }
            }
        });
    }

    public boolean canPayForAnimal() {
        return this.price.canBuy(this.client.playerMob, false, false);
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (this.buyableAnimalMob == null) {
            return false;
        }
        return super.isValid(client);
    }

    public static Packet getContainerContent(ItemCostList cost) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        cost.writePacketData(writer);
        return packet;
    }
}

