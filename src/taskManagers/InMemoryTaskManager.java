package taskManagers;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private InMemoryHistoryManager historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
    private int idCount = 1;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

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
            checkEpicStatus(epic.getID());
        }
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add((Task) epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add((Task) subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void addTask(Task task) {
        task.setID(idCount);
        tasks.put(idCount, task);
        idCount++;
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setID(idCount);
        epics.put(idCount, epic);
        idCount++;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (!(epics.isEmpty()) || epics.containsKey(subtask.getEpicID())) {
            subtask.setID(idCount);
            subtasks.put(idCount, subtask);

            Epic epic = epics.get(subtask.getEpicID());

            epic.addSubtaskID(idCount);
            checkEpicStatus(epic.getID());
            idCount++;
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getID())) {
            tasks.put(task.getID(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getID())){
            Epic epicReq = epics.get(epic.getID());

            epicReq.setName(epic.getName());
            epicReq.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getID())){
            if (subtasks.get(subtask.getID()).getEpicID() == subtask.getEpicID()){
                subtasks.put(subtask.getID(), subtask);
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
            checkEpicStatus(epic.getID());
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
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
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



}
