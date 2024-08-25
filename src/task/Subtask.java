package task;

public class Subtask extends Task {

    private final int epicId;
    private Type type;

    public Subtask(String title, String description, int id, Status status, int epicId) {
        super(title, description, id, status);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public Subtask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    @Override
    public Type getType() {
        return type;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        String result = "Подзадача {" +
                "title='" + super.getTitle() + '\'';

        if (super.getDescription() != null) {
            result = result + ", description.length=" + super.getDescription().length();
        } else {
            result = result + ", description=null";
        }

        return result + ", status=" + super.getStatus() + '}';
    }

    @Override
    public String toStringFromFile() {
        String result = String.format("%s,%s,%s,%s,%s,%s\n", getId(), getType(), getTitle(), getStatus(), getDescription(), getEpicId());
        return result;
    }

}
