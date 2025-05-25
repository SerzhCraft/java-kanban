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

class TaskManagerTest extends AbstractTaskManagerTest<TaskManager> {

    @BeforeEach
    @Override
    public void setUp() {
        taskManager = Managers.getDefault();
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();// Используйте фабрику для получения экземпляра
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
    void shouldUpdateEpicStatusWhenSubtaskChanged() {
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();

        Epic epic = taskManager.createEpic(new Epic("Epic", "Description", duration, startTime));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "Description", epic));

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());

        // Обновляем статус подзадачи
        subtask.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);

        // Обновляем статус эпика
        epic.updateStatus(); // Убедитесь, что вызываете updateStatus здесь

        assertEquals(TaskStatus.DONE, epic.getTaskStatus());
    }

}