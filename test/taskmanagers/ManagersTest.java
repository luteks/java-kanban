package taskmanagers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class ManagersTest {

    @Test
    public void classReturnsReadyManager() {

        TaskManager taskManager = new Managers().getDefault();

        Assertions.assertNotNull(taskManager);

    }


}
