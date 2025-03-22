package tasks;

import com.google.gson.annotations.Expose;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    @Expose
    private final Integer epicID;

    public Subtask(int epicID, String name, String description, TaskStatus status) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public Subtask(int epicID, String name, String description, TaskStatus status,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicID = epicID;
    }

    public Subtask(int id, int epicID, String name, String description, TaskStatus status,
                   LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s",
                getID(), TaskType.SUBTASK, getName(),
                getStatus(), getDescription(), getEpicID(), getStartTime(), getDuration());
    }
}
