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
        String result =  "Subtask{" +
                "epicId=" + epicID +
                ", id=" + getID() +
                ", name='" + getName() + '\'';
        if (getDescription() != null) {
            result = result + ", description.length='" + getDescription().length();
        } else {
            result = result + ", description=null";
        }
        return result + ", status=" + getStatus() +
                '}';
    }
}
