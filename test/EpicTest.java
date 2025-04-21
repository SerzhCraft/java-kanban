import main.java.enums.TaskStatus;
import main.java.models.Epic;
import main.java.models.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    void shouldBeEqualsByIdEpicsWithSameId() {
        Epic epic1 = Epic.createWithId(1, "Epic 1", "Description 1");
        Epic epic2 = Epic.createWithId(1, "Epic 2", "Description 2");

        assertEquals(epic1, epic2, "Epics with same id should be equal");
        assertEquals(epic1.hashCode(), epic2.hashCode(), "Hash codes should match");
    }

    @Test
    void shouldNotAddInvalidSubtask() {
        Epic epic1 = Epic.createWithId(1, "Epic 1", "Description 1");
        Epic epic2 = Epic.createWithId(2, "Epic 2", "Description 2");

        epic1.addSubtask(null);
        assertTrue(epic1.getSubtasks().isEmpty(), "Should not add null subtask");

        Subtask subtaskWithWrongEpic = Subtask.createWithId(3, "Wrong", "Description", epic2);
        epic1.addSubtask(subtaskWithWrongEpic);
        assertTrue(epic1.getSubtasks().isEmpty(), "Should not add subtask with wrong epic");

        Subtask validSubtask = Subtask.createWithId(4, "Valid", "Description", epic1);
        epic1.addSubtask(validSubtask);
        assertFalse(epic1.getSubtasks().isEmpty(), "Should add valid subtask");
        assertEquals(1, epic1.getSubtasks().size(), "Should contain exactly one subtask");
    }

    @Test
    void shouldUpdateStatusCorrectly() {
        Epic epic = Epic.createWithId(1, "Epic", "Description");

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());

        Subtask subtask1 = Subtask.createWithId(2, "Subtask 1", "Description 1", epic);
        epic.addSubtask(subtask1);
        assertEquals(TaskStatus.NEW, epic.getTaskStatus());

        subtask1.setTaskStatus(TaskStatus.IN_PROGRESS);
        epic.updateStatus();
        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());

        Subtask subtask2 = Subtask.createWithId(3, "Subtask 2", "Description 2", epic);
        subtask2.setTaskStatus(TaskStatus.DONE);
        epic.addSubtask(subtask2);
        epic.updateStatus();
        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());

        subtask1.setTaskStatus(TaskStatus.DONE);
        epic.updateStatus();
        assertEquals(TaskStatus.DONE, epic.getTaskStatus());
    }

}
