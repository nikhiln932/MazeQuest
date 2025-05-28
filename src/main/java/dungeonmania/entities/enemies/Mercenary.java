package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.PotionListener;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.movement.AlliedMovement;
import dungeonmania.entities.enemies.movement.HostileMovement;
import dungeonmania.entities.enemies.movement.RandomMovement;
import dungeonmania.entities.enemies.movement.RunAwayMovement;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Mercenary extends Enemy implements Interactable, PotionListener {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final int DEFAULT_BRIBE_RADIUS = 1;
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 10.0;

    private int bribeAmount = Mercenary.DEFAULT_BRIBE_AMOUNT;
    private int bribeRadius = Mercenary.DEFAULT_BRIBE_RADIUS;

    private double allyAttack;
    private double allyDefence;
    private boolean isAdjacentToPlayer = false;

    private boolean mindControlled = false;
    private boolean bribed = false;

    public Mercenary(Position position, double health, double attack, int bribeAmount, int bribeRadius,
            double allyAttack, double allyDefence) {
        super(position, health, attack);
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
        this.allyAttack = allyAttack;
        this.allyDefence = allyDefence;
    }

    public boolean isAllied() {
        return bribed || mindControlled;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (isAllied())
            return;
        super.onOverlap(map, entity);
    }

    /**
     * check whether the current merc can be bribed
     * @param player
     * @return
     */
    private boolean canBeBribed(Player player) {
        return bribeRadius >= 0 && player.countEntityOfType(Treasure.class) >= bribeAmount;
    }

    /**
     * bribe the merc
     */
    private void bribe(Player player) {
        for (int i = 0; i < bribeAmount; i++) {
            player.use(Treasure.class);
        }

    }

    @Override
    public void interact(Player player, Game game) {

        if (player.hasSceptre()) {
            player.useSceptreOn(this);
        } else {
            bribe(player);
            bribed = true;
        }
        if (!isAdjacentToPlayer && Position.isAdjacent(player.getPosition(), getPosition())) {
            isAdjacentToPlayer = true;
        }

        setMovementStrategy(new AlliedMovement(isAdjacentToPlayer));
    }

    @Override
    public boolean isInteractable(Player player) {
        return !isAllied() && (canBeBribed(player) || player.hasSceptre());
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        if (!isAllied())
            return super.getBattleStatistics();
        return new BattleStatistics(0, allyAttack, allyDefence, 1, 1);
    }

    @Override
    public void notifyPotion(Potion potion) {
        if (isAllied())
            return;

        if (potion instanceof InvisibilityPotion)
            setMovementStrategy(new RandomMovement());
        if (potion instanceof InvincibilityPotion)
            setMovementStrategy(new RunAwayMovement());
    }

    @Override
    public void notifyNoPotion() {
        if (isAllied())
            return;

        setMovementStrategy(new HostileMovement());
    }

    public void releaseMindControl() {
        mindControlled = false;
        setMovementStrategy(new HostileMovement()); // Reset to hostile movement behavior
    }

    public void mindControl() {
        mindControlled = true;
    }
}
