package global.com.bluetoothchat;

/**
 * Created by onemoretime on 2015/06/05.
 */
public class ChatEntity {

    private int userImage;
    private String content;
    private String name;
    private String date;
    private boolean isComeMsg;

    public int getUserImage() {
        return userImage;
    }

    public void setUserImage(int userImage) {
        this.userImage = userImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isComeMsg() {
        return isComeMsg;
    }

    public void setComeMsg(boolean isComeMsg) {
        this.isComeMsg = isComeMsg;
    }

}