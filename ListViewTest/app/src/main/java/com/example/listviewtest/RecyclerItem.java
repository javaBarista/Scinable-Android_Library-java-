package com.example.listviewtest;

public class RecyclerItem {
    private String pushNum;
    private String imgUrl;
    private String title;
    private String body;
    private String receivedDate;
    private String chkread;
    private String userUrl;

    public RecyclerItem(String pushNum, String imgUrl, String title, String body, String receivedDate, String chkread, String userUrl) {
        this.pushNum = pushNum;
        this.imgUrl= imgUrl;
        this.title = title;
        this.body = body;
        this.receivedDate = receivedDate;
        this.chkread = chkread;
        this.userUrl = userUrl;
    }

    public String getPushNum(){ return this.pushNum; }
    public String getImgUrl() {
        return this.imgUrl;
    }
    public String getTitle() {
        return this.title;
    }
    public String getBody() { return this.body; }
    public String getDate() { return this.receivedDate; }
    public String getChkread() { return this.chkread; }
    public String getUserUrl() { return this.userUrl; }

}