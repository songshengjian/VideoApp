package com.example.videoapp.ui.categories;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\u0010\b\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u0000 )2\u00020\u0001:\u0001)B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0017\u001a\u00020\u0018H\u0002J\u0012\u0010\u0019\u001a\u00020\u00182\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH\u0016J$\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010!2\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH\u0016J\b\u0010\"\u001a\u00020\u0018H\u0016J\u001a\u0010#\u001a\u00020\u00182\u0006\u0010$\u001a\u00020\u001d2\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH\u0016J\u0010\u0010%\u001a\u00020\u00042\u0006\u0010&\u001a\u00020\u0004H\u0002J\b\u0010\'\u001a\u00020\u0018H\u0002J\b\u0010(\u001a\u00020\u0018H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\u00020\u00068BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\b\u0010\tR,\u0010\n\u001a \u0012\u0004\u0012\u00020\f\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00040\u000e0\r0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0011\u001a\u00020\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0015\u0010\u0016\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006*"}, d2 = {"Lcom/example/videoapp/ui/categories/CategoriesFragment;", "Landroidx/fragment/app/Fragment;", "()V", "TAG", "", "_binding", "Lcom/example/videoapp/databinding/FragmentCategoriesBinding;", "binding", "getBinding", "()Lcom/example/videoapp/databinding/FragmentCategoriesBinding;", "childCategories", "", "", "", "Lkotlin/Pair;", "currentChildTypeId", "unifiedTypeId", "viewModel", "Lcom/example/videoapp/ui/categories/CategoriesViewModel;", "getViewModel", "()Lcom/example/videoapp/ui/categories/CategoriesViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "observeViewModel", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "onDestroyView", "onViewCreated", "view", "parseFirstEpisodeUrl", "playUrl", "setupChildNavigation", "setupRecyclerView", "Companion", "app_debug"})
public final class CategoriesFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable
    private com.example.videoapp.databinding.FragmentCategoriesBinding _binding;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String TAG = "CategoriesFragment";
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy viewModel$delegate = null;
    private int unifiedTypeId = 0;
    private int currentChildTypeId = 0;
    @org.jetbrains.annotations.NotNull
    private final java.util.Map<java.lang.Integer, java.util.List<kotlin.Pair<java.lang.Integer, java.lang.String>>> childCategories = null;
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String ARG_TYPE_ID = "type_id";
    @org.jetbrains.annotations.NotNull
    public static final com.example.videoapp.ui.categories.CategoriesFragment.Companion Companion = null;
    
    public CategoriesFragment() {
        super();
    }
    
    private final com.example.videoapp.databinding.FragmentCategoriesBinding getBinding() {
        return null;
    }
    
    private final com.example.videoapp.ui.categories.CategoriesViewModel getViewModel() {
        return null;
    }
    
    @java.lang.Override
    public void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override
    public void onViewCreated(@org.jetbrains.annotations.NotNull
    android.view.View view, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupRecyclerView() {
    }
    
    private final void setupChildNavigation() {
    }
    
    /**
     * 解析第一个剧集的播放 URL
     */
    private final java.lang.String parseFirstEpisodeUrl(java.lang.String playUrl) {
        return null;
    }
    
    private final void observeViewModel() {
    }
    
    @java.lang.Override
    public void onDestroyView() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/example/videoapp/ui/categories/CategoriesFragment$Companion;", "", "()V", "ARG_TYPE_ID", "", "newInstance", "Lcom/example/videoapp/ui/categories/CategoriesFragment;", "typeId", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.example.videoapp.ui.categories.CategoriesFragment newInstance(int typeId) {
            return null;
        }
    }
}