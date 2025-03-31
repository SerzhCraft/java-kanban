import main.java.managers.HistoryManager;
import main.java.managers.Managers;
import main.java.managers.TaskManager;
import main.java.models.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryManagerTest {

    @Test
    public void historyManagerShouldSavePreviousTaskVersion() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager<Task> historyManager = Managers.getDefaultHistory();

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
