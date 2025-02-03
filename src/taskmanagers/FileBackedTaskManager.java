package taskmanagers;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String SAVE_DIR = "src/save";
    private static final String SAVE_FILE = "save.csv";
    private final File saveFile;

    public FileBackedTaskManager(Path savePath) {
        super();
        try {
            if (savePath == null || !Files.exists(savePath)) {
                Path dir = Paths.get(SAVE_DIR);
                Path defaultFile = dir.resolve(SAVE_FILE);

                if (!Files.exists(defaultFile)) {
                    Files.createFile(defaultFile);
                    System.out.println("Создан файл сохранения: " + defaultFile.toAbsolutePath());
                }
                this.saveFile = defaultFile.toFile();

            } else {
                this.saveFile = savePath.toFile();
            }
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при создании файла сохранения.", exception);
        }
    }

    private FileBackedTaskManager(File saveFile) {
        super();
        this.saveFile = saveFile;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            for (Task task : getTasksList()) {
                writer.write(task.toString() + "/n");
            }

            for (Epic epic : getEpicsList()) {
                writer.write(epic.toString() + "/n");
            }

            for (Subtask subtask : getSubtasksList()) {
                writer.write(subtask.toString() + "/n");
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл.", exception);
        }
    }

    static FileBackedTaskManager loadFromFile(File file) {
        if (file == null) {
            System.out.println("Файл сохранения отсутствует.");
            return null;
        }

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String string = bufferedReader.readLine().trim();

                if (!string.isEmpty()) {
                    Task task = fileBackedTaskManager.fromString(string);

                    switch (task) {
                        case null -> {
                            System.out.println("Ошибка. Задача пустая");
                            return null;
                        }
                        case Epic epic -> fileBackedTaskManager.addEpicFromFile(epic);
                        case Subtask subtask -> fileBackedTaskManager.addSubtaskFromFile(subtask);
                        default -> fileBackedTaskManager.addTaskFromFile(task);
                    }
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при загрузке данных из файла", exception);
        }

        return fileBackedTaskManager;
    }

    private Task fromString(String value) {
        String[] fields = value.split(",");
        TaskType tasksTypes = TaskType.valueOf(fields[1]);

        return switch (tasksTypes) {
            case TASK -> parseTask(fields);
            case EPIC -> parseEpic(fields);
            case SUBTASK -> parseSubtask(fields);
        };
    }

    private Task parseTask(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        return new Task(name, description, id, status);
    }

    private Epic parseEpic(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        Epic epic = new Epic(name, description);
        epic.setID(id);
        epic.setStatus(status);
        return epic;
    }

    private Subtask parseSubtask(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        int idEpic = Integer.parseInt(fields[5]);
        Subtask subtask = new Subtask(name, description, status, idEpic);
        subtask.setID(id);
        return subtask;
    }

    public void addTaskFromFile(Task task) {
        int taskId = task.getID();
        Task taskCopy = new Task(task.getName(), task.getDescription(), task.getStatus());
        taskCopy.setID(taskId);
        tasks.put(taskId, taskCopy);

        if (taskCopy.getID() >= idCount) {
            idCount = taskCopy.getID() + 1;
        }

        System.out.println("Таск загружен: " + taskCopy);
    }

    public void addEpicFromFile(Epic epic) {
        int epicID = epic.getID();
        Epic epicCopy = new Epic(epic.getName(), epic.getDescription());
        epicCopy.setID(epicID);
        epics.put(epicID, epicCopy);

        if (epicCopy.getID() >= idCount) {
            idCount = epicCopy.getID() + 1;
        }

        System.out.println("Эпик загружен: " + epicCopy);
    }

    public void addSubtaskFromFile(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicID());

        int subtaskID = subtask.getID();
        Subtask subtaskCopy =
                new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicID());
        subtaskCopy.setID(subtaskID);
        subtasks.put(subtaskID, subtaskCopy);
        epic.addSubtaskID(subtaskID);
        checkEpicStatus(epic.getID());

        if (subtaskCopy.getID() >= idCount) {
            idCount = subtaskCopy.getID() + 1;
        }

        System.out.println("Сабтаск загружен: " + subtaskCopy);
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void clearTasksList() {
        super.clearTasksList();
        save();
    }

    @Override
    public void clearEpicsList() {
        super.clearEpicsList();
        save();
    }

    @Override
    public void clearSubtasksList() {
        super.clearSubtasksList();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }
}
