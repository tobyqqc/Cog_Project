package com.lumosity.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseJob<M extends BaseJob<M>> extends Model<M> implements IBean {

	public void setJobId(java.lang.Integer jobId) {
		set("jobId", jobId);
	}

	public java.lang.Integer getJobId() {
		return get("jobId");
	}

	public void setJobName(java.lang.String jobName) {
		set("jobName", jobName);
	}

	public java.lang.String getJobName() {
		return get("jobName");
	}

}
