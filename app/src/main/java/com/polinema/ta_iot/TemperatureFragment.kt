package com.polinema.ta_iot

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class TemperatureFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var lineChart1: LineChart
    private lateinit var lineChart2: LineChart
    private lateinit var lineChart3: LineChart
    private lateinit var btnDateFilter1: Button
    private lateinit var btnDateFilter2: Button
    private lateinit var btnDateFilter3: Button

    private lateinit var lastValue1: TextView
    private lateinit var lastValue2: TextView
    private lateinit var lastValue3: TextView

    private var selectedDate: String = ""
    private var selectedSensor: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_temperature, container, false)

        lineChart1 = view.findViewById(R.id.Lnc1)
        lineChart2 = view.findViewById(R.id.Lnc2)
        lineChart3 = view.findViewById(R.id.Lnc3)
        btnDateFilter1 = view.findViewById(R.id.btnDateFilter1)
        btnDateFilter2 = view.findViewById(R.id.btnDateFilter2)
        btnDateFilter3 = view.findViewById(R.id.btnDateFilter3)

        lastValue1 = view.findViewById(R.id.lastValue1)
        lastValue2 = view.findViewById(R.id.lastValue2)
        lastValue3 = view.findViewById(R.id.lastValue3)

        setupLineChart(lineChart1)
        setupLineChart(lineChart2)
        setupLineChart(lineChart3)

        btnDateFilter1.setOnClickListener {
            selectedSensor = "Data_sensor1"
            showDatePickerDialog()
        }
        btnDateFilter2.setOnClickListener {
            selectedSensor = "Data_sensor2"
            showDatePickerDialog()
        }
        btnDateFilter3.setOnClickListener {
            selectedSensor = "Data_sensor3"
            showDatePickerDialog()
        }

        // Fetch and display data for the current date when the fragment is opened
        val currentDate = getCurrentDate()
        fetchSensorData("Data_sensor1", lineChart1, currentDate, lastValue1)
        fetchSensorData("Data_sensor2", lineChart2, currentDate, lastValue2)
        fetchSensorData("Data_sensor3", lineChart3, currentDate, lastValue3)

        return view
    }

    private fun setupLineChart(lineChart: LineChart) {
        lineChart.setDrawGridBackground(false)
        lineChart.setDrawBorders(false)
        lineChart.description.isEnabled = false
        lineChart.legend.form = Legend.LegendForm.LINE
        lineChart.animateX(1000)
        lineChart.animateY(1000)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        selectedDate = format.format(calendar.time)

        when (selectedSensor) {
            "Data_sensor1" -> fetchSensorData("Data_sensor1", lineChart1, selectedDate, lastValue1)
            "Data_sensor2" -> fetchSensorData("Data_sensor2", lineChart2, selectedDate, lastValue2)
            "Data_sensor3" -> fetchSensorData("Data_sensor3", lineChart3, selectedDate, lastValue3)
        }
    }

    private fun fetchSensorData(sensorPath: String, lineChart: LineChart, date: String, lastValueTextView: TextView) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(sensorPath).child(date)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val co2Entries = ArrayList<Entry>()
                val humidityEntries = ArrayList<Entry>()
                val temperatureEntries = ArrayList<Entry>()

                var lastCo2 = 0f
                var lastHumidity = 0f
                var lastTemperature = 0f

                var index = 0f
                for (snapshot in dataSnapshot.children) {
                    val co2 = snapshot.child("kadar_co2").getValue(Float::class.java) ?: 0f
                    val humidity = snapshot.child("kelembapan").getValue(Float::class.java) ?: 0f
                    val temperature = snapshot.child("suhu").getValue(Float::class.java) ?: 0f

                    co2Entries.add(Entry(index, co2))
                    humidityEntries.add(Entry(index, humidity))
                    temperatureEntries.add(Entry(index, temperature))

                    lastCo2 = co2
                    lastHumidity = humidity
                    lastTemperature = temperature

                    index++
                }

                lastValueTextView.text = "Last Value: CO2: $lastCo2, Humidity: $lastHumidity, Temperature: $lastTemperature"

                val co2DataSet = LineDataSet(co2Entries, "CO2")
                val humidityDataSet = LineDataSet(humidityEntries, "Humidity")
                val temperatureDataSet = LineDataSet(temperatureEntries, "Temperature")

                // Set different colors for each chart
                when (lineChart) {
                    lineChart1 -> {
                        co2DataSet.color = resources.getColor(R.color.chart1_co2)
                        humidityDataSet.color = resources.getColor(R.color.chart1_humidity)
                        temperatureDataSet.color = resources.getColor(R.color.chart1_temperature)
                    }
                    lineChart2 -> {
                        co2DataSet.color = resources.getColor(R.color.chart2_co2)
                        humidityDataSet.color = resources.getColor(R.color.chart2_humidity)
                        temperatureDataSet.color = resources.getColor(R.color.chart2_temperature)
                    }
                    lineChart3 -> {
                        co2DataSet.color = resources.getColor(R.color.chart3_co2)
                        humidityDataSet.color = resources.getColor(R.color.chart3_humidity)
                        temperatureDataSet.color = resources.getColor(R.color.chart3_temperature)
                    }
                }

                val lineData = LineData(co2DataSet, humidityDataSet, temperatureDataSet)
                lineChart.data = lineData
                lineChart.invalidate() // Refresh the chart
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return format.format(calendar.time)
    }
}
