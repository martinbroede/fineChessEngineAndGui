package core;


import java.util.HashSet;

public class Squares extends HashSet<Square> {

    public void print() {

        for (Square sq : this) {
            System.out.print("#" + Util.parse(sq.getPosition()) + " ");
        }
        System.out.println();
    }
}
