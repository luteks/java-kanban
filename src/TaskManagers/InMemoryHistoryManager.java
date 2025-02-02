package TaskManagers;

import Tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    private final Map<Integer, Node> taskNodeMap = new HashMap<>();

    private Node head;
    private Node tail;


    private Node linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }

        return newNode;
    }

    private void removeNode(Node node) {
        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        node.item = null;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node currentNode = head;

        while (currentNode != null) {
            tasks.add(currentNode.item);
            currentNode = currentNode.next;
        }

        return tasks;
    }


    @Override
    public ArrayList<Task> getHistory() {

        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            System.out.println("Задачи не существует.");
            return;
        }

        remove(task.getID());
        Node newNode = linkLast(task);
        taskNodeMap.put(task.getID(), newNode);
    }

    @Override
    public void remove(int id) {
        Node node = taskNodeMap.get(id);

        if (node != null) {
            removeNode(node);
        }
    }

}
