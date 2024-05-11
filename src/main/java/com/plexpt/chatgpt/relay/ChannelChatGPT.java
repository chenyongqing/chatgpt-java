package com.plexpt.chatgpt.relay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.plexpt.chatgpt.entity.BaseResponse;
import com.plexpt.chatgpt.exception.ChatException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;


/**
 * open ai 客户端
 *
 * @author plexpt
 */

@Slf4j
@Getter
public abstract class ChannelChatGPT<A extends Api> implements ChatGPT {

    private final ClientConfig clientConfig;

    protected OkHttpClient okHttpClient;

    protected A apiClient;

    protected ChannelChatGPT(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    @Override
    public ChatGPT init() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();

        client.addInterceptor(chain -> {
            Request original = chain.request();

            Map<String, Object> content = new HashMap<>();
            JSONObject extConfig = JSONObject.parseObject(clientConfig.getExtConfig());
            if (Objects.nonNull(extConfig)) {
                content.putAll(extConfig);
            }

            content.put("apiKeys", clientConfig.getApiKeys());
            content.put("apiVersion", clientConfig.getApiVersion());
            content.put("apiHost", clientConfig.getApiHost());

//            String key = RandomUtil.randomEle(clientConfig.getApiKeys());
            Request.Builder builder = original.newBuilder()
//                .header(Header.AUTHORIZATION.getValue(), "Bearer " + key)
//                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .method(original.method(), original.body());
            this.setupRequestHeader(content, builder);

            Request request = builder.build();
            return chain.proceed(request);
        }).addInterceptor(chain -> {
            Request original = chain.request();
            Response response = chain.proceed(original);
            if (!response.isSuccessful()) {
                String errorMsg = response.body().string();

                log.error("请求异常：{}", errorMsg);
                BaseResponse baseResponse = JSON.parseObject(errorMsg, BaseResponse.class);
                if (Objects.nonNull(baseResponse.getError())) {
                    log.error(baseResponse.getError().getMessage());
                    throw new ChatException(baseResponse.getError().getMessage());
                }
                throw new ChatException("init error!");
            }
            return response;
        });

        // 超时时间
        long timeout = clientConfig.getTimeout();
        client.connectTimeout(timeout, TimeUnit.SECONDS);
        client.writeTimeout(timeout, TimeUnit.SECONDS);
        client.readTimeout(timeout, TimeUnit.SECONDS);

        // 代理设置
        Proxy proxy = clientConfig.getProxy();
        if (Objects.nonNull(proxy)) {
            client.proxy(proxy);
        }
        this.okHttpClient = client.build();

        this.apiClient = new Retrofit.Builder()
            .baseUrl(clientConfig.getApiHost())
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
            .create(apiClass());
        return this;
    }

    protected abstract Class<A> apiClass();

    protected abstract void setupRequestHeader(Map<String, Object> context, Request.Builder builder);
}
