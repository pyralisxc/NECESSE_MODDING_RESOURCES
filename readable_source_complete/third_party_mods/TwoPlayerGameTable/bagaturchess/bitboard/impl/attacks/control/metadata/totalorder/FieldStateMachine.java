/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.control.metadata.totalorder;

import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.StaticScores;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;
import bagaturchess.bitboard.impl.attacks.control.metadata.totalorder.FieldState;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class FieldStateMachine
implements Serializable {
    private static final long serialVersionUID = 7412575151839070935L;
    private static final String STATE_MACHINE_LOCATION = "C:\\StateMachine.bin";
    public static final int TRANSITION_ADD_ATTACK = 0;
    public static final int TRANSITION_REM_ATTACK = 1;
    public static final int TRANSITION_ADD_FIGURE = 2;
    public static final int TRANSITION_REM_FIGURE = 3;
    public static final int TRANSITION_MAX_INDEX = 4;
    public static final String[] TRANSITION_SIGN = new String[4];
    private static FieldStateMachine singleton;
    private int[][][][] machine;
    private List<FieldState> states;
    private StaticScores scores;

    public static final FieldStateMachine getInstance() {
        return FieldStateMachine.getInstance(-1, false);
    }

    public static final FieldStateMachine getInstanceForGen(int statesCount) {
        return FieldStateMachine.getInstance(statesCount, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static final FieldStateMachine getInstance(int statesCount, boolean generate) {
        if (singleton != null) return singleton;
        Class<FieldAttacksStateMachine> clazz = FieldAttacksStateMachine.class;
        synchronized (FieldAttacksStateMachine.class) {
            if (singleton != null) return singleton;
            singleton = new FieldStateMachine(statesCount, generate);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return singleton;
        }
    }

    private FieldStateMachine(int statesCount, boolean generate) {
        if (generate) {
            this.machine = new int[Figures.COLOUR_MAX][7][4][statesCount];
        } else {
            this.deserialize();
        }
    }

    public String stateToString(int id) {
        FieldState stateObj = this.states.get(id);
        Object result = "";
        result = (String)result + "ON=";
        if (stateObj.figureOnFieldColour == -1) {
            result = (String)result + "EMPTY";
        } else {
            result = (String)result + Figures.COLOURS_SIGN[stateObj.figureOnFieldColour];
            result = (String)result + Figures.TYPES_SIGN[stateObj.figureOnFieldType];
        }
        result = (String)result + ", ";
        result = (String)result + "WA=" + String.valueOf(stateObj.whiteAttacks);
        result = (String)result + ", ";
        result = (String)result + "BA=" + String.valueOf(stateObj.whiteAttacks);
        return result;
    }

    public int nextState(int colour, int type, int op, int currentState) {
        int next = this.machine[colour][type][op][currentState];
        return next;
    }

    public void serialize() {
        String file = STATE_MACHINE_LOCATION;
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
            os.writeObject(this);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deserialize() {
        String location = STATE_MACHINE_LOCATION;
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(location));
            FieldStateMachine obj = (FieldStateMachine)is.readObject();
            this.machine = obj.machine;
            this.states = obj.states;
            this.scores = new StaticScores(this);
        }
        catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Deserialize ...");
            FieldStateMachine fsm = new FieldStateMachine(-1, false);
            System.out.println("OK");
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public int[][][][] getMachine() {
        return this.machine;
    }

    public List<FieldState> getStates() {
        return this.states;
    }

    public void setStates(List<FieldState> states) {
        this.states = states;
    }

    public StaticScores getScores() {
        return this.scores;
    }

    static {
        FieldStateMachine.TRANSITION_SIGN[0] = "ADD_A";
        FieldStateMachine.TRANSITION_SIGN[1] = "REM_A";
        FieldStateMachine.TRANSITION_SIGN[2] = "ADD_F";
        FieldStateMachine.TRANSITION_SIGN[3] = "REM_F";
    }
}

