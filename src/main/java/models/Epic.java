package main.java.models;

import main.java.enums.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks;

    protected Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
        this.subtasks = new ArrayList<>();
    }

    public Epic(String name, String description) {
        this(0, name, description);
    }

    public static Epic createWithId(int id, String name, String description) {
        return new Epic(id, name, description);
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
    }

    @Override
    public Epic copy() {
        Epic copy = Epic.createWithId(this.getId(), this.getName(), this.getDescription());
        copy.setTaskStatus(this.getTaskStatus());
        return copy;
    }
}


