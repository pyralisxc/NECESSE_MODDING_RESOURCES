/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity;

import necesse.engine.util.GameLinkedList;
import necesse.entity.particle.Particle;

public class ParticleTypeSwitcher {
    protected GameLinkedList<CountType> types;
    protected GameLinkedList.Element currentType;
    protected long currentCount = 0L;

    public ParticleTypeSwitcher(int firstCount, Particle.GType firstType, Object ... extraCountAndTypes) {
        this.types = new GameLinkedList();
        this.currentType = this.types.addLast(new CountType(firstCount, firstType));
        int nextCount = 1;
        for (Object obj : extraCountAndTypes) {
            if (obj instanceof Integer) {
                nextCount = (Integer)obj;
                continue;
            }
            if (!(obj instanceof Particle.GType)) continue;
            this.types.addLast(new CountType(nextCount, (Particle.GType)((Object)obj)));
            nextCount = 1;
        }
    }

    public ParticleTypeSwitcher(Particle.GType ... switcher) {
        if (switcher.length == 0) {
            throw new IllegalArgumentException("Must have at least one type");
        }
        this.types = new GameLinkedList();
        Particle.GType type = null;
        int count = 0;
        for (Particle.GType nextType : switcher) {
            if (type != null && nextType != type) {
                this.types.addLast(new CountType(count, type));
                count = 0;
            }
            type = nextType;
            ++count;
        }
        if (count > 0) {
            this.types.addLast(new CountType(count, type));
        }
        this.currentType = this.types.getFirstElement();
    }

    public Particle.GType next() {
        Particle.GType out = ((CountType)this.currentType.object).type;
        ++this.currentCount;
        if (this.currentCount >= (long)((CountType)this.currentType.object).count) {
            this.currentType = this.currentType.nextWrap();
            this.currentCount = 0L;
        }
        return out;
    }

    protected static class CountType {
        public final int count;
        public final Particle.GType type;

        public CountType(int count, Particle.GType type) {
            this.count = count;
            this.type = type;
        }
    }
}

