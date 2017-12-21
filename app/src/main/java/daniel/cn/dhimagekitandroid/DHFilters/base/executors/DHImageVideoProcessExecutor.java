package daniel.cn.dhimagekitandroid.DHFilters.base.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by huanghongsen on 2017/12/20.
 */

public class DHImageVideoProcessExecutor {
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void runTaskOnVideoProcessQueue(Runnable task) {
        executor.execute(task);
    }
}
