package org.uit.director.threads;

import java.io.Serializable;

import org.uit.director.managers.TaskCacheManager;

import ru.md.spo.util.Config;

/**
 * Created by IntelliJ IDEA.
 * User: pd190390
 * Date: 03.10.2005
 * Time: 9:13:02
 * To change this template use File | Settings | File Templates.
 */
public class CacheAutoDelete extends Thread implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8701771353900331617L;
	private TaskCacheManager cacheManager;
    long timeToSleep = 60000 * Long.parseLong(Config.getProperty("CACHE_INTERVAL_TO_DELETE"));
    boolean flag = false;

    public CacheAutoDelete(TaskCacheManager manager) {
        cacheManager = manager;
        this.setName("Cache auto delete");

    }

    public void stopLoop() {
        flag = true;
        cacheManager.deleteOldCache();
        cacheManager = null;

    }

    @Override
	public void run() {

        while (!flag) {

            try {
//                sleep(6000);
                sleep(timeToSleep);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (cacheManager != null) {
                if (!cacheManager.isEmpty()) {
                    int countClear = cacheManager.deleteOldCache();
                    if (countClear > 0) {
						System.out.println("FlexWorkflow: Clear cache (" + countClear + " instances)");
					}
                }

            }

        }


    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return flag;
    }


}
