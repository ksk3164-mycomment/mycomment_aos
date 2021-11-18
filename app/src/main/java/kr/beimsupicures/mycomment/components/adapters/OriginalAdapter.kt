package kr.beimsupicures.mycomment.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.list_item_original.view.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.OriginalModel

class OriginalAdapter(val activity: FragmentActivity?, var items: MutableList<OriginalModel>) :
    RecyclerView.Adapter<OriginalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_original,
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
            }

        }
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileView = itemView.profileView
        val titleLabel = itemView.titleLabel


        fun bind(viewModel: OriginalModel, position: Int) {

            Glide.with(itemView.context).load(viewModel.thumbnail_url)
                .placeholder(R.color.colorGrey)
                .transform(CenterCrop(), RoundedCorners(30))
                .placeholder(R.color.colorGrey)
                .into(profileView)
            titleLabel.text = viewModel.title

            profileView.setOnClickListener {
                activity?.let { activity ->
                    val action = NavigationDirections.actionGlobalOriginalDetailFragment(viewModel)
                    Navigation.findNavController(activity, R.id.nav_host_fragment)
                        .navigate(action)
                }
            }
        }
    }
}
