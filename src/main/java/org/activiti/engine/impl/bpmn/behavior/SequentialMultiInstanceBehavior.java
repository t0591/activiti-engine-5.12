/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.engine.impl.bpmn.behavior;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.TaskContext;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import com.frameworkset.util.StringUtil;


/**
 * @author Joram Barrez
 * @author Falko Menge
 */
public class SequentialMultiInstanceBehavior extends MultiInstanceActivityBehavior {
  
  public SequentialMultiInstanceBehavior(ActivityImpl activity, AbstractBpmnActivityBehavior innerActivityBehavior) {
    super(activity, innerActivityBehavior);
  }
  
  /**
   * Handles the sequential case of spawning the instances.
   * Will only create one instance, since at most one instance can be active.
   */
  protected void createInstances(ActivityExecution execution) throws Exception {
    int nrOfInstances = resolveNrOfInstances(execution);
    if (nrOfInstances <= 0) {
      throw new ActivitiIllegalArgumentException("Invalid number of instances: must be positive integer value" 
              + ", but was " + nrOfInstances);
    }
    
    setLoopVariable(execution, NUMBER_OF_INSTANCES, nrOfInstances);
    setLoopVariable(execution, NUMBER_OF_COMPLETED_INSTANCES, 0);
    setLoopVariable(execution, LOOP_COUNTER, 0);
    setLoopVariable(execution, NUMBER_OF_ACTIVE_INSTANCES, 1);
    logLoopDetails(execution, "initialized", 0, 0, 1, nrOfInstances);
    
    executeOriginalBehavior(execution, 0);
  }
  
  /**
   * Called when the wrapped {@link ActivityBehavior} calls the 
   * {@link AbstractBpmnActivityBehavior#leave(ActivityExecution)} method.
   * Handles the completion of one instance, and executes the logic for the sequential behavior.    
   */
//  public void leave(ActivityExecution execution)
//  {
//	  leave(execution,null);
//  }
  /**
   * added by biaoping.yin
   */
  public void leave(ActivityExecution execution) {
    callActivityEndListeners(execution);
    TaskContext taskContext = execution.getTaskContext();
    int loopCounter = getLoopVariable(execution, LOOP_COUNTER) + 1;
    int nrOfInstances = getLoopVariable(execution, NUMBER_OF_INSTANCES);
    int nrOfCompletedInstances = getLoopVariable(execution, NUMBER_OF_COMPLETED_INSTANCES) + 1;
    int nrOfActiveInstances = getLoopVariable(execution, NUMBER_OF_ACTIVE_INSTANCES);
    
    setLoopVariable(execution, LOOP_COUNTER, loopCounter);
    setLoopVariable(execution, NUMBER_OF_COMPLETED_INSTANCES, nrOfCompletedInstances);
    logLoopDetails(execution, "instance completed", loopCounter, nrOfCompletedInstances, nrOfActiveInstances, nrOfInstances);
    boolean reject = taskContext.getDestinationTaskKey() != null && !taskContext.getDestinationTaskKey().equals("") && !taskContext.isFromreject();
    if (loopCounter == nrOfInstances || completionConditionSatisfied(execution) || reject) {
      super.leave(execution);
    } else {
      try {
        executeOriginalBehavior(execution, loopCounter);
      } catch (BpmnError error) {
        // re-throw business fault so that it can be caught by an Error Intermediate Event or Error Event Sub-Process in the process
        throw error;
      } catch (Exception e) {
        throw new ActivitiException("Could not execute inner activity behavior of multi instance behavior", e);
      }
    }
  }
  public static void main(String[] args)
  {
	  String aa = "${aa}";
	  System.out.println(aa.indexOf("{")+","+aa.substring(2,aa.length() -1));
  }
  @Override
  public void execute(ActivityExecution execution) throws Exception {
	if(execution.getTaskContext().isHasassignee())
	{
	    super.execute(execution,true);
	    
	    if(innerActivityBehavior instanceof SubProcessActivityBehavior) {
	      // ACT-1185: end-event in subprocess may have inactivated execution
	      if(!execution.isActive() && execution.isEnded() && (execution.getExecutions() == null || execution.getExecutions().size() == 0)) {
	        execution.setActive(true);
	      }
	    }
	}
	else
	{
		Context.getCommandContext().getHistoryManager()
	      .recordUseTaskActivityAutoComplete((ExecutionEntity) execution);
		String BUSSINESSCONTROLCLASS = execution.getTaskContext().getBUSSINESSCONTROLCLASS();
		if(StringUtil.isNotEmpty(BUSSINESSCONTROLCLASS))
		{
			JavaDelegate javaDelegate = Context.getJavaDelegate(BUSSINESSCONTROLCLASS);
			super.execute(execution, javaDelegate);
		}
		super.leave(execution);
			
	}
  }

}
