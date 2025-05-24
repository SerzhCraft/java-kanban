import main.java.enums.TaskStatus;
import main.java.models.Epic;
import main.java.models.Subtask;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    void shouldBeEqualsByIdEpicsWithSameId() {
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();

        Epic epic1 = Epic.createWithId(1, "Epic 1", "Description 1", duration, startTime);
        Epic epic2 = Epic.createWithId(1, "Epic 2", "Description 2", duration, startTime);

        assertEquals(epic1, epic2, "Epics with same id should be equal");
        assertEquals(epic1.hashCode(), epic2.hashCode(), "Hash codes should match");
    }

    @Test
    void shouldNotAddInvalidSubtask() {
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();

        Epic epic1 = Epic.createWithId(1, "Epic 1", "Description 1", duration, startTime);
        Epic epic2 = Epic.createWithId(2, "Epic 2", "Description 2", duration, startTime);

        epic1.addSubtask(null);
        assertTrue(epic1.getSubtasks().isEmpty(), "Should not add null subtask");

        Subtask subtaskWithWrongEpic = Subtask.createWithId(3,
                "Wrong",
                "Description",
                epic2,
                duration,
                startTime);
        epic1.addSubtask(subtaskWithWrongEpic);
        assertTrue(epic1.getSubtasks().isEmpty(), "Should not add subtask with wrong epic");

        Subtask validSubtask = Subtask.createWithId(4,
                "Valid",
                "Description",
                epic1,
                duration,
                startTime);
        epic1.addSubtask(validSubtask);
        assertFalse(epic1.getSubtasks().isEmpty(), "Should add valid subtask");
        assertEquals(1, epic1.getSubtasks().size(), "Should contain exactly one subtask");
    }

    @Test
    void shouldUpdateStatusCorrectly() {
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();

        Epic epic = Epic.createWithId(1, "Epic", "Description", duration, startTime);

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());

        Subtask subtask1 = Subtask.createWithId(2,
                "Subtask 1",
                "Description 1",
                epic,
                duration,
                startTime);
        epic.addSubtask(subtask1);
        assertEquals(TaskStatus.NEW, epic.getTaskStatus());

        subtask1.setTaskStatus(TaskStatus.IN_PROGRESS);
        epic.updateStatus();
        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());

        Subtask subtask2 = Subtask.createWithId(3,
                "Subtask 2",
                "Description 2",
                epic,
                duration,
                startTime);
        subtask2.setTaskStatus(TaskStatus.DONE);
        epic.addSubtask(subtask2);
        epic.updateStatus();
        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());

        subtask1.setTaskStatus(TaskStatus.DONE);
        epic.updateStatus();
        assertEquals(TaskStatus.DONE, epic.getTaskStatus());
    }

    @Test
    void shouldBeCorrectlyEpicDurationAndStartTimeWithSubtasks() {
        Epic epic = new Epic("Epic", "Description", Duration.ZERO, null);

        Subtask sub1 = new Subtask("Sub1", "Desc1", epic);
        sub1.setDuration(Duration.ofMinutes(60));
        sub1.setStartTime(LocalDateTime.of(2024, 6, 2, 10, 0));

        Subtask sub2 = new Subtask("Sub2", "Desc2", epic);
        sub2.setDuration(Duration.ofMinutes(120));
        sub2.setStartTime(LocalDateTime.of(2024, 6, 2, 8, 0));

        epic.addSubtask(sub1);
        epic.addSubtask(sub2);

        // Проверяем суммарную длительность и самое раннее время старта
        assertEquals(Duration.ofMinutes(180), epic.getDuration(),
                "The total duration of the epic should be equal to the sum of the subtask durations.");
        assertEquals(LocalDateTime.of(2024, 6, 2, 8, 0), epic.getStartTime(),
                "The epic start time should be the minimum start time for the subtasks.");

        // Проверяем корректность getEndTime()
        assertEquals(epic.getStartTime().plus(epic.getDuration()), epic.getEndTime(),
                "The end time of the epic must be calculated correctly.");
    }

    @Test
    void shouldBeCorrectlyEpicDurationAndStartAfterRemovingSubtask() {
        Epic epic = new Epic("Epic", "Description", Duration.ZERO, null);

        Subtask sub1 = new Subtask("Sub1", "Desc1", epic);
        sub1.setDuration(Duration.ofMinutes(60));
        sub1.setStartTime(LocalDateTime.of(2024, 6, 2, 10, 0));

        Subtask sub2 = new Subtask("Sub2", "Desc2", epic);
        sub2.setDuration(Duration.ofMinutes(120));
        sub2.setStartTime(LocalDateTime.of(2024, 6, 2, 8, 0));

        epic.addSubtask(sub1);
        epic.addSubtask(sub2);

        // Удаляем одну подзадачу
        epic.removeSubtask(sub1);

        assertEquals(Duration.ofMinutes(120), epic.getDuration(),
                "The duration of the epic should be updated after deleting the subtask.");
        assertEquals(LocalDateTime.of(2024, 6, 2, 8, 0), epic.getStartTime(),
                "The epic start time should be updated after deleting the subtask.");
    }

}
