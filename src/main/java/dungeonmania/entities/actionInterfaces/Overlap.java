package dungeonmania.entities.actionInterfaces;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;

public interface Overlap {
    public void onOverlap(GameMap map, Entity entity);
}
