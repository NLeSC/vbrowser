/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */
// source: 

package nl.esciencecenter.ptk.task;

import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.ptk.task.ITaskMonitor.TaskStats;
import nl.esciencecenter.ptk.util.StringUtil;

/** 
 * Class which calculates statistics from an ITaskMonitor object. 
 * Also provides some extra status to String methods like estimated time of arrival (ETA).   
 */ 
public class MonitorStats
{
    
    // ========================================================================
    //
    // ========================================================================
    
    protected ITaskMonitor monitor=null; 
    
    //Presentation presentation=new Presentation(); 
    
    public MonitorStats(ITaskMonitor monitor)
    {
        this.monitor=monitor; 
    }
    
    /**
     * Returns current task time time in millis or total time when task is done
     */
    public long getTotalDoneTime()
    {
        TaskStats stats=monitor.getTaskStats(); 
        
        if (monitor.isDone())
        {
            return (stats.stopTimeMillies-stats.startTimeMillies);  
            
        }
        else
        {
            return System.currentTimeMillis() - stats.startTimeMillies; 
        }
    }
    
    /** 
     * Returns ETA in millis. 
     * Depends on totalTodo/totalDone to calculate ETA.  
     * <pre>
     * -1 = no statistics
     *  0 = done 
     * >0 = estimated finishing time in milli seconds 
     * </pre>
     * @return
     */ 
    public long getETA()
    {
        if (monitor.isDone())
            return 0; // done
        return calcETA(monitor.getTaskStats().done,monitor.getTaskStats().todo,getTotalSpeed());
    }

    
    public String getStatusText()
    {
        if (monitor.isDone() == false)
        {
            String subStr = monitor.getCurrentSubTaskName();

            if (StringUtil.isEmpty(subStr))
                return "Busy ...";
            else
                return subStr;
        }

        if (monitor.hasError())
            return "Error!";

        return "Finished!";
    }
    
    /**
     * Return ETA in milli sconds. 
     * @return the return value is -1 for unknown, 0 for done or 0> for actual estimated time in milli seconds. 
     */  
    public long calcETA(long done,long todo, double speed)
    {
        // no statistics ! 
        if (done<0)
            return -1; //unknown
        
        // nr of bytes/ nr of total work todo   
        long delta=todo-done;
        
        if (speed<=0) 
            return -1; //unknown, prevent divide by zero; 
        // return ETA in millis ! 
        return (long)((1000*delta)/speed); 
    }
    
    /**
     * Return speed total transfer amount of workdone/seconds 
     * for transfer this is bytes/second. 
     * If nr of bytes equals amount of work done.  
     */ 
   public double getTotalSpeed()
   {
       // time is in millis, total work amount in bytes (for transfers) 
       double speed=((double)monitor.getTaskStats().done)/(double)(getTotalDoneDeltaTime()); 

       // convert from work/millisecond to work/seconds 
       speed=speed*1000.0;
       
       return speed; 
   }

   public double getTotalProgress()
   {
       if (this.monitor.getTaskStats().todo<=0)
       {
           return 0; 
       }
           
       return ((double)monitor.getTaskStats().done)/(double)monitor.getTaskStats().todo; 
   }
      
   /** 
    * Calculate total time busy in milli secondss but use Last Update Time to prevent 'degrading'
    * performance time between updates ! 
    * This happens when the transfer is stalled but the time continues. 
    */
   public long getTotalDoneDeltaTime()
   {
       TaskStats stats=monitor.getTaskStats(); 
       return stats.doneLastUpdateTimeMillies-stats.startTimeMillies; 
   }

   public long getTotalDoneLastUpdateTime()
   {
       return monitor.getTaskStats().doneLastUpdateTimeMillies; 
   }

   /**
    *  Return total Time Running in millies.
    */ 
   public long getTotalTimeRunning()
   {
      long time=System.currentTimeMillis()-monitor.getTaskStats().startTimeMillies;
      return time; 
   }
   
   // =========================================================================
   // Sub Task Stats 
   // =========================================================================
   
   public String getCurrentSubTaskName()
   {
      return monitor.getCurrentSubTaskName(); 
   }
  
   public double getSubTaskProgress(String subTaskName)
   {
       TaskStats stats= monitor.getSubTaskStats(subTaskName);
       
       if (stats==null)
           return 0;  
       
       if (stats.todo<=0)
       {
           return 0; // Double.NaN; // divide by null.    
       }
       
       return ((double)stats.done/(double)stats.todo); 
   }
   
   public long getSubTaskDone(String subTaskName)
   {
       TaskStats subTask = monitor.getSubTaskStats(subTaskName); 
       if (subTask==null)
           return 0; 
       return subTask.done; 
   }
   
   public long getSubTaskTodo(String subTaskName)
   {
       TaskStats subTask = monitor.getSubTaskStats(subTaskName); 
       if (subTask==null)
           return 0; 
       return subTask.todo; 
   }

  /**
   * Return speed total transfer amount of workdone/seconds 
   * For example for file transfers this is bytes/second. 
   */ 
  public double getSubTaskSpeed(String subTaskName)
  {
      TaskStats stats=monitor.getSubTaskStats(subTaskName);
      return calculateTaskSpeed(stats);
  }
  
  public double calculateTaskSpeed(TaskStats stats)
  {
      if (stats==null)
          return 0.0; 
      
      // time is in millis, total work amount in bytes (for transfers) 
      double speed=stats.done/((double)getTaskDoneDeltaTime(stats)); 

      // convert from work/millisecond to work/seconds (or bytes/seconds) 
      speed=speed*1000.0;
      
      return speed; 
  }
  
  /** 
   * Calculated total time busy in millies but uses Last Update Time to prevent 'degrading'
   * performance time between (subtask) updates ! 
   */
  public long getSubTaskDoneDeltaTime(String subTaskName)
  {
      return getTaskDoneDeltaTime(monitor.getSubTaskStats(subTaskName)); 
  }
  
  public long getTaskDoneDeltaTime(TaskStats stats)
  {
      if (stats==null)
          return Long.MAX_VALUE;
      
      return stats.doneLastUpdateTimeMillies-stats.startTimeMillies; 
      
  }

  public long getSubTaskETA(String subTaskName)
  {
      if (monitor.isDone())
          return 0; // done
      
      return calculateSubTaskETA(monitor.getSubTaskStats(subTaskName));  
  }
  
  public long calculateSubTaskETA(TaskStats stats)
  {
      if (stats==null)
          return -1; 
      
      return calcETA(stats.done,
              stats.todo,
              this.calculateTaskSpeed(stats));
  }
  
  /** 
   * Return total time of specified sub task running in millies.
   */ 
  public long getSubTaskTimeRunning(String taskName)
  {
      TaskStats stats = monitor.getSubTaskStats(taskName); 
      
      if (stats==null)
          return -1; 
      
      long time=System.currentTimeMillis()-monitor.getTaskStats().startTimeMillies;
      return time; 
  }

  /** 
   * Produce Time String of current Subtask. 
   * Returns time running plus estimated time of arrival.  
   * @return
   */
  public String getCurrentSubTaskTimeStatusText()
  {
    String subTaskName= getCurrentSubTaskName(); 
    TaskStats stats=monitor.getSubTaskStats(subTaskName); 
    
    long   subTime= getTaskDoneDeltaTime(stats);
    
    String timestr=Presentation.createRelativeTimeString(subTime,false);
    
    long eta=calculateSubTaskETA(stats); 
    
    if (eta<0)
        timestr+=" (?)";
    else if (eta==0) 
        timestr+= " (done)";
    else
        timestr+=" ("+Presentation.createRelativeTimeString(eta,false)+")";
    
    return timestr; 
  }

}
