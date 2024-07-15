package Task;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String title, String description, int id, Status status, int epicId) {
        super(title, description, id, status);
        this.epicId = epicId;
    }
    public Subtask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }
    public int getEpicId() {
        return epicId;
    }
    @Override
    public String toString() {
        String result = "Подзадача {" +
                "title='" +super.getTitle() + '\'';

        if(super.getDescription() != null) {
            result = result + ", description.length=" + super.getDescription().length();
        } else {
            result = result + ", description=null";
        }

        return result + ", status=" + super.getStatus() + '}';
    }
}
