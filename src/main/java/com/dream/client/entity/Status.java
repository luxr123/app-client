package com.dream.client.entity;

import java.util.Date;


/**
 * User: xiaorui.lu
 * Date: 2013年12月25日 下午8:42:49
 */
public class Status{ 
	
	private long id;

	private long taskid;// 被选择的任务的键值
	
	private long candidateuserid;
	
	private int status;   //0：等待领取；1：领取成功；2：领取失败
	
    private Date createtime; // 更新状态时间

  
  public long getTaskid() {
    return taskid;
  }

  
  public void setTaskid(long taskid) {
    this.taskid = taskid;
  }

  
  public long getCandidateuserid() {
    return candidateuserid;
  }

  
  public void setCandidateuserid(long candidateuserid) {
    this.candidateuserid = candidateuserid;
  }

  
  public int getStatus() {
    return status;
  }

  
  public void setStatus(int status) {
    this.status = status;
  }

  
  public Date getCreatetime() {
    return createtime;
  }

  
  public void setCreatetime(Date createtime) {
    this.createtime = createtime;
  }
	
	


	
}

