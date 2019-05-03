package cn.w_wei.groupon.bean;

import java.util.List;

public class DistrictBean {
    private String status;
    private List<City> cities;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    @Override
    public String toString() {
        return "DistrictBean{" +
                "status='" + status + '\'' +
                ", cities=" + cities +
                '}';
    }

    public static class City{
        private String city_name;
        private List<District> districts;

        public String getCity_name() {
            return city_name;
        }

        public void setCity_name(String city_name) {
            this.city_name = city_name;
        }

        public List<District> getDistricts() {
            return districts;
        }

        public void setDistricts(List<District> districts) {
            this.districts = districts;
        }

        @Override
        public String toString() {
            return "City{" +
                    "city_name='" + city_name + '\'' +
                    ", neighborhoods=" + districts +
                    '}';
        }

        public static class District{
            private String district_name;
            private List<String> neighborhoods;

            public String getDistrict_name() {
                return district_name;
            }

            public void setDistrict_name(String district_name) {
                this.district_name = district_name;
            }

            public List<String> getNeighborhoods() {
                return neighborhoods;
            }

            public void setNeighborhoods(List<String> neighborhoods) {
                this.neighborhoods = neighborhoods;
            }

            @Override
            public String toString() {
                return "District{" +
                        "district_name='" + district_name + '\'' +
                        ", neighborhoods=" + neighborhoods +
                        '}';
            }
        }
    }

}
