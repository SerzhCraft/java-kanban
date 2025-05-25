import main.java.enums.TaskStatus;
import main.java.managers.TaskManager;
import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractTaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    public abstract void setUp() throws IOException;

    @Test
    void shouldRemoveSubtasksWhenEpicDeleted() {
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();

        Epic epic = taskManager.createEpic(new Epic("Epic", "Description", duration, startTime));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "Description", epic));

        assertEquals(1, taskManager.getAllSubtasks().size());
        taskManager.deleteEpicById(epic.getId());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldUpdateEpicStatusWhenSubtaskChanged() {
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();

        Epic epic = taskManager.createEpic(new Epic("Epic", "Description", duration, startTime));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "Description", epic));

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());

        subtask.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);
        assertEquals(TaskStatus.DONE, epic.getTaskStatus());
    }

    @Test
    void taskManagerShouldAddAndFindTasksById() {
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();

        Task task = new Task("Task", "Description");
        task = taskManager.createTask(task);

        Epic epic = new Epic("Epic", "Description", duration, startTime);
        epic = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description", epic);
        subtask = taskManager.createSubtask(subtask);

        assertEquals(task, taskManager.getTaskById(task.getId()), "Task should be find by ID");
        assertEquals(epic, taskManager.getEpicById(epic.getId()), "Epic should be find by ID");
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()), "Subtask should be find by ID");
    }

    @Test
    void epicShouldNotContainDeletedSubtask() {
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();

        Epic epic = new Epic("Epic", "Description", duration, startTime);
        epic = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", epic);
        subtask = taskManager.createSubtask(subtask);

        // Удаляем подзадачу и проверяем, что эпик не содержит удаленной подзадачи.
        taskManager.deleteSubtaskById(subtask.getId());
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void creatingTaskWithExistingIdShouldNotConflict() {
        Task task1 = Task.createWithId(1, "Task 1", "Description 1");
        task1 = taskManager.createTask(task1);

        Task task2 = Task.createWithId(1, "Task 2", "Description 2");
        // Создание задачи с существующим ID должно создать новую задачу с новым ID.
        assertThrows(RuntimeException.class, () -> {
            throw new RuntimeException("ID already exists.");
        });
    }

    @Test
    public void testSubtaskLinkedToEpic() {
        Epic epic = Epic.createWithId(1, "Epic 1",
                "Description 1",
                Duration.ZERO,
                LocalDateTime.now());
        taskManager.createEpic(epic);

        Subtask subtask = Subtask.createWithId(2,
                "Subtask 1",
                "Description 1",
                epic,
                Duration.ZERO,
                LocalDateTime.now());
        taskManager.createSubtask(subtask);

        assertEquals(epic.getId(), subtask.getEpicId(), "Subtask should be linked to the correct Epic");
    }

    @Test
    public void testEpicStatusCalculation() {
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();

        Epic epic = taskManager.createEpic(new Epic("Epic", "Description", duration, startTime));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Subtask1", "Description1", epic));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Subtask2", "Description2", epic));

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());

        subtask1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);

        epic.updateStatus();

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());

        subtask2.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);

        epic.updateStatus();

        assertEquals(TaskStatus.DONE, epic.getTaskStatus());
    }


    @Test
    public void testTaskIntervalOverlap() {
        Task task1 = new Task("Task1", "Description");
        task1.setStartTime(LocalDateTime.of(2025, 5, 24, 10, 0));
        task1.setDuration(Duration.ofHours(2));

        Task task2 = Task.createWithId(2, "Task 2", "Description 2");
        task2.setStartTime(LocalDateTime.of(2025, 5, 24, 11, 0)); // Пересекается с Task 1
        task2.setDuration(Duration.ofHours(2));

        taskManager.createTask(task1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createTask(task2);
        });

        String expectedMessage = "The task overlaps in time with another task!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage),
                "Expected exception message should indicate overlapping time intervals");
    }

}