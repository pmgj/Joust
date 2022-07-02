# Joust

This project contains the implementation of the Joust board game. In this game, each player has a Knight that moves according to the rules of Chess. However, every time a piece moves, the square previously occupied by the Knight cannot be visited again by either player. Knights cannot capture each other. The objective is to leave the opponent without valid moves.

This implementation has a web interface, allowing two players to play remotely using Web Sockets to communicate with the server. Several rooms are created, allowing several pairs of players to play simultaneously.
