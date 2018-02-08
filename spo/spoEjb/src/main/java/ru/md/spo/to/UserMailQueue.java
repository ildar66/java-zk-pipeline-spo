package ru.md.spo.to;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.md.pup.dbobjects.TaskInfoJPA;

/**
 * Created by Andrey Pavlenko on 13.02.2017.
 */
public class UserMailQueue {
    private HashMap<Long, List<TaskInfoJPA>> queue;

    public UserMailQueue() {
        this.queue = new HashMap<Long, List<TaskInfoJPA>>();
    }
    public void put(Long userid, TaskInfoJPA task){
        touch(userid);
        queue.get(userid).add(task);
    }
    private void touch(Long userid){
        if (!queue.containsKey(userid))
            queue.put(userid, new ArrayList<TaskInfoJPA>());
    }
    public int queueSize() {
        return queue.size();
    }

    public HashMap<Long, List<TaskInfoJPA>> getQueue() {
        return queue;
    }
}
