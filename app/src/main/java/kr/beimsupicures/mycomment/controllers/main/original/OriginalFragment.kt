package kr.beimsupicures.mycomment.controllers.main.original

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.models.OriginalModel
import kr.beimsupicures.mycomment.components.adapters.OriginalAdapter
import kr.beimsupicures.mycomment.components.fragments.BaseFragment

class OriginalFragment : BaseFragment() {

    lateinit var originalView: RecyclerView
    lateinit var ivContentImage: ImageView
    lateinit var originalAdapter: OriginalAdapter
    var original: MutableList<OriginalModel> = mutableListOf()

    private val originalListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {

            dataSnapshot.value?.let { value ->
                try {

                    original.clear()
                    for (data in dataSnapshot.children) {
                        val modelResult = data.getValue(OriginalModel::class.java)
                        original.add(modelResult!!)
                    }

                    original.let { original ->
                        originalAdapter.items = original
                        originalAdapter.notifyDataSetChanged()
                    }

                } catch (e: NumberFormatException) {
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_original, container, false)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            originalAdapter = OriginalAdapter(activity, original)
            originalView = view.findViewById(R.id.original_view)
            originalView.layoutManager = GridLayoutManager(context, 3)
            originalView.adapter = originalAdapter

            ivContentImage = view.findViewById(R.id.ivContentImage)

            val database = FirebaseDatabase.getInstance()

            database.getReference("original").child("list")
                .addValueEventListener(originalListener)

            database.getReference("original").child("banner_url").get().addOnSuccessListener {

                Glide.with(requireContext()).load(it.value)
                    .placeholder(R.color.colorGrey)
                    .override(Target.SIZE_ORIGINAL)
                    .into(ivContentImage)

            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        }
    }

}