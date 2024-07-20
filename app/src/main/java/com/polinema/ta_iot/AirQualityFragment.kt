package com.polinema.ta_iot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AirQualityFragment : Fragment() {

    private lateinit var locationTextView1: TextView
    private lateinit var co2TextView1: TextView
    private lateinit var scaleTextView1: TextView
    private lateinit var temperatureTextView1: TextView
    private lateinit var humidityTextView1: TextView

    private lateinit var locationTextView2: TextView
    private lateinit var co2TextView2: TextView
    private lateinit var scaleTextView2: TextView
    private lateinit var temperatureTextView2: TextView
    private lateinit var humidityTextView2: TextView

    private lateinit var locationTextView3: TextView
    private lateinit var co2TextView3: TextView
    private lateinit var scaleTextView3: TextView
    private lateinit var temperatureTextView3: TextView
    private lateinit var humidityTextView3: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_air_quality, container, false)

        locationTextView1 = view.findViewById(R.id.tv1)
        co2TextView1 = view.findViewById(R.id.tv2)
        scaleTextView1 = view.findViewById(R.id.tv4)
        temperatureTextView1 = view.findViewById(R.id.tv13)
        humidityTextView1 = view.findViewById(R.id.tv14)

        locationTextView2 = view.findViewById(R.id.tv5)
        co2TextView2 = view.findViewById(R.id.tv6)
        scaleTextView2 = view.findViewById(R.id.tv8)
        temperatureTextView2 = view.findViewById(R.id.tv16)
        humidityTextView2 = view.findViewById(R.id.tv17)

        locationTextView3 = view.findViewById(R.id.tv9)
        co2TextView3 = view.findViewById(R.id.tv10)
        scaleTextView3 = view.findViewById(R.id.tv12)
        temperatureTextView3 = view.findViewById(R.id.tv19)
        humidityTextView3 = view.findViewById(R.id.tv20)

        // Fetch location data and air quality data from Firebase
        fetchLocationData()
        fetchAirQualityData("data_sensor_laporan1", co2TextView1, scaleTextView1, temperatureTextView1, humidityTextView1)
        fetchAirQualityData("data_sensor_laporan2", co2TextView2, scaleTextView2, temperatureTextView2, humidityTextView2)
        fetchAirQualityData("data_sensor_laporan3", co2TextView3, scaleTextView3, temperatureTextView3, humidityTextView3)

        return view
    }

    private fun fetchLocationData() {
        val database = FirebaseDatabase.getInstance()
        val locationsRef = database.getReference("lokasi")

        locationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (deviceSnapshot in snapshot.children) {
                    val deviceName = deviceSnapshot.child("alat").getValue(String::class.java)
                    val location = deviceSnapshot.child("lokasi").getValue(String::class.java)

                    when (deviceName) {
                        "alat 1" -> locationTextView1.text = "ALAT 1 : $location"
                        "alat 2" -> locationTextView2.text = "ALAT 2 : $location"
                        "alat 3" -> locationTextView3.text = "ALAT 3 : $location"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun fetchAirQualityData(path: String, co2TextView: TextView, scaleTextView: TextView, temperatureTextView: TextView, humidityTextView: TextView) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(path)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val co2 = dataSnapshot.child("kadar_co2").getValue(Float::class.java) ?: 0f
                    val scale = dataSnapshot.child("skala").getValue(String::class.java) ?: "N/A"
                    val temperature = dataSnapshot.child("suhu").getValue(Float::class.java) ?: 0f
                    val humidity = dataSnapshot.child("kelembapan").getValue(Float::class.java) ?: 0f

                    // Update text views with the latest data
                    co2TextView.text = "CO2: $co2 ppm"
                    scaleTextView.text = "Skala: $scale"
                    temperatureTextView.text = "Suhu: $temperature °C"
                    humidityTextView.text = "Kelembapan: $humidity %"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}