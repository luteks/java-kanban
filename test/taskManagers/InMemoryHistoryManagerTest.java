package taskManagers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import taskManagers.*;
import tasks.*;

public class InMemoryHistoryManagerTest {

    @Test
    public void addHistoryTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task1);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getID());
        taskManager.addTask(subtask1);

        Assertions.assertEquals(0, taskManager.getHistory().size());

        taskManager.getTask(task1.getID());
        Assertions.assertEquals(1, taskManager.getHistory().size());

    }

    @Test
    public void updateHistoryAfterAdding11Task() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.addTask(task2);
        Task task3 = new Task("Задача 3", "Описание задачи 3");
        taskManager.addTask(task3);
        Task task4 = new Task("Задача 4", "Описание задачи 4");
        taskManager.addTask(task4);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.addEpic(epic2);
        Epic epic3 = new Epic("Эпик 3", "Описание эпика 3");
        taskManager.addEpic(epic3);
        Epic epic4 = new Epic("Эпик 4", "Описание эпика 4");
        taskManager.addEpic(epic3);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getID());
        taskManager.addTask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getID());
        taskManager.addTask(subtask2);
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1.getID());
        taskManager.addTask(subtask3);

        Assertions.assertEquals(0, taskManager.getHistory().size());

        taskManager.getTask(task1.getID());
        taskManager.getTask(task2.getID());
        taskManager.getTask(task3.getID());
        taskManager.getTask(task4.getID());
        taskManager.getTask(epic1.getID());
        taskManager.getTask(epic2.getID());
        taskManager.getTask(epic3.getID());
        taskManager.getTask(epic4.getID());
        taskManager.getTask(subtask1.getID());
        taskManager.getTask(subtask2.getID());

        Assertions.assertEquals(10, taskManager.getHistory().size());

        taskManager.getTask(subtask3.getID());

        Assertions.assertFalse(taskManager.getHistory().contains(task1));
        Assertions.assertTrue(taskManager.getHistory().contains(subtask3));
        Assertions.assertEquals(10, taskManager.getHistory().size());

    }

}
