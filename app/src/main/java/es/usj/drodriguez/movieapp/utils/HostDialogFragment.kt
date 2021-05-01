package es.usj.drodriguez.movieapp.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.databinding.FragmentDialogHostBinding

class HostDialogFragment(var hostName: String? = null) : DialogFragment() {
    private var _binding : FragmentDialogHostBinding? = null
    private val binding get() = _binding!!
    @Volatile var end = false
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val layout = inflater.inflate(R.layout.fragment_dialog_host, null)
            val editText = layout.findViewById<EditText>(R.id.ed_newHost)
            editText.addTextChangedListener (object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    hostName = "$s"
                }
            })
            if (hostName != null || hostName != "") {
                editText.setText(hostName, TextView.BufferType.EDITABLE)
            }
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(layout)
                .setTitle(if(hostName == null || hostName == "") getString(R.string.tv_host_dialog_first_connect)else getString(R.string.tv_host_dialog_reconnect_title))
                .setMessage(if(hostName != null && hostName != "") getString(R.string.tv_host_dialog_reconnect_message) else null)
                // Add action buttons
                .setPositiveButton(R.string.dialog_host_connect
                ) { _, _ ->
                    DatabasePreferences(it).setOnline(true, Context.MODE_PRIVATE)
                    DatabasePreferences(it).setHost(hostName, Context.MODE_PRIVATE)
                    end = true
                }
                .setNegativeButton(
                R.string.dialog_host_cancel
                ) { _, _ ->
                    DatabasePreferences(it).setOnline(false, Context.MODE_PRIVATE)
                    end = true
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}