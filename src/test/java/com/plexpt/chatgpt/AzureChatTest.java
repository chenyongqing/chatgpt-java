package com.plexpt.chatgpt;

import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.relay.azure.AzureChat;
import com.plexpt.chatgpt.util.Proxys;
import java.net.Proxy;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;

/**
 * @description:
 * @date: 2024/5/11 18:00
 * @user: yongqing.chen
 */

@Slf4j
public class AzureChatTest {


    public static void main(String[] args) {

        System.out.println("test");
    }

    private AzureChat chatGPT;

    @Before
    public void before() {
//        Proxy proxy = Proxys.http("127.0.0.1", 1080);
//
//        chatGPT = ChatGPT.builder()
//            .apiKey("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")
//            .timeout(900)
//            .proxy(proxy)
//            .apiHost("https://api.openai.com/") //代理地址
//            .build()
//            .init();

//        CreditGrantsResponse response = chatGPT.creditGrants();
//        log.info("余额：{}", response.getTotalAvailable());

        //
//     "model": "rta-data",
//         "api_key": "e7fc281482f6473493f99f05c5fe563f",
//         "base_url": "https://rta-data.openai.azure.com/",
//         "api_type": "azure",
//         "api_version": "2024-02-15-preview"

        chatGPT = new AzureChat(
            com.plexpt.chatgpt.relay.ClientConfig.builder()
                .apiKeys(Arrays.asList("e7fc281482f6473493f99f05c5fe563f"))
                .apiVersion("2024-02-15-preview")
                .apiHost("https://rta-data.openai.azure.com/")
                .build(),
            "rta-data",
            "2024-02-15-preview"
        );

        chatGPT.init();
    }

    @org.junit.Test
    public void chat() {
        Message system = Message.ofSystem("你现在是一个诗人，专门写七言绝句");
        Message message = Message.of("写一段七言绝句诗，题目是：火锅！");

        ChatCompletion chatCompletion = ChatCompletion.builder()
            .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
            .messages(Arrays.asList(system, message))
            .maxTokens(3000)
            .temperature(0.9)
            .build();
        ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
        Message res = response.getChoices().get(0).getMessage();
        System.out.println(res);
        System.out.println(response.getUsage());
    }

    @org.junit.Test
    public void chatmsg() {
        String res = chatGPT.chat("写一段七言绝句诗，题目是：火锅！");
        System.out.println(res);
    }

    /**
     * 测试tokens数量计算
     */
    @org.junit.Test
    public void tokens() {
        Message system = Message.ofSystem("你现在是一个诗人，专门写七言绝句");
        Message message = Message.of("写一段七言绝句诗，题目是：火锅！");

        ChatCompletion chatCompletion1 = ChatCompletion.builder()
            .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
            .messages(Arrays.asList(system, message))
            .maxTokens(3000)
            .temperature(0.9)
            .build();
        ChatCompletion chatCompletion2 = ChatCompletion.builder()
            .model(ChatCompletion.Model.GPT_4.getName())
            .messages(Arrays.asList(system, message))
            .maxTokens(3000)
            .temperature(0.9)
            .build();

        log.info("{} tokens: {}", chatCompletion1.getModel(), chatCompletion1.countTokens());
        log.info("{} tokens: {}", chatCompletion2.getModel(), chatCompletion2.countTokens());
    }
}
