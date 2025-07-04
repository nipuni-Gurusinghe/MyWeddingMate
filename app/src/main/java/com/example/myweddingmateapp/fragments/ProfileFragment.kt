package com.example.myweddingmateapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.myweddingmateapp.R

class ProfileFragment : Fragment() {

    private lateinit var profilePicture: ImageView
    private lateinit var btnEditPicture: ImageView
    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editBio: EditText
    private lateinit var editPhone: EditText
    private lateinit var btnUpdate: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_planner_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profilePicture = view.findViewById(R.id.profilePicture)
        btnEditPicture = view.findViewById(R.id.btnEditPicture)
        editName = view.findViewById(R.id.editName)
        editEmail = view.findViewById(R.id.editEmail)
        editBio = view.findViewById(R.id.editBio)
        editPhone = view.findViewById(R.id.editPhone)
        btnUpdate = view.findViewById(R.id.btnUpdate)

        btnEditPicture.setOnClickListener {
            Toast.makeText(requireContext(), "Edit picture clicked", Toast.LENGTH_SHORT).show()
        }

        btnUpdate.setOnClickListener {
            val name = editName.text.toString()
            val email = editEmail.text.toString()
            val bio = editBio.text.toString()
            val phone = editPhone.text.toString()

            Toast.makeText(
                requireContext(),
                "Updated:\n$name\n$email\n$bio\n$phone",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
