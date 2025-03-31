import main.java.enums.TaskStatus;
import main.java.managers.Managers;
import main.java.managers.TaskManager;
import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
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

}