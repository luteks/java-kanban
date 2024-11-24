import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getTasksList();

    ArrayList<Task> getEpicsList();

    ArrayList<Task> getSubtasksList();

    void clearTasksList();

    void clearEpicsList();

    void clearSubtasksList();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    ArrayList<Subtask> getEpicSubtasksList(int id);

    ArrayList<Task> getHistory();

    
}
