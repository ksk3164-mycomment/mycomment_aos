package kr.beimsupicures.mycomment.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.list_item_cast.view.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.*

class TalkDetailCastAdapter(val activity: FragmentActivity?, var items: MutableList<TMDBCastModel>) :
    RecyclerView.Adapter<TalkDetailCastAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_cast,
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
                holder.bind(items[holder.bindingAdapterPosition], holder.bindingAdapterPosition)
            }

        }
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleLabel = itemView.titleLabel
        val characterLabel = itemView.characterLabel
        val profileView = itemView.profileView

        fun bind(viewModel: TMDBCastModel, position: Int) {

            Glide.with(itemView.context).load("https://image.tmdb.org/t/p/w500"+viewModel.profile_path)
                .placeholder(R.color.colorGrey)
                .transform(CenterCrop(),RoundedCorners(30))
                .into(profileView)
            titleLabel.text = viewModel.name
            characterLabel.text =  viewModel.character


        }
    }
}
