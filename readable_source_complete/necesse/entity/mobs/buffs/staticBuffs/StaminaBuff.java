/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.io.FileNotFoundException;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.MovementTickBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTexture.GameTexture;

public class StaminaBuff
extends Buff
implements MovementTickBuff {
    protected GameTexture cooldownTexture;

    public StaminaBuff() {
        this.isPassive = true;
        this.shouldSave = true;
        this.overrideSync = true;
        this.canCancel = false;
        this.isImportant = true;
        this.sortByDuration = false;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        try {
            this.cooldownTexture = GameTexture.fromFileRaw("buffs/" + this.getStringID() + "_cooldown");
        }
        catch (FileNotFoundException e) {
            this.cooldownTexture = GameTexture.fromFile("buffs/unknown");
        }
    }

    @Override
    public void drawIcon(int x, int y, ActiveBuff buff) {
        GNDItemMap gndData = buff.getGndData();
        boolean onCooldown = gndData.getBoolean("onCooldown");
        if (onCooldown) {
            this.cooldownTexture.initDraw().size(32, 32).draw(x, y);
        } else {
            this.iconTexture.initDraw().size(32, 32).draw(x, y);
        }
        float stamina = gndData.getFloat("stamina");
        stamina = GameMath.limit(stamina, 0.0f, 1.0f);
        int staminaInt = (int)(Math.abs(stamina - 1.0f) * 100.0f);
        staminaInt = Math.max(staminaInt, onCooldown ? 0 : 1);
        String text = staminaInt + "%";
        int width = FontManager.bit.getWidthCeil(text, durationFontOptions);
        FontManager.bit.drawString(x + 16 - width / 2, y + 30, text, durationFontOptions);
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void tickMovement(ActiveBuff buff, float delta) {
        long time = buff.owner.getWorldEntity().getTime();
        GNDItemMap gndData = buff.getGndData();
        boolean onCooldown = gndData.getBoolean("onCooldown");
        float stamina = gndData.getFloat("stamina");
        if (stamina <= 0.0f) {
            gndData.setBoolean("onCooldown", false);
            buff.remove();
        } else {
            int regenDelay;
            long lastUsageTime = gndData.getLong("lastUsageTime");
            long timeSinceLastUsage = time - lastUsageTime;
            int n = regenDelay = onCooldown ? 800 : 200;
            if (timeSinceLastUsage <= -1000L || timeSinceLastUsage >= (long)regenDelay) {
                long msToReplenish = 3000L;
                float decrease = delta / (float)msToReplenish;
                decrease *= buff.owner.buffManager.getModifier(BuffModifiers.STAMINA_REGEN).floatValue();
                float capacityMod = buff.owner.buffManager.getModifier(BuffModifiers.STAMINA_CAPACITY).floatValue();
                decrease = capacityMod != 0.0f ? (decrease *= 1.0f / capacityMod) : 1.0f;
                stamina -= decrease;
                if (stamina <= 0.0f) {
                    gndData.setFloat("stamina", 0.0f);
                    gndData.setBoolean("onCooldown", false);
                    buff.remove();
                } else {
                    gndData.setFloat("stamina", stamina);
                }
            }
        }
    }

    @Override
    public boolean shouldDrawDuration(ActiveBuff buff) {
        return true;
    }

    public static float getCurrentStamina(Mob mob) {
        ActiveBuff buff = mob.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
        if (buff != null) {
            return buff.getGndData().getFloat("stamina");
        }
        return 0.0f;
    }

    public static void setCurrentStamina(Mob mob, float stamina) {
        ActiveBuff buff = mob.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
        if (buff == null) {
            buff = mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.STAMINA_BUFF, mob, 0.0f, null), false);
        }
        buff.getGndData().setFloat("stamina", stamina);
    }

    public static boolean canStartStaminaUsage(Mob mob) {
        ActiveBuff buff = mob.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
        if (buff != null) {
            GNDItemMap gndData = buff.getGndData();
            if (gndData.getBoolean("onCooldown")) {
                return false;
            }
            return gndData.getFloat("stamina") < 1.0f;
        }
        return true;
    }

    public static void writeStaminaData(Mob mob, PacketWriter writer) {
        ActiveBuff buff = mob.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
        if (buff != null) {
            writer.putNextBoolean(true);
            buff.getGndData().writePacket(writer);
        } else {
            writer.putNextBoolean(false);
        }
    }

    public static void readStaminaData(Mob mob, PacketReader reader) {
        boolean active = reader.getNextBoolean();
        if (!active) {
            mob.buffManager.removeBuff(BuffRegistry.STAMINA_BUFF, false);
        } else {
            ActiveBuff buff = mob.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
            if (buff == null) {
                buff = mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.STAMINA_BUFF, mob, 0.0f, null), false);
            }
            buff.getGndData().readPacket(reader);
        }
    }

    public static void keepStaminaUsage(Mob mob) {
        ActiveBuff buff = mob.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
        if (buff != null) {
            long time = mob.getWorldEntity().getTime();
            buff.getGndData().setLong("lastUsageTime", time);
        }
    }

    public static float getStamina(Mob mob) {
        ActiveBuff buff = mob.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
        if (buff == null) {
            return 0.0f;
        }
        GNDItemMap gndData = buff.getGndData();
        return gndData.getFloat("stamina");
    }

    public static void setStamina(Mob mob, float stamina, boolean setLastUsageTime, boolean onCooldown) {
        ActiveBuff buff = mob.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
        if (buff == null) {
            buff = mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.STAMINA_BUFF, mob, 1.0f, null), false);
        }
        GNDItemMap gndData = buff.getGndData();
        gndData.setFloat("stamina", stamina);
        if (setLastUsageTime) {
            gndData.setLong("lastUsageTime", mob.getWorldEntity().getTime());
        }
        if (stamina >= 1.0f || onCooldown) {
            gndData.setBoolean("onCooldown", true);
        }
    }

    public static boolean useStaminaAndGetValid(Mob mob, float usage) {
        usage *= mob.buffManager.getModifier(BuffModifiers.STAMINA_USAGE).floatValue();
        float capacityMod = mob.buffManager.getModifier(BuffModifiers.STAMINA_CAPACITY).floatValue();
        usage = capacityMod != 0.0f ? (usage *= 1.0f / capacityMod) : 1.0f;
        if (usage > 0.0f) {
            long time = mob.getWorldEntity().getTime();
            ActiveBuff buff = mob.buffManager.getBuff(BuffRegistry.STAMINA_BUFF);
            if (buff == null) {
                buff = mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.STAMINA_BUFF, mob, 1.0f, null), false);
            }
            GNDItemMap gndData = buff.getGndData();
            float stamina = gndData.getFloat("stamina");
            stamina = Math.min(stamina + usage, 1.0f);
            gndData.setFloat("stamina", stamina);
            gndData.setLong("lastUsageTime", time);
            if (stamina >= 1.0f) {
                gndData.setBoolean("onCooldown", true);
                return false;
            }
            return true;
        }
        return true;
    }
}

