import main.java.enums.TaskStatus;
import main.java.managers.Managers;
import main.java.managers.TaskManager;
import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldGenerateUniqueIds() {
        Task task1 = taskManager.createTask(new Task("Task 1", "Description 1"));
        Task task2 = taskManager.createTask(new Task("Task 2", "Description 2"));

        assertNotEquals(task1.getId(), task2.getId(), "Tasks should have unique IDs");
    }

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
    void tasksWithSpecifyAndGeneratedIdShouldNotConflict() {
        Task taskWithSpecifyId = Task.createWithId(1, "Task 1", "Description 1");
        taskWithSpecifyId = taskManager.createTask(taskWithSpecifyId);

        Task taskWithGeneratedId = new Task("Task 2", "Description 2");
        taskWithGeneratedId = taskManager.createTask(taskWithGeneratedId);

        assertNotEquals(taskWithSpecifyId.getId(), taskWithGeneratedId.getId(),
                "Task's ID should not conflict");
        assertEquals(taskWithSpecifyId, taskManager.getTaskById(1),
                "Task should be find with specify ID");
        assertEquals(taskWithGeneratedId, taskManager.getTaskById(taskWithGeneratedId.getId()),
                "Task should be find with generated ID");
    }

    @Test
    void taskShouldRemainUnchangedWhenAddedToManager() {
        Task originalTask = Task.createWithId(2, "Original Task", "Original Description");
        originalTask.setTaskStatus(TaskStatus.IN_PROGRESS);

        originalTask = taskManager.createTask(originalTask);
        Task managedTask = taskManager.getTaskById(originalTask.getId());

        assertEquals(originalTask.getName(), managedTask.getName(),
                "Task's name should not change");
        assertEquals(originalTask.getDescription(), managedTask.getDescription(),
                "Task's description should not change");
        assertEquals(originalTask.getTaskStatus(), managedTask.getTaskStatus(),
                "Task's status should not change");
        assertEquals(originalTask.getId(), managedTask.getId(),
                "Task's ID should not change");
    }


    @Test
    void epicShouldNotContainDeletedSubtask() {
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();

        Epic epic = new Epic("Epic", "Description", duration, startTime);
        epic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", epic);
        subtask = taskManager.createSubtask(subtask);

        taskManager.deleteSubtaskById(subtask.getId());
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void creatingTaskWithExistingIdShouldNotConflict() {
        Task task1 = Task.createWithId(1, "Task 1", "Description 1");
        task1 = taskManager.createTask(task1);

        Task task2 = Task.createWithId(1, "Task 2", "Description 2");
        task2 = taskManager.createTask(task2);

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void updatingNonExistentTaskShouldDoNothing() {
        Task task = Task.createWithId(999, "Task", "Description");
        taskManager.updateTask(task);

        assertNull(taskManager.getTaskById(999));
    }

}