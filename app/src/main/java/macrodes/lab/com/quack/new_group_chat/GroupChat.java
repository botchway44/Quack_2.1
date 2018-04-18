package macrodes.lab.com.quack.new_group_chat;

import java.util.Date;

/**
 * Created by Botchway on 3/24/2018.
 */

public class GroupChat {
    private String userId;
    private String groupId;
    private String Url;
    private String message;
    private String isMediaType;
    private Date time;
    private  String offlineUrl;

    public GroupChat() {
    }

    public GroupChat(String userId, String groupId, String url, String message, String isMediaType, Date time, String offlineUrl) {
        this.userId = userId;
        this.groupId = groupId;
        Url = url;
        this.message = message;
        this.isMediaType = isMediaType;
        this.time = time;
        this.offlineUrl = offlineUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIsMediaType() {
        return isMediaType;
    }

    public void setIsMediaType(String isMediaType) {
        this.isMediaType = isMediaType;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getOfflineUrl() {
        return offlineUrl;
    }

    public void setOfflineUrl(String offlineUrl) {
        this.offlineUrl = offlineUrl;
    }
}
