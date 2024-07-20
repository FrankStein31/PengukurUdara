package com.polinema.ta_iot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.polinema.ta_iot.databinding.FragmentDataTableBinding

class DataTableFragment : Fragment() {
    private var _binding: FragmentDataTableBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataAdapter: DataAdapter
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDataTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        dataAdapter = DataAdapter(emptyList())
        recyclerView.adapter = dataAdapter

        spinner = binding.spinner
        val sensors = arrayOf("Data_sensor1", "Data_sensor2", "Data_sensor3")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sensors)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSensor = parent?.getItemAtPosition(position).toString()
                fetchData(selectedSensor)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Fetch data for the first sensor initially
        fetchData(sensors[0])
    }

    private fun fetchData(sensorPath: String) {
        database.child(sensorPath).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList = mutableListOf<Data>()
                for (dateSnapshot in snapshot.children) {
                    for (timeSnapshot in dateSnapshot.children) {
                        val dataMap = timeSnapshot.value as? Map<*, *>
                        val data = Data(
                            kadar_co2_value = dataMap?.get("kadar_co2").toString().toFloatOrNull() ?: 0f,
                            kelembapan_value = dataMap?.get("kelembapan").toString().toFloatOrNull() ?: 0f,
                            skala_value = dataMap?.get("skala").toString() ?: "",
                            suhu_value = dataMap?.get("suhu").toString().toFloatOrNull() ?: 0f,
                            waktu_value = timeSnapshot.key.toString()
                        )
                        dataList.add(data)
                    }
                }
                dataAdapter.updateData(dataList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}