package com.polinema.ta_iot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SettingLocationFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var lokasi1EditText: EditText
    private lateinit var lokasi2EditText: EditText
    private lateinit var lokasi3EditText: EditText
    private lateinit var simpan1Button: Button
    private lateinit var simpan2Button: Button
    private lateinit var simpan3Button: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        lokasi1EditText = view.findViewById(R.id.lokasi1)
        lokasi2EditText = view.findViewById(R.id.lokasi2)
        lokasi3EditText = view.findViewById(R.id.lokasi3)
        simpan1Button = view.findViewById(R.id.btnSimpan1)
        simpan2Button = view.findViewById(R.id.btnSimpan2)
        simpan3Button = view.findViewById(R.id.btnSimpan3)

        // Fetch and display existing data
        fetchExistingData()

        simpan1Button.setOnClickListener { saveLocation("alat 1", lokasi1EditText.text.toString()) }
        simpan2Button.setOnClickListener { saveLocation("alat 2", lokasi2EditText.text.toString()) }
        simpan3Button.setOnClickListener { saveLocation("alat 3", lokasi3EditText.text.toString()) }

        // Handle back press
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_settingLocationFragment_to_nav_air_quality)
                }
            }
        )
    }

    private fun fetchExistingData() {
        database.child("lokasi").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val alat = childSnapshot.child("alat").getValue(String::class.java)
                    val lokasi = childSnapshot.child("lokasi").getValue(String::class.java)
                    when (alat) {
                        "alat 1" -> lokasi1EditText.setText(lokasi)
                        "alat 2" -> lokasi2EditText.setText(lokasi)
                        "alat 3" -> lokasi3EditText.setText(lokasi)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to fetch data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveLocation(alat: String, lokasi: String) {
        val locationData = mapOf(
            "alat" to alat,
            "lokasi" to lokasi
        )
        database.child("lokasi").child(alat).setValue(locationData)
            .addOnSuccessListener {
                Toast.makeText(context, "Lokasi berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Lokasi tidak berhasil disimpan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
