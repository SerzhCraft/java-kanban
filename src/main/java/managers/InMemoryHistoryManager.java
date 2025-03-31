package main.java.managers;

import main.java.models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager<Task> {
    public static final int MAX_HISTORY_SIZE = 10;
    private final List<Task> history = new ArrayList<>(MAX_HISTORY_SIZE);

    @Override
    public void add(Task task) {
        if (task == null) return;

        if (history.size() >= MAX_HISTORY_SIZE) {
            history.removeFirst();
        }

        Task taskCopy = task.copy();
        history.addLast(taskCopy);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

}
