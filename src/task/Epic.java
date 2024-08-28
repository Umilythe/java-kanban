package task;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasksIds = new ArrayList<>();
    private Type type;

    public Epic(String title, String description, int id) {
        super(title, description, id, Status.NEW);
        this.type = Type.EPIC;
    }

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
        this.type = Type.EPIC;
    }
    public Epic(String title, String description, int id, Status status) {
        super(title, description, id, status);
        this.type = Type.EPIC;
    }

    @Override
    public Type getType() {
        return type;
    }

    public ArrayList<Integer> getSubTasksIds() {
        return new ArrayList<>(subTasksIds);
    }

    public void setSubTasksIds(Integer subTasksId) {
        this.subTasksIds.add(subTasksId);
    }

    public void clearSubtasksIds() {
        subTasksIds.clear();
    }

    public void removeOneSubtaskId(Integer subtaskId) {
        subTasksIds.remove(subtaskId);
    }

    @Override
    public String toString() {
        String result = "Эпик {" +
                "title='" + super.getTitle() + '\'';

        if (super.getDescription() != null) {
            result = result + ", description.length=" + super.getDescription().length();
        } else {
            result = result + ", description=null";
        }

        return result + ", status=" + this.getStatus() + '}';
    }


}
