import main.java.models.Epic;
import main.java.models.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubtaskTest {

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
    public void testSubtaskCannotBeItsOwnEpic() {
        Subtask subtask = new Subtask("Subtask 1", "Description", null);
        Epic epic = new Epic("Epic", "Epic's description");

        assertNotEquals(subtask, subtask.getEpic(), "Subtask should not be its own epic");
    }

}
