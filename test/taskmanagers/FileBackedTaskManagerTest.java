package taskmanagers;

import static org.junit.jupiter.api.Assertions.*;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

        private static final Path FILE_PATH = Paths.get("src/saveTest.csv");
        private File file;
        private FileBackedTaskManager fileBackedTaskManager;

        private Task task1;
        private Task task2;
        private Task task3;
        private Epic epic1;
        private Epic epic2;
        private Subtask subtask1;
        private Subtask subtask2;
        private Subtask subtask3;
        private Task updatedTask;

        @Override
        protected FileBackedTaskManager createTaskManager() {
            return new FileBackedTaskManager(null);
        }
        @BeforeEach
        void beforeEach() throws IOException {
            if (Files.exists(FILE_PATH)) {
                Files.writeString(FILE_PATH, "");
            } else {
                Files.createFile(FILE_PATH);
            }

            file = FILE_PATH.toFile();
            fileBackedTaskManager = new FileBackedTaskManager(FILE_PATH);

            task1 = new Task("Первый таск", "Описание 1", TaskStatus.NEW,
                    LocalDateTime.of(2025, Month.JANUARY, 1, 13, 0), Duration.ofMinutes(120));
            task2 = new Task("Второй таск", "Описание 2", TaskStatus.DONE,
                    LocalDateTime.of(2025, Month.JANUARY, 1, 10, 0), Duration.ofMinutes(180));

            epic1 = new Epic("Первый эпик", "Описание 1");
            epic2 = new Epic("Второй эпик", "Описание 2");

            subtask1 = new Subtask(0,
                    "Первая подзадача", "Описание 1", TaskStatus.NEW,
                    LocalDateTime.of(2025, Month.MARCH, 1, 12, 0), Duration.ofMinutes(120));
            subtask2 = new Subtask(0,
                    "Вторая подзадача", "Описание 2", TaskStatus.NEW,
                    LocalDateTime.of(2025, Month.JANUARY, 26, 12, 0), Duration.ofMinutes(180));
            subtask3 = new Subtask(0,
                    "Третья подзадача", "Описание 3", TaskStatus.DONE,
                    LocalDateTime.of(2025, Month.MARCH, 1, 20, 15), Duration.ofMinutes(150));

            updatedTask = new Task(0,
                    "Изменённое название", "Описание 1",
                    TaskStatus.DONE,
                    LocalDateTime.of(2025, Month.JANUARY, 1, 13, 0), Duration.ofMinutes(120));
        }

        @AfterAll
        static void afterAll() throws IOException {
            Files.delete(FILE_PATH);
        }

        @Test
        void creatingAndUploadingEmptyFile() {
            try {
                assertNotNull(fileBackedTaskManager,
                        "Метод loadFromFile() должен возвращать " +
                                "проинициализированный экземпляр FileBackedTaskManager.");

                String fileContent = Files.readString(file.toPath());
                assertTrue(fileContent.isEmpty(), "Файл должен быть пустым.");

            } catch (IOException e) {
                fail("Не удалось выполнить тест: " + e.getMessage());
            }
        }

        private void operationsWithTasksAndPopulateManager(FileBackedTaskManager fileBackedTaskManager) {
            fileBackedTaskManager.addTask(task1);
            int task1id = fileBackedTaskManager.getTasksList().getLast().getID();
            fileBackedTaskManager.addTask(task2);
            int task2id = fileBackedTaskManager.getTasksList().getLast().getID();
            fileBackedTaskManager.addEpic(epic1);
            int epic1id = fileBackedTaskManager.getEpicsList().getLast().getID();
            fileBackedTaskManager.addEpic(epic2);
            int epic2id = fileBackedTaskManager.getEpicsList().getLast().getID();

            task1 = new Task(task1id, task1.getName(), task1.getDescription(), task1.getStatus(), task1.getStartTime(),
                    task1.getDuration());

            task2 = new Task(task2id, task2.getName(), task2.getDescription(), task2.getStatus(), task2.getStartTime(),
                    task2.getDuration());
            subtask1 = new Subtask(epic1id, subtask1.getName(), subtask1.getDescription(), subtask1.getStatus(),
                    subtask1.getStartTime(), subtask1.getDuration());
            subtask2 = new Subtask(epic2id, subtask2.getName(), subtask2.getDescription(), subtask2.getStatus(),
                    subtask2.getStartTime(), subtask2.getDuration());
            subtask3 = new Subtask(epic2id, subtask3.getName(), subtask3.getDescription(), subtask3.getStatus(),
                    subtask3.getStartTime(), subtask3.getDuration());

            fileBackedTaskManager.addSubtask(subtask1);
            int subtask1id = fileBackedTaskManager.getSubtasksList().getFirst().getID();
            fileBackedTaskManager.addSubtask(subtask2);
            int subtask2id = fileBackedTaskManager.getSubtasksList().getLast().getID();
            fileBackedTaskManager.addSubtask(subtask3);
            int subtask3id = fileBackedTaskManager.getSubtasksList().getLast().getID();

            subtask1 = new Subtask(subtask1id, epic1id, subtask1.getName(), subtask1.getDescription(), subtask1.getStatus(),
                    subtask1.getStartTime(), subtask1.getDuration());
            subtask2 = new Subtask(subtask2id, epic2id, subtask2.getName(), subtask2.getDescription(), subtask2.getStatus(),
                    subtask2.getStartTime(), subtask2.getDuration());
            subtask3 = new Subtask(subtask3id, epic2id, subtask3.getName(), subtask3.getDescription(), subtask3.getStatus(),
                    subtask3.getStartTime(), subtask3.getDuration());
            epic2 = fileBackedTaskManager.getEpic(epic2id);
            epic1 = fileBackedTaskManager.getEpic(epic1id);

            updatedTask = new Task(task1id,
                    "Изменённое название", "Описание 1",
                    TaskStatus.DONE,
                    LocalDateTime.of(2025, Month.JANUARY, 1, 13, 0), Duration.ofMinutes(120));
            fileBackedTaskManager.updateTask(updatedTask);

            fileBackedTaskManager.deleteTask(fileBackedTaskManager.getTasksList().getLast().getID());
            fileBackedTaskManager.deleteEpic(fileBackedTaskManager.getEpicsList().getFirst().getID());

            task3 = new Task("Третий таск", "Описание 3", TaskStatus.DONE,
                    LocalDateTime.of(2025, Month.JANUARY, 1, 10, 0), Duration.ofMinutes(180));
            fileBackedTaskManager.addTask(task3);
            int task3id = fileBackedTaskManager.getTasksList().getLast().getID();
            task3 = new Task(task3id, "Третий таск", "Описание 3", TaskStatus.DONE,
                    LocalDateTime.of(2025, Month.JANUARY, 1, 10, 0), Duration.ofMinutes(180));
        }

        @Test
        void savingTasks() {
            operationsWithTasksAndPopulateManager(fileBackedTaskManager);

            assertEquals(2, fileBackedTaskManager.getTasksList().size(), "Должны быть две задачи.");
            assertEquals(updatedTask, fileBackedTaskManager.getTasksList().getFirst(), "Задача должна совпадать.");
            assertEquals(task3, fileBackedTaskManager.getTasksList().getLast(), "Задача должна совпадать.");

            assertEquals(1, fileBackedTaskManager.getEpicsList().size(), "Количество эпиков должно совпадать.");
            assertEquals(epic2, fileBackedTaskManager.getEpicsList().getFirst(), "Эпик должен совпадать.");

            assertEquals(2, fileBackedTaskManager.getSubtasksList().size(), "Количество подзадач должно совпадать.");
            assertEquals(subtask2, fileBackedTaskManager.getSubtasksList().getFirst(), "Подзадача должна совпадать.");
            assertEquals(subtask3.getEpicID(), epic2.getID(), "Подзадача не привязана.");
        }

        @Test
        void loadingTasks() {
            operationsWithTasksAndPopulateManager(fileBackedTaskManager);
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

            assertEquals(2, loadedManager.getTasksList().size(), "Количество задач должно совпадать.");
            assertEquals(updatedTask, loadedManager.getTasksList().getFirst(), "Задача должна совпадать.");
            assertEquals(task3, loadedManager.getTasksList().getLast(), "Задача должна совпадать.");

            assertEquals(1, loadedManager.getEpicsList().size(), "Количество эпиков должно совпадать.");
            assertEquals(epic2, loadedManager.getEpicsList().getFirst(), "Эпик должен совпадать.");

            assertEquals(2, loadedManager.getSubtasksList().size(), "Количество подзадач должно совпадать.");
            assertEquals(subtask2, loadedManager.getSubtasksList().getFirst(), "Подзадача должна совпадать.");
            assertEquals(subtask2.getEpicID(), epic2.getID(), "Подзадача не привязана.");
            assertEquals(subtask3, loadedManager.getSubtasksList().getLast(), "Подзадача должна совпадать.");
            assertEquals(subtask3.getEpicID(), epic2.getID(), "Подзадача не привязана.");
        }
}