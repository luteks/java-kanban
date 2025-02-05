package tasks;

public class Subtask extends Task {
    private Integer epicID;

    public Subtask(String name, String description, int epicID) {
        super(name, description, TaskStatus.NEW);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, TaskStatus status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, int id, TaskStatus status, int epicID) {
        super(name, description, id, status);
        this.epicID = epicID;
    }


    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d",
                getID(), TaskType.SUBTASK, getName(),
                getStatus(), getDescription(), getEpicID());
    }
}
