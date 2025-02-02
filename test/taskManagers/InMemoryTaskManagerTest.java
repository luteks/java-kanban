package taskManagers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import tasks.*;

import java.util.List;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testCreateTask() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        taskManager.addTask(task);

        assertNotNull(taskManager.getTask(task.getID()), "Задача должна быть создана");
        assertEquals(task, taskManager.getTask(task.getID()), "Созданная задача должна совпадать с полученной из менеджера");
    }

    @Test
    public void testDeleteTask() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        taskManager.addTask(task);

        assertNotNull(taskManager.getTask(task.getID()), "Задача должна существовать перед удалением");

        taskManager.deleteTask(task.getID());
        assertNull(taskManager.getTask(task.getID()), "Задача должна быть удалена");
    }

    @Test
    public void testCreateEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);

        assertNotNull(taskManager.getEpic(epic.getID()), "Эпик должен быть создан");
        assertEquals(epic, taskManager.getEpic(epic.getID()), "Созданный эпик должен совпадать с полученным из менеджера");
    }

    @Test
    public void testCreateSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", TaskStatus.NEW, epic.getID());
        taskManager.addSubtask(subtask);

        assertNotNull(taskManager.getSubtask(subtask.getID()), "Подзадача должна быть создана");
        assertEquals(subtask, taskManager.getSubtask(subtask.getID()), "Созданная подзадача должна совпадать с полученной из менеджера");

        List<Subtask> subtasks = taskManager.getEpicSubtasksList(epic.getID());
        assertEquals(1, subtasks.size(), "Должна быть одна подзадача для эпика");
        assertEquals(subtask, subtasks.get(0), "Подзадача должна быть связана с эпиком");
    }

    @Test
    public void testDeleteSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", TaskStatus.NEW, epic.getID());
        taskManager.addSubtask(subtask);

        assertNotNull(taskManager.getSubtask(subtask.getID()), "Подзадача должна существовать перед удалением");

        taskManager.deleteSubtask(subtask.getID());
        assertNull(taskManager.getSubtask(subtask.getID()), "Подзадача должна быть удалена");

        List<Subtask> subtasks = taskManager.getEpicSubtasksList(epic.getID());
        assertTrue(subtasks.isEmpty(), "Список подзадач эпика должен быть пустым после удаления");
    }

    @Test
    public void testSetterIntegrity() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        taskManager.addTask(task);

        // Изменяем имя задачи через сеттер
        task.setName("Updated Task 1");
        Task updatedTask = taskManager.getTask(task.getID());
        assertEquals("Updated Task 1", updatedTask.getName(), "Имя должно быть обновлено");

        // Создаем другую задачу и проверяем, что изменения не повлияли на неё
        Task anotherTask = new Task("Task 2", "Description 2", TaskStatus.NEW);
        taskManager.addTask(anotherTask);

        assertNotNull(taskManager.getTask(anotherTask.getID()), "Другие задачи не должны быть затронуты");
    }

    @Test
    public void testDeleteEpicWithSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", TaskStatus.NEW, epic.getID());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", TaskStatus.NEW, epic.getID());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertNotNull(taskManager.getEpic(epic.getID()), "Эпик должен существовать перед удалением");
        assertNotNull(taskManager.getSubtask(subtask1.getID()), "Подзадача 1 должна существовать перед удалением");
        assertNotNull(taskManager.getSubtask(subtask2.getID()), "Подзадача 2 должна существовать перед удалением");

        taskManager.deleteEpic(epic.getID());

        assertNull(taskManager.getEpic(epic.getID()), "Эпик должен быть удален");
        assertNull(taskManager.getSubtask(subtask1.getID()), "Подзадача 1 должна быть удалена вместе с эпиком");
        assertNull(taskManager.getSubtask(subtask2.getID()), "Подзадача 2 должна быть удалена вместе с эпиком");
    }

}
