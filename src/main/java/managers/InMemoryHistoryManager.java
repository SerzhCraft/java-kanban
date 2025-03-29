package main.java.managers;

import main.java.models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T> {
    public static final int MAX_HISTORY_SIZE = 10;
    private final List<T> history = new ArrayList<>(MAX_HISTORY_SIZE);

    @Override
    public void add(T task) {
        if (task == null) return;

        if (history.size() >= MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
        history.addLast(task);
    }

    @Override
    public List<T> getHistory() {
        return new ArrayList<>(history);
    }

}
