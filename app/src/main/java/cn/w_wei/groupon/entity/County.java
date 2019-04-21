package cn.w_wei.groupon.entity;

public class County {
    private String countName;
    private int cityId;

    public String getCountName() {
        return countName;
    }

    public void setCountName(String countName) {
        this.countName = countName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    @Override
    public String toString() {
        return "County{" +
                "countName='" + countName + '\'' +
                ", cityId=" + cityId +
                '}';
    }
}
