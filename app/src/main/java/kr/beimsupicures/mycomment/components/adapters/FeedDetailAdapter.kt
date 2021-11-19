package kr.beimsupicures.mycomment.components.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_item_talk.view.*
import kotlinx.android.synthetic.main.list_item_talk.view.profileView
import kotlinx.android.synthetic.main.list_item_talk_comment.view.*
import kotlinx.android.synthetic.main.list_item_talk_content.view.*
import kotlinx.android.synthetic.main.list_item_talk_header.view.contentLabel
import kotlinx.android.synthetic.main.list_item_talk_tab.view.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.CommentLoader
import kr.beimsupicures.mycomment.api.loaders.FeedCommentLoader
import kr.beimsupicures.mycomment.api.loaders.PickLoader
import kr.beimsupicures.mycomment.api.loaders.ReportLoader
import kr.beimsupicures.mycomment.api.models.*
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.dialogs.BubbleUserListDialog
import kr.beimsupicures.mycomment.components.dialogs.ReportDialog
import kr.beimsupicures.mycomment.extensions.*

class FeedDetailAdapter(
    val activity: FragmentActivity?,
    var feed: FeedDetailModel,
    var items: MutableList<CommentModel>,
    val onReplied: (String) -> Unit,
    var onclickInterface: onClickInterface
) :
    RecyclerView.Adapter<FeedDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder4(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_talk_comment,
                parent,
                false
            )
        )
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.contentLabel.isEnabled = false
        holder.itemView.contentLabel.isEnabled = true
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: FeedDetailAdapter.ViewHolder, position: Int) {
        when (holder) {
            is FeedDetailAdapter.ViewHolder4 -> {
                holder.bind(items[holder.bindingAdapterPosition], holder.bindingAdapterPosition)
            }
        }
    }

    open inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView)

    var isLoad = hashMapOf<Int, Boolean>()

    inner class ViewHolder4(itemView: View) : ViewHolder(itemView) {

        val cardView = itemView.constraintLayout
        val profileLayout = itemView.profileLayout
        val replyLayout = itemView.replyLayout
        val likeCountLabel = itemView.likeCountLabel
        val likeView = itemView.likeView
        val tvActorTalk = itemView.tv_actorTalk
        val profileView = itemView.profileView
        val nameLabel = itemView.nameLabel
        val aliasLabel = itemView.aliasLabel
        val contentLabel = itemView.contentLabel
        val timelineLabel = itemView.timelineLabel
        val deleteView = itemView.deleteView
        val reportView = itemView.reportView
        val blockView = itemView.blockView
        val replyView = itemView.replyView
        val likeBackgroundView = itemView.likeBackgroundView


        private fun showLikeMeusers(viewModel: CommentModel): Boolean {
            if (viewModel.isMe) {
                FeedCommentLoader.shared.getFeedPickedUsers(viewModel.id) { list ->
                    activity?.let { activity ->
                        BubbleUserListDialog(activity, list).show()
                    }
                }
                return true
            } else
                return false
        }

        fun bind(viewModel: CommentModel, position: Int) {

            viewModel.owner.let { userModel ->
                Glide.with(itemView.context).load(userModel.profile_image_url)
                    .transform(CenterCrop(), CircleCrop())
                    .override(200, 200)
                    .thumbnail(0.1f)
                    .fallback(R.drawable.bg_drama_thumbnail)
                    .into(profileView)

                profileView.setOnClickListener { view ->
                    val action =
                        NavigationDirections.actionGlobalProfileFragment(userModel.id)
                    view.findNavController().navigate(action)
                }

                nameLabel.text = userModel.nickname
                aliasLabel.text = userModel.title
            }

            activity?.let { activity ->
                likeBackgroundView.background =
                    ContextCompat.getDrawable(
                        activity.baseContext,
                        if (viewModel.isMe) R.drawable.bg_like_box else R.drawable.bg_clear_box
                    )
                likeView.background =
                    ContextCompat.getDrawable(
                        itemView.context,
                        if (viewModel.isMe) R.drawable.bubble_white else {
                            if (viewModel.pick == true) R.drawable.bubble_fill else R.drawable.bubble_empty
                        }
                    )
                likeCountLabel.setTextColor(
                    ContextCompat.getColor(
                        activity.baseContext, if (viewModel.isMe) R.color.white else R.color.black
                    )
                )
                cardView.setBackgroundColor(
                    ContextCompat.getColor(
                        activity.baseContext,
                        if (viewModel.isMe) R.color.paleGrey else android.R.color.white
                    )
                )
                if (viewModel.owner.isActor()) {
                    tvActorTalk.visibility = View.VISIBLE
                    val layoutParams = profileLayout.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.setMargins(0, 8.dp, 0, 0)
                    profileLayout.layoutParams = layoutParams
                    replyLayout.visibility = View.GONE
                    reportView.visibility = View.GONE
                    blockView.visibility = View.GONE
                    deleteView.visibility = if (viewModel.isMe) View.VISIBLE else View.GONE
                } else {
                    tvActorTalk.visibility = View.GONE
                    val layoutParams = profileLayout.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.setMargins(0, 12.dp, 0, 0)
                    profileLayout.layoutParams = layoutParams
                    replyLayout.visibility = View.VISIBLE
                    deleteView.visibility = if (viewModel.isMe) View.VISIBLE else View.GONE
                    blockView.visibility = if (!viewModel.isMe) View.VISIBLE else View.GONE
                    reportView.visibility = if (!viewModel.isMe) View.VISIBLE else View.GONE
                }
            }
            likeCountLabel.text = "${viewModel.pick_count.currencyValue}"

            likeCountLabel.setOnClickListener {
                showLikeMeusers(viewModel)
            }

            likeBackgroundView.setOnClickListener {
                showLikeMeusers(viewModel)
            }

            likeView.setOnClickListener {
                if (showLikeMeusers(viewModel)) {
                    return@setOnClickListener
                }

                isLoad[position]?.let { isLoad ->
                    if (isLoad) return@setOnClickListener
                }

                BaseApplication.shared.getSharedPreferences().getUser()?.let {
                    isLoad[position] = true
                    when (viewModel.pick) {
                        true -> {
                            var newValue = items[bindingAdapterPosition]
                            newValue.pick = false
                            items[bindingAdapterPosition] = newValue

                            PickLoader.shared.unpick(
                                category = PickModel.Category.fcomment,
                                category_id = viewModel.id
                            ) { pickModel ->
                                var newValue = items[bindingAdapterPosition]
                                newValue.pick = pickModel.pick()
                                newValue.pick_count = newValue.pick_count - 1
                                items[bindingAdapterPosition] = newValue

                                val comment = this@FeedDetailAdapter.items
                                FeedCommentLoader.shared.items = comment.toMutableList()

                                isLoad[position] = false
                                notifyItemChanged(bindingAdapterPosition)

                                FirebaseDatabase.getInstance().getReference("feed")
                                    .child("${feed.feed_seq}").child("like")
                                    .setValue("${viewModel.id}/${newValue.pick_count}")
                            }
                        }

                        else -> {
                            var newValue = items[bindingAdapterPosition]
                            newValue.pick = true
                            items[bindingAdapterPosition] = newValue


                            PickLoader.shared.pick(
                                category = PickModel.Category.fcomment,
                                category_owner_id = viewModel.user_id,
                                category_id = viewModel.id
                            ) { pickModel ->
                                var newValue = items[bindingAdapterPosition]
                                newValue.pick = pickModel.pick()
                                newValue.pick_count = newValue.pick_count + 1
                                items[bindingAdapterPosition] = newValue

                                val comment = this@FeedDetailAdapter.items
                                CommentLoader.shared.items = comment.toMutableList()

                                isLoad[bindingAdapterPosition] = false
                                notifyDataSetChanged()

                                FirebaseDatabase.getInstance().getReference("feed")
                                    .child("${feed.feed_seq}").child("like")
                                    .setValue("${viewModel.id}/${newValue.pick_count}")
                            }
                        }
                    }

                } ?: run {
                    activity?.let { activity ->
                        activity.popup(
                            activity.getString(R.string.Doyouwantlogin),
                            activity.getString(R.string.Login)
                        ) {
                            Navigation.findNavController(activity, R.id.nav_host_fragment)
                                .navigate(R.id.action_global_signInFragment)
                        }
                    }
                }
            }
            contentLabel.setTextIsSelectable(false)
            contentLabel.measure(-1, -1)
            contentLabel.setTextIsSelectable(true)
            contentLabel.text = viewModel.content


            timelineLabel.text = "${viewModel.created_at.timeline(itemView.context)}"
            reportView.setOnClickListener { view ->
                activity?.supportFragmentManager?.let { fragmentManager ->

                    ReportDialog(view.context, didSelectAt = { reason ->
                        ReportLoader.shared.report(
                            ReportModel.Category.fcomment,
                            viewModel.id,
                            reason
                        ) {
                            activity.alert(
                                activity.getString(R.string.report_complete_sub),
                                activity.getString(R.string.report_complete)
                            ) { }
                        }
                    }).show(fragmentManager, "")
                }
            }
            blockView.visibility = if (!viewModel.isMe) View.VISIBLE else View.GONE
            blockView.setOnClickListener { view ->
                activity?.let { activity ->
                    activity.popup(
                        activity.getString(R.string.Block_alert),
                        activity.getString(R.string.Block_Comment)
                    ) {
                        items.removeAt(position)
                        notifyDataSetChanged()
                    }
                }
            }

            deleteView.setOnClickListener { view ->
                activity?.let { activity ->
                    activity.popup(
                        activity.getString(R.string.Delete_comment_sub),
                        activity.getString(R.string.Delete_comment)
                    ) {
                        FeedCommentLoader.shared.deleteFeedComment(viewModel.id) { comment ->

                        }
                        FeedCommentLoader.shared.getFeedCommentCount(feed.feed_seq) { count ->

                            var mils = System.currentTimeMillis()

                            // Write a message to the database
                            val database = FirebaseDatabase.getInstance()
                            val myRef = database.getReference("feed").child("${feed.feed_seq}")
                            myRef.setValue(HashMap<String, String>().apply {
                                put("total", mils.toString())
                                put("count", mils.toString())
                            })
                            onclickInterface.onClick(count)

                            activity.alert(
                                activity.getString(R.string.Delete_alert),
                                activity.getString(R.string.Notification)
                            ) {

                            }

                            items.removeAt(bindingAdapterPosition)
                            notifyItemRemoved(bindingAdapterPosition)

                            val newValue = this@FeedDetailAdapter.items
                            FeedCommentLoader.shared.items = newValue
                        }
                    }
                }
            }
            replyView.setOnClickListener {

                BaseApplication.shared.getSharedPreferences().getUser()?.let {

                    val content = contentLabel.text.toString()
                    val tag = "@${nameLabel.text}"
                    val original = content.originalString()
                    val replyMessage = "${tag} | ${original}\n"
                    onReplied(replyMessage)
                } ?: run {
                    activity?.let { activity ->
                        activity.popup(
                            activity.getString(R.string.Doyouwantlogin),
                            activity.getString(R.string.Login)
                        ) {
                            Navigation.findNavController(activity, R.id.nav_host_fragment)
                                .navigate(R.id.action_global_signInFragment)
                        }
                    }
                }
            }
            val replyInfos = viewModel.content.getReplyInfo()
            if (replyInfos != null) {
                activity?.let { activity ->
                    val tag = replyInfos.first
                    val origin = replyInfos.second
                    contentLabel.text = viewModel.content.makeRepliedMessage(
                        tag,
                        origin,
                        ContextCompat.getColor(activity.baseContext, R.color.dullTeal)
                    )

                    val nickname = tag.parseNickname()
                    if (!viewModel.isMe && nickname == BaseApplication.shared.getSharedPreferences()
                            .getUser()?.nickname
                    )
                        cardView.setBackgroundColor(
                            ContextCompat.getColor(
                                activity.baseContext,
                                R.color.lightBlueGrey
                            )
                        )
                }
            }
        }
    }
}

interface onClickInterface {

    fun onClick(count: Int)

}

