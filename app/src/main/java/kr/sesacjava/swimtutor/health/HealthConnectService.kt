package kr.sesacjava.swimtutor.health

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.response.ReadRecordsResponse
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.health.api.RetrofitInstance
import com.example.health.api.SendData
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class HealthConnectService(private val context: Context, private val healthConnectClient: HealthConnectClient, private val webView: WebView/*, private val requestPermissions: ActivityResultLauncher<Set<String>>*/): WebViewClient() {
    suspend fun getRecords(date: String): ReadRecordsResponse<ExerciseSessionRecord> {
        val endTime = Instant.now()
        val ldt = LocalDateTime.parse(date)
        val startTime = ldt.atZone(ZoneId.systemDefault()).toInstant()

        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                )
            ).records.filter{
                74 = 풀 수영
                it.exerciseType.equals(79)
            }

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
            SendData(record.energy.inJoules,
                record.endTime.toString(),
                record.startTime.toString(),
                "calories")
        }

        RetrofitInstance.sendDataToServer(dataList)
    }

    suspend fun sendDistanceData(startTime: Instant, endTime: Instant) {
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    DistanceRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                )
            )

        val dataList = response.records.map { record ->
            SendData(record.distance.inMeters,
                record.endTime.toString(),
                record.startTime.toString(),
                "distance")
        }

        RetrofitInstance.sendDataToServer(dataList)
    }

    suspend fun sendSpeedData(startTime: Instant, endTime: Instant) {
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    SpeedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                )
            )

        val dataList = response.records.map { record ->
            val avg = record.samples.fold(0.0) { acc, cur -> acc + cur.speed.inMetersPerSecond } / record.samples.size

            SendData(avg,
                record.endTime.toString(),
                record.startTime.toString(),
                "speed")
        }

        RetrofitInstance.sendDataToServer(dataList)
    }

    suspend fun sendHeartRateData(startTime: Instant, endTime: Instant) {
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                )
            )

        val dataList = response.records.map { record ->
            val avg = record.samples.fold(0.0) { acc, cur -> acc + cur.beatsPerMinute } / record.samples.size

            SendData(avg,
                record.endTime.toString(),
                record.startTime.toString(),
                "heartRate")
        }

        RetrofitInstance.sendDataToServer(dataList)
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
