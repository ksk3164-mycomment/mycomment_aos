package kr.beimsupicures.mycomment.controllers.main.feed

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.FeedLoader
import kr.beimsupicures.mycomment.api.models.FeedModel
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.components.adapters.DramaFeedAdapter
import kr.beimsupicures.mycomment.components.fragments.BaseFragment


class DramaFeedFragment(val viewModel: TalkModel) : BaseFragment() {

    var talk: TalkModel? = null
    var items: MutableList<FeedModel> = mutableListOf()
    var page = 1

    lateinit var dramaFeedAdapter: DramaFeedAdapter
    lateinit var countLabel: TextView
    lateinit var tvOderbyViewcount: TextView
    lateinit var tvOderbyCreate: TextView
    lateinit var rvDramaFeed: RecyclerView

    companion object {

        fun newInstance(
            viewModel: TalkModel
        ): DramaFeedFragment {
            return DramaFeedFragment(viewModel)
        }
    }

    override fun loadModel() {
        super.loadModel()
        talk = viewModel
    }

    override fun onResume() {
        super.onResume()
        fetchModel()
        page= 1
    }

    override fun onPause() {
        super.onPause()
        items.clear()
        tvOderbyCreate.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        tvOderbyCreate.setTypeface(tvOderbyCreate.typeface, Typeface.BOLD)
        tvOderbyViewcount.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTextSegment2))
        tvOderbyViewcount.setTypeface(null, Typeface.NORMAL)
    }

    override fun fetchModel() {
        super.fetchModel()

        talk?.let { talk ->

            FeedLoader.shared.getFeedList(
                talk.id, true,0
            ) { items ->
                this.items = items
                dramaFeedAdapter.items = this.items
                dramaFeedAdapter.notifyDataSetChanged()

            }
            FeedLoader.shared.getFeedCount(talk_id = talk.id){ count ->
                countLabel.text = "${count} ${activity?.getString(R.string.feed_count)}"
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drama_feed, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            countLabel = view.findViewById(R.id.countLabel)
            tvOderbyViewcount = view.findViewById(R.id.tvOderbyViewcount)
            tvOderbyCreate = view.findViewById(R.id.tvOderbyCreate)

            rvDramaFeed = view.findViewById(R.id.rvDramaFeed)
            dramaFeedAdapter = DramaFeedAdapter(activity,items)

            rvDramaFeed.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            rvDramaFeed.setHasFixedSize(true)

//            rvDramaFeed.layoutManager = LinearLayoutManager(context)
//            rvDramaFeed.layoutManager = GridLayoutManager(context,2)
            rvDramaFeed.adapter = dramaFeedAdapter

            rvDramaFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    rvDramaFeed.layoutManager?.let {
                        (it as? StaggeredGridLayoutManager)?.let { layoutManager ->
                            recyclerView.adapter?.let { adapter ->
                                val lastPositions = IntArray(layoutManager.spanCount)
                                layoutManager.findLastVisibleItemPositions(lastPositions)
                                val lastVisibleItemPosition = findMax(lastPositions)
                                if (lastVisibleItemPosition == adapter.itemCount - 1) {

                                    talk?.let { talk ->
                                        FeedLoader.shared.getFeedList(
                                            talk_id = talk.id,reset = false,page=page
                                        ) { talk ->
                                            page+=1
                                            if (page!=0){
                                                val newtalk = ArrayList<FeedModel>()
                                                newtalk.addAll(talk)
//                                            this@DramaFeedFragment.items = talk.toMutableList()
                                                dramaFeedAdapter.items = newtalk
                                                dramaFeedAdapter.notifyDataSetChanged()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            })

            tvOderbyCreate.setOnClickListener {
                tvOderbyCreate.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
                tvOderbyCreate.setTypeface(tvOderbyCreate.typeface, Typeface.BOLD)
                tvOderbyViewcount.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTextSegment2))
                tvOderbyViewcount.setTypeface(null, Typeface.NORMAL)

                items.sortByDescending { it.c_ts }
                dramaFeedAdapter.items = this.items
                dramaFeedAdapter.notifyDataSetChanged()

            }

            tvOderbyViewcount.setOnClickListener {
                tvOderbyViewcount.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
                tvOderbyViewcount.setTypeface(tvOderbyViewcount.typeface, Typeface.BOLD)
                tvOderbyCreate.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTextSegment2))
                tvOderbyCreate.setTypeface(null, Typeface.NORMAL)

                items.sortByDescending { it.view_cnt }
                dramaFeedAdapter.items = this.items
                dramaFeedAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun findMax(lastPositions: IntArray): Int {
        var max = lastPositions[0]
        for (value in lastPositions) {
            if (value > max) {
                max = value
            }
        }
        return max
    }
}