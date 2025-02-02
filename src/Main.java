import TaskManagers.*;
import Tasks.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("задача 1", "описание 1");
        Task task2 = new Task("задача 2", "описание 2");

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("эпик 1", "описание 1");
        Epic epic2 = new Epic("эпик 2", "описание 2");

        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("подзадача 1", "описание 1", epic1.getID());
        Subtask subtask2 = new Subtask("подзадача 2", "описание 2", epic1.getID());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.addEpic(epic2);

        Subtask subtask3 = new Subtask("подзадача 3", "описание 3", epic1.getID());

        taskManager.addSubtask(subtask3);

        Task task3 = new Task("Задача 21", "12313", task2.getID(), TaskStatus.IN_PROGRESS );
        taskManager.updateTask(task3);

        Subtask subtask4 = new Subtask("Подзадача 123", "222222222", subtask1.getID(),
                TaskStatus.IN_PROGRESS, epic1.getID());
        taskManager.updateSubtask(subtask3);

        taskManager.getTask(task1.getID());
        taskManager.getSubtask(subtask4.getID());
        taskManager.getEpic(epic1.getID());

        System.out.println("Задачи:");
        for (Task task : taskManager.getTasksList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : taskManager.getEpicsList()) {
            System.out.println(epic);

            for (Task task : taskManager.getEpicSubtasksList(epic.getID())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.getSubtasksList()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
