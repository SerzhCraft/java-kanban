package main.java.managers;

import main.java.models.Task;

public class Managers {
    public static TaskManager<Task> getDefault() {
        return new InMemoryTaskManager<>();
    }
}
