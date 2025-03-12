import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
        this.setTaskStatus(TaskStatus.NEW);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean isAllDone = true;
        boolean isAllNew = true;
        for (Subtask subtask : subtasks) {
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

}


