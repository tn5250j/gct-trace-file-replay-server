package io.github.tn5250j.gct;

public class ReaderState {
    public int lineNumber = 0;
    public int screenNumber;
    public Direction direction;
    public byte[] data;
    public int length;

    public void initNewScreenState(int screenNumber) {
        this.screenNumber = screenNumber;
        this.data = new byte[0];
        this.direction = null;
        this.length = 0;
    }

    public enum Direction {
        HLLFunc,
        Recv,
        Send;
    }
}
