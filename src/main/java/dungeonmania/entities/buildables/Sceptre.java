package dungeonmania.entities.buildables;

import dungeonmania.entities.collectables.*;
import dungeonmania.entities.inventory.Inventory;

public class Sceptre extends Buildable {
    private int duration;

    public Sceptre(int duration) {
        super(null);
        this.duration = duration;
    }

    @Override
    public boolean canCraft(Inventory inventory) {
        return hasWoodOrArrows(inventory) && hasKeyTreasureOrSunStone(inventory) && hasRequiredSunStone(inventory);
    }

    @Override
    public void consumeCraftingMaterials(Inventory inventory) {
        if (inventory.count(Wood.class) > 0) {
            inventory.remove(inventory.getFirst(Wood.class));
        } else {
            for (int i = 0; i < 2; i++) {
                inventory.remove(inventory.getFirst(Arrow.class));
            }
        }

        if (inventory.count(Key.class) > 0) {
            inventory.remove(inventory.getFirst(Key.class));
        } else if (inventory.count(Treasure.class) > 0) {
            inventory.remove(inventory.getFirst(Treasure.class));
        }

        inventory.remove(inventory.getFirst(SunStone.class));
    }

    public int getDuration() {
        return duration;
    }

    private boolean hasWoodOrArrows(Inventory inventory) {
        return inventory.count(Wood.class) >= 1 || inventory.count(Arrow.class) >= 2;
    }

    private boolean hasKeyTreasureOrSunStone(Inventory inventory) {
        return inventory.count(Key.class) >= 1 || inventory.count(Treasure.class) >= 1
                || inventory.count(SunStone.class) >= 1;
    }

    private boolean hasRequiredSunStone(Inventory inventory) {
        int sunStoneCount = inventory.count(SunStone.class);
        boolean usedSunStoneAsSubstitute = inventory.count(Key.class) == 0 && inventory.count(Treasure.class) == 0
                && sunStoneCount >= 1;

        // If a Sun Stone is used as a substitute, we need an additional Sun Stone
        return usedSunStoneAsSubstitute ? sunStoneCount >= 2 : sunStoneCount >= 1;
    }
}
