package taskmanagers;

import tasks.*;

import java.util.List;

public interface TaskManager {
    List<Task> getTasksList();

    List<Task> getEpicsList();

    List<Task> getSubtasksList();

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

    List<Subtask> getEpicSubtasksList(int id);

    List<Task> getHistory();

}
