package main.java.models;

import main.java.enums.TaskStatus;
import main.java.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        boolean isAllDone = true;
        boolean isAllNew = true;

        for (Subtask subtask : subtasks) {
            if (subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                setTaskStatus(TaskStatus.IN_PROGRESS);
                return;
            }
            if (subtask.getTaskStatus() != TaskStatus.DONE) {
                isAllDone = false;
            }
            if (subtask.getTaskStatus() != TaskStatus.NEW) {
                isAllNew = false;
            }
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

        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Subtask subtask : subtasks) {
            totalDuration = totalDuration.plus(subtask.getDuration());
            if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                earliestStart = subtask.getStartTime();
            }
            if (latestEnd == null || subtask.getEndTime().isAfter(latestEnd)) {
                latestEnd = subtask.getEndTime();
            }
        }

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


