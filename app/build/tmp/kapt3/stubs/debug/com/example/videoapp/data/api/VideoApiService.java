package com.example.videoapp.data.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\bf\u0018\u00002\u00020\u0001J\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0005J5\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00070\u00032\b\b\u0001\u0010\b\u001a\u00020\t2\b\b\u0003\u0010\n\u001a\u00020\t2\b\b\u0003\u0010\u000b\u001a\u00020\tH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\fJ+\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00070\u00032\b\b\u0003\u0010\n\u001a\u00020\t2\b\b\u0003\u0010\u000b\u001a\u00020\tH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000eJ!\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00070\u00032\b\b\u0001\u0010\u0010\u001a\u00020\u0011H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0012J+\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u00032\b\b\u0001\u0010\u0015\u001a\u00020\u00112\b\b\u0001\u0010\u0016\u001a\u00020\u0011H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0017J5\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00140\u00032\b\b\u0001\u0010\u0015\u001a\u00020\u00112\b\b\u0001\u0010\u0016\u001a\u00020\u00112\b\b\u0003\u0010\u0019\u001a\u00020\u0011H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001aJ5\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00032\b\b\u0001\u0010\u001c\u001a\u00020\u00112\b\b\u0003\u0010\n\u001a\u00020\t2\b\b\u0003\u0010\u000b\u001a\u00020\tH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001dJ!\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00070\u00032\b\b\u0001\u0010\u001c\u001a\u00020\u0011H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0012\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u001f"}, d2 = {"Lcom/example/videoapp/data/api/VideoApiService;", "", "getCategories", "Lretrofit2/Response;", "Lcom/example/videoapp/data/model/CategoryResponse;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCategoryVideos", "Lcom/example/videoapp/data/model/VideoResponse;", "typeId", "", "page", "limit", "(IIILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getHomeVideos", "(IILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getVideoDetail", "ids", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "login", "Lcom/example/videoapp/data/api/LoginResponse;", "username", "password", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "register", "email", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchVideos", "keyword", "(Ljava/lang/String;IILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchVideosForDetail", "app_debug"})
public abstract interface VideoApiService {
    
    @retrofit2.http.GET(value = "/api/video/list")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getHomeVideos(@retrofit2.http.Query(value = "page")
    int page, @retrofit2.http.Query(value = "limit")
    int limit, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.videoapp.data.model.VideoResponse>> $completion);
    
    @retrofit2.http.GET(value = "/api/video/type")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getCategories(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.videoapp.data.model.CategoryResponse>> $completion);
    
    @retrofit2.http.GET(value = "/api/video/list")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getCategoryVideos(@retrofit2.http.Query(value = "type_id")
    int typeId, @retrofit2.http.Query(value = "page")
    int page, @retrofit2.http.Query(value = "limit")
    int limit, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.videoapp.data.model.VideoResponse>> $completion);
    
    @retrofit2.http.GET(value = "/api/video/search")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object searchVideos(@retrofit2.http.Query(value = "wd")
    @org.jetbrains.annotations.NotNull
    java.lang.String keyword, @retrofit2.http.Query(value = "page")
    int page, @retrofit2.http.Query(value = "limit")
    int limit, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.videoapp.data.model.VideoResponse>> $completion);
    
    @retrofit2.http.GET(value = "/api/video/detail")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getVideoDetail(@retrofit2.http.Query(value = "ids")
    @org.jetbrains.annotations.NotNull
    java.lang.String ids, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.videoapp.data.model.VideoResponse>> $completion);
    
    @retrofit2.http.GET(value = "/api/video/search")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object searchVideosForDetail(@retrofit2.http.Query(value = "wd")
    @org.jetbrains.annotations.NotNull
    java.lang.String keyword, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.videoapp.data.model.VideoResponse>> $completion);
    
    @retrofit2.http.POST(value = "/api/user/login")
    @retrofit2.http.FormUrlEncoded
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object login(@retrofit2.http.Field(value = "user_name")
    @org.jetbrains.annotations.NotNull
    java.lang.String username, @retrofit2.http.Field(value = "user_pwd")
    @org.jetbrains.annotations.NotNull
    java.lang.String password, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.videoapp.data.api.LoginResponse>> $completion);
    
    @retrofit2.http.POST(value = "/api/user/register")
    @retrofit2.http.FormUrlEncoded
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object register(@retrofit2.http.Field(value = "user_name")
    @org.jetbrains.annotations.NotNull
    java.lang.String username, @retrofit2.http.Field(value = "user_pwd")
    @org.jetbrains.annotations.NotNull
    java.lang.String password, @retrofit2.http.Field(value = "user_email")
    @org.jetbrains.annotations.NotNull
    java.lang.String email, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.example.videoapp.data.api.LoginResponse>> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}