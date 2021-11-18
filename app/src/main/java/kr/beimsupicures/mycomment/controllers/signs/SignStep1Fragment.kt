package kr.beimsupicures.mycomment.controllers.signs

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.TermModel
import kr.beimsupicures.mycomment.common.accessUser
import kr.beimsupicures.mycomment.components.dialogs.AgreeDialog
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.afterTextChanged
import kr.beimsupicures.mycomment.extensions.alert
import kr.beimsupicures.mycomment.viewmodels.signs.SignStep1ViewModel
import stfalcon.universalpickerdialog.UniversalPickerDialog
import java.util.*

class SignStep1Fragment : BaseFragment() {

    var showing: Boolean = false
    var check1: Boolean = false
    var check2: Boolean = false
    var check3: Boolean = false
    var check4: Boolean = false
    var check5: Boolean = false

    lateinit var viewModel: SignStep1ViewModel

    val validation: Boolean
        get() {
            if (nicknameField.text.isNullOrEmpty()) {
                return false
            }
            if (nicknameField.text.length > 15) {
                return false
            }
            if (nicknameField.text.length < 2) {
                return false
            }
            if (!nicknameField.text.matches("""^[a-zA-Z0-9가-힣]+$""".toRegex())) {
                return false
            }
//            if (genderField.text.toString().isNullOrEmpty()) {
//                return false
//            }
//            if (birthField.text.toString().isNullOrEmpty()) {
//                return false
//            }
            return true
        }


    lateinit var nicknameField: EditText
    lateinit var birthField: TextView
    lateinit var genderField: TextView
    lateinit var btnNext: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_step1, container, false)
    }

    override fun loadModel() {
        super.loadModel()
        viewModel = SignStep1FragmentArgs.fromBundle(requireArguments()).viewmodel
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            nicknameField = view.findViewById(R.id.nicknameField)
            nicknameField.setText(viewModel.nickname)
            nicknameField.afterTextChanged {
                validateUI()
            }
            birthField = view.findViewById(R.id.birthField)
            birthField.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                activity?.let { activity ->
                    DatePickerDialog(
                        activity, R.style.MySpinnerDatePickerStyle,
                        { view, year, monthOfYear, dayOfMonth ->
                            birthField.text = String.format(
                                "%02d-%02d-%02d",
                                year,
                                monthOfYear + 1,
                                dayOfMonth
                            )
//                            validateUI()
                        },
                        year,
                        month,
                        day
                    ).show()

                }

            }
            val gender = arrayListOf(getString(R.string.male), getString(R.string.female))
            genderField = view.findViewById(R.id.genderField)
            genderField.setOnClickListener {
                UniversalPickerDialog.Builder(context)
                    .setTitle("")
                    .setListener { selectedValues, key ->
                        genderField.text = "${gender[selectedValues[0]]}"
//                        validateUI()
                    }
                    .setInputs(UniversalPickerDialog.Input(0, gender))
                    .show()

            }
            btnNext = view.findViewById(R.id.btnNext)
            btnNext.setOnClickListener { view ->

//                val genderString: String = genderField.text.toString()
//                val birthString = birthField.text.toString()

//                if (!genderString.isNullOrBlank() && !birthString.isNullOrBlank()) {

                    if (validation) {
                        showAgreeView()
                    } else {
                        checkIfValidateNickname()
                    }
//                } else {
//
//                }
            }

            if (showing) {
                showAgreeView()
            }

            validateUI()
        }
    }

    fun validateUI() {
        context?.let { context ->
            when (validation) {
                true -> {
                    btnNext.background =
                        ContextCompat.getDrawable(context, R.drawable.bg_dialog_button_enable)
                }
                false -> {
                    btnNext.background =
                        ContextCompat.getDrawable(context, R.drawable.bg_dialog_button_disable)
                }
            }
        }
    }

    private fun checkIfValidateNickname(): Boolean {
        val nickname = nicknameField.text
        if (!nickname.matches("""^[a-zA-Z0-9가-힣]+$""".toRegex())) {
            activity?.let {
                it.alert(
                    it.getString(R.string.signstep1fragment_nickname),
                    it.getString(R.string.Nickname)
                ) { }
            }
            return false
        }
        return true
    }

    fun showAgreeView() {
        view?.let { view ->
            activity?.supportFragmentManager?.let { fragmentManager ->
                showing = true

                AgreeDialog.newInstance(check1, check2, check3, check4, check5,

                    // didUpdatedAt
                    { check1, check2, check3, check4, check5 ->
                        this@SignStep1Fragment.check1 = check1
                        this@SignStep1Fragment.check2 = check2
                        this@SignStep1Fragment.check3 = check3
                        this@SignStep1Fragment.check4 = check4
                        this@SignStep1Fragment.check5 = check5
                    },

                    // onConfirm
                    {
                        if (!checkIfValidateNickname()) {
                            return@newInstance
                        }
                        viewModel.let { viewModel ->

                            // 닉네임 중복 체크
                            UserLoader.shared.uniqueNickname(nicknameField.text.toString()) { result ->
                                when (result) {
                                    true -> {
                                        var gender = ""
                                        genderField.text.toString().let { genderText ->
                                            when (genderText) {
                                                "남" -> gender = "male"
                                                "여" -> gender = "female"
                                                "male" -> gender = "male"
                                                "female" -> gender = "female"
                                            }
                                        }
                                        Log.e("tjdrnr", "gender = " + gender)
                                        UserLoader.shared.addUser(
                                            viewModel.email,
                                            birthField.text.toString(),
                                            viewModel.type,
                                            gender,
                                            nicknameField.text.toString()
                                        ) {
                                            UserLoader.shared.getUser {
                                                accessUser()
                                                findNavController().popBackStack(
                                                    R.id.signInFragment,
                                                    true
                                                )
                                            }
                                        }

                                    }
                                    false -> {
                                        activity?.let {
                                            it.alert(
                                                it.getString(R.string.NicknameDialog),
                                                it.getString(R.string.Nicknamealreadyexists)
                                            ) { }
                                        }

                                    }
                                }
                            }
                        }
                    },

                    // onDismiss
                    {
                        showing = false
                    },

                    // onSelected
                    { index ->

                        when (index) {
                            1 -> {
                                val action =
                                    NavigationDirections.actionGlobalTermFragment(TermModel.Category.service)
                                view.findNavController().navigate(action)
                            }
                            2 -> {
                                val action =
                                    NavigationDirections.actionGlobalTermFragment(TermModel.Category.privacy)
                                view.findNavController().navigate(action)
                            }
                            3 -> {
                                val action =
                                    NavigationDirections.actionGlobalTermFragment(TermModel.Category.guide)
                                view.findNavController().navigate(action)
                            }
                        }

                    }).show(fragmentManager, "")
            }
        }
    }
}
