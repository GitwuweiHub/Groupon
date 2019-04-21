package cn.w_wei.groupon.entity;

public class Province {
    private String provinceName;
    private int procinceId;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProcinceId() {
        return procinceId;
    }

    public void setProcinceId(int procinceId) {
        this.procinceId = procinceId;
    }

    @Override
    public String toString() {
        return "Province{" +
                "provinceName='" + provinceName + '\'' +
                ", procinceId=" + procinceId +
                '}';
    }
}
