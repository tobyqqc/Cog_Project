package com.lumosity.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseGameClass<M extends BaseGameClass<M>> extends Model<M> implements IBean {

	public void setGameClassId(java.lang.Integer gameClassId) {
		set("gameClassId", gameClassId);
	}

	public java.lang.Integer getGameClassId() {
		return get("gameClassId");
	}

	public void setGameClassName(java.lang.String gameClassName) {
		set("gameClassName", gameClassName);
	}

	public java.lang.String getGameClassName() {
		return get("gameClassName");
	}

	public void setTrainClassId(java.lang.Integer trainClassId) {
		set("trainClassId", trainClassId);
	}

	public java.lang.Integer getTrainClassId() {
		return get("trainClassId");
	}

	public void setDis(java.lang.String dis) {
		set("dis", dis);
	}

	public java.lang.String getDis() {
		return get("dis");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("createTime", createTime);
	}

	public java.util.Date getCreateTime() {
		return get("createTime");
	}

	public void setCreateAdminId(java.lang.Integer createAdminId) {
		set("createAdminId", createAdminId);
	}

	public java.lang.Integer getCreateAdminId() {
		return get("createAdminId");
	}

	public void setUpdateTime(java.util.Date updateTime) {
		set("updateTime", updateTime);
	}

	public java.util.Date getUpdateTime() {
		return get("updateTime");
	}

	public void setUpdateAdminId(java.lang.Integer updateAdminId) {
		set("updateAdminId", updateAdminId);
	}

	public java.lang.Integer getUpdateAdminId() {
		return get("updateAdminId");
	}

}
