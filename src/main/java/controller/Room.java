package controller;

import java.util.ArrayList;
import java.util.List;
import jakarta.websocket.Session;
import model.Joust;

public class Room {
    private Session s1;
    private Session s2;
    private Joust game;
    private List<Session> visitors = new ArrayList<>();

    public Room() {
        this.game = null;
        visitors.clear();
    }

    public void reset() {
        this.s1 = null;
        this.s2 = null;
        this.game = null;
        visitors.clear();
    }

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
    
    public void createGame() {
        this.game = new Joust(8, 8);
    }
}
