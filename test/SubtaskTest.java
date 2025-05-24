import main.java.models.Epic;
import main.java.models.Subtask;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubtaskTest {

    @Test
    void shouldBeEqualsByIdSubtasksWithSameId() {
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();

        Epic epic = Epic.createWithId(1, "Epic", "Description", duration, startTime);
        Subtask subtask1 = Subtask.createWithId(2,
                "Subtask 1",
                "Description 1",
                epic,
                duration,
                startTime);
        Subtask subtask2 = Subtask.createWithId(2,
                "Subtask 2",
                "Description 2",
                epic,
                duration,
                startTime);

        assertEquals(subtask1, subtask2, "Subtasks with same id should be equal");
    }

    @Test
    public void testSubtaskCannotBeItsOwnEpic() {
        Duration duration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.now();

        Subtask subtask = Subtask.createWithId(1,
                "Subtask",
                "Description",
                null,
                duration,
                startTime);
        Epic epic = Epic.createWithId(2, "Epic", "Description", duration, startTime);

        assertNotEquals(subtask, subtask.getEpic(), "Subtask should not be its own epic");
    }

}
