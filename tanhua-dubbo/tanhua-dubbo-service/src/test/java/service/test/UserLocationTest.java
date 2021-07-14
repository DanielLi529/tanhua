package service.test;

import com.tanhua.dubbo.api.BaiduApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserLocationTest {

    @Autowired
    private BaiduApi baiduApi;

    @Test
    public void addLocation(){
        baiduApi.reportLocation(113.929778,22.582111,"深圳黑马程序员",1l);
        baiduApi.reportLocation(113.925528,22.587995,"红荔村肠粉",2l);
        baiduApi.reportLocation(113.93814,22.562578,"深圳南头直升机场",3l);
        baiduApi.reportLocation(114.064478,22.549528,"深圳市政府",4l);
        baiduApi.reportLocation(113.986074,22.547726,"欢乐谷",5l);
        baiduApi.reportLocation(113.979399,22.540746,"世界之窗",6l);
        baiduApi.reportLocation(114.294924,22.632275,"东部华侨城",7l);
        baiduApi.reportLocation(114.314011,22.598196,"大梅沙海滨公园",8l);
        baiduApi.reportLocation(113.821705,22.638172,"深圳宝安国际机场",9l);
        baiduApi.reportLocation(113.912386,22.566223,"海雅缤纷城(宝安店)",10l);
    }
}