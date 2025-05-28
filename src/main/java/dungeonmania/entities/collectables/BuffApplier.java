package dungeonmania.entities.collectables;

import dungeonmania.battles.BattleStatistics;

public interface BuffApplier {
    BattleStatistics applyBuff(BattleStatistics origin);
}
