package core;

import java.util.InputMismatchException;
import java.util.Scanner;

public class CoreASCII {

    public static void main(String[] args) {
        run();
    }

    public static void run(){

        Chess chess = new Chess();
        chess.newGame();
        Scanner scanner = new Scanner(System.in);

        while(chess.currentStatus.getStatus() == Status.UNDECIDED){
            chess.print();
            System.out.println("NEXT MOVE: _");
            String nextMoveInput = scanner.nextLine();

            try
            {
                Move nextMove = new Move(nextMoveInput);
                if(!chess.userMove(nextMove,Constants.WHITE,true))
                    System.err.println("ASCII MOVE ILLEGAL");
            }
            catch(InputMismatchException exception)
            {
                exception.printStackTrace();
            }
        }
        System.out.println(chess.currentStatus.getStatusNotice());
    }
}
