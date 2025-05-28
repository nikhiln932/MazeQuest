package dungeonmania.entities.buildables;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.collectables.BuffApplier;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.inventory.Inventory;

public class MidnightArmour extends Buildable implements BuffApplier {
    private int armourAttack;
    private int armourDefence;

    public MidnightArmour(int armourAttack, int armourDefence) {
        super(null);
        this.armourAttack = armourAttack;
        this.armourDefence = armourDefence;
    }

    @Override
    public boolean canCraft(Inventory inventory) {
        boolean hasSword = inventory.count(Sword.class) > 0;
        boolean hasSunStone = inventory.count(SunStone.class) > 0;

        return hasSunStone && hasSword;
    }

    @Override
    public void consumeCraftingMaterials(Inventory inventory) {
        inventory.remove(inventory.getFirst(Sword.class));
        inventory.remove(inventory.getFirst(SunStone.class));
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(0, armourAttack, armourDefence, 1, 1));
    }

}
