package socket;

public class RoomMessage {
    private boolean s1;
    private boolean s2;

    public RoomMessage() {
        
    }
    
    public RoomMessage(Room room) {
        this.s1 = room.getS1() != null;
        this.s2 = room.getS2() != null;
    }
    
    public boolean isS1() {
        return s1;
    }

    public void setS1(boolean s1) {
        this.s1 = s1;
    }

    public boolean isS2() {
        return s2;
    }

    public void setS2(boolean s2) {
        this.s2 = s2;
    }
}
