package com.plexpt.chatgpt.relay;

import java.net.Proxy;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * @description:ClientConfig
 * @date: 2024/5/10 17:43
 * @author: yongqing.chen
 */
@Data
@Builder
public class ClientConfig {

    /**
     * 授权密钥
     */
    private List<String> apiKeys;
    /**
     * API接口版本
     */
    private String apiVersion;

    /**
     * 接口地址
     */
    private String apiHost;
    /**
     * 扩展配置
     */
    private String extConfig;

    /**
     * 超时 默认300
     */
    @Builder.Default
    private long timeout = 300;
    /**
     * okhttp 代理
     */
    @Builder.Default
    private Proxy proxy = Proxy.NO_PROXY;
}