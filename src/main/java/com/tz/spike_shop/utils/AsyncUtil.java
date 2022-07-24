package com.tz.spike_shop.utils;

import com.tz.spike_shop.pojo.SpikeResultMessage;
import com.tz.spike_shop.websocket.SpikeResultWebsocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncUtil {

    @Autowired
    private SpikeResultWebsocket websocket;

    /**
     * 组装信息，通过websocket发送
     * @param userId
     * @param spikeResultMessage
     */
    @Async(value = "spikePool")
    public void sendInfo(Long userId, SpikeResultMessage spikeResultMessage) {
        String message = JsonUtil.object2JsonStr(spikeResultMessage);
        websocket.sendInfo(userId, message);
    }
}
