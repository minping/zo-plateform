package com.jfinal.plugin.activerecord.driver;

import com.common.util.StrKit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DriverContext {

    private List<Driver> drivers;

    /**
     * 获取driver
     * @param driverFullClass
     * @param version
     * @return
     */
    public Driver getDriver(String driverFullClass,String version){
        if(StrKit.isNotBlank(version) && drivers!=null){
            for (int i = 0; i < drivers.size(); i++) {
                Driver driver = drivers.get(i);
                if(driver.match(driverFullClass,version)){
                    return driver;
                }
            }
        }
        return null;
    }

}