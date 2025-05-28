package dungeonmania.entities.buildables;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.collectables.BuffApplier;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Useable;
import dungeonmania.entities.collectables.Wood;
import dungeonmania.entities.inventory.Inventory;

public class Shield extends Buildable implements Useable, BuffApplier {
    private int durability;
    private double defence;

    public Shield(int durability, double defence) {
        super(null);
        this.durability = durability;
        this.defence = defence;
    }

    @Override
    public void use(Game game) {
        durability--;
        if (durability <= 0) {
            game.getPlayer().remove(this);
        }
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(0, 0, defence, 1, 1));
    }

    @Override
    public int getDurability() {
        return durability;
    }

    public boolean canCraft(Inventory inventory) {
        return inventory.count(Wood.class) >= 2 && (inventory.count(Treasure.class) >= 1
                || (inventory.count(Key.class) >= 1) || (inventory.count(SunStone.class) >= 1));
    }

    public void consumeCraftingMaterials(Inventory inventory) {
        for (int i = 0; i < 2; i++) {
            inventory.remove(inventory.getFirst(Wood.class));
        }
        if (inventory.count(Treasure.class) > 0) {
            inventory.remove(inventory.getFirst(Treasure.class));
        } else if (inventory.count(Key.class) > 0) {
            inventory.remove(inventory.getFirst(Key.class));
        }
    }
}
