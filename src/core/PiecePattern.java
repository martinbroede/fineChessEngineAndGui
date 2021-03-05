package core;

abstract class PiecePattern {
    abstract Moves getMoves(byte from, byte[] threats);
    abstract void updateThreats(byte from, byte[] threats);
}
