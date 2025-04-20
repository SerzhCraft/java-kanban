import main.java.managers.HistoryManager;
import main.java.managers.Managers;
import main.java.managers.TaskManager;
import main.java.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryManagerTest {
    TaskManager taskManager;
    HistoryManager<Task> historyManager;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void shouldKeepOriginalTaskVersionInHistory() {
        Task task = Task.createWithId(1, "Task", "Original description");

        historyManager.add(task);
        task.setDescription("Updated description");

        Task fromHistory = historyManager.getHistory().get(0);
        assertEquals("Original description", fromHistory.getDescription());
    }

    @Test
    void shouldRemoveTaskFromHistoryWhenDeleted() {
        Task task1 = Task.createWithId(1, "Task 1", "Description 1");
        Task task2 = Task.createWithId(2, "Task 2", "Description 2");

        historyManager.add(task1);
        historyManager.add(task2);
        assertEquals(2, historyManager.getHistory().size());

        historyManager.remove(1);
        assertEquals(1, historyManager.getHistory().size());
        assertEquals(2, historyManager.getHistory().get(0).getId());
    }

    @Test
    void shouldSavePreserveOrder() {
        Task task1 = Task.createWithId(1, "Task 1", "Description 1");
        Task task2 = Task.createWithId(2, "Task 2", "Description 2");
        Task task3 = Task.createWithId(3, "Task 3", "Description 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.get(0).getId());
        assertEquals(2, history.get(1).getId());
        assertEquals(3, history.get(2).getId());
    }

    @Test
    public void shouldNotContainDuplicates() {
        Task task = Task.createWithId(1, "Task", "Description");

        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
    }

}
