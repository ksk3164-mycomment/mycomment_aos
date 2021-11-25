package kr.beimsupicures.mycomment.controllers.main.organization

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.SearchLoader
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.components.adapters.TalkTodayAdapter
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.components.fragments.startLoadingUI
import kr.beimsupicures.mycomment.components.fragments.stopLoadingUI

class OrganizationFragment : BaseFragment() {

    var monTalk: MutableList<TalkModel> = mutableListOf()
    var tueTalk: MutableList<TalkModel> = mutableListOf()
    var wedTalk: MutableList<TalkModel> = mutableListOf()
    var thuTalk: MutableList<TalkModel> = mutableListOf()
    var friTalk: MutableList<TalkModel> = mutableListOf()
    var satTalk: MutableList<TalkModel> = mutableListOf()
    var sunTalk: MutableList<TalkModel> = mutableListOf()

    lateinit var rvMon: RecyclerView
    lateinit var rvTue: RecyclerView
    lateinit var rvWed: RecyclerView
    lateinit var rvThu: RecyclerView
    lateinit var rvFri: RecyclerView
    lateinit var rvSat: RecyclerView
    lateinit var rvSun: RecyclerView

    lateinit var monAdapter: TalkTodayAdapter
    lateinit var tueAdapter: TalkTodayAdapter
    lateinit var wedAdapter: TalkTodayAdapter
    lateinit var thuAdapter: TalkTodayAdapter
    lateinit var friAdapter: TalkTodayAdapter
    lateinit var satAdapter: TalkTodayAdapter
    lateinit var sunAdapter: TalkTodayAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_organization, container, false)
    }

    override fun onResume() {
        super.onResume()
        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            startLoadingUI()
            rvMon = view.findViewById(R.id.rv_mon)
            monAdapter = TalkTodayAdapter(activity, monTalk)
            monAdapter.setHasStableIds(true)
            rvMon.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvMon.adapter = monAdapter

            rvTue = view.findViewById(R.id.rv_tue)
            tueAdapter = TalkTodayAdapter(activity, tueTalk)
            tueAdapter.setHasStableIds(true)
            rvTue.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvTue.adapter = tueAdapter

            rvWed = view.findViewById(R.id.rv_wed)
            wedAdapter = TalkTodayAdapter(activity, wedTalk)
            wedAdapter.setHasStableIds(true)
            rvWed.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvWed.adapter = wedAdapter

            rvThu = view.findViewById(R.id.rv_thu)
            thuAdapter = TalkTodayAdapter(activity, thuTalk)
            thuAdapter.setHasStableIds(true)
            rvThu.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvThu.adapter = thuAdapter

            rvFri = view.findViewById(R.id.rv_fri)
            friAdapter = TalkTodayAdapter(activity, friTalk)
            friAdapter.setHasStableIds(true)
            rvFri.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvFri.adapter = friAdapter

            rvSat = view.findViewById(R.id.rv_sat)
            satAdapter = TalkTodayAdapter(activity, satTalk)
            satAdapter.setHasStableIds(true)
            rvSat.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvSat.adapter = satAdapter

            rvSun = view.findViewById(R.id.rv_sun)
            sunAdapter = TalkTodayAdapter(activity, sunTalk)
            sunAdapter.setHasStableIds(true)
            rvSun.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvSun.adapter = sunAdapter

        }
    }

    override fun fetchModel() {
        super.fetchModel()

        SearchLoader.shared.searchTalk("") { talk ->

            monTalk = talk.filter { it.live == 2 && it.open_day.contains("월") }.toMutableList()
            monAdapter.items = monTalk
            rvMon.setHasFixedSize(true)

            tueTalk = talk.filter { it.live == 2 && it.open_day.contains("화") }.toMutableList()
            tueAdapter.items = tueTalk
            rvTue.setHasFixedSize(true)

            wedTalk = talk.filter { it.live == 2 && it.open_day.contains("수") }.toMutableList()
            wedAdapter.items = wedTalk
            rvWed.setHasFixedSize(true)

            thuTalk = talk.filter { it.live == 2 && it.open_day.contains("목") }.toMutableList()
            thuAdapter.items = thuTalk
            rvThu.setHasFixedSize(true)

            friTalk = talk.filter { it.live == 2 && it.open_day.contains("금") }.toMutableList()
            friAdapter.items = friTalk
            rvFri.setHasFixedSize(true)

            satTalk = talk.filter { it.live == 2 && it.open_day.contains("토") }.toMutableList()
            satAdapter.items = satTalk
            rvSat.setHasFixedSize(true)

            sunTalk = talk.filter { it.live == 2 && it.open_day.contains("일") }.toMutableList()
            sunAdapter.items = sunTalk
            rvSun.setHasFixedSize(true)

            Handler(Looper.getMainLooper()).postDelayed({
                monAdapter.notifyDataSetChanged()
                tueAdapter.notifyDataSetChanged()
                wedAdapter.notifyDataSetChanged()
                thuAdapter.notifyDataSetChanged()
                friAdapter.notifyDataSetChanged()
                satAdapter.notifyDataSetChanged()
                sunAdapter.notifyDataSetChanged()
                stopLoadingUI()
            }, 500)


        }

    }
}