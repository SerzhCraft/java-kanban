package main.java.models;

import main.java.enums.TaskStatus;
import main.java.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Subtask> subtasks;

    protected Epic(int id, String name, String description, Duration duration, LocalDateTime startTime) {
        super(id, name, description, TaskStatus.NEW, duration, startTime);
        this.subtasks = new ArrayList<>();
    }

    public Epic(String name, String description, Duration duration, LocalDateTime startTime) {
        this(0, name, description, duration, startTime);
    }

    public static Epic createWithId(int id,
                                    String name,
                                    String description,
                                    Duration duration,
                                    LocalDateTime startTime) {
        return new Epic(id, name, description, duration, startTime);
    }


    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    public void addSubtask(Subtask subtask) {
        if (subtask == null || subtask.getEpic() != this) {
            return;
        }
        subtasks.add(subtask);
        updateStatus();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStatus();
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean isAllDone = subtasks.stream()
                .allMatch(subtask -> subtask.getTaskStatus() == TaskStatus.DONE);
        boolean isAllNew = subtasks.stream()
                .allMatch(subtask -> subtask.getTaskStatus() == TaskStatus.NEW);

        if (subtasks.stream().
                anyMatch(subtask -> subtask.getTaskStatus() == TaskStatus.IN_PROGRESS)) {
            setTaskStatus(TaskStatus.IN_PROGRESS);
            return;
        }
        if (isAllNew) {
            setTaskStatus(TaskStatus.NEW);
        } else if (isAllDone) {
            setTaskStatus(TaskStatus.DONE);
        } else {
            setTaskStatus(TaskStatus.IN_PROGRESS);
        }
        updateDurationAndStartEndTimes();
    }

    private void updateDurationAndStartEndTimes() {
        if (subtasks.isEmpty()) {
            setDuration(Duration.ZERO);
            setStartTime(null);
            return;
        }

        Duration totalDuration = subtasks.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        LocalDateTime earliestStart = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latestEnd = subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        setDuration(totalDuration);
        setStartTime(earliestStart);
    }

    @Override
    public LocalDateTime getEndTime() {
        return getStartTime().plus(getDuration());
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public Epic copy() {
        Epic copy = Epic.createWithId(this.getId(),
                this.getName(),
                this.getDescription(),
                this.getDuration(),
                this.getStartTime());
        copy.setTaskStatus(this.getTaskStatus());
        return copy;
    }
}