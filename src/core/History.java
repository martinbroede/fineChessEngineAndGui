package core;

import java.util.HashMap;
import java.util.Stack;

public class History extends Stack<State> {

    private final Object FIRST = new Object();
    private final Object SECOND = new Object();
    private final Object THIRD = new Object();
    HashMap<Long, Object> repetitionCountMap = new HashMap<>();
    Object repetitionCount;

    public short getLastMoveCoordinates() {

        if (this.size() > 0) return this.lastElement().moveInformation;
        return -1;
    }

    public boolean drawDueToRepetition(Long hash) {
        return repetitionCountMap.get(hash) == SECOND;
    }

    @Override
    public boolean add(State state) {

        System.err.println("PLEASE USE PUSH INSTEAD OF ADD");
        return super.add(state);
    }

    @Override
    public void clear() {

        repetitionCountMap.clear();
        super.clear();
    }

    @Override
    public State push(State state) {

        repetitionCount = repetitionCountMap.get(state.hashCode);
        //todo keep in mind even "Long" provides only "int", i.e. 32bit hashcode...

        if (repetitionCount == null) {
            repetitionCountMap.put(state.hashCode, FIRST);

        } else if (repetitionCount == FIRST) {
            repetitionCountMap.put(state.hashCode, SECOND);

        } else if (repetitionCount == SECOND) {
            repetitionCountMap.put(state.hashCode, THIRD);

        } else {
            System.err.println("SOMETHING EVIL HAPPENED HERE");
        }

        return super.push(state);
    }

    @Override
    public State pop() {

        State state = super.pop();
        repetitionCount = repetitionCountMap.get(state.hashCode);

        if (repetitionCount == THIRD) {
            repetitionCountMap.put(state.hashCode, SECOND);

        } else if (repetitionCount == SECOND) {
            repetitionCountMap.put(state.hashCode, FIRST);

        } else if (repetitionCount == FIRST) {
            repetitionCountMap.remove(state.hashCode);

        } else {
            System.err.println("THAT'S EVIL - HASH NOT IN HISTORY");
        }
        return state;
    }

    @Override
    public String toString() {

        StringBuilder outp = new StringBuilder();
        int count = 2;
        for (State state : this) {
            count++;
            if (count % 2 == 1) outp.append(count / 2).append(". ");
            else outp.append(" - ");
            outp.append(Util.parseSymbol(state.pieceType)).
                    append(" ").
                    append(Util.parseLowerCase(state.moveInformation % 64)).
                    append(" ").
                    append(Util.parseLowerCase(state.moveInformation >> 6));
            if (count % 2 == 0) outp.append("\n");
        }
        return outp.toString();
    }
}