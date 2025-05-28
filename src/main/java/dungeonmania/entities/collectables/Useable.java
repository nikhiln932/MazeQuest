package dungeonmania.entities.collectables;

import dungeonmania.Game;

public interface Useable {
    void use(Game game);

    int getDurability();

}
