package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksID;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subtasksID = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, id, TaskStatus.NEW);
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

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s",
                getID(), TaskType.EPIC, getName(), getStatus(), getDescription());
    }
}
