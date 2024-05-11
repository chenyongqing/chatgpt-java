package com.plexpt.chatgpt.relay.azure;

import static com.plexpt.chatgpt.util.FormatDateUtil.formatDate;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.alibaba.fastjson.JSONObject;
import com.plexpt.chatgpt.entity.billing.CreditGrantsResponse;
import com.plexpt.chatgpt.entity.billing.SubscriptionData;
import com.plexpt.chatgpt.entity.billing.UseageResponse;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.relay.ChannelChatGPT;
import com.plexpt.chatgpt.relay.ClientConfig;
import com.plexpt.chatgpt.relay.azure.api.AzureApi;
import io.reactivex.Single;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request.Builder;


/**
 * open ai 客户端
 *
 * @author plexpt
 */

@Slf4j
@Getter
public class AzureChat extends ChannelChatGPT<AzureApi> {

    /**
     * 部署名称
     */
    private final String deploymentName;
    /**
     * api 版本
     */
    private final String apiVersion;

    public AzureChat(ClientConfig clientConfig, String deploymentName, String apiVersion) {
        super(clientConfig);
        this.deploymentName = deploymentName;
        this.apiVersion = apiVersion;
    }

    @Override
    protected Class<AzureApi> apiClass() {
        return AzureApi.class;
    }

    @Override
    protected void setupRequestHeader(Map<String, Object> context, Builder builder) {
        String key = RandomUtil.randomEle(getClientConfig().getApiKeys());
        builder.header(Header.AUTHORIZATION.getValue(), "Bearer " + key)
            .header("api-key", key)
            .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
    }

    @Override
    public ChatCompletionResponse chatCompletion(ChatCompletion chatCompletion) {
        Single<ChatCompletionResponse> chatCompletionResponse = this.apiClient.chatCompletion(deploymentName,
            apiVersion, chatCompletion);
        return chatCompletionResponse.blockingGet();
    }

    @Override
    public ChatCompletionResponse chatCompletion(List<Message> messages) {
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(messages).build();
        return this.chatCompletion(chatCompletion);
    }

    @Override
    public String chat(String message) {
        ChatCompletion chatCompletion = ChatCompletion.builder()
            .messages(Collections.singletonList(Message.of(message)))
            .build();
        ChatCompletionResponse response = this.chatCompletion(chatCompletion);
        return response.getChoices().get(0).getMessage().getContent();
    }

    @Override
    public CreditGrantsResponse creditGrants() {
        Single<CreditGrantsResponse> creditGrants = this.apiClient.creditGrants();
        return creditGrants.blockingGet();
    }

    @Override
    public BigDecimal balance() {
        Single<SubscriptionData> subscription = apiClient.subscription();
        SubscriptionData subscriptionData = subscription.blockingGet();
        BigDecimal total = subscriptionData.getHardLimitUsd();
        DateTime start = DateUtil.offsetDay(new Date(), -90);
        DateTime end = DateUtil.offsetDay(new Date(), 1);

        Single<UseageResponse> usage = apiClient.usage(formatDate(start), formatDate(end));
        UseageResponse useageResponse = usage.blockingGet();
        BigDecimal used = useageResponse.getTotalUsage().divide(BigDecimal.valueOf(100));

        return total.subtract(used);
    }
}
