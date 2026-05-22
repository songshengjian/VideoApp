package com.example.videoapp.ui.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0002\u0016\u0017B\u0019\u0012\u0012\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004\u00a2\u0006\u0002\u0010\u0007J\b\u0010\u000b\u001a\u00020\fH\u0016J\u001c\u0010\r\u001a\u00020\u00062\n\u0010\u000e\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u000f\u001a\u00020\fH\u0016J\u001c\u0010\u0010\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\fH\u0016J\u0014\u0010\u0014\u001a\u00020\u00062\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\n0\tR\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/example/videoapp/ui/home/HomeSectionAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/example/videoapp/ui/home/HomeSectionAdapter$SectionViewHolder;", "onVideoClick", "Lkotlin/Function1;", "Lcom/example/videoapp/data/model/Video;", "", "(Lkotlin/jvm/functions/Function1;)V", "sections", "", "Lcom/example/videoapp/ui/home/HomeSection;", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "setSections", "newSections", "SectionVideoAdapter", "SectionViewHolder", "app_debug"})
public final class HomeSectionAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.example.videoapp.ui.home.HomeSectionAdapter.SectionViewHolder> {
    @org.jetbrains.annotations.NotNull
    private final kotlin.jvm.functions.Function1<com.example.videoapp.data.model.Video, kotlin.Unit> onVideoClick = null;
    @org.jetbrains.annotations.NotNull
    private java.util.List<com.example.videoapp.ui.home.HomeSection> sections;
    
    public HomeSectionAdapter(@org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super com.example.videoapp.data.model.Video, kotlin.Unit> onVideoClick) {
        super();
    }
    
    public final void setSections(@org.jetbrains.annotations.NotNull
    java.util.List<com.example.videoapp.ui.home.HomeSection> newSections) {
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public com.example.videoapp.ui.home.HomeSectionAdapter.SectionViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull
    com.example.videoapp.ui.home.HomeSectionAdapter.SectionViewHolder holder, int position) {
    }
    
    @java.lang.Override
    public int getItemCount() {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u0010\u0012\f\u0012\n0\u0002R\u00060\u0000R\u00020\u00030\u0001:\u0001\u0014B\'\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\u0002\u0010\nJ\b\u0010\u000b\u001a\u00020\fH\u0016J \u0010\r\u001a\u00020\t2\u000e\u0010\u000e\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u000f\u001a\u00020\fH\u0016J \u0010\u0010\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\fH\u0016R\u001a\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/example/videoapp/ui/home/HomeSectionAdapter$SectionVideoAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/example/videoapp/ui/home/HomeSectionAdapter$SectionVideoAdapter$VideoViewHolder;", "Lcom/example/videoapp/ui/home/HomeSectionAdapter;", "videos", "", "Lcom/example/videoapp/data/model/Video;", "onItemClick", "Lkotlin/Function1;", "", "(Lcom/example/videoapp/ui/home/HomeSectionAdapter;Ljava/util/List;Lkotlin/jvm/functions/Function1;)V", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "VideoViewHolder", "app_debug"})
    public final class SectionVideoAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.example.videoapp.ui.home.HomeSectionAdapter.SectionVideoAdapter.VideoViewHolder> {
        @org.jetbrains.annotations.NotNull
        private final java.util.List<com.example.videoapp.data.model.Video> videos = null;
        @org.jetbrains.annotations.NotNull
        private final kotlin.jvm.functions.Function1<com.example.videoapp.data.model.Video, kotlin.Unit> onItemClick = null;
        
        public SectionVideoAdapter(@org.jetbrains.annotations.NotNull
        java.util.List<com.example.videoapp.data.model.Video> videos, @org.jetbrains.annotations.NotNull
        kotlin.jvm.functions.Function1<? super com.example.videoapp.data.model.Video, kotlin.Unit> onItemClick) {
            super();
        }
        
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public com.example.videoapp.ui.home.HomeSectionAdapter.SectionVideoAdapter.VideoViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull
        com.example.videoapp.ui.home.HomeSectionAdapter.SectionVideoAdapter.VideoViewHolder holder, int position) {
        }
        
        @java.lang.Override
        public int getItemCount() {
            return 0;
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/example/videoapp/ui/home/HomeSectionAdapter$SectionVideoAdapter$VideoViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "binding", "Lcom/example/videoapp/databinding/ItemHomeVideoBinding;", "context", "Landroid/content/Context;", "(Lcom/example/videoapp/ui/home/HomeSectionAdapter$SectionVideoAdapter;Lcom/example/videoapp/databinding/ItemHomeVideoBinding;Landroid/content/Context;)V", "bind", "", "video", "Lcom/example/videoapp/data/model/Video;", "app_debug"})
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/example/videoapp/ui/home/HomeSectionAdapter$SectionViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "binding", "Lcom/example/videoapp/databinding/ItemHomeSectionBinding;", "context", "Landroid/content/Context;", "(Lcom/example/videoapp/ui/home/HomeSectionAdapter;Lcom/example/videoapp/databinding/ItemHomeSectionBinding;Landroid/content/Context;)V", "bind", "", "section", "Lcom/example/videoapp/ui/home/HomeSection;", "app_debug"})
    public final class SectionViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull
        private final com.example.videoapp.databinding.ItemHomeSectionBinding binding = null;
        @org.jetbrains.annotations.NotNull
        private final android.content.Context context = null;
        
        public SectionViewHolder(@org.jetbrains.annotations.NotNull
        com.example.videoapp.databinding.ItemHomeSectionBinding binding, @org.jetbrains.annotations.NotNull
        android.content.Context context) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull
        com.example.videoapp.ui.home.HomeSection section) {
        }
    }
}