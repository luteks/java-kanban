import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager<T extends Task> implements TaskManager {
    private int taskID = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private ArrayList<Task> history = new ArrayList<>();

    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Task> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Task> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearTasksList() {
        tasks.clear();
    }

    @Override
    public void clearEpicsList() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasksList() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskID();
            checkEpicStatus(epic.getUnicID());
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        addHistory((T) task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic task = epics.get(id);
        addHistory((T) task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask task = subtasks.get(id);
        addHistory((T) task);
        return task;
    }

    @Override
    public void addTask(Task task) {
        task.setUnicID(taskID);
        tasks.put(taskID, task);
        taskID++;
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setUnicID(taskID);
        epics.put(taskID, epic);
        taskID++;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (!(epics.isEmpty()) || epics.containsKey(subtask.getEpicID())) {
            subtask.setUnicID(taskID);
            subtasks.put(taskID, subtask);

            Epic epic = epics.get(subtask.getEpicID());

            epic.addSubtaskID(taskID);
            checkEpicStatus(epic.getUnicID());
            taskID++;
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getUnicID())) {
            tasks.put(task.getUnicID(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getUnicID())){
            Epic epicReq = epics.get(epic.getUnicID());

            epicReq.setName(epic.getName());
            epicReq.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getUnicID())){
            if (subtasks.get(subtask.getUnicID()).getEpicID() == subtask.getEpicID()){
                subtasks.put(subtask.getUnicID(), subtask);
                checkEpicStatus(subtask.getEpicID());
            }
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id){
        Epic epic = epics.get(id);

        if (epic != null){
            for (int subtaskID : epic.getSubtasksID()) {
                subtasks.remove(subtaskID);
            }
            epics.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getEpicID());

            epic.deleteSubtaskID(id);
            subtasks.remove(id);
            checkEpicStatus(epic.getUnicID());
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasksList(int id) {
        Epic epic = epics.get(id);
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();

        if (epic != null) {
            for (int subtaskID : epic.getSubtasksID()) {
                Subtask subtask = subtasks.get(subtaskID);
                epicSubtasks.add(subtask);
            }
        }

        return epicSubtasks;
    }

    @Override
    public ArrayList<Task> getHistory(){
        return history;
    }

    private void addHistory(T task) {
        if (history.size() > 9) {
            history.remove(0);
            history.add(task);
        } else {
            history.add(task);
        }
    }

    private void checkEpicStatus(int id) {
        ArrayList<TaskStatus> statuses = checkEpicSubtasksStatues(getEpicSubtasksList(id));

        if (statuses.size() == 1 && statuses.contains(TaskStatus.NEW) || statuses.isEmpty()) {
            epics.get(id).setStatus(TaskStatus.NEW);
        } else if (statuses.size() == 1 && statuses.contains(TaskStatus.DONE)) {
            epics.get(id).setStatus(TaskStatus.DONE);
        } else {
            epics.get(id).setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private ArrayList<TaskStatus> checkEpicSubtasksStatues(ArrayList<Subtask> subtasksList) {
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
