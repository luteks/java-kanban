package taskManagers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import taskManagers.*;
import tasks.*;

public class InMemoryTaskManagerTest {

    @Test
    public void createdTaskEqualsSavedTaskIfIdsIdentical() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task1);

        Task savedTask = taskManager.getTask(task1.getID());

        Assertions.assertEquals(task1, savedTask, "Задачи не совпадают");

    }

    @Test
    public void createdEpicEqualsSavedEpicIfIdsIdentical() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Epic savedEpic = taskManager.getEpic(epic1.getID());

        Assertions.assertEquals(epic1, savedEpic, "Задачи не совпадают");

    }

    @Test
    public void createdSubtaskEqualsSavedSubtaskIfIdsIdentical() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1", epic1.getID());
        taskManager.addSubtask(subtask1);

        Subtask savedSubtask = taskManager.getSubtask(subtask1.getID());

        Assertions.assertEquals(subtask1, savedSubtask, "Сабтаски не совпадают");

    }

    @Test
    public void taskConflictIds() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Assertions.assertNotEquals(task1, task2);
    }

    @Test
    public void immutabilityTaskTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task1);

        Task updatedTask = new Task(task1.getName(), task1.getDescription(), task1.getID(), TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        Assertions.assertEquals(task1.getName(), updatedTask.getName());
        Assertions.assertEquals(task1.getDescription(), updatedTask.getDescription());
        Assertions.assertEquals(task1.getID(), updatedTask.getID());
    }

    @Test
    public void clearListOfTasksTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.addTask(task2);

        taskManager.clearTasksList();

        Assertions.assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    public void clearListOfEpicsTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1", epic1.getID());
        taskManager.addSubtask(subtask1);

        taskManager.clearEpicsList();

        Assertions.assertEquals(0, taskManager.getEpicsList().size());
        Assertions.assertEquals(0, taskManager.getSubtasksList().size());
    }

    @Test
    public void clearListOfSubtasksTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getID());
        taskManager.addTask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getID());
        taskManager.addTask(subtask2);

        taskManager.clearSubtasksList();

        Assertions.assertEquals(0, taskManager.getSubtasksList().size());
        Assertions.assertEquals(1, taskManager.getEpicsList().size());
    }

    @Test
    public void updateEpicStatusTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Assertions.assertEquals(TaskStatus.NEW, epic1.getStatus());

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getID());
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getID());
        taskManager.addSubtask(subtask2);

        Subtask updatedSubtask1 = new Subtask( "Подзадача 1", "Описание подзадачи 1",subtask1.getID(),
                TaskStatus.IN_PROGRESS, epic1.getID());
        taskManager.updateSubtask(updatedSubtask1);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    public void updateTaskStatusTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task1);

        Task updatedTask1 = new Task( "Задача 1", "Описание задачи 1",task1.getID(), TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask1);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, updatedTask1.getStatus());
    }

    @Test
    public void deleteTaskTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task1);

        taskManager.deleteTask(task1.getID());

        Assertions.assertEquals(0, taskManager.getTasksList().size());
    }


    @Test
    public void deleteEpicTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getID());
        taskManager.addTask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getID());
        taskManager.addTask(subtask2);

        taskManager.deleteEpic(epic1.getID());

        Assertions.assertEquals(0, taskManager.getEpicsList().size());
        Assertions.assertEquals(0, taskManager.getSubtasksList().size());
    }

    @Test
    public void deleteSubtaskTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getID());
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getID());
        taskManager.addSubtask(subtask2);

        Assertions.assertEquals(2, taskManager.getSubtasksList().size());

        taskManager.deleteSubtask(subtask1.getID());

        Assertions.assertEquals(1, taskManager.getSubtasksList().size());
        Assertions.assertEquals(1, epic1.getSubtasksID().size());

    }

}
