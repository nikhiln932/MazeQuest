package dungeonmania.task2;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.mvp.TestUtils;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;

public class BasicEnemyGoalTest {
    @Test
    @Tag("13-5")
    @DisplayName("Test achieving a basic enemy goal")
    public void enemy() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_enemy", "c_basicGoalsTest_enemy");
        // Start of game
        assertTrue(TestUtils.getGoals(res).contains(":enemy"));
        List<EntityResponse> entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "spider") == 1);
        assertTrue(TestUtils.countEntityOfType(entities, "zombie_toast") == 1);

        res = dmc.tick(Direction.DOWN);

        assertTrue(TestUtils.getGoals(res).contains(":enemy"));
        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "spider") == 1);
        assertTrue(TestUtils.countEntityOfType(entities, "zombie_toast") == 0);

        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.getGoals(res).contains(":enemy"));
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();

        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));

        assertTrue(TestUtils.getGoals(res).contains(":enemy"));
        assertEquals(0, TestUtils.getEntities(res, "zombie_toast_spawner").size());

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        assertTrue(TestUtils.getEntities(res, "zombie_toast").size() == 0);
        assertTrue(TestUtils.getEntities(res, "spider").size() == 0);
        assertTrue(TestUtils.getEntities(res, "zombie_toast_spawner").size() == 0);
        // assert goal met
        assertEquals("", TestUtils.getGoals(res));

    }
}
