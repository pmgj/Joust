package controller;

import java.util.List;

import model.Joust;
import model.Player;

public record OutputMessage(ConnectionType type, Player turn, List<RoomMessage> rooms, Joust game) {

}
