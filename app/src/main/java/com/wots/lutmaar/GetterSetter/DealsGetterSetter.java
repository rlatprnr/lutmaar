package com.wots.lutmaar.GetterSetter;

/**
 * Created by Super Star on 15-06-2015.
 */
public class DealsGetterSetter {
  
    String UserName;
    String Title;
    String Body;
    String HotCold;
    String Comment;
    String Created;

    public DealsGetterSetter() {
    }

    public DealsGetterSetter(  String userName, String title, String body,  String hotCold, String comment, String created) {

        UserName = userName;
        Title = title;
        Body = body;
        HotCold = hotCold;
        Comment = comment;
        Created = created;
    }
 

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }


    public String getHotCold() {
        return HotCold;
    }

    public void setHotCold(String hotCold) {
        HotCold = hotCold;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String Created) {
        Created = Created;
    }
}
