package taskmanagers;

import static org.junit.jupiter.api.Assertions.*;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    private File file;
    private Task updatedTask;
    private Task task5;
    private Epic epic2;
    private Subtask subtask3;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("save", "csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл.", exception);
        }

        taskManager = new FileBackedTaskManager(file.toPath());
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

    @Test
    void testCreatingAndUploadingEmptyFile() {
        try {
            assertNotNull(taskManager,
                    "Метод loadFromFile() должен возвращать " +
                            "проинициализированный экземпляр FileBackedTaskManager.");

            List<String> fileContent = Files.readAllLines(file.toPath());
            assertEquals(1, fileContent.size(), "Файл должен быть пустым.");

        } catch (IOException e) {
            fail("Не удалось выполнить тест: " + e.getMessage());
        }
    }

    private void operationsWithTasksAndPopulateManager(FileBackedTaskManager fileBackedTaskManager) {
        Task task1 = new Task("Первая", "1", TaskStatus.NEW);
        fileBackedTaskManager.addTask(task1);
        Task task2 = new Task("Вторая", "2", TaskStatus.DONE);
        fileBackedTaskManager.addTask(task2);
        Epic epic1 = new Epic("Первый", "1");
        fileBackedTaskManager.addEpic(epic1);
        epic2 = new Epic("Второй", "2");
        fileBackedTaskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask(
                "Первая", "1", TaskStatus.NEW, fileBackedTaskManager.getEpicsList().getFirst().getID());
        fileBackedTaskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask(
                "Вторая", "2", TaskStatus.NEW, fileBackedTaskManager.getEpicsList().getFirst().getID());
        fileBackedTaskManager.addSubtask(subtask2);
        subtask3 = new Subtask(
                "Третья", "3", TaskStatus.NEW, fileBackedTaskManager.getEpicsList().getLast().getID());
        fileBackedTaskManager.addSubtask(subtask3);
        epic2.addSubtaskID(subtask3.getID());

        updatedTask = new Task(
                "Четвертая", "Обновленная", fileBackedTaskManager.getTasksList().getFirst().getID(), TaskStatus.DONE);
        fileBackedTaskManager.updateTask(updatedTask);

        fileBackedTaskManager.deleteTask(fileBackedTaskManager.getTasksList().getLast().getID());
        fileBackedTaskManager.deleteEpic(fileBackedTaskManager.getEpicsList().getFirst().getID());

        task5 = new Task("Пятая", "5", TaskStatus.NEW);
        fileBackedTaskManager.addTask(task5);
    }

    @Test
    void testSavingTasks() {
        operationsWithTasksAndPopulateManager(taskManager);

        assertEquals(2, taskManager.getTasksList().size(), "Должны быть две задачи.");
        assertEquals(updatedTask, taskManager.getTasksList().getFirst(), "Задача должна совпадать.");
        assertEquals(task5, taskManager.getTasksList().getLast(), "Задача должна совпадать.");

        assertEquals(1, taskManager.getEpicsList().size(), "Количество эпиков должно совпадать.");
        assertEquals(epic2, taskManager.getEpicsList().getFirst(), "Эпик должен совпадать.");

        assertEquals(1, taskManager.getSubtasksList().size(), "Количество сабтасок должно совпадать.");
        assertEquals(subtask3, taskManager.getSubtasksList().getFirst(), "Сабтаск должен совпадать.");
        assertEquals(subtask3.getEpicID(), epic2.getID(), "Сабтаск не привязан.");
    }

    @Test
    void testLoadingTasks() {
        operationsWithTasksAndPopulateManager(taskManager);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, loadedManager.getTasksList().size(), "Количество задач должно совпадать.");
        assertEquals(updatedTask, loadedManager.getTasksList().getFirst(), "Задача должна совпадать.");
        assertEquals(task5, loadedManager.getTasksList().getLast(), "Задача должна совпадать.");

        assertEquals(1, loadedManager.getEpicsList().size(), "Количество эпиков должно совпадать.");
        assertEquals(epic2, loadedManager.getEpicsList().getFirst(), "Эпик должен совпадать.");

        assertEquals(1, loadedManager.getSubtasksList().size(), "Количество сабтасок должно совпадать.");
        assertEquals(subtask3, loadedManager.getSubtasksList().getFirst(), "Сабтаск должен совпадать.");
        assertEquals(subtask3.getEpicID(), epic2.getID(), "Сабтаск не привязан.");
    }
}