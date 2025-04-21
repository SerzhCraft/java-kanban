import main.java.models.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    void shouldBeEqualsByIdTasksWithSameId() {
        Task task1 = Task.createWithId(1, "Task 1", "Task 1's description");
        Task task2 = Task.createWithId(1, "Task 2", "Task 2's description");

        assertEquals(task1, task2, "Tasks with same id should be same by id");
    }

}
