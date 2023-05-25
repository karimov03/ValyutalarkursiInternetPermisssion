package com.example.valyutalarkursi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.valyutalarkursi.Adapters.RvAdapter
import com.example.valyutalarkursi.Class.Valyuta
import com.example.valyutalarkursi.databinding.ActivityMainBinding
import com.google.gson.Gson
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var requestQueue: RequestQueue
    private lateinit var networkHelper: NetworkHelper
    private lateinit var rvAdapter: RvAdapter
    private var valyuta: Double = 0.0
    private var position: Int = 0
    private val url = "https://cbu.uz/uz/arkhiv-kursov-valyut/json/"
    private var valyutaList: List<Valyuta> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkHelper = NetworkHelper(this)
        requestQueue = Volley.newRequestQueue(this)
        val network = networkHelper.isNetworkConnected()
        if (network) {
            binding.noConnectionLayout.visibility = View.GONE
            getAllValyutas()
        } else {
            binding.noConnectionLayout.visibility = View.VISIBLE
        }
        binding.btnQaytaUrinish.setOnClickListener {
            val network = networkHelper.isNetworkConnected()
            if (network) {
                binding.noConnectionLayout.visibility = View.GONE
                getAllValyutas()
            } else {
                binding.noConnectionLayout.visibility = View.VISIBLE
                Toast.makeText(this, "Internetga ulanaolmadik", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnRefresh.setOnClickListener {
            Toast.makeText(this, "Qayta yuklash", Toast.LENGTH_SHORT).show()
            val network = networkHelper.isNetworkConnected()
            if (network) {
                binding.noConnectionLayout.visibility = View.GONE
                getAllValyutas()

            } else {
                binding.noConnectionLayout.visibility = View.VISIBLE
                Toast.makeText(this, "Internetga ulanaolmadik", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnExit.setOnClickListener {
            finish()
        }
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position)
                valyuta = valyutaList[position].Rate.toDouble()
                this@MainActivity.position = position
                try {
                    if (binding.edtCalculator.text.toString() == "") {
                        binding.tvSum.text = "0"
                    } else {
                        val summa = binding.edtCalculator.text.toString().toLong() * valyuta
                        binding.tvSum.text = summa.toString()

                    }
                }
                catch (e:Exception){
                    binding.tvSum.text = "0"
                    binding.edtCalculator.setText("0")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                valyuta = valyutaList[0].Rate.toDouble()
            }
        }

        binding.edtCalculator.addTextChangedListener {
            try {
                if (binding.edtCalculator.text.toString() == "") {
                    binding.tvSum.text = "0"
                } else {
                    val summa = binding.edtCalculator.text.toString().toLong() * valyuta
                    binding.tvSum.text = summa.toString()

                }
            }
            catch (e:Exception){
                binding.tvSum.text = "0"
                binding.edtCalculator.setText("0")
            }
        }
        var bool=true
        binding.btnZoom.setOnClickListener {
            if (bool) {
                binding.btnZoom.setImageResource(R.drawable.ic_zoom_minus)
                val layoutParams = binding.rvLayout.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT // dpToPx() funksiyasini o'zgartiring
                binding.rvLayout.layoutParams = layoutParams
                bool=false
                binding.actionBar.visibility=View.INVISIBLE
            }
            else{
                binding.btnZoom.setImageResource(R.drawable.ic_zoom_plus)
                val layoutParams = binding.rvLayout.layoutParams
                layoutParams.height = 800 // dpToPx() funksiyasini o'zgartiring
                binding.rvLayout.layoutParams = layoutParams
                bool=true
                binding.actionBar.visibility=View.VISIBLE

            }
        }
        }


        private fun getAllValyutas() {
            binding.rv.visibility = View.GONE
            binding.loadingLayout.visibility = View.VISIBLE

            val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
                object : Response.Listener<JSONArray> {
                    override fun onResponse(response: JSONArray?) {
                        binding.rv.visibility = View.GONE
                        binding.loadingLayout.visibility = View.VISIBLE

                        val gson = Gson()
                        valyutaList =
                            gson.fromJson(response.toString(), Array<Valyuta>::class.java)
                                .toList() as ArrayList<Valyuta>
                        rvAdapter = RvAdapter(valyutaList as ArrayList<Valyuta>)
                        binding.rv.adapter = rvAdapter
                        binding.rv.visibility = View.VISIBLE
                        binding.loadingLayout.visibility = View.GONE
                        val dollar = valyutaList[0]
                        binding.tvDollarSotibOlish.text =
                            ((dollar.Rate.toDouble() - dollar.Diff.toDouble()).toString())
                        binding.tvDollarSotish.text =
                            ((dollar.Rate.toDouble() + dollar.Diff.toDouble()).toString())

                        val euro = valyutaList[1]
                        binding.tvEuroSotibOlish.text =
                            ((euro.Rate.toDouble() - euro.Diff.toDouble()).toString())
                        binding.tvEuroSotish.text =
                            ((euro.Rate.toDouble() + euro.Diff.toDouble()).toString()
                                .substring(0, 8))


                        val spinnerlist = ArrayList<String>()
                        valyutaList.forEach {
                            spinnerlist.add(it.Ccy)
                        }
                        val adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_spinner_item,
                            spinnerlist
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinner.adapter = adapter


                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {

                    }
                })

            requestQueue.add(jsonArrayRequest)
        }

    }
