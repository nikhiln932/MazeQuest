package dungeonmania.entities.enemies;

import dungeonmania.entities.PotionListener;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.movement.RandomMovement;
import dungeonmania.entities.enemies.movement.RunAwayMovement;
import dungeonmania.util.Position;

public class ZombieToast extends Enemy implements PotionListener {
    public static final double DEFAULT_HEALTH = 5.0;
    public static final double DEFAULT_ATTACK = 6.0;

    public ZombieToast(Position position, double health, double attack) {
        super(position, health, attack);
    }

    @Override
    public void notifyPotion(Potion potion) {
        if (potion instanceof InvincibilityPotion)
            setMovementStrategy(new RunAwayMovement());
    }

    @Override
    public void notifyNoPotion() {
        setMovementStrategy(new RandomMovement());
    }

}
