package socket;

import java.util.ArrayList;
import java.util.List;
import javax.websocket.Session;
import model.Joust;

public class Room {
    private Session s1;
    private Session s2;
    private Joust game;
    private List<Session> visitors = new ArrayList();

    public Room() {
        this.game = new Joust(8, 8);
        visitors.clear();
    }
    
//    public void reset() {
//        this.game = new Joust(8, 8);
//        visitors.clear();        
//    }
    
    public List<Session> getVisitors() {
        return visitors;
    }

    public void setVisitors(List<Session> visitors) {
        this.visitors = visitors;
    }

    public Session getS1() {
        return s1;
    }

    public void setS1(Session s1) {
        this.s1 = s1;
    }

    public Session getS2() {
        return s2;
    }

    public void setS2(Session s2) {
        this.s2 = s2;
    }

    public Joust getGame() {
        return game;
    }

    public void setGame(Joust game) {
        this.game = game;
    }
}
