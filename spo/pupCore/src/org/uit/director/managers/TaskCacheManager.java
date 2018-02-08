package org.uit.director.managers;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.uit.director.tasks.TaskInfo;

import ru.md.spo.util.Config;


public class TaskCacheManager extends CachManager{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    long timeToDelete;

    public TaskCacheManager() {
        timeToDelete = 60000 * Long.parseLong(Config.getProperty("CACHE_TIMELIFE"));

    }
    


    public int deleteOldCache() {

        int countClear = 0;

        if (cache != null) {
            Iterator it = cache.keySet().iterator();
            List keysToDelete = new ArrayList();

            while (it.hasNext()) {
                Long key = (Long) it.next();
                TaskInfo taskInf = cache.get(key);
                long timeNow = Calendar.getInstance().getTimeInMillis();
//                System.out.println("diff="+ (timeNow - taskInf.getTimeToLife()) + "; life=" + timeToDelete);
                if ((timeNow - taskInf.getTimeToLife()) > timeToDelete) {
					keysToDelete.add(key);
				}


            }

            countClear = keysToDelete.size();

            for (int i = 0; i < countClear; i++) {
                //deleteCacheElement(keysToDelete.get(i));
                deleteCacheElement((Long)keysToDelete.get(i));

            }
        }
        return countClear;

    }

   
    
}
