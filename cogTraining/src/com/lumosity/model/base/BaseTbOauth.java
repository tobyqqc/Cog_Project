package com.lumosity.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseTbOauth<M extends BaseTbOauth<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setOpenId(java.lang.String openId) {
		set("open_id", openId);
	}

	public java.lang.String getOpenId() {
		return get("open_id");
	}

	public void setUserId(java.lang.Long userId) {
		set("user_id", userId);
	}

	public java.lang.Long getUserId() {
		return get("user_id");
	}

	public void setNickname(java.lang.String nickname) {
		set("nickname", nickname);
	}

	public java.lang.String getNickname() {
		return get("nickname");
	}

	public void setCover(java.lang.String cover) {
		set("cover", cover);
	}

	public java.lang.String getCover() {
		return get("cover");
	}

	public void setGender(java.lang.Integer gender) {
		set("gender", gender);
	}

	public java.lang.Integer getGender() {
		return get("gender");
	}

	public void setPlatform(java.lang.Integer platform) {
		set("platform", platform);
	}

	public java.lang.Integer getPlatform() {
		return get("platform");
	}

	public void setCreateTime(java.lang.Long createTime) {
		set("create_time", createTime);
	}

	public java.lang.Long getCreateTime() {
		return get("create_time");
	}

}
