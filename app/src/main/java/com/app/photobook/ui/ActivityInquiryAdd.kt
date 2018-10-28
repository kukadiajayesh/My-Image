package com.app.photobook.ui

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.app.photobook.R
import com.app.photobook.tools.Constants
import com.app.photobook.tools.Utils
import kotlinx.android.synthetic.main.activity_create_inquiry.*
import kotlinx.android.synthetic.main.navigation_toolbar.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ActivityInquiryAdd : BaseActivity() {

    private val TAG = ActivityInquiryAdd::class.java.simpleName

    internal var hasDateSelect = false
    internal var calEventTime = Calendar.getInstance()
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_inquiry)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please Wait...")

        setActionbar()
        btnEventDate.setOnClickListener(onclick)
        btnSubmit.setOnClickListener(onclick)

        spnEventType.onItemSelectedListener = onItemSelection

        showEventDate()
    }

    var onItemSelection = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position == spnEventType.adapter.count - 1) {
                edtEventTypeOther.visibility = View.VISIBLE
            } else {
                edtEventTypeOther.visibility = View.GONE
            }
        }
    }

    private fun setActionbar() {
        setSupportActionBar(toolbar)
        title = "Inquiry Form"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private var onclick: View.OnClickListener = View.OnClickListener { v ->

        when (v.id) {
            R.id.btnSubmit -> {
                if (submit()) return@OnClickListener
            }
            R.id.btnEventDate -> {
                DatePickerDialog(this@ActivityInquiryAdd,
                        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                            calEventTime.set(year, month, dayOfMonth)
                            hasDateSelect = true
                            showEventDate()
                        }, calEventTime.get(Calendar.YEAR), calEventTime.get(Calendar.MONTH), calEventTime.get(Calendar.DAY_OF_MONTH))
                        .show()
            }
        }
    }

    private fun submit(): Boolean {
        if (!Utils.isOnline(this)) {
            Utils.showNoInternetMessage(this, btnSubmit)
            return true
        }

        var name = edtName.text.toString().trim()
        var email = edtEmail.text.toString().trim()
        var mobile = edtMobile.text.toString().trim()
        var date = DateFormat.format("yyyy-MM-dd", calEventTime).toString()
        var location = edtLocation.text.toString().trim()

        var type = spnEventType.selectedItem.toString()
        if (spnEventType.selectedItemPosition == spnEventType.adapter.count - 1) {
            type = edtEventTypeOther.text.toString()
        }

        var message = edtMessage.text.toString().trim()
        var refBy = edtRefBy.text.toString().trim()

        if (validateFields(name, email, mobile, location)) return true

        progressDialog.show()
        retroApi.addInquiry(user.id.toString(), name, email, mobile, date, location, type,
                message, refBy)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                        progressDialog.dismiss()

                        if (response!!.code() != 200) {
                            val res = response.errorBody().string()
                            Log.e(TAG, "onResponse: $res")
                            return
                        }

                        try {
                            var res = response.body().string()

                            var json = JSONObject(res)
                            var error = json.getInt("error")
                            var msg = json.getString("msg")

                            if (error == Constants.RESPONSE_STATUS_OK) {
                                Toast.makeText(this@ActivityInquiryAdd, msg, Toast.LENGTH_LONG).show()
                                onBackPressed()
                            } else {
                                Utils.showDialog(this@ActivityInquiryAdd, getString(R.string.app_name),
                                        msg, null)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        progressDialog.dismiss()
                        t!!.printStackTrace()
                    }
                })
        return false
    }

    fun validateFields(fullName: String, email: String, mobile: String, location: String): Boolean {

        return when {
            fullName.isEmpty() -> {
                edtName.error = "Enter Full Name"
                edtName.requestFocus()
                true
            }
            email.isEmpty() -> {
                edtEmail.error = "Enter Email"
                edtEmail.requestFocus()
                true
            }
            mobile.isEmpty() -> {
                edtMobile.error = "Enter Mobile"
                edtMobile.requestFocus()
                true
            }
            mobile.length < 10 -> {
                edtMobile.error = "Enter Mobile Must Be 10 Digit"
                edtMobile.requestFocus()
                true
            }
            !hasDateSelect -> {
                Toast.makeText(this@ActivityInquiryAdd, "Select Event Date", Toast.LENGTH_SHORT).show()
                true
            }
            location.isEmpty() -> {
                edtLocation.error = "Enter Event Location"
                edtLocation.requestFocus()
                true
            }
            spnEventType.selectedItemPosition == 0 -> {
                Toast.makeText(this@ActivityInquiryAdd, "Select Event Type", Toast.LENGTH_SHORT).show()
                true
            }
            (spnEventType.selectedItemPosition == spnEventType.adapter.count - 1) -> {
                if (edtEventTypeOther.text.isEmpty()) {
                    edtEventTypeOther.error = "Enter Other Event Type"
                    edtEventTypeOther.requestFocus()
                    true
                } else {
                    false
                }
            }
            else -> {
                false
            }
        }

    }

    internal fun showEventDate() {
        tvEventDate.text = DateFormat.format("dd/MM/yyyy", calEventTime)
    }

    private fun getContext(): Context {
        return this
    }

    internal val contentView: View
        get() = findViewById(android.R.id.content)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


}