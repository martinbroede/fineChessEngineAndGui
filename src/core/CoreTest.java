package core;

import java.util.InputMismatchException;
import java.util.Scanner;

public class CoreTest {

    public static void main(String[] args) {
        Chess chess = new Chess();

        Scanner scanner = new Scanner(System.in);

        while(true){
            chess.printASCII();
            System.out.println("NEXT MOVE: _");
            String nextMoveInput = scanner.nextLine();

            try
            {
                Move nextMove = new Move(nextMoveInput);
                if(!chess.movePieceUser(nextMove)) System.err.println("MOVE ILLEGAL");
            }
            catch(InputMismatchException exception)
            {
                exception.printStackTrace();
            }
        }
    }
}
