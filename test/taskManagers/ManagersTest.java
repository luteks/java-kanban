package taskManagers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import taskManagers.*;
import tasks.*;

public class ManagersTest {

    @Test
    public void classReturnsReadyManager() {

        TaskManager taskManager = new Managers().getDefault();

        Assertions.assertNotNull(taskManager);

    }


}
