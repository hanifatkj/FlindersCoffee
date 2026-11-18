package com.flinders.coffee.data

import com.flinders.coffee.model.FinanceEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * REST API Client untuk menghubungkan aplikasi Android Flinders Coffee
 * dengan backend MySQL XAMPP (flinders-api).
 */
object ApiClient {

    /**
     * URL API XAMPP:
     * - Emulator Android Studio: http://10.0.2.2/flinders-api/api.php
     * - HP Fisik Android (Wi-Fi lokal): http://192.168.1.75/flinders-api/api.php
     */
    var baseUrl: String = "http://10.0.2.2/flinders-api/api.php"

    /**
     * Helper HTTP GET Request
     */
    private fun get(action: String, queryParams: Map<String, String> = emptyMap()): JSONObject {
        val queryBuilder = StringBuilder("$baseUrl?action=$action")
        for ((key, value) in queryParams) {
            queryBuilder.append("&$key=${java.net.URLEncoder.encode(value, "UTF-8")}")
        }

        val url = URL(queryBuilder.toString())
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 5000
        conn.readTimeout = 5000
        conn.setRequestProperty("Accept", "application/json")

        val responseCode = conn.responseCode
        val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
        val reader = BufferedReader(InputStreamReader(stream))
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line)
        }
        reader.close()
        conn.disconnect()

        return JSONObject(sb.toString())
    }

    /**
     * Helper HTTP POST Request
     */
    private fun post(action: String, payload: JSONObject): JSONObject {
        val url = URL("$baseUrl?action=$action")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.connectTimeout = 5000
        conn.readTimeout = 5000
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        conn.setRequestProperty("Accept", "application/json")

        val writer = OutputStreamWriter(conn.outputStream, "UTF-8")
        writer.write(payload.toString())
        writer.flush()
        writer.close()

        val responseCode = conn.responseCode
        val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
        val reader = BufferedReader(InputStreamReader(stream))
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line)
        }
        reader.close()
        conn.disconnect()

        return JSONObject(sb.toString())
    }

    // =========================================================
    // API METHODS (ASYNC WITH COROUTINES)
    // =========================================================

    /**
     * Login User (Owner / Kasir / Member)
     */
    fun login(username: String, password: String, onResult: (Boolean, String, String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = JSONObject().apply {
                    put("username", username)
                    put("password", password)
                }
                val res = post("login", json)
                val status = res.optString("status")
                val isSuccess = status == "success"
                val msg = res.optString("message", "")
                val role = if (isSuccess) res.optJSONObject("data")?.optString("role") else null

                withContext(Dispatchers.Main) {
                    onResult(isSuccess, msg, role)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, "Gagal koneksi ke server XAMPP: ${e.localizedMessage}", null)
                }
            }
        }
    }

    /**
     * Ambil Ringkasan Finansial Realtime (Penjualan Asli, HPP Asli, Pengeluaran, Laba Bersih)
     */
    fun getSummary(period: String? = null, onResult: (Boolean, JSONObject?, String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val params = mutableMapOf<String, String>()
                if (!period.isNullOrEmpty()) params["period"] = period
                val res = get("get_summary", params)
                val isSuccess = res.optString("status") == "success"
                val data = res.optJSONObject("data")

                withContext(Dispatchers.Main) {
                    onResult(isSuccess, data, null)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, null, e.localizedMessage)
                }
            }
        }
    }

    /**
     * Ambil Daftar Pengeluaran Asli dari MySQL
     */
    fun getExpenses(month: String? = null, onResult: (Boolean, List<FinanceEntry>?, String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val params = if (!month.isNullOrEmpty()) mapOf("month" to month) else emptyMap()
                val res = get("get_expenses", params)
                val isSuccess = res.optString("status") == "success"
                val data = res.optJSONArray("data")
                val list = mutableListOf<FinanceEntry>()
                if (data != null) {
                    for (i in 0 until data.length()) {
                        val obj = data.getJSONObject(i)
                        list.add(
                            FinanceEntry(
                                id = obj.optLong("id", System.currentTimeMillis()),
                                name = obj.optString("description", "Pengeluaran"),
                                amount = obj.optLong("amount", 0L),
                                isExpense = true
                            )
                        )
                    }
                }
                withContext(Dispatchers.Main) {
                    onResult(isSuccess, list, null)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, null, e.localizedMessage)
                }
            }
        }
    }

    /**
     * Catat Pengeluaran Operasional Baru ke MySQL
     */
    fun addExpense(category: String, amount: Long, description: String, recordedBy: String = "Owner", onResult: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = JSONObject().apply {
                    put("category", category)
                    put("amount", amount)
                    put("description", description)
                    put("recorded_by", recordedBy)
                }
                val res = post("add_expense", json)
                val isSuccess = res.optString("status") == "success"
                val msg = res.optString("message", "")

                withContext(Dispatchers.Main) {
                    onResult(isSuccess, msg)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, e.localizedMessage ?: "Gagal")
                }
            }
        }
    }

    /**
     * Hapus Pengeluaran dari MySQL
     */
    fun deleteExpense(id: Long, onResult: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = JSONObject().apply { put("id", id) }
                val res = post("delete_expense", json)
                val isSuccess = res.optString("status") == "success"
                val msg = res.optString("message", "")

                withContext(Dispatchers.Main) {
                    onResult(isSuccess, msg)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, e.localizedMessage ?: "Gagal")
                }
            }
        }
    }

    /**
     * Ambil Katalog Menu dari MySQL
     */
    fun getMenus(category: String = "Semua", onResult: (Boolean, JSONArray?, String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val params = mapOf("active" to "1", "category" to category)
                val res = get("get_menus", params)
                val isSuccess = res.optString("status") == "success"
                val data = res.optJSONArray("data")

                withContext(Dispatchers.Main) {
                    onResult(isSuccess, data, null)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, null, e.localizedMessage)
                }
            }
        }
    }

    /**
     * Toggle status aktif/nonaktif menu di MySQL
     */
    fun toggleMenu(id: String, onResult: (Boolean, Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = JSONObject().apply { put("id", id) }
                val res = post("toggle_menu", json)
                val isSuccess = res.optString("status") == "success"
                val newActive = res.optInt("is_active", 1) == 1
                withContext(Dispatchers.Main) {
                    onResult(isSuccess, newActive)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, false)
                }
            }
        }
    }

    /**
     * Simpan / Tambah Menu Baru ke MySQL
     */
    fun saveMenu(id: String, name: String, category: String, price: Long, hpp: Long, desc: String, onResult: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = JSONObject().apply {
                    put("id", id)
                    put("name", name)
                    put("category", category)
                    put("price", price)
                    put("hpp", hpp)
                    put("description", desc)
                    put("isActive", 1)
                }
                val res = post("save_menu", json)
                val isSuccess = res.optString("status") == "success"
                val msg = res.optString("message", "")

                withContext(Dispatchers.Main) {
                    onResult(isSuccess, msg)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, e.localizedMessage ?: "Gagal")
                }
            }
        }
    }

    /**
     * Ambil Status Denah Meja (Ruang A, B, C) dari MySQL
     */
    fun getTables(room: String = "Semua", onResult: (Boolean, JSONArray?, String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val params = if (room != "Semua") mapOf("room" to room) else emptyMap()
                val res = get("get_tables", params)
                val isSuccess = res.optString("status") == "success"
                val data = res.optJSONArray("data")

                withContext(Dispatchers.Main) {
                    onResult(isSuccess, data, null)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, null, e.localizedMessage)
                }
            }
        }
    }

    /**
     * Update Status Meja (AVAILABLE, OCCUPIED, BOOKED) di MySQL
     */
    fun updateTable(id: String, status: String, onResult: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = JSONObject().apply {
                    put("id", id)
                    put("status", status)
                }
                val res = post("update_table", json)
                val isSuccess = res.optString("status") == "success"
                val msg = res.optString("message", "")

                withContext(Dispatchers.Main) {
                    onResult(isSuccess, msg)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, e.localizedMessage ?: "Gagal")
                }
            }
        }
    }
}
