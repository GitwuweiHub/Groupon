package cn.w_wei.groupon.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class CityNameBean {
    @DatabaseField
    private boolean flag = false;
    @DatabaseField(id = true)
    private String cityName;//城市的中文名称
    @DatabaseField
    private String pyName;//城市中文名称的拼音
    @DatabaseField
    private char letter;//城市拼音的首字母

    public CityNameBean() {
    }

    public CityNameBean(String cityName, String pyName, char letter) {
        this.cityName = cityName;
        this.pyName = pyName;
        this.letter = letter;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPyName() {
        return pyName;
    }

    public void setPyName(String pyName) {
        this.pyName = pyName;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    @Override
    public String toString() {
        return "CityNameBean{" +
                "flag=" + flag +
                ", cityName='" + cityName + '\'' +
                ", pyName='" + pyName + '\'' +
                ", letter=" + letter +
                '}';
    }
}
