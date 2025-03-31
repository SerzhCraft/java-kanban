import main.java.models.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    void shouldBeEqualsByIdTasksWithSameId() {
        Task task1 = new Task("Task 1", "Task 1's description");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Task 2's description");
        task2.setId(1);

        assertEquals(task1, task2, "Tasks with same id should be same by id");
    }

}
