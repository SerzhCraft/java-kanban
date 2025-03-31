import main.java.enums.TaskStatus;
import main.java.models.Epic;
import main.java.models.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EpicTest {

    @Test
    void shouldBeEqualsByIdEpicsWithSameId() {
        Epic epic1 = new Epic("Epic 1", "Epic 1's description");
        epic1.setId(1);
        Epic epic2 = new Epic("Epic 2", "Epic 2's description");
        epic2.setId(1);

        assertEquals(epic1, epic2, "Epics with same id should be same by id");
    }

    @Test
    void shouldNotAddEpicAsItsOwnSubtask() {
        Epic epic = new Epic("Epic", "Epic's description");
        Subtask subtask = new Subtask("Subtask", "Should not be added", epic);

        assertTrue(epic.getSubtasks().isEmpty(), "Epic should not has any subtasks");

        epic.addSubtask(subtask);

        assertTrue(epic.getSubtasks().isEmpty(), "Epic should not be added as it's own subtask");

        assertEquals(TaskStatus.NEW, epic.getTaskStatus(), "Epic's status should not update");
    }

}
