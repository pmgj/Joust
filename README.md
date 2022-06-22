# Joust

Este projeto contém a implementação do jogo Joust. Neste jogo cada jogador possui um Cavalo que se move de acordo com as regras do Xadrez. Entretanto, toda vez que uma peça se move, a casa previamente ocupada pelo Cavalo não pode ser visitada novamente por nenhum dos jogadores. Os Cavalos não podem capturar um ao outro. O objetivo é deixar o oponente sem movimentações válidas.

Esta implementação possui interface web, permitindo que dois jogadores possam jogar entre si de forma remota utilizando Web Sockets para comunicação com o servidor. Várias salas são criadas, permitindo que vários pares de jogadores possam jogar de forma simultânea.
