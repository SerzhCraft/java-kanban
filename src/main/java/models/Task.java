package main.java.models;

import main.java.enums.TaskStatus;
import main.java.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus taskStatus;
    private Duration duration;
    private LocalDateTime startTime;

    protected Task(int id,
                   String name,
                   String description,
                   TaskStatus status,
                   Duration duration,
                   LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskStatus = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description) {
        this(0, name, description, TaskStatus.NEW, Duration.ZERO, LocalDateTime.now());
    }

    public static Task createWithId(int id, String name, String description) {
        return new Task(id, name, description, TaskStatus.NEW, Duration.ZERO, LocalDateTime.now());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Task copy() {
        Task copy = Task.createWithId(this.id, this.name, this.description);
        copy.setTaskStatus(this.taskStatus);
        return copy;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", duration=" + duration.toMinutes() + " minutes" +
                ", startTime=" + startTime +
                '}';
    }
}