package taskmanagers;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String SAVE_DIR = "src";
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
            writer.write("id,type,name,status,description,epic,start_time,duration");
            writer.newLine();
            for (Task task : getTasksList()) {
                writer.write(task.toString());
                writer.newLine();
            }

            for (Epic epic : getEpicsList()) {
                writer.write(epic.toString());
                writer.newLine();
            }

            for (Subtask subtask : getSubtasksList()) {
                writer.write(subtask.toString());
                writer.newLine();
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл.", exception);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (file == null) {
            throw new NullPointerException("Файл не существует.");
        } else {

            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                bufferedReader.readLine();
                while (bufferedReader.ready()) {
                    String string = bufferedReader.readLine().trim();

                    if (!string.isEmpty()) {
                        Task task = fileBackedTaskManager.fromString(string);

                        switch (task) {
                            case null -> {
                                System.out.println("Ошибка. Задача пустая");
                                return null;
                            }
                            case Epic epic -> fileBackedTaskManager.addEpic(epic);
                            case Subtask subtask -> fileBackedTaskManager.addSubtask(subtask);
                            default -> fileBackedTaskManager.addTask(task);
                        }
                    }
                }

            } catch (IOException exception) {
                throw new RuntimeException("Ошибка при загрузке данных из файла", exception);
            }

            return fileBackedTaskManager;
        }
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
        LocalDateTime startTime = LocalDateTime.parse(fields[5]);
        Duration duration = Duration.parse(fields[6]);
        return new Task(id, name, description, status, startTime, duration);
    }

    private Epic parseEpic(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        Epic epic = new Epic(id, name, description);
        epic.setStatus(status);
        return epic;
    }

    private Subtask parseSubtask(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        int idEpic = Integer.parseInt(fields[5]);
        LocalDateTime startTime = LocalDateTime.parse(fields[6]);
        Duration duration = Duration.parse(fields[7]);
        return new Subtask(id, idEpic, name, description, status, startTime, duration);
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

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }
}
