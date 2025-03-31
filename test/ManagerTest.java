import main.java.managers.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagerTest {

    @Test
    void managersShouldReturnInitializedInstances() {

        assertNotNull(Managers.getDefault(), "The initialized taskManager should be returned.");

        assertNotNull(Managers.getDefaultHistory(), "The initialized historyManager should be returned.");
    }

}
