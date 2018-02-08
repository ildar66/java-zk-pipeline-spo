package org.uit.director.managers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.uit.director.tasks.TaskList;

import com.vtb.domain.TaskListType;

/**
 * В этом классе списки для страницы Управление этапами.
 * @author Andrey Pavlenko
 *
 */
public class StagesDirectionManager implements Serializable {
	private static final long serialVersionUID = 1L;
	private HashMap<TaskListType,TaskList> list = new LinkedHashMap<TaskListType, TaskList>();

    public TaskList getTasksNewWork() {
        return list.get(TaskListType.NOT_ACCEPT);
    }

    public TaskList getTasksInWork() {
        return list.get(TaskListType.ACCEPT);
    }

    public HashMap<TaskListType, TaskList> getList() {
        return list;
    }

    
}
