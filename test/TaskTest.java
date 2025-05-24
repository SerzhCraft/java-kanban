import main.java.models.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    void shouldBeEqualsByIdTasksWithSameId() {
        Task task1 = Task.createWithId(1, "Task 1", "Task 1's description");
        Task task2 = Task.createWithId(1, "Task 2", "Task 2's description");

        assertEquals(task1, task2, "Tasks with same id should be same by id");
    }

    @Test
    void shouldBeEqualsByDurationAndStartTimeSetAndGet() {
        Duration duration = Duration.ofMinutes(90);
        LocalDateTime startTime = LocalDateTime.of(2024, 6, 1, 10, 0);

        Task task = new Task("Test", "Description");
        task.setDuration(duration);
        task.setStartTime(startTime);

        assertEquals(duration, task.getDuration(), "Durations should be equal");
        assertEquals(startTime, task.getStartTime(), "Start times should be equal");
    }

    @Test
    void shouldBeCorrectGetEndTime() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime startTime = LocalDateTime.of(2024, 6, 1, 12, 0);

        Task task = new Task("Test", "Description");
        task.setDuration(duration);
        task.setStartTime(startTime);

        assertEquals(startTime.plus(duration), task.getEndTime(), "The end time must be calculated correctly.");
    }

}
