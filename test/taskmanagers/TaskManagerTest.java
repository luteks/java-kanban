package taskmanagers;

import exceptions.NotFoundException;
import exceptions.TaskIntersectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task1, task2;
    protected Epic epic1, epic2;
    protected Subtask subtask1, subtask2, subtask3;

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();

        task1 = new Task("Первый таск", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 1, 13, 0), Duration.ofMinutes(120));
        task2 = new Task("Второй таск", "Описание 2", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 1, 10, 0), Duration.ofMinutes(180));

        epic1 = new Epic("Первый эпик", "Описание 1");
        epic2 = new Epic("Второй эпик", "Описание 2");

        subtask1 = new Subtask(-1,
                "Первая подзадача", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.MARCH, 1, 12, 0), Duration.ofMinutes(120));
        subtask2 = new Subtask(-1,
                "Вторая подзадача", "Описание 2", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 26, 12, 0), Duration.ofMinutes(180));
        subtask3 = new Subtask(-1,
                "Третья подзадача", "Описание 3", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.MARCH, 1, 20, 15), Duration.ofMinutes(150));
    }

    protected abstract T createTaskManager();

    @Test
    void testAddAndGetTasks() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Task savedTask1 = taskManager.getTask(1);
        Task savedTask2 = taskManager.getTask(2);

        assertNotNull(savedTask1, "Задача 1 не найдена.");
        assertNotNull(savedTask2, "Задача 2 не найдена.");

        assertEquals(task1.getName(), savedTask1.getName(), "Названия задачи 1 не совпадают.");
        assertEquals(task1.getDescription(), savedTask1.getDescription(), "Описания задачи 1 не совпадают.");
        assertEquals(task1.getStatus(), savedTask1.getStatus(), "Статусы задачи 1 не совпадают.");
        assertEquals(task1.getStartTime(), savedTask1.getStartTime(), "Время начала задачи 1 не совпадает.");
        assertEquals(task1.getDuration(), savedTask1.getDuration(), "Продолжительность задачи 1 не совпадает.");

        List<Task> tasks = taskManager.getTasksList();
        assertNotNull(tasks, "Список задач не должен быть null.");
        assertEquals(2, tasks.size(), "Должны быть две задачи.");
        assertEquals(task1.getName(), tasks.getFirst().getName(), "Название первой задачи не совпадает.");
        assertEquals(task2.getName(), tasks.getLast().getName(), "Название второй задачи не совпадает.");

        assertThrows(NotFoundException.class, () -> taskManager.getTask(3),
                "Должно выбрасываться исключение NotFoundException после удаления задачи.");
    }

    @Test
    void testAddAndGetEpics() {
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Epic savedEpic1 = taskManager.getEpic(1);
        Epic savedEpic2 = taskManager.getEpic(2);

        assertNotNull(savedEpic1, "Эпик 1 не найден.");
        assertNotNull(savedEpic2, "Эпик 2 не найден.");

        assertEquals(epic1.getName(), savedEpic1.getName(), "Названия эпика 1 не совпадают.");
        assertEquals(epic1.getDescription(), savedEpic1.getDescription(), "Описания эпика 1 не совпадают.");
        assertEquals(TaskStatus.NEW, savedEpic1.getStatus(), "Статус эпика 1 должен быть NEW.");

        List<Epic> epics = taskManager.getEpicsList();
        assertNotNull(epics, "Список эпиков не должен быть null.");
        assertEquals(2, epics.size(), "Должны быть два эпика.");
        assertEquals(epic1.getName(), epics.getFirst().getName(), "Название первого эпика не совпадает.");
        assertEquals(epic2.getName(), epics.getLast().getName(), "Название второго эпика не совпадает.");

        assertThrows(NotFoundException.class, () -> taskManager.getEpic(3),
                "Должно выбрасываться исключение NotFoundException после удаления эпика.");
    }

    @Test
    void testAddAndGetSubtasks() {
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        subtask1 = new Subtask(1, subtask1.getName(), subtask1.getDescription(), subtask1.getStatus(),
                subtask1.getStartTime(), subtask1.getDuration());
        subtask2 = new Subtask(2, subtask2.getName(), subtask2.getDescription(), subtask2.getStatus(),
                subtask2.getStartTime(), subtask2.getDuration());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Subtask savedSubtask1 = taskManager.getSubtask(3);
        Subtask savedSubtask2 = taskManager.getSubtask(4);

        assertNotNull(savedSubtask1, "Подзадача 1 не найдена.");
        assertNotNull(savedSubtask2, "Подзадача 2 не найдена.");

        assertEquals(subtask1.getName(), savedSubtask1.getName(), "Названия подзадачи 1 не совпадают.");
        assertEquals(subtask1.getDescription(), savedSubtask1.getDescription(), "Описания подзадачи 1 не совпадают.");
        assertEquals(subtask1.getStatus(), savedSubtask1.getStatus(), "Статусы подзадачи 1 не совпадают.");
        assertEquals(subtask1.getStartTime(), savedSubtask1.getStartTime(), "Время начала подзадачи 1 не совпадает.");
        assertEquals(subtask1.getDuration(), savedSubtask1.getDuration(), "Продолжительность подзадачи 1 не совпадает.");

        List<Subtask> subtasks = taskManager.getSubtasksList();
        assertNotNull(subtasks, "Список подзадач не должен быть null.");
        assertEquals(2, subtasks.size(), "Должны быть две подзадачи.");
        assertEquals(subtask1.getName(), subtasks.getFirst().getName(), "Название первой подзадачи не совпадает.");
        assertEquals(subtask2.getName(), subtasks.getLast().getName(), "Название второй подзадачи не совпадает.");

        Epic epic = taskManager.getEpicsList().getFirst();
        assertEquals(1, epic.getSubtasksID().size(),
                "Первый эпик должен иметь одну подзадачу.");
        assertEquals(3, epic.getSubtasksID().getFirst(), "id подзадачи должно совпадать.");

        assertThrows(NotFoundException.class, () -> taskManager.getSubtask(5),
                "Должно выбрасываться исключение NotFoundException после удаления подзадачи.");
    }

    @Test
    void testTaskIntersectionException() {
        taskManager.addTask(task1);

        Task overlappingTask = new Task("Вторая", "Описание 2", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.JANUARY, 1, 13, 30), Duration.ofMinutes(60));

        TaskIntersectionException exception = assertThrows(TaskIntersectionException.class, () -> {
            taskManager.addTask(overlappingTask);
        });

        assertEquals("Задача \"Вторая\" пересекается по времени с другой задачей!", exception.getMessage());
    }

    @Test
    void testHistoryAfterGettingTasks() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getTask(1);

        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История просмотров не должна быть null.");
        assertEquals(2, history.size(), "История должна содержать две задачи.");
        assertEquals(task1.getName(), history.getLast().getName(),
                "Последняя просмотренная задача должна быть последней в истории.");
        assertEquals(task2.getName(), history.getFirst().getName(),
                "Первая просмотренная задача должна быть первой в истории.");
    }

    @Test
    void shouldNotAllowEpicToBeItsOwnSubtask() {
        taskManager.addEpic(epic1);
        Subtask invalidSubtask = new Subtask(taskManager.getEpicsList().getFirst().getID(),
                taskManager.getEpicsList().getFirst().getID(), "Некорректная подзадача", "Описание", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.JANUARY, 1, 13, 30), Duration.ofMinutes(60));
        taskManager.addSubtask(invalidSubtask);
        assertEquals(0, taskManager.getSubtasksList().size(), "Подзадач не должно быть добавлено.");
    }

    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        Subtask invalidSubtask = new Subtask(subtask1.getID(),
                "Некорректная подзадача", "Описание 1", TaskStatus.NEW,
                LocalDateTime.of(2025, Month.MARCH, 1, 12, 0), Duration.ofMinutes(120));
        taskManager.addSubtask(invalidSubtask);

        assertEquals(0, taskManager.getSubtasksList().size(),
                "Подзадача с некорректным ID не должна добавляться.");
    }

    @Test
    void testEpicStatusWhenAllSubtasksAreNew() {
        taskManager.addEpic(epic1);
        subtask1 = new Subtask(1, subtask1.getName(), subtask1.getDescription(), subtask1.getStatus(),
                subtask1.getStartTime(), subtask1.getDuration());
        subtask2 = new Subtask(1, subtask2.getName(), subtask2.getDescription(), subtask2.getStatus(),
                subtask2.getStartTime(), subtask2.getDuration());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Epic updatedEpic = taskManager.getEpic(1);

        assertEquals(TaskStatus.NEW, updatedEpic.getStatus(),
                "Статус эпика должен быть NEW, если все подзадачи имеют статус NEW.");
    }

    @Test
    void testEpicStatusWhenAllSubtasksAreDone() {
        taskManager.addEpic(epic1);
        subtask3 = new Subtask(1, subtask3.getName(), subtask3.getDescription(), subtask3.getStatus(),
                subtask3.getStartTime(), subtask3.getDuration());
        Subtask subtask4 = new Subtask(1, "Дополнительная подзадача", "Описание 1", TaskStatus.DONE,
                LocalDateTime.of(2025, Month.MARCH, 1, 12, 0), Duration.ofMinutes(120));
        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);

        Epic updatedEpic = taskManager.getEpic(1);

        assertEquals(TaskStatus.DONE, updatedEpic.getStatus(),
                "Статус эпика должен быть DONE, если все подзадачи имеют статус DONE.");
    }

    @Test
    void testEpicStatusWhenSubtasksAreNewAndDone() {
        taskManager.addEpic(epic1);
        subtask2 = new Subtask(1, subtask2.getName(), subtask2.getDescription(), subtask2.getStatus(),
                subtask2.getStartTime(), subtask2.getDuration());
        subtask3 = new Subtask(1, subtask3.getName(), subtask3.getDescription(), subtask3.getStatus(),
                subtask3.getStartTime(), subtask3.getDuration());
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        Epic updatedEpic = taskManager.getEpic(1);

        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus(),
                "Статус эпика должен быть IN_PROGRESS, если подзадачи имеют статусы NEW и DONE.");
    }

    @Test
    void testEpicStatusWhenSubtasksAreInProgress() {
        taskManager.addEpic(epic1);
        subtask2 = new Subtask(1, subtask2.getName(), subtask2.getDescription(), subtask2.getStatus(),
                subtask2.getStartTime(), subtask2.getDuration());
        subtask3 = new Subtask(1, subtask3.getName(), subtask3.getDescription(), subtask3.getStatus(),
                subtask3.getStartTime(), subtask3.getDuration());
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        Epic updatedEpic = taskManager.getEpic(1);

        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus(),
                "Статус эпика должен быть IN_PROGRESS, если подзадачи имеют статус IN_PROGRESS.");
    }

    @Test
    void shouldRemoveSubtaskAndClearItsIdFromEpic() {
        taskManager.addEpic(epic1);
        subtask1 = new Subtask(1, subtask1.getName(), subtask1.getDescription(), subtask1.getStatus(),
                subtask1.getStartTime(), subtask1.getDuration());
        taskManager.addSubtask(subtask1);

        Epic epicBeforeDelete = taskManager.getEpic(1);
        assertTrue(epicBeforeDelete.getSubtasksID().contains(2),
                "Id подзадачи должен быть в эпике.");

        taskManager.deleteSubtask(2);

        Epic epicAfterDelete = taskManager.getEpic(1);
        assertEquals(0, taskManager.getSubtasksList().size(), "Список подзадач должен быть пуст.");
        assertFalse(epicAfterDelete.getSubtasksID().contains(2),
                "Id подзадачи не должен оставаться в эпике.");
    }

    @Test
    void shouldPreserveSubtasksAfterEpicUpdate() {
        taskManager.addEpic(epic1);
        subtask2 = new Subtask(1, subtask2.getName(), subtask2.getDescription(), subtask2.getStatus(),
                subtask2.getStartTime(), subtask2.getDuration());
        subtask3 = new Subtask(1, subtask3.getName(), subtask3.getDescription(), subtask3.getStatus(),
                subtask3.getStartTime(), subtask3.getDuration());
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        epic1 = taskManager.getEpic(1);

        taskManager.updateEpic(new Epic(1, "Обновлённый эпик", "Новое описание"));

        Epic savedEpic = taskManager.getEpic(1);

        assertEquals("Обновлённый эпик", savedEpic.getName(), "Название эпика не обновилось.");
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Статус эпика должен быть IN_PROGRESS.");
        assertEquals(2, savedEpic.getSubtasksID().size(), "Количество подзадач не должно измениться.");
        assertTrue(savedEpic.getSubtasksID().contains(2),
                "Вторая подзадача должна быть привязана к эпику.");
        assertTrue(savedEpic.getSubtasksID().contains(3),
                "Третья подзадача должна быть привязана к эпику.");
    }

    @Test
    void shouldRemoveAllSubtasksWhenEpicIsDeleted() {
        taskManager.addEpic(epic1);
        subtask2 = new Subtask(1, subtask2.getName(), subtask2.getDescription(), subtask2.getStatus(),
                subtask2.getStartTime(), subtask2.getDuration());
        subtask3 = new Subtask(1, subtask3.getName(), subtask3.getDescription(), subtask3.getStatus(),
                subtask3.getStartTime(), subtask3.getDuration());
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        taskManager.deleteEpic(1);

        List<Subtask> subtasks = taskManager.getSubtasksList();
        List<Epic> epics = taskManager.getEpicsList();
        assertTrue(subtasks.isEmpty(), "Список подзадач должен быть пустым.");
        assertTrue(epics.isEmpty(), "Список эпиков должен быть пустым.");
    }
}
