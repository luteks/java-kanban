package taskManagers;

import tasks.Task;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    ArrayList<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>(10);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        if (history.size() == 10) {
            history.remove(0);
        }
        history.add(task);
    }

    }
