package com.example.videoapp.ui.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u0013B\'\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u0012\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\u0002\u0010\tJ\b\u0010\n\u001a\u00020\u000bH\u0016J\u001c\u0010\f\u001a\u00020\b2\n\u0010\r\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u000e\u001a\u00020\u000bH\u0016J\u001c\u0010\u000f\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000bH\u0016R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/example/videoapp/ui/home/HomeSectionVideoAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/example/videoapp/ui/home/HomeSectionVideoAdapter$VideoViewHolder;", "videos", "", "Lcom/example/videoapp/data/model/Video;", "onItemClick", "Lkotlin/Function1;", "", "(Ljava/util/List;Lkotlin/jvm/functions/Function1;)V", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "VideoViewHolder", "app_debug"})
public final class HomeSectionVideoAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.example.videoapp.ui.home.HomeSectionVideoAdapter.VideoViewHolder> {
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.example.videoapp.data.model.Video> videos = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.jvm.functions.Function1<com.example.videoapp.data.model.Video, kotlin.Unit> onItemClick = null;
    
    public HomeSectionVideoAdapter(@org.jetbrains.annotations.NotNull
    java.util.List<com.example.videoapp.data.model.Video> videos, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super com.example.videoapp.data.model.Video, kotlin.Unit> onItemClick) {
        super();
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public com.example.videoapp.ui.home.HomeSectionVideoAdapter.VideoViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull
    com.example.videoapp.ui.home.HomeSectionVideoAdapter.VideoViewHolder holder, int position) {
    }
    
    @java.lang.Override
    public int getItemCount() {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/example/videoapp/ui/home/HomeSectionVideoAdapter$VideoViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "binding", "Lcom/example/videoapp/databinding/ItemHomeVideoBinding;", "context", "Landroid/content/Context;", "(Lcom/example/videoapp/ui/home/HomeSectionVideoAdapter;Lcom/example/videoapp/databinding/ItemHomeVideoBinding;Landroid/content/Context;)V", "bind", "", "video", "Lcom/example/videoapp/data/model/Video;", "app_debug"})
    public final class VideoViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull
        private final com.example.videoapp.databinding.ItemHomeVideoBinding binding = null;
        @org.jetbrains.annotations.NotNull
        private final android.content.Context context = null;
        
        public VideoViewHolder(@org.jetbrains.annotations.NotNull
        com.example.videoapp.databinding.ItemHomeVideoBinding binding, @org.jetbrains.annotations.NotNull
        android.content.Context context) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull
        com.example.videoapp.data.model.Video video) {
        }
    }
}