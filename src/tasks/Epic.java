package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksID;

    public Epic(String name, String description) {
        super(name, description);
        subtasksID = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksID() {
        return subtasksID;
    }

    public void addSubtaskID(int id) {
        subtasksID.add(id);
    }

    public void deleteSubtaskID(int id) {
        subtasksID.remove(id);
    }

    public void clearSubtaskID() {
        subtasksID.clear();
    }
}
