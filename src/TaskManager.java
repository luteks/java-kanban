import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    static int taskID = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Task> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Task> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    public void clearTasksList() {
        tasks.clear();
    }

    public void clearEpicsList() {
        epics.clear();
        subtasks.clear();
    }

    public void clearSubtasksList() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskID();
            checkEpicStatus(epic.getUnicID());
        }
    }

    public Task getTaskByID(int id) {
        return tasks.get(id);
    }

    public Epic getEpicByID(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskByID(int id) {
        return subtasks.get(id);
    }

    public void addTask(Task task) {
        task.setUnicID(taskID);
        tasks.put(taskID, task);
        taskID++;
    }

    public void addEpic(Epic epic) {
        epic.setUnicID(taskID);
        epics.put(taskID, epic);
        taskID++;
    }

    public void addSubtask(Subtask subtask) {
        subtask.setUnicID(taskID);
        subtasks.put(taskID, subtask);
        Epic epic = epics.get(subtask.getEpicID());
        epic.addSubtaskID(taskID);
        checkEpicStatus(epic.getUnicID());
        taskID++;
    }

    public void updateTask(Task task) {
        tasks.put(task.getUnicID(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getUnicID(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getUnicID(), subtask);
        checkEpicStatus(subtask.getEpicID());
    }

    public void deleteTaskByID(int id) {
        tasks.remove(id);
    }

    public void deleteEpicByID(int id){
        Epic epic = epics.get(id);

        if (epic != null){
            for (int subtaskID : epic.getSubtasksID()) {
                subtasks.remove(subtaskID);
            }
            epics.remove(id);
        }
    }

    public void deleteSubtaskByID(int id) {
        Epic epic = epics.get(subtasks.get(id).getEpicID());
        epic.deleteSubtaskID(id);
        subtasks.remove(id);
        checkEpicStatus(epic.getUnicID());
    }

    public ArrayList<Subtask> getEpicSubtasksList(int id) {
        Epic epic = epics.get(id);
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();

        for (int subtaskID : epic.getSubtasksID()) {
            Subtask subtask = subtasks.get(subtaskID);
            epicSubtasks.add(subtask);
        }

        return epicSubtasks;
    }

    public void checkEpicStatus(int id) {
        ArrayList<TaskStatus> statuses = checkEpicSubtasksStatues(getEpicSubtasksList(id));

        if (statuses.size() == 1 && statuses.contains(TaskStatus.NEW) || statuses.isEmpty()) {
            epics.get(id).setStatus(TaskStatus.NEW);
        } else if (statuses.size() == 1 && statuses.contains(TaskStatus.DONE)) {
            epics.get(id).setStatus(TaskStatus.DONE);
        } else {
            epics.get(id).setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public ArrayList<TaskStatus> checkEpicSubtasksStatues(ArrayList<Subtask> subtasksList) {
        ArrayList<TaskStatus> statuses = new ArrayList<>();

        for (Subtask subtask : subtasksList) {
            TaskStatus status = subtask.getStatus();

            if (!(statuses.contains(status))) {
                statuses.add(status);
            }
        }

        return statuses;
    }
/*
Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
Методы для каждого из типа задач(Задача/Эпик/Подзадача):
 a. Получение списка всех задач.
 b. Удаление всех задач.
 c. Получение по идентификатору.
 d. Создание. Сам объект должен передаваться в качестве параметра.
 e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
 f. Удаление по идентификатору.
Дополнительные методы:
a. Получение списка всех подзадач определённого эпика.
Управление статусами осуществляется по следующему правилу:
 a. Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией о самой задаче. По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.
 b. Для эпиков:
если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
во всех остальных случаях статус должен быть IN_PROGRESS.
 */


}
