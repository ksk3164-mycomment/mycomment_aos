package kr.beimsupicures.mycomment.components.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_item_original.view.*
import kotlinx.android.synthetic.main.list_item_original.view.profileView
import kotlinx.android.synthetic.main.list_item_original.view.titleLabel
import kotlinx.android.synthetic.main.list_item_original_detail.view.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.OriginalDetailModel
import kr.beimsupicures.mycomment.api.models.OriginalModel

class OriginalChapterAdapter(
    val activity: FragmentActivity?,
    var items: MutableList<OriginalDetailModel>,
    var onClickChapter: onClickChapter
) :
    RecyclerView.Adapter<OriginalChapterAdapter.ViewHolder>() {

    var row_index : Int? = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_original_detail,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {

            is ViewHolder -> {
                holder.bind(items[position], position)
                if(row_index == position) {
                    holder.ivPlay.visibility = View.VISIBLE
                    holder.ivBlur.visibility = View.VISIBLE
                }
                else {
                    holder.ivPlay.visibility = View.GONE
                    holder.ivBlur.visibility = View.GONE
                }
            }



        }
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileView = itemView.profileView
        val titleLabel = itemView.titleLabel
        val playtimeLabel = itemView.playtimeLabel
        val ivPlay = itemView.ivPlay
        val ivBlur = itemView.ivBlur

        fun bind(viewModel: OriginalDetailModel, position: Int) {

            Glide.with(itemView.context).load(viewModel.thumbnail_url)
                .placeholder(R.color.colorGrey)
                .transform(CenterCrop(), RoundedCorners(30))
                .into(profileView)
            titleLabel.text = viewModel.sub_title
            playtimeLabel.text = (viewModel.play_time?.div(60)).toString()+" ${activity?.getString(R.string.minutes)}"

            profileView.setOnClickListener {
                activity?.let { activity ->
                    row_index = position
                    viewModel.chapter_seq?.let { it -> onClickChapter.onClick(it) }
                    notifyDataSetChanged()
                }
            }

        }
    }
}

interface onClickChapter {

    fun onClick(chapter: Int)

}

