import main.java.models.Epic;
import main.java.models.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubtaskTest {

    @Test
    void shouldBeEqualsByIdSubtasksWithSameId() {
        Epic epic = Epic.createWithId(1, "Epic", "Description");
        Subtask subtask1 = Subtask.createWithId(2,"Subtask 1", "Description 1", epic);
        Subtask subtask2 = Subtask.createWithId(2, "Subtask 2", "Description 2", epic);

        assertEquals(subtask1, subtask2, "Subtasks with same id should be equal");
    }

    @Test
    public void testSubtaskCannotBeItsOwnEpic() {
        Subtask subtask = Subtask.createWithId(1, "Subtask", "Description", null);
        Epic epic = Epic.createWithId(2, "Epic", "Description");

        assertNotEquals(subtask, subtask.getEpic(), "Subtask should not be its own epic");
    }



}
