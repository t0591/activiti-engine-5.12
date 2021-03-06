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

package org.activiti.engine.impl.persistence.entity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.db.PersistentObject;
import org.activiti.engine.impl.util.ClockUtil;


/**
 * @author Tom Baeyens
 */
public class HistoricTaskInstanceEntity extends HistoricScopeInstanceEntity implements HistoricTaskInstance, PersistentObject {

  private static final long serialVersionUID = 1L;
  
  protected String executionId;
  protected String name;
  protected String parentTaskId;
  protected String description;
  
  protected String assignee;
  protected String taskDefinitionKey;
  protected String formKey;
  protected int priority;
  protected Date dueDate;
 
  /**
   * 超时是否已经发送
   */
  protected int OVERTIMESEND;
  /**
   * '任务持续时间限制
   */
  protected long DURATION_NODE;
  /**
   * 预警是否已发送
   */
  protected int ADVANCESEND;
  /**
   * 提前预警时间
   */
  protected Timestamp ALERTTIME;
  /**
   * 超时告警时间
   */
  protected Timestamp OVERTIME;
  /**
   * 预警时间率
   */
  protected int NOTICERATE;
  
  /**
   * 节假日策略
   * '节假日策略，0-考虑节假日，不考虑作息时间，1-不考虑节假日，不考虑作息时间，2-考虑节假日，考虑作息时间，默认值为1';
   */
  protected int IS_CONTAIN_HOLIDAY;
  public HistoricTaskInstanceEntity() {
  }

  public HistoricTaskInstanceEntity(TaskEntity task, ExecutionEntity execution) {
    this.id = task.getId();
    if (execution != null) {
      this.processDefinitionId = execution.getProcessDefinitionId();
      this.processInstanceId = execution.getProcessInstanceId();
      this.executionId = execution.getId();
    }
    this.name = task.getName();
    this.parentTaskId = task.getParentTaskId();
    this.description = task.getDescription();
    this.owner = task.getOwner();
    this.assignee = task.getAssignee();
    this.startTime = ClockUtil.getCurrentTime();
    this.startTimeLong = this.startTime.getTime();
    this.taskDefinitionKey = task.getTaskDefinitionKey();

    this.setPriority(task.getPriority());
    this.setDueDate(task.getDueDate());
    task.setHistoricTaskInstanceEntity(this);
  }

  // persistence //////////////////////////////////////////////////////////////
  
  public Object getPersistentState() {
    Map<String, Object> persistentState = new HashMap<String, Object>();
    persistentState.put("name", name);
    persistentState.put("owner", owner);
    persistentState.put("assignee", assignee);
    persistentState.put("endTime", endTime);
    persistentState.put("endTimeLong", endTimeLong);
    persistentState.put("durationInMillis", durationInMillis);
    persistentState.put("description", description);
    persistentState.put("deleteReason", deleteReason);
    persistentState.put("taskDefinitionKey", taskDefinitionKey);
    persistentState.put("formKey", formKey);
    persistentState.put("priority", priority);
    if(parentTaskId != null) {
      persistentState.put("parentTaskId", parentTaskId);
    }
    if(dueDate != null) {
      persistentState.put("dueDate", dueDate);
    }
    if (claimTime != null) {
      persistentState.put("claimTime", claimTime);
    }
    if(this.bussinessOperation != null)
    	persistentState.put("bussinessOperation", bussinessOperation);
    if(this.bussinessRemark != null)
    {
    	persistentState.put("bussinessRemark", bussinessRemark);
    }
    return persistentState;
  }

  // getters and setters //////////////////////////////////////////////////////
  public String getExecutionId() {
    return executionId;
  }
  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getAssignee() {
    return assignee;
  }
  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }
  public String getTaskDefinitionKey() {
    return taskDefinitionKey;
  }
  public void setTaskDefinitionKey(String taskDefinitionKey) {
    this.taskDefinitionKey = taskDefinitionKey;
  }
  public String getFormKey() {
    return formKey;
  }
  public void setFormKey(String formKey) {
    this.formKey = formKey;
  }
  public int getPriority() {
    return priority;
  }
  public void setPriority(int priority) {
    this.priority = priority;
  }
  public Date getDueDate() {
    return dueDate;
  }
  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }
  
  public String getParentTaskId() {
    return parentTaskId;
  }
  public void setParentTaskId(String parentTaskId) {
    this.parentTaskId = parentTaskId;
  }
 
  public Long getWorkTimeInMillis() {
    if (endTime == null || claimTime == null) {
      return null;
    }
    return endTime.getTime() - claimTime.getTime();
  }

public int getOVERTIMESEND() {
	return OVERTIMESEND;
}

public void setOVERTIMESEND(int oVERTIMESEND) {
	OVERTIMESEND = oVERTIMESEND;
}

public long getDURATION_NODE() {
	return DURATION_NODE;
}

public void setDURATION_NODE(long dURATION_NODE) {
	DURATION_NODE = dURATION_NODE;
}

public int getADVANCESEND() {
	return ADVANCESEND;
}

public void setADVANCESEND(int aDVANCESEND) {
	ADVANCESEND = aDVANCESEND;
}

public Timestamp getALERTTIME() {
	return ALERTTIME;
}

public void setALERTTIME(Timestamp aLERTTIME) {
	ALERTTIME = aLERTTIME;
}

public Timestamp getOVERTIME() {
	return OVERTIME;
}

public void setOVERTIME(Timestamp oVERTIME) {
	OVERTIME = oVERTIME;
}

public int getNOTICERATE() {
	return NOTICERATE;
}

public void setNOTICERATE(int nOTICERATE) {
	NOTICERATE = nOTICERATE;
}

public int getIS_CONTAIN_HOLIDAY() {
	return IS_CONTAIN_HOLIDAY;
}

public void setIS_CONTAIN_HOLIDAY(int iS_CONTAIN_HOLIDAY) {
	IS_CONTAIN_HOLIDAY = iS_CONTAIN_HOLIDAY;
}
}
