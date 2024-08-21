package com.example.physio.retrofitutil

import com.example.physio.models.ApiResponse
import com.example.physio.models.ExerciseDetailedResponse
import com.example.physio.models.ListDiseaseResponse
import com.example.physio.models.ListEquipmentResponse
import com.example.physio.models.ListFilteredExerciseResponse
import com.example.physio.models.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {
    @FormUrlEncoded
    @POST("register-user")
    fun performUserSignIn(
        @Field("name") name: String?,
        @Field("lastname") lastname: String?,
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("user_type") user_type: String?,
        @Field("license_number") license_number: String?
    ): Call<ApiResponse?>?

    @FormUrlEncoded
    @POST("login-user")
    fun performUserLogin(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<LoginResponse?>?

    @get:POST("get-list/equipments")
    val equipments: Call<ListEquipmentResponse?>?

    @get:POST("get-list/diseases")
    val diseases: Call<ListDiseaseResponse?>?

    @FormUrlEncoded
    @POST("get-list/favorites")
    fun getFavoritesList(@Field("id_user") user_id: String?): Call<ListFilteredExerciseResponse?>?

    @FormUrlEncoded
    @POST("exercise/find")
    fun getExercises(
        @Field("id_equipment") id_equipmentList: String?,
        @Field("id_disease") id_diseasesList: String?
    ): Call<ListFilteredExerciseResponse?>?

    @FormUrlEncoded
    @POST("exercise/read")
    fun getExerciseDetails(@Field("id_exercise") id_exercise: Int): Call<ExerciseDetailedResponse?>?

    @FormUrlEncoded
    @POST("exercise/add")
    fun addExerciseDetailed(
        @Field("id_disease") id_diseasesList: String?,
        @Field("title") title: String?,
        @Field("id_equipment") id_equipmentList: String?,
        @Field("description") description: String?
    ): Call<ApiResponse?>?

    @FormUrlEncoded
    @POST("user/edit")
    fun editUser(
        @Field("user_id") id: Int,
        @Field("newEmail") newEmail: String?,
        @Field("firstName") firstName: String?,
        @Field("lastName") lastName: String?,
        @Field("password") password: String?
    ): Call<ApiResponse?>?

    @FormUrlEncoded
    @POST("user/verify-password")
    fun verifyPassword(
        @Field("user_id") userId: String?,
        @Field("password") password: String?
    ): Call<ApiResponse?>?

    @FormUrlEncoded
    @POST("user/change-password")
    fun changePassword(
        @Field("email") email: String?,
        @Field("oldPassword") oldPassword: String?,
        @Field("newPassword") newPassword: String?
    ): Call<ApiResponse?>?

    @FormUrlEncoded
    @POST("exercise/favorite/add")
    fun addToFavorites(
        @Field("id_user") userId: Int,
        @Field("id_exercise") exercise_id: Int
    ): Call<ApiResponse?>?

    @FormUrlEncoded
    @POST("exercise/favorite/remove")
    fun removeFromFavorites(
        @Field("id_user") user_id: String?,
        @Field("id_exercise") exercise_id: String?
    ): Call<ApiResponse?>?

    @FormUrlEncoded
    @POST("get-list/favorites")
    fun getFavoritesList(@Field("id_user") user_id: Int): Call<ListFilteredExerciseResponse?>?


    @FormUrlEncoded
    @POST("exercise/edit")
    fun editExercise(
        @Field("id_exercise") id: Int,
        @Field("title") title: String?,
        @Field("description") description: String?
    ): Call<ApiResponse?>?

    @FormUrlEncoded
    @POST("exercise/remove")
    fun removeExercise(@Field("id_exercise") id: Int): Call<ApiResponse?>?
}
