package com.plexpt.chatgpt.relay;

import com.plexpt.chatgpt.entity.billing.CreditGrantsResponse;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import java.math.BigDecimal;
import java.util.List;


/**
 * open ai 客户端
 *
 * @author plexpt
 */

public interface ChatGPT {


    /**
     * 初始化：与服务端建立连接，成功后可直接与服务端进行对话
     */
    ChatGPT init();


    /**
     * 最新版的GPT-3.5 chat completion 更加贴近官方网站的问答模型
     *
     * @param chatCompletion 问答参数，即咨询的内容
     * @return 服务端的问答响应
     */
    ChatCompletionResponse chatCompletion(ChatCompletion chatCompletion);

    /**
     * 支持多个问答参数来与服务端进行对话
     *
     * @param messages 问答参数，即咨询的内容
     * @return 服务端的问答响应
     */
    ChatCompletionResponse chatCompletion(List<Message> messages);

    /**
     * 与服务端进行对话
     *
     * @param message 问答参数，即咨询的内容
     * @return 服务端的问答响应
     */
    String chat(String message);

    /**
     * 余额查询
     *
     * @return 余额总金额及明细
     */
    CreditGrantsResponse creditGrants();

    /**
     * 余额查询
     *
     * @return 余额总金额
     */
    BigDecimal balance();
}
