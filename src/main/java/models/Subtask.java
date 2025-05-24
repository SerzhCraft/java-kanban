package main.java.models;

import main.java.enums.TaskStatus;
import main.java.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;

    protected Subtask(int id, String name, String description, Epic epic, Duration duration, LocalDateTime startTime) {
        super(id, name, description, TaskStatus.NEW, duration, startTime);
        setEpic(epic);
    }

    public Subtask(String name, String description, Epic epic) {
        this(0, name, description, epic, Duration.ZERO, LocalDateTime.now());
    }

    public static Subtask createWithId(int id,
                                       String name,
                                       String description,
                                       Epic epic,
                                       Duration duration,
                                       LocalDateTime startTime) {
        return new Subtask(id, name, description, epic, duration, startTime);
    }

    public Epic getEpic() {
        return epic;
    }

    public int getEpicId() {
        return (epic != null) ? epic.getId() : -1;
    }

    public void setEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        if (epic.equals(this)) {
            return;
        }
        this.epic = epic;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public Subtask copy() {
        Subtask copy = Subtask.createWithId(this.getId(),
                this.getName(),
                this.getDescription(),
                this.epic,
                this.getDuration(),
                this.getStartTime());
        copy.setTaskStatus(this.getTaskStatus());
        return copy;
    }

    @Override
    public String toString() {
        return "Subtask{" + super.toString() +
                "epic=" + (epic != null ? epic.getName() : "null") +
                '}';
    }
}
