import java.util.ArrayList;
public class Epic extends Task {
    private ArrayList<Integer> subTasksIds = new ArrayList<>();


    public Epic(String title, String description, int id) {
        super(title, description, id);
    }
    public Epic(String title, String description) {
        super(title, description);
    }

    public ArrayList<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setSubTasksIds(Integer subTasksId) {
        this.subTasksIds.add(subTasksId);
    }
    public void setSubTasksIds(ArrayList<Integer> subTasksIds) {
        this.subTasksIds = subTasksIds;
    }

    public void clearSubtasksIds() {
        subTasksIds.clear();
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
