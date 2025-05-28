package dungeonmania.entities.inventory;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.actionInterfaces.Overlap;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

/**
 * An item in the inventory
 */
public abstract class InventoryItem extends Entity implements Overlap {
    public InventoryItem(Position position) {
        super(position);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (player.pickUp(this)) {
                map.destroyEntity(this);
            }
        }
    }
}
