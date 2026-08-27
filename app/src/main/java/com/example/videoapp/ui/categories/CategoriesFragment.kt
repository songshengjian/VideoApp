package com.example.videoapp.ui.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videoapp.R
import com.example.videoapp.databinding.FragmentCategoriesBinding
import com.example.videoapp.ui.home.VideoAdapter
import com.example.videoapp.ui.detail.VideoDetailActivity

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private val TAG = "CategoriesFragment"

    private val viewModel: CategoriesViewModel by viewModels()

    private var unifiedTypeId: Int = 0
    private var currentChildTypeId: Int = 0

    // 大类导航（同步 Web 端：1=电影，2=连续剧，3=综艺，4=动漫，5=短剧）
    private val parentCategories = listOf(
        Pair(1, "电影"),
        Pair(2, "连续剧"),
        Pair(3, "综艺"),
        Pair(4, "动漫"),
        Pair(5, "短剧")
    )

    // 当前大类的子分类列表（动态从 API 加载）
    private var currentChildCategories: List<Pair<Int, String>> = listOf(Pair(0, "全部"))

    companion object {
        private const val ARG_TYPE_ID = "type_id"

        fun newInstance(typeId: Int): CategoriesFragment {
            val fragment = CategoriesFragment()
            val args = Bundle()
            args.putInt(ARG_TYPE_ID, typeId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            unifiedTypeId = it.getInt(ARG_TYPE_ID, 0)
            currentChildTypeId = 0
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupParentNavigation()
        observeViewModel()

        // 从底部导航进入时 typeId=0，默认选中"电影"；从首页点击分类进入时用传入的 typeId
        if (unifiedTypeId <= 0) {
            unifiedTypeId = 1
        }
        selectParentCategory(unifiedTypeId)

        // 加载分类列表并构建子导航
        viewModel.loadCategories()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewCategories.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewCategories.adapter = VideoAdapter { video ->
            try {
                val intent = android.content.Intent(requireContext(), VideoDetailActivity::class.java)
                intent.putExtra(VideoDetailActivity.EXTRA_VIDEO_ID, video.vod_id.toString())
                intent.putExtra(VideoDetailActivity.EXTRA_VIDEO_TITLE, video.vod_name)

                val videoJson = com.google.gson.Gson().toJson(video)
                intent.putExtra(VideoDetailActivity.EXTRA_VIDEO_DATA, videoJson)

                startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Error opening video detail", e)
                Toast.makeText(requireContext(), "打开详情页失败：${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupParentNavigation() {
        val container = binding.parentNavContainer
        container.removeAllViews()

        parentCategories.forEachIndexed { index, pair ->
            val (typeId, name) = pair
            val itemView = LayoutInflater.from(requireContext()).inflate(
                R.layout.item_nav_category, container, false
            )
            val textView = itemView.findViewById<TextView>(R.id.text_view_nav_item)
            textView.text = name

            itemView.setOnClickListener {
                selectParentCategory(typeId)
            }

            container.addView(itemView)
        }
    }

    private fun selectParentCategory(typeId: Int) {
        if (typeId == unifiedTypeId && currentChildTypeId == 0 && !binding.childNavContainer.let { it.childCount == 0 }) {
            return
        }
        unifiedTypeId = typeId
        currentChildTypeId = 0
        updateParentNavUI()
        viewModel.loadCategoryVideos(unifiedTypeId, 0)
        // 子分类需等分类列表加载后重建
    }

    private fun updateParentNavUI() {
        for (i in 0 until binding.parentNavContainer.childCount) {
            val child = binding.parentNavContainer.getChildAt(i)
            val textView = child.findViewById<TextView>(R.id.text_view_nav_item)
            val isSelected = parentCategories.getOrNull(i)?.first == unifiedTypeId
            child.isSelected = isSelected
            textView.setTextColor(
                resources.getColor(if (isSelected) android.R.color.white else R.color.text_secondary, requireContext().theme)
            )
        }
    }

    private fun setupChildNavigation() {
        val container = binding.childNavContainer
        container.removeAllViews()

        currentChildCategories.forEachIndexed { index, pair ->
            val (childTypeId, name) = pair
            val itemView = LayoutInflater.from(requireContext()).inflate(
                R.layout.item_nav_category, container, false
            )
            val textView = itemView.findViewById<TextView>(R.id.text_view_nav_item)
            textView.text = name

            if (index == 0) {
                itemView.isSelected = true
                textView.setTextColor(resources.getColor(android.R.color.white, requireContext().theme))
            }

            itemView.setOnClickListener {
                for (i in 0 until container.childCount) {
                    val child = container.getChildAt(i)
                    child.isSelected = false
                    child.findViewById<TextView>(R.id.text_view_nav_item).setTextColor(
                        resources.getColor(R.color.text_secondary, requireContext().theme)
                    )
                }
                itemView.isSelected = true
                textView.setTextColor(resources.getColor(android.R.color.white, requireContext().theme))

                currentChildTypeId = childTypeId
                viewModel.loadCategoryVideos(unifiedTypeId, childTypeId)
            }

            container.addView(itemView)
        }
    }

    private fun observeViewModel() {
        viewModel.hierarchicalCategories.observe(viewLifecycleOwner) { hierarchicalCats ->
            hierarchicalCats?.let { cats ->
                val parentCategory = cats.find { it.type_id == unifiedTypeId }
                if (parentCategory != null && parentCategory.children.isNotEmpty()) {
                    currentChildCategories = listOf(Pair(0, "全部")) + parentCategory.children.map {
                        Pair(it.type_id, it.type_name)
                    }
                } else {
                    currentChildCategories = listOf(Pair(0, "全部"))
                }
                setupChildNavigation()
            }
        }

        viewModel.videos.observe(viewLifecycleOwner) { videos ->
            videos?.let {
                (binding.recyclerViewCategories.adapter as? VideoAdapter)?.submitList(it)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarCategories.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), "加载失败：$it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
