package server.logic.factory;

import server.logic.Logic;

/**
 * A task contained within a <code>ChainTaskExecutor</code>.
 * 
 * @author lare96
 */
public interface ChainTask extends Logic {

    /**
     * The logic within the task that will be executed.
     */
    public void run();

    /**
     * @return the delay after the last execution that this task will finally be
     *         executed.
     */
    public int delay();
}
