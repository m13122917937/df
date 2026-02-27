package com.ruoyi.biz.mq;

import com.aliyun.openservices.ons.api.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class MsgClient {

    @Autowired
    private Producer producer;


    /**
     * 异步发送消息, 发送结果通过callback返回给客户端。
     *
     * @param msg
     */
    public void sendMsgDeliverTime(Message msg, TimeUnit timeUnit, long time) {
        Long deliverTime = System.currentTimeMillis() + timeUnit.toMillis(time);
        msg.setStartDeliverTime(deliverTime);
        SendResult sendResult = producer.send(msg);
        if (sendResult != null) {
            log.info(" Send mq message success. Topic is:" + msg.getTopic() + " msgId is: " + sendResult.getMessageId());
        }
    }


    /**
     * 异步发送消息, 发送结果通过callback返回给客户端。
     *
     * @param msg
     * @param successConsumer
     * @param failConsumer
     */
    public void sendMsg(Message msg, java.util.function.Consumer<SendResult> successConsumer, java.util.function.Consumer<OnExceptionContext> failConsumer) {


        producer.sendAsync(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                successConsumer.accept(sendResult);
            }

            @Override
            public void onException(OnExceptionContext context) {
                failConsumer.accept(context);
            }
        });
    }


    /**
     *
     * @param msg
     */
    public void sendMsg(Message msg) {

        producer.sendOneway(msg);
    }


}
