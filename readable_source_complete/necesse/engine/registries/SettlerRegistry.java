/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.level.maps.levelData.settlementData.settler.AlchemistSettler;
import necesse.level.maps.levelData.settlementData.settler.AnglerSettler;
import necesse.level.maps.levelData.settlementData.settler.AnimalKeeperSettler;
import necesse.level.maps.levelData.settlementData.settler.BlacksmithSettler;
import necesse.level.maps.levelData.settlementData.settler.ElderSettler;
import necesse.level.maps.levelData.settlementData.settler.ExoticMerchantSettler;
import necesse.level.maps.levelData.settlementData.settler.ExplorerSettler;
import necesse.level.maps.levelData.settlementData.settler.FarmerSettler;
import necesse.level.maps.levelData.settlementData.settler.GenericSettler;
import necesse.level.maps.levelData.settlementData.settler.GuardSettler;
import necesse.level.maps.levelData.settlementData.settler.GunsmithSettler;
import necesse.level.maps.levelData.settlementData.settler.HunterSettler;
import necesse.level.maps.levelData.settlementData.settler.LockedNoSettler;
import necesse.level.maps.levelData.settlementData.settler.MageSettler;
import necesse.level.maps.levelData.settlementData.settler.MinerSettler;
import necesse.level.maps.levelData.settlementData.settler.PawnBrokerSettler;
import necesse.level.maps.levelData.settlementData.settler.PirateSettler;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.StylistSettler;
import necesse.level.maps.levelData.settlementData.settler.TraderSettler;

public class SettlerRegistry
extends GameRegistry<SettlerRegistryElement> {
    public static final SettlerRegistry instance = new SettlerRegistry();
    public static final LockedNoSettler SETTLER_LOCKED = new LockedNoSettler();

    private SettlerRegistry() {
        super("Settler", Short.MAX_VALUE);
    }

    @Override
    public void registerCore() {
        SettlerRegistry.registerSettler("elder", new ElderSettler());
        SettlerRegistry.registerSettler("farmer", new FarmerSettler());
        SettlerRegistry.registerSettler("blacksmith", new BlacksmithSettler());
        SettlerRegistry.registerSettler("mage", new MageSettler());
        SettlerRegistry.registerSettler("hunter", new HunterSettler());
        SettlerRegistry.registerSettler("gunsmith", new GunsmithSettler());
        SettlerRegistry.registerSettler("alchemist", new AlchemistSettler());
        SettlerRegistry.registerSettler("angler", new AnglerSettler());
        SettlerRegistry.registerSettler("pawnbroker", new PawnBrokerSettler());
        SettlerRegistry.registerSettler("trader", new TraderSettler());
        SettlerRegistry.registerSettler("exoticmerchant", new ExoticMerchantSettler());
        SettlerRegistry.registerSettler("explorer", new ExplorerSettler());
        SettlerRegistry.registerSettler("miner", new MinerSettler());
        SettlerRegistry.registerSettler("animalkeeper", new AnimalKeeperSettler());
        SettlerRegistry.registerSettler("stylist", new StylistSettler());
        SettlerRegistry.registerSettler("pirate", new PirateSettler());
        SettlerRegistry.registerSettler("guard", new GuardSettler());
        SettlerRegistry.registerSettler("generic", new GenericSettler());
    }

    @Override
    protected void onRegister(SettlerRegistryElement object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
        for (SettlerRegistryElement element : this.getElements()) {
            element.settler.onSettlerRegistryClosed();
        }
    }

    public static void loadSettlerIconTextures() {
        instance.streamElements().map(e -> e.settler).forEach(Settler::loadTextures);
    }

    public static int registerSettler(String stringID, Settler settler) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register settlers");
        }
        return instance.register(stringID, new SettlerRegistryElement(settler));
    }

    public static int replaceSettler(String stringID, Settler settler) {
        return instance.replace(stringID, new SettlerRegistryElement(settler));
    }

    public static Settler getSettler(int id) {
        if (id < 0 || id >= instance.size()) {
            return null;
        }
        SettlerRegistryElement e = (SettlerRegistryElement)instance.getElement(id);
        return e == null ? null : e.settler;
    }

    public static Settler getSettler(String stringID) {
        SettlerRegistryElement e = (SettlerRegistryElement)instance.getElement(stringID);
        return e == null ? null : e.settler;
    }

    public static int getSettlerID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static String getSettlerStringID(int id) {
        return instance.getElementStringID(id);
    }

    public static Stream<Settler> streamSettlers() {
        return instance.streamElements().map(s -> s.settler);
    }

    public static List<Settler> getSettlers() {
        return SettlerRegistry.streamSettlers().collect(Collectors.toList());
    }

    protected static class SettlerRegistryElement
    implements IDDataContainer {
        public final Settler settler;

        public SettlerRegistryElement(Settler settler) {
            this.settler = settler;
        }

        @Override
        public IDData getIDData() {
            return this.settler.idData;
        }
    }
}

