package com.itheima.manage.listener;

import com.tanhua.commons.templates.HuaWeiUGCTemplate;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.dubbo.api.MovementsApi;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        // 一个是提供者定义的topic主题名，第二个是消费者组名，自己定义
        topic = "tanhua_publish",consumerGroup = "tanhua_publish_consumer"
)
public class PublishMessageListener implements RocketMQListener<String> {
    @Autowired
    private HuaWeiUGCTemplate huaWeiUGCTemplate;

    @Reference
    private MovementsApi movementsApi;


    @Override
    public void onMessage(String publishId) {

        // 根据 publishId 查询动态对应的动态内容（文字+图片）
        Publish publish = movementsApi.queryPublishById(publishId);

        // 未审核状态
        if (publish.getState() == 0){
            // 对内容进行审核
            boolean text = huaWeiUGCTemplate.textContentCheck(publish.getTextContent());
            // 图片的格式需要转换为 string 数组
            boolean image = huaWeiUGCTemplate.imageContentCheck(publish.getMedias().toArray(new String[]{}));

            Integer state = 2;
            if (text && image){
                // 修改状态为已审核
                state = 1;
                movementsApi.updatePublishState(publishId,state);
            }
        }
    }
}
