package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksID = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, null, null);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW, null, null);
    }

    public Epic(int id, String name, String description, TaskStatus status,
                LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
    }

    public ArrayList<Integer> getSubtasksID() {
        return new ArrayList<>(subtasksID);
    }

    public void addSubtaskID(Integer id) {
        subtasksID.add(id);
    }

    public void deleteSubtaskID(Integer id) {
        subtasksID.remove(id);
    }

    public void clearSubtaskID() {
        subtasksID.clear();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s",
                getID(), TaskType.EPIC, getName(), getStatus(), getDescription(), getStartTime(), getDuration());
    }
}
