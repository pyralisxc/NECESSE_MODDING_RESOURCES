/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.utils;

import bagaturchess.bitboard.impl.utils.StringUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VarStatistic
implements Serializable {
    private static final long serialVersionUID = -3762105781046761947L;
    private static int HISTORY_LENGTH = 100;
    private double count;
    private double countNonNull;
    private double entropy;
    private double variance;
    private double total_amount;
    private double total_direction;
    private double max_val;
    private double old_val;
    private double path;
    private boolean keepHistory;
    private List<Double> history_short;
    private List<Double> history_long;

    public VarStatistic() {
        this(false);
    }

    public VarStatistic(boolean _keepHistory) {
        this.keepHistory = _keepHistory;
        this.clear();
    }

    public void norm() {
        this.count /= 2.0;
        if (this.count < 1.0) {
            this.count = 1.0;
        }
        this.countNonNull /= 2.0;
        if (this.countNonNull < 1.0) {
            this.countNonNull = 1.0;
        }
        this.total_amount /= 2.0;
        if (this.total_amount < 1.0) {
            this.total_amount = 1.0;
        }
        this.total_direction /= 2.0;
    }

    public double getEntropy() {
        return this.entropy;
    }

    public double getDisperse() {
        return Math.sqrt(this.variance);
    }

    public double getVariance() {
        return this.variance;
    }

    public double getTotalAmount() {
        return this.total_amount;
    }

    public double getStability() {
        if (this.count == 0.0) {
            return 0.0;
        }
        if (this.path == 0.0) {
            return 1.0;
        }
        return 1.0 / this.path;
    }

    public double getTotalDirection() {
        return this.total_direction;
    }

    public void clear() {
        this.count = 0.0;
        this.countNonNull = 0.0;
        this.entropy = 0.0;
        this.variance = 0.0;
        this.total_amount = 0.0;
        this.total_direction = 0.0;
        this.old_val = 0.0;
        this.path = 0.0;
        this.history_short = new ArrayList<Double>();
        this.history_long = new ArrayList<Double>();
    }

    public void devideMax(int factor) {
        this.max_val /= (double)factor;
    }

    public double getChaos() {
        if (this.total_amount == 0.0) {
            return 1.0;
        }
        return this.total_amount / Math.max(Math.abs(this.total_direction), 1.0E-10);
    }

    public void addValue(double value) {
        this.addValue(value, value);
    }

    public void addValue(double nv, double adjustment) {
        if (nv != 0.0) {
            this.countNonNull += 1.0;
        }
        this.count += 1.0;
        this.entropy = (this.count * this.entropy + nv) / (this.count + 1.0);
        this.variance = 1.0 / this.count * ((this.count - 1.0) * this.variance + (nv - this.entropy) * (nv - this.entropy));
        this.path += Math.abs(nv - this.old_val);
        if (nv > this.max_val) {
            this.max_val = nv;
        }
        if (Math.abs(nv) > this.max_val) {
            this.max_val = Math.abs(nv);
        }
        this.total_amount += Math.abs(adjustment);
        this.total_direction += adjustment;
        this.old_val = nv;
        if (this.keepHistory) {
            if (this.history_short.size() >= HISTORY_LENGTH) {
                double sum = 0.0;
                for (Double cur : this.history_short) {
                    sum += cur.doubleValue();
                }
                double avg = sum / (double)this.history_short.size();
                this.history_short.clear();
                if (this.history_long.size() >= HISTORY_LENGTH) {
                    this.history_long.remove(0);
                }
                this.history_long.add(avg);
            } else {
                this.history_short.add(nv);
            }
        }
    }

    public String toString() {
        Object result = "";
        result = (String)result + StringUtils.fill("SAMPLES_COUNT=" + this.count, 6);
        result = (String)result + ", MEAN=" + StringUtils.align(this.entropy);
        result = (String)result + ", STDEV=" + StringUtils.align(this.getDisperse());
        result = (String)result + ", AMOUNT=" + StringUtils.align(this.getTotalAmount());
        result = (String)result + ", DIRECTION=" + StringUtils.align(this.getTotalDirection());
        result = (String)result + ", CHAOS=" + StringUtils.align(this.getChaos());
        return result;
    }

    public static void main(String[] args) {
        VarStatistic a = new VarStatistic(true);
        a.addValue(1.0, 1.0);
        a.addValue(2.0, 2.0);
        a.addValue(3.0, 3.0);
        System.out.println(a);
    }

    public double getMaxVal() {
        return this.max_val;
    }

    public void setEntropy(double entropy) {
        this.entropy = entropy;
    }

    public double getCount() {
        return this.count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public void setDisperse(double disperse) {
        this.variance = disperse;
    }

    public double getCountNonNull() {
        return this.countNonNull;
    }
}

