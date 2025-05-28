package dungeonmania.entities;

import dungeonmania.entities.collectables.potions.Potion;

public interface PotionListener {
    public void notifyPotion(Potion potion);

    public void notifyNoPotion();
}
