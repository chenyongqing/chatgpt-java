package com.plexpt.chatgpt.relay.azure.api;

import com.plexpt.chatgpt.entity.audio.AudioResponse;
import com.plexpt.chatgpt.entity.audio.Transcriptions;
import com.plexpt.chatgpt.entity.billing.CreditGrantsResponse;
import com.plexpt.chatgpt.entity.billing.SubscriptionData;
import com.plexpt.chatgpt.entity.billing.UseageResponse;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.embedding.EmbeddingRequest;
import com.plexpt.chatgpt.entity.embedding.EmbeddingResult;
import com.plexpt.chatgpt.entity.images.Edits;
import com.plexpt.chatgpt.entity.images.Generations;
import com.plexpt.chatgpt.entity.images.ImagesRensponse;
import com.plexpt.chatgpt.entity.images.Variations;
import com.plexpt.chatgpt.relay.Api;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 *
 */
public interface AzureApi extends Api {

    /**
     * chat
     */
    @POST("openai/deployments/{deployName}/chat/completions")
    Single<ChatCompletionResponse> chatCompletion(@Path("deployName") String deployName,
        @Query("api-version") String apiVersion, @Body ChatCompletion chatCompletion);

    /**
     * image_generations
     */
    @POST("v1/images/generations")
    Single<ImagesRensponse> imageGenerations(@Body Generations generations);

    /**
     * image_edits
     */
    @Multipart
    @POST("v1/images/edits")
    Single<ImagesRensponse> imageEdits(@Part() MultipartBody.Part image,
        @Part() MultipartBody.Part mask,
        @PartMap Edits edits);


    /**
     * image_variations
     */
    @Multipart
    @POST("v1/images/variations")
    Single<ImagesRensponse> imageVariations(@Part() MultipartBody.Part image,
        @PartMap Variations variations);

    /**
     * audio_transcriptions
     */
    @Multipart
    @POST("v1/audio/transcriptions")
    Single<AudioResponse> audioTranscriptions(@Part() MultipartBody.Part audio,
        @PartMap Transcriptions transcriptions);

    /**
     * audio_translations
     */
    @Multipart
    @POST("v1/audio/translations")
    Single<AudioResponse> audioTranslations(@Part() MultipartBody.Part audio,
        @PartMap Transcriptions transcriptions);


    /**
     * 余额查询
     */
    @GET("dashboard/billing/credit_grants")
    Single<CreditGrantsResponse> creditGrants();

    /**
     * 余额查询
     */
    @GET("v1/dashboard/billing/subscription")
    Single<SubscriptionData> subscription();

    /**
     * 余额查询
     */
    @GET("v1/dashboard/billing/usage")
    Single<UseageResponse> usage(@Query("start_date") String startDate,
        @Query("end_date") String endDate);


    /**
     * 生成向量
     */
    @POST("v1/embeddings")
    Single<EmbeddingResult> createEmbeddings(@Body EmbeddingRequest request);

}
