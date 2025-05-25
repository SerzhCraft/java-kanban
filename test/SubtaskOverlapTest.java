import main.java.models.Subtask;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtaskOverlapTest {

    private boolean isTimeOverlap(Subtask newSubtask, Subtask existingSub) {
        LocalDateTime newStart = newSubtask.getStartTime();
        LocalDateTime newEnd = newStart.plus(newSubtask.getDuration());
        LocalDateTime existingStart = existingSub.getStartTime();
        LocalDateTime existingEnd = existingStart.plus(existingSub.getDuration());

        return (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart));
    }

    @Test
    void testOverlap() {
        Subtask subtask1 = new Subtask("Task 1", "Description", null);
        subtask1.setStartTime(LocalDateTime.of(2025, 5, 24, 10, 0));
        subtask1.setDuration(Duration.ofHours(2)); // 10:00 - 12:00

        Subtask subtask2 = new Subtask("Task 2", "Description", null);
        subtask2.setStartTime(LocalDateTime.of(2025, 5, 24, 11, 0)); // Пересекается с Task 1
        subtask2.setDuration(Duration.ofHours(2)); // 11:00 - 13:00

        assertTrue(isTimeOverlap(subtask1, subtask2), "Tasks should overlap");
    }

    @Test
    void testTouching() {
        Subtask subtask1 = new Subtask("Task 1", "Description", null);
        subtask1.setStartTime(LocalDateTime.of(2025, 5, 24, 10, 0));
        subtask1.setDuration(Duration.ofHours(2)); // 10:00 - 12:00

        Subtask subtask2 = new Subtask("Task 2", "Description", null);
        subtask2.setStartTime(LocalDateTime.of(2025, 5, 24, 12, 0)); // Соприкасается с Task 1
        subtask2.setDuration(Duration.ofHours(2)); // 12:00 - 14:00

        assertFalse(isTimeOverlap(subtask1, subtask2), "Tasks should not overlap (touching)");
    }

    @Test
    void testNoOverlap() {
        Subtask subtask1 = new Subtask("Task 1", "Description", null);
        subtask1.setStartTime(LocalDateTime.of(2025, 5, 24, 10, 0));
        subtask1.setDuration(Duration.ofHours(2)); // 10:00 - 12:00

        Subtask subtask2 = new Subtask("Task 2", "Description", null);
        subtask2.setStartTime(LocalDateTime.of(2025, 5, 24, 13, 0)); // Не пересекается с Task 1
        subtask2.setDuration(Duration.ofHours(2)); // 13:00 -15:00

        assertFalse(isTimeOverlap(subtask1, subtask2), "Tasks should not overlap");
    }

    @Test
    void testCompletelyInside() {
        Subtask subtask1 = new Subtask("Task A", "Description A", null);
        subtask1.setStartTime(LocalDateTime.of(2025, 5, 24, 10, 0));
        subtask1.setDuration(Duration.ofHours(4)); // Task A: (10:00 -14:00)

        Subtask subtask2 = new Subtask("Task B", "Description B", null);
        subtask2.setStartTime(LocalDateTime.of(2025, 5, 24, 11, 0)); // Task B полностью внутри Task A
        subtask2.setDuration(Duration.ofHours(1)); // Task B: (11:00 -12:00)

        assertTrue(isTimeOverlap(subtask1, subtask2), "Tasks should overlap (B inside A)");
    }

    @Test
    void testCompletelyOutside() {
        Subtask subtask1 = new Subtask("Task A", "Description A", null);
        subtask1.setStartTime(LocalDateTime.of(2025, 5, 24, 10, 0));
        subtask1.setDuration(Duration.ofHours(4)); // Task A: (10:00 -14:00)

        Subtask subtask2 = new Subtask("Task B", "Description B", null);
        subtask2.setStartTime(LocalDateTime.of(2025, 5, 24, 15, 0)); // Task B полностью вне Task A
        subtask2.setDuration(Duration.ofHours(1)); // Task B: (15:00 -16:00)

        assertFalse(isTimeOverlap(subtask1, subtask2), "Tasks should not overlap (completely outside)");
    }

}