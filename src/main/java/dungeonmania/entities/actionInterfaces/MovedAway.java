package dungeonmania.entities.actionInterfaces;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;

public interface MovedAway {
    public void onMovedAway(GameMap map, Entity entity);
}
