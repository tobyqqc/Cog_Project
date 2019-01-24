package com.lumosity.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

public abstract class BaseOauthUser<M extends BaseOauthUser<M>> extends Model<M> implements IBean {

	public Integer getGender() {
        return get("gender");
    }

    public void setGender(Integer gender) {
        set("gender", gender);
    }

    public Integer getPlatform() {
        return get("platform");
    }

    public void setPlatform(Integer platform) {
        set("platform", platform);
    }

    public Integer getId() {
        return get("id");
    }

    public void setId(Integer id) {
        set("id", id);
    }

    public String getOpenId() {
        return get("open_id");
    }

    public void setOpenId(String openId) {
        set("open_id", openId);
    }

    public String getNickname() {
        return get("nickname");
    }

    public void setNickname(String nickname) {
        set("nickname", nickname);
    }

    public String getCover() {
        return get("cover");
    }

    public void setCover(String cover) {
        set("cover", cover);
    }

    public Long getUserId() {
        return get("user_id");
    }

    public void setUserId(Long userId) {
        set("user_id", userId);
    }

    public Long getCreateTime() {
        return get("create_time");
    }

    public void setCreateTime(Long createTime) {
        set("create_time", createTime);
    }
}
