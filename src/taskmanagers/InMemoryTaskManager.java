package taskmanagers;

import exceptions.TaskIntersectionException;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    private final Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int idCount = 1;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearTasksList() {
        for (Task task : tasks.values()) {
            removePrioritizedTask(task);
        }
        tasks.clear();
    }

    @Override
    public void clearEpicsList() {
        clearSubtasksList();
        epics.clear();
    }

    @Override
    public void clearSubtasksList() {
        for (Epic epic : epics.values()) {
            for (Integer subtaskId : epic.getSubtasksID()) {
                epic.deleteSubtaskID(subtaskId);
                removePrioritizedTask(subtasks.get(subtaskId));
            }
            checkEpicStatus(epic.getID());
        }

        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);

        if (task == null) {
            return null;
        }

        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }

        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return null;
        }

        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void addTask(Task task) {
        if (isIntersectionTasks(task)) {
            throw new TaskIntersectionException(
                    "Задача \"" + task.getName() + "\" пересекается по времени с другой задачей!");
        }

        int taskId;
        if (task.getID() <= 0) {
            taskId = idCount++;
        } else {
            taskId = task.getID();
        }

        task = new Task(taskId, task.getName(), task.getDescription(),
                    task.getStatus(), task.getStartTime(), task.getDuration());


        if (task.getStartTime() != null) {
            addToPrioritizedTasks(task);
        }

        tasks.put(taskId, task);
    }

    @Override
    public void addEpic(Epic epic) {
        int epicId;
        if (epic.getID() <= 0) {
            epicId = idCount++;
        } else {
            epicId = epic.getID();
        }
        epic = new Epic(epicId, epic.getName(), epic.getDescription());
        epics.put(epicId, epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (isIntersectionTasks(subtask)) {
            throw new TaskIntersectionException(
                    "Задача \"" + subtask.getName() + "\" пересекается по времени с другой задачей!");
        }

        Epic epic = epics.get(subtask.getEpicID());
        if (epic == null) {
            return;
        }

        if (epic.getID() == subtask.getID()) {
            return;
        }

        if (subtask.getEpicID() == subtask.getID()) {
            return;
        }

        int subtaskId;
        if (subtask.getID() <= 0) {
            subtaskId = idCount++;
        } else {
            subtaskId = subtask.getID();
        }
        subtask = new Subtask(subtaskId, subtask.getEpicID(), subtask.getName(), subtask.getDescription(),
                    subtask.getStatus(), subtask.getStartTime(), subtask.getDuration());

        subtasks.put(subtaskId, subtask);
        epic.addSubtaskID(subtaskId);
        checkEpicStatus(epic.getID());

        if (subtask.getEndTime() != null) {
            addToPrioritizedTasks(subtask);
        }
    }

    @Override
    public void updateTask(Task newTask) {
        Task oldTask = tasks.get(newTask.getID());
        if (oldTask == null) {
            return;
        }

        if (isIntersectionTasks(newTask)) {
            throw new TaskIntersectionException(
                    "Задача \"" + newTask.getName() + "\" пересекается по времени с другой задачей!");
        }

        removePrioritizedTask(tasks.get(newTask.getID()));
        addToPrioritizedTasks(newTask);
        tasks.put(newTask.getID(), newTask);
    }

    @Override
    public void updateEpic(Epic newEpic) {
        Epic oldEpic = epics.get(newEpic.getID());
        if (oldEpic == null) {
            return;
        }

        Epic updatedEpic = new Epic(newEpic.getID(), newEpic.getName(), newEpic.getDescription(),
                oldEpic.getStatus(), oldEpic.getStartTime(), oldEpic.getDuration());

        for (int subtaskId : oldEpic.getSubtasksID()) {
            updatedEpic.addSubtaskID(subtaskId);
        }

        epics.put(oldEpic.getID(), updatedEpic);
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {

        Subtask oldSubtask = subtasks.get(newSubtask.getID());
        if (oldSubtask == null) {
            return;
        }

        if (isIntersectionTasks(newSubtask)) {
            throw new TaskIntersectionException(
                    "Задача \"" + newSubtask.getName() + "\" пересекается по времени с другой задачей!");
        }

        removePrioritizedTask(oldSubtask);
        addToPrioritizedTasks(newSubtask);
        subtasks.put(newSubtask.getID(), newSubtask);

        Epic epic = epics.get(newSubtask.getEpicID());
        if (epic != null) {
            checkEpicStatus(epic.getID());
        }
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        historyManager.remove(id);
        removePrioritizedTask(task);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) return;

        epic.getSubtasksID().forEach(this::deleteSubtask);

        String name = epics.get(id).getName();
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }

        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            historyManager.remove(id);
            removePrioritizedTask(subtask);
            epic.deleteSubtaskID(id);
            checkEpicStatus(epic.getID());
        }
    }

    @Override
    public List<Subtask> getEpicSubtasksList(int id) {
        Epic epic = epics.get(id);

        if (epic == null) {
            return new ArrayList<>();
        }

        return epic.getSubtasksID().stream()
                .filter(subtasks::containsKey)
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) historyManager.getHistory();
    }


    protected void checkEpicStatus(int id) {
        List<TaskStatus> statuses = checkEpicSubtasksStatues(getEpicSubtasksList(id));

        if (statuses.size() == 1 && statuses.contains(TaskStatus.NEW) || statuses.isEmpty()) {
            epics.get(id).setStatus(TaskStatus.NEW);
        } else if (statuses.size() == 1 && statuses.contains(TaskStatus.DONE)) {
            epics.get(id).setStatus(TaskStatus.DONE);
        } else {
            epics.get(id).setStatus(TaskStatus.IN_PROGRESS);
        }

        calculateEpicTimeParameters(epics.get(id));
    }

    private void calculateEpicTimeParameters(Epic epic) {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime startTime = LocalDateTime.MAX;
        LocalDateTime endTime = LocalDateTime.MIN;

        for (int subtaskId : epic.getSubtasksID()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            LocalDateTime subtaskEndTime = subtask.getStartTime().plus(subtask.getDuration());
            if (subtaskEndTime.isAfter(endTime)) {
                endTime = subtaskEndTime;
            }
            totalDuration = totalDuration.plus(subtask.getDuration());
        }

        startTime = startTime.equals(LocalDateTime.MAX) ? null : startTime;

        Epic updEpic = new Epic(epic.getID(), epic.getName(), epic.getDescription(), epic.getStatus(), startTime,
                totalDuration);

        for (int subtaskId : epic.getSubtasksID()) {
            updEpic.addSubtaskID(subtaskId);
        }

        epics.put(epic.getID(), updEpic);
    }

    private List<TaskStatus> checkEpicSubtasksStatues(List<Subtask> subtasksList) {
        ArrayList<TaskStatus> statuses = new ArrayList<>();

        for (Subtask subtask : subtasksList) {
            TaskStatus status = subtask.getStatus();

            if (!(statuses.contains(status))) {
                statuses.add(status);
            }
        }

        return statuses;
    }


    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removePrioritizedTask(Task task) {
        prioritizedTasks.remove(task);
    }

    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }

    private boolean isIntersectionTasks(Task newTask) {
        if (newTask.getStartTime() == null) {
            throw new TaskIntersectionException("В задаче отсутствует время начала или окончания.");
        }

        int idAddedTask = newTask.getID();
        return getPrioritizedTasks().stream()
                .filter(task -> task.getID() != idAddedTask)
                .anyMatch(prioritizedTask -> {
                    LocalDateTime startTime = prioritizedTask.getStartTime();
                    LocalDateTime endTime = prioritizedTask.getEndTime();
                    return newTask.getStartTime().isBefore(endTime) &&
                            newTask.getEndTime().isAfter(startTime);
                });
    }

    private static final Comparator<Task> taskComparator = (o1, o2) -> {
        if (!o1.getStartTime().equals(o2.getStartTime())) {
            return o1.getStartTime().compareTo(o2.getStartTime());
        }

        return Integer.compare(o1.getID(), o2.getID());
    };
}
