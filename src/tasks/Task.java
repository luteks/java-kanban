package tasks;

import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int unicID;
    protected TaskStatus status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, int unicID, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.unicID = unicID;
        this.status = status;
    }

    public int getID() {
        return unicID;
    }

    public void setID(int id) {
        this.unicID = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return unicID == task.unicID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unicID);
    }

    @Override
    public String toString() {
        String result = "Task{" +
                "name='" + name + '\'' +
                ", id=" + unicID;
        if (description != null) {
            result = result + ", description.length='" + description.length();
        } else {
            result = result + ", description=null'";
        }
        result += ", status=" + status +
                '}';
        return result;
    }
}
