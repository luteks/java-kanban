package TaskManagers;

import org.junit.jupiter.api.Test;
import Tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {


    @Test
    public void testAddTaskToHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 2, TaskStatus.NEW);

        historyManager.add(task1);
        assertEquals(List.of(task1), historyManager.getHistory(), "История должна содержать только task1");

        historyManager.add(task2);
        assertEquals(List.of(task1, task2), historyManager.getHistory(), "История должна содержать task1 и task2");

    }

    @Test
    public void testRemoveTaskFromHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 2, TaskStatus.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getID());
        assertEquals(List.of(task2), historyManager.getHistory(), "Задача task1 должна быть удалена из истории");

        historyManager.remove(999);
        assertEquals(List.of(task2), historyManager.getHistory(), "История не должна измениться после попытки удалить несуществующую задачу");
    }

    @Test
    public void testGetHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 2, TaskStatus.NEW);
        Epic epic1 = new Epic("Epic 1", "Description 3", 3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "История должна содержать три элемента");
        assertEquals(task1, history.get(0), "Первый элемент должен быть task1");
        assertEquals(task2, history.get(1), "Второй элемент должен быть task2");
        assertEquals(epic1, history.get(2), "Третий элемент должен быть epic1");
    }

    @Test
    public void testEmptyHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    public void testAddDifferentTaskTypes() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task = new Task("Task", "Description", 1, TaskStatus.NEW);
        Epic epic = new Epic("Epic", "Description", 2);
        Subtask subtask = new Subtask("Subtask", "Description", TaskStatus.NEW, 2);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "История должна содержать три элемента");
        assertEquals(task, history.get(0), "Первый элемент должен быть task");
        assertEquals(epic, history.get(1), "Второй элемент должен быть epic");
        assertEquals(subtask, history.get(2), "Третий элемент должен быть subtask");
    }
}