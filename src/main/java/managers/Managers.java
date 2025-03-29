package main.java.managers;

import main.java.models.Task;

public class Managers<T extends Task> {

    public static TaskManager<Task> getDefault() {
        return new InMemoryTaskManager<>();
    }

    public static HistoryManager<Task> getDefaultHistory() {
        return new InMemoryHistoryManager<>();
    }

}
