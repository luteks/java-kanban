import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("задача 1", "описание 1");
        Task task2 = new Task("задача 2", "описание 2");

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("эпик 1", "описание 1");
        Epic epic2 = new Epic("эпик 2", "описание 2");

        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("подзадача 1", "описание 1", epic1.getUnicID());
        Subtask subtask2 = new Subtask("подзадача 2", "описание 2", epic1.getUnicID());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.addEpic(epic2);

        Subtask subtask3 = new Subtask("подзадача 3", "описание 3", epic1.getUnicID());

        taskManager.addSubtask(subtask3);

        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());
        System.out.println();

        Task task3 = new Task("Задача 21", "12313", task2.getUnicID(), TaskStatus.IN_PROGRESS );
        taskManager.updateTask(task3);

        Subtask subtask4 = new Subtask("Подзадача 123", "222222222", subtask1.getUnicID(),
                TaskStatus.IN_PROGRESS, epic1.getUnicID());
        taskManager.updateSubtask(subtask3);

        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());
        System.out.println();

        taskManager.deleteTaskByID(task1.getUnicID());
        taskManager.deleteEpicByID(epic2.getUnicID());

        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());
        System.out.println();
    }
}
