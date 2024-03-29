package core;

import java.util.InputMismatchException;
import java.util.Scanner;

public class CoreASCII {

    public static void main(String[] args) {
        play();
    }

    public static void play() {

        Chess chess = new Chess();
        chess.newGame();
        Scanner scanner = new Scanner(System.in);

        while (chess.gameStatus.getStatus() == Status.UNDECIDED) {
            chess.print();
            System.out.println("NEXT MOVE: _");
            String nextMoveInput = scanner.nextLine();

            try {
                Move nextMove = new Move(nextMoveInput);
                if (!chess.userMove(nextMove, Constants.WHITE, true))
                    System.out.println("ASCII MOVE ILLEGAL");
            } catch (InputMismatchException ex) {
                ex.printStackTrace();
            }
        }
        scanner.close();
        System.out.println(chess.gameStatus.getStatusNotice());
    }
}
