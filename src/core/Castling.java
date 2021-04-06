package core;

public class Castling {

    public final static byte ALL_RIGHTS = 0b1111;
    public final static byte NO_RIGHTS = 0;

    private byte rights;

    public byte getRights() {
        return rights;
    }

    public void setRights(byte rights) {
        this.rights = rights;
    }

    public void reset() {
        /* MSB-wKS wQS bKS bQS-LSB */
        rights = 0b1111;
    }

    public void disableWhiteKingSide() {
        rights &= ~(1 << 3);
    }

    public void disableWhiteQueenSide() {
        rights &= ~(1 << 2);
    }

    public void disableBlackKingSide() {
        rights &= ~(1 << 1);
    }

    public void disableBlackQueenSide() {
        rights &= ~(1);
    }

    public boolean whiteKingSide() {
        return (rights & (1 << 3)) > 0;
    }

    public boolean whiteQueenSide() {
        return (rights & (1 << 2)) > 0;
    }

    public boolean blackKingSide() {
        return (rights & (1 << 1)) > 0;
    }

    public boolean blackQueenSide() {
        return (rights & 1) > 0;
    }

    public void print() {

        System.out.println("WHITE KINGSIDE:\t" + whiteKingSide() +
                "\nWHITE QUEENSIDE:" + whiteQueenSide() +
                "\nBLACK KINGSIDE:\t" + blackKingSide() +
                "\nBLACK QUEENSIDE:" + blackQueenSide() +
                "\nRIGHTS:\t\t\t" + Integer.toBinaryString(rights) + "\n");
    }
}
