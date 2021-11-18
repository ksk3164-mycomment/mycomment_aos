package kr.beimsupicures.mycomment.components.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.FeedLoader
import kr.beimsupicures.mycomment.api.loaders.ReportLoader
import kr.beimsupicures.mycomment.api.models.ReportModel
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.*

class BottomSheetDialog : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.let {

        }
        val user = BaseApplication.shared.getSharedPreferences().getUser()
        val feedUserId = BaseApplication.shared.getSharedPreferences().getFeedDetailUserId()
        var feedId = BaseApplication.shared.getSharedPreferences().getFeedId()

        if (user?.id == feedUserId) {

            view?.findViewById<TextView>(R.id.tvReport)?.visibility = View.GONE
            view?.findViewById<LinearLayout>(R.id.linearLayout)?.visibility = View.VISIBLE

        } else {

            view?.findViewById<TextView>(R.id.tvReport)?.visibility = View.VISIBLE
            view?.findViewById<LinearLayout>(R.id.linearLayout)?.visibility = View.GONE

        }


        view?.findViewById<TextView>(R.id.tvReport)?.setOnClickListener { view ->
            activity?.supportFragmentManager?.let { fragmentManager ->

                ReportDialog(view.context, didSelectAt = { reason ->
                    ReportLoader.shared.report(
                        ReportModel.Category.feed,
                        feedUserId,
                        reason
                    ) {
                        activity?.let{
                            it.alert(it.getString(R.string.report_complete_sub), it.getString(R.string.report_complete)) { }
                        }


                    }
                }).show(fragmentManager, "")
            }

        }
        view?.findViewById<TextView>(R.id.tvCancel)?.setOnClickListener {
            dismiss()
        }
        view?.findViewById<TextView>(R.id.tvModify)?.setOnClickListener {
            dismiss()
            val action = NavigationDirections.actionGlobalDramaFeedModifyFragment()
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(action)
        }
        view?.findViewById<TextView>(R.id.tvDelete)?.setOnClickListener {
            activity?.let{
                it.popup(it.getString(R.string.feed_delete_sub),it.getString(R.string.feed_delete_title)) {
                    FeedLoader.shared.deleteFeedDetail(feedId){
                        dismiss()
                        activity?.onBackPressed()
//                    activity?.supportFragmentManager?.popBackStack()

                    }
                }
            }

        }
    }
}