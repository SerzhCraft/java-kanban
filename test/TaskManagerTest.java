import main.java.enums.TaskStatus;
import main.java.managers.HistoryManager;
import main.java.managers.Managers;
import main.java.managers.TaskManager;
import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    TaskManager<Task> taskManager;
    HistoryManager<Task> historyManager;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void shouldBeEqualsByIdTasksWithSameId() {
        Task task1 = new Task("Task 1", "Task 1's description");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Task 2's description");
        task2.setId(1);

        assertEquals(task1, task2, "Tasks with same id should be same by id");
    }

    @Test
    void shouldBeEqualsByIdEpicsWithSameId() {
        Epic epic1 = new Epic("Epic 1", "Epic 1's description");
        epic1.setId(1);
        Epic epic2 = new Epic("Epic 2", "Epic 2's description");
        epic2.setId(1);

        assertEquals(epic1, epic2, "Epics with same id should be same by id");
    }

    @Test
    void shouldBeEqualsByIdSubtasksWithSameId() {
        Epic epic = new Epic("Epic", "Epic's description");
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask 1 's description", epic);
        subtask1.setId(2);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask 2 's description", epic);
        subtask2.setId(2);

        assertEquals(subtask1, subtask2, "Subtasks with same id should be same by id");
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

    @Test
    public void testSubtaskCannotBeItsOwnEpic() {
        Subtask subtask = new Subtask("Subtask 1", "Description", null);
        Epic epic = new Epic("Epic", "Epic's description");

        assertNotEquals(subtask, subtask.getEpic(), "Subtask should not be its own epic");
    }

    @Test
    void managersShouldReturnInitializedInstances() {
        assertNotNull(Managers.getDefault(), "The initialized taskManager should be returned.");

        assertNotNull(Managers.getDefaultHistory(), "The initialized historyManager should be returned.");
    }

    @Test
    void taskManagerShouldAddAndFindTasksById() {
        Task task = new Task("Task", "Task's description");
        taskManager.createTask(task);
        Epic epic = new Epic("Epic", "Epic's description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask 1 's description", epic);
        taskManager.createSubtask(subtask);

        assertEquals(task, taskManager.getTaskById(task.getId()), "Task should be find by ID");
        assertEquals(epic, taskManager.getEpicById(epic.getId()), "Epic should be find by ID");
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()), "Subtask should be find by ID");
    }

    @Test
    void tasksWithSpecifyAndGeneratedIdShouldNotConflict() {
        Task taskWithSpecifyId = new Task("Task 1", "Task 1's description");
        taskWithSpecifyId.setId(1);
        taskManager.createTask(taskWithSpecifyId);

        Task taskWithGeneratedId = new Task("Task 2", "Task 2's description");
        taskManager.createTask(taskWithGeneratedId);

        assertNotEquals(taskWithSpecifyId.getId(), taskWithGeneratedId.getId(),
                "Task's ID should not conflict");
        assertEquals(taskWithSpecifyId, taskManager.getTaskById(1),
                "Task should be find with specify ID");
        assertEquals(taskWithGeneratedId, taskManager.getTaskById(taskWithGeneratedId.getId()),
                "Task should be find with generated ID");
    }

    @Test
    void taskShouldRemainUnchangedWhenAddedToManager() {
        Task originalTask = new Task("Original Task", "Original Description");
        originalTask.setTaskStatus(TaskStatus.IN_PROGRESS);
        originalTask.setId(2);

        taskManager.createTask(originalTask);
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
    public void historyManagerShouldSavePreviousTaskVersion() {
        Task task = new Task("Task", "Task's description");
        taskManager.createTask(task);

        taskManager.getTaskById(task.getId());

        task.setDescription("Updated task's description");
        taskManager.updateTask(task);

        List<Task> history = taskManager.getHistory();

        assertEquals(1, history.size());
        assertEquals("Task's description", history.get(0).getDescription(), "Task's description should be save in history");
    }
}