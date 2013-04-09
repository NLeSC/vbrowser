/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.task;

import java.util.Vector;

import nl.esciencecenter.ptk.util.logging.ClassLogger;

/**
 * ActionTask Watcher/Manager 
 */
public class TaskWatcher implements ITaskSource
{
    private static ClassLogger logger; 
    private static TaskWatcher instance=null;
    
    static
    {
        logger=ClassLogger.getLogger(TaskWatcher.class); 
    }
    
    // === //

    private int maxTerminatedTasks=100; 
    
	protected Vector<ActionTask> activeTasks=new Vector<ActionTask>(); 

	protected Vector<ActionTask> terminatedTasks=new Vector<ActionTask>(); 

	public static ITaskSource getTaskWatcher()
	{
		if (instance==null)
			instance=new TaskWatcher(); 

		return instance; 
	}
	
	public TaskWatcher()
	{
	}
	
    @Override
    public void registerTask(ActionTask actionTask)
    {
        synchronized(activeTasks)
        {
            this.activeTasks.add(actionTask); 
        }
    }
    
    @Override
    public void unregisterTask(ActionTask actionTask)
    {
        synchronized(activeTasks)
        {
            this.activeTasks.remove(actionTask); 
        }
        
        synchronized(terminatedTasks)
        {
            this.activeTasks.remove(actionTask); 
        }
    }
    
	@Override
	public void notifyTaskStarted(ActionTask actionTask) 
	{	
		; // 'kee
	}

	@Override
	public void notifyTaskTerminated(ActionTask task)
	{
	    synchronized(activeTasks)
        {
    		boolean removed=this.activeTasks.remove(task); 
    		if (removed==false)
    		{
    		    ; // already in terminatedTasks ? 
    		}
        }
	    
	    synchronized(terminatedTasks)
	    {
    		this.terminatedTasks.add(task); 
        }
	    
	    checkCleanup(); 
    }

	protected void checkCleanup()
	{
	    synchronized(this.terminatedTasks)
	    {
	        if (this.terminatedTasks.size()<=maxTerminatedTasks)
	            return; 
	                
	        for (int i=0;i<maxTerminatedTasks;i++)
	            terminatedTasks.remove(0); // not efficient array remove.  
        }
    }
	   
    public ActionTask getCurrentThreadActionTask()
    {
        return findActionTaskForThread(Thread.currentThread()); 
    }
	/**
     * Find ActionTask with thread id. 
     * Since all ActionTask are currently started in their
     * own thread, this method will find the actionTask
     * currently executed in the specified thread. 
     * 
     * @param thread Thread which started an ActionTask 
     * @return 
     */
    public ActionTask findActionTaskForThread(Thread thread)
    {
        if (thread==null)
            return null; 
        
        ActionTask tasks[]=getActiveTaskArray();
        
        for (ActionTask task:tasks)
        {
            if ((task!=null) && (task.hasThread(thread)) )
                return task;
        }
         
        return null; 
    }
    
    /** Return a private copy of the task list, for thread save operations */
    protected final ActionTask[] getActiveTaskArray()
    {
        synchronized (activeTasks)
        {
            ActionTask tasks[]=new ActionTask[activeTasks.size()];
            tasks=activeTasks.toArray(tasks);
            return tasks;
        }
    }

    @Override
    public void notifyTaskException(ActionTask task,Throwable ex)
    {
        // optional handling of exception throw by Task Execution. (ignore here)
        // subclasses might do something here. 

        //logger.errorPrintf("Received Exception for task:"+task.toString()); 
        logger.logException(ClassLogger.ERROR,task,ex,"TaskException for %s\n",task); 
    }
    
    /** Check whether there are active tasks running for the TaskSource */ 
    public boolean hasActiveTasks(ITaskSource source)
    {
        ActionTask tasks[]=getActiveTaskArray();
        
        if ((tasks==null) || (tasks.length<=0))
            return false; 
        
        boolean active=false;
        // send stop signal first: 
        for (ActionTask task:tasks)
        {
            if ((task.getTaskSource()!=null) && (task.getTaskSource()==source))
            {
                if (task.isAlive())
                    active=true;
            }
        }
        
        return active; 
    }

    @Override
    public void setHasActiveTasks(boolean active)
    {
        ; // is actually called by me.
    }
    
    public void stopActionTasksFor(ITaskSource source,boolean all)
    {
        ActionTask tasks[]=getActiveTaskArray();
        
        // send stop signal first: 
        for (ActionTask task:tasks)
        {
            if ( (all==true)
                 || ((task.getTaskSource()!=null) && (task.getTaskSource()==source))
                 )
            {
                task.signalTerminate(); 
            }
        }
        
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            logger.logException(ClassLogger.ERROR,e,"***Error: Exception:"+e); 
        } 
        
        // now send interrupt:   
        for (ActionTask task:tasks)
        {
            if ((task.getTaskSource()!=null) && (task.getTaskSource()==source))
            {
                if (task.isAlive())
                    task.interruptAll();  
            }
        }
        
        // check already stopped tasks: 
        if (this.hasActiveTasks(source))
        {
            source.setHasActiveTasks(false); 
        }
    }
    
}