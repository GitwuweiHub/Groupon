package cn.w_wei.groupon.bean;

import java.util.Arrays;

public class CommentBean {
    private String avatar;//头像
    private String name;
    private String date;
    private String rating;//打分
    private String price;
    private String content;
    private String[] imgs;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getImgs() {
        return imgs;
    }

    public void setImgs(String[] imgs) {
        this.imgs = imgs;
    }

    @Override
    public String toString() {
        return "CommentBean{" +
                "avatar='" + avatar + '\'' +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", rating='" + rating + '\'' +
                ", price='" + price + '\'' +
                ", content='" + content + '\'' +
                ", imgs=" + Arrays.toString(imgs) +
                '}';
    }
}
