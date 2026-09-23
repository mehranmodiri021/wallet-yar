package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

// Blockchair API Response Models
@JsonClass(generateAdapter = true)
data class BlockchairBtcResponse(
    @param:Json(name = "data") val data: Map<String, BlockchairBtcData>?,
    @param:Json(name = "context") val context: BlockchairContext?
)

@JsonClass(generateAdapter = true)
data class BlockchairBtcData(
    @param:Json(name = "address") val address: BlockchairAddressDetails?
)

@JsonClass(generateAdapter = true)
data class BlockchairEthResponse(
    @param:Json(name = "data") val data: Map<String, BlockchairEthData>?,
    @param:Json(name = "context") val context: BlockchairContext?
)

@JsonClass(generateAdapter = true)
data class BlockchairEthData(
    @param:Json(name = "address") val address: BlockchairAddressDetails?
)

@JsonClass(generateAdapter = true)
data class BlockchairAddressDetails(
    @param:Json(name = "type") val type: String?,
    @param:Json(name = "balance") val balance: Double?,
    @param:Json(name = "balance_usd") val balanceUsd: Double?,
    @param:Json(name = "received") val received: Double?,
    @param:Json(name = "transaction_count") val transactionCount: Int?,
    @param:Json(name = "first_seen_receiving") val firstSeen: String?,
    @param:Json(name = "last_seen_receiving") val lastSeen: String?
)

@JsonClass(generateAdapter = true)
data class BlockchairContext(
    @param:Json(name = "code") val code: Int?,
    @param:Json(name = "market_price_usd") val marketPriceUsd: Double?
)

interface BlockchairApiService {
    @GET("bitcoin/dashboards/address/{address}")
    suspend fun getBitcoinAddress(
        @Path("address") address: String
    ): Response<BlockchairBtcResponse>

    @GET("ethereum/dashboards/address/{address}")
    suspend fun getEthereumAddress(
        @Path("address") address: String
    ): Response<BlockchairEthResponse>

    companion object {
        private const val BASE_URL = "https://api.blockchair.com/"

        fun create(): BlockchairApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(BlockchairApiService::class.java)
        }
    }
}
