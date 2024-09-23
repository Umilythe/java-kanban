package task;

import com.google.gson.annotations.Expose;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    @Expose
    private String title;
    @Expose
    private String description;
    @Expose
    private int id;
    @Expose
    private Status status;
    private Type type;
    @Expose
    private long duration;
    @Expose
    private LocalDateTime startTime;

    public Task(String title, String description, int id, Status status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.type = Type.TASK;
    }

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = Type.TASK;
    }

    public Task(String title, String description, int id, Status status, long durationInMinutes, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.type = Type.TASK;
        this.duration = durationInMinutes;
        this.startTime = startTime;
    }

    public Task(String title, String description, Status status, long durationInMinutes, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = Type.TASK;
        this.duration = durationInMinutes;
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            LocalDateTime endTime = startTime.plus(Duration.ofMinutes(duration));
            return endTime;
        } else {
            return null;
        }
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {

        return status;
    }

    public void setStatus(Status status) {

        this.status = status;
    }

    @Override
    public String toString() {
        String result = "Задание {" +
                "title='" + title + '\'';

        if (description != null) {
            result = result + ", description.length=" + description.length();
        } else {
            result = result + ", description=null";
        }

        return result + ", status=" + status + '}';
    }

    public String toStringFromFile() {
        String result;
        if ((getStartTime() != null) && (getDuration() != 0)) {
            result = String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", getId(), getType(), getTitle(), getStatus(), getDescription(), getDuration(), getStartTime(), " ");
        } else {
            result = String.format("%s,%s,%s,%s,%s,%s\n", getId(), getType(), getTitle(), getStatus(), getDescription(), " ");
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
