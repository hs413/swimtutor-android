package kr.sesacjava.swimtutor.health

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.response.ReadRecordsResponse
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.health.api.CaloriesData
import com.example.health.api.DistanceData
import com.example.health.api.RetrofitInstance
import java.time.Instant

class HealthConnectService(private val context: Context, private val healthConnectClient: HealthConnectClient, private val webView: WebView/*, private val requestPermissions: ActivityResultLauncher<Set<String>>*/): WebViewClient() {
    suspend fun getRecords(date: String): ReadRecordsResponse<ExerciseSessionRecord> {
        val endTime = Instant.now()
//        val ldt = LocalDateTime.parse(date)
//        val startTime = ldt.atZone(ZoneId.systemDefault()).toInstant()
        val startTime = endTime.minusSeconds(5 * 24 * 60 * 60)

        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                )
            )
//            ).records.filter{
//                74 = 풀 수영
//                it.exerciseType.equals(79)
//            }

        return response

    }

    suspend fun sendCaloriesData(startTime: Instant, endTime: Instant) {
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    ActiveCaloriesBurnedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                )
            )

        val dataList = response.records.map { record ->
            CaloriesData(record.energy.inJoules,
                record.endTime.toString(),
                record.startTime.toString())
        }

//        withContext(Dispatchers.Main) {
//            RetrofitInstance.sendDataToServer(dataList, context)
//        }
    }

    suspend fun sendSpeedData(startTime: Instant, endTime: Instant) {
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    DistanceRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                )
            )

        val dataList = response.records.map { record ->
            DistanceData(record.distance.inMeters,
                record.endTime.toString(),
                record.startTime.toString())
        }

        RetrofitInstance.sendDataToServer(dataList, context)
    }

    /*suspend fun checkPermissionsAndRun() {
        val PERMISSIONS =
            setOf(
                HealthPermission.getReadPermission(HeartRateRecord::class),
                HealthPermission.getWritePermission(HeartRateRecord::class),
                HealthPermission.getReadPermission(StepsRecord::class),
                HealthPermission.getWritePermission(StepsRecord::class)
            )

        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (granted.containsAll(PERMISSIONS)) {
            println("OOOOOO")
            requestPermissions.launch(PERMISSIONS)
            // Permissions already granted; proceed with inserting or reading data
        } else {
            requestPermissions.launch(PERMISSIONS)
        }
    }*/
}