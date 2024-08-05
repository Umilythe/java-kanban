package Manager;

import Manager.HistoryManager;
import Task.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import Task.Node;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node<Task>> historyTable = new HashMap<>();
    private Node<Task> tail;
    private Node<Task> head;


    @Override
    public void add(Task task) {
        if (task != null) {
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(historyTable.get(id));
    }


    public void removeNode(Node<Task> node) {
        if (node != null) {
            final Node<Task> nodeAfter = node.getNext();
            final Node<Task> nodeBefore = node.getPrevious();
            historyTable.remove(node.getData().getId());
            if (nodeAfter != null && nodeBefore != null) {
                nodeBefore.setNext(nodeAfter);
                nodeAfter.setPrevious(nodeBefore);
            } else if (nodeBefore != null && nodeAfter == null) {
                nodeBefore.setNext(null);
                tail = nodeBefore;
            } else if (nodeBefore == null && nodeAfter != null) {
                nodeAfter.setPrevious(null);
                head = nodeAfter;
            } else {
                head = null;
                tail = null;
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public void linkLast(Task task) {
        if (historyTable.containsKey(task.getId())) {
            removeNode(historyTable.get(task.getId()));
        }
        Node<Task> exTail = tail;
        Node<Task> newNode = new Node<Task>(task, tail, null);
        tail = newNode;
        historyTable.put(task.getId(), newNode);
        if (exTail == null) {
            head = newNode;
        } else {
            exTail.setNext(newNode);
        }

    }

    public List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        Node<Task> thisNode = head;
        while (thisNode != null) {
            tasksList.add(thisNode.getData());
            thisNode = thisNode.getNext();
        }
        return tasksList;

    }
}
