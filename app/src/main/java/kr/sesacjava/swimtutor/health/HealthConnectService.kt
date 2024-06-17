package kr.sesacjava.swimtutor.health

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSegment
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Length
import com.example.health.api.RetrofitInstance
import com.example.health.api.StepsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZonedDateTime
import java.util.TimeZone

class HealthConnectService(private val context: Context, private val healthConnectClient: HealthConnectClient, private val webView: WebView/*, private val requestPermissions: ActivityResultLauncher<Set<String>>*/): WebViewClient() {
    suspend fun getRecords() {
        val endTime = Instant.now()
        val startTime = endTime.minusSeconds(10 * 24 * 60 * 60)
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )

        response.records.forEach {
            val steps = it.count
            println("AAAAAA" + steps);

            webView.post {
                webView.evaluateJavascript("javascript:showSteps($steps)", null)
            }
        }

//        val stepsDataList = response.records.map { record ->
//            StepsData(record.count, record.endTime.toEpochMilli())
//        }
//
//        withContext(Dispatchers.Main) {
//            RetrofitInstance.sendDataToServer(stepsDataList, context)
//        }
    }
    suspend fun insertSteps() {
//        val endTime = Instant.now()
//        val startTime = endTime.minusSeconds(1 * 60 * 60)

        val endZoneId = TimeZone.getDefault().toZoneId();
        val endTime = Instant.now()
        val endZonedDateTime = endTime.atZone(endZoneId);

        val startZoneId = TimeZone.getDefault().toZoneId();
        val startTime = endTime.minusSeconds(1 * 60 * 60)
        val zonedDateTime = startTime.atZone(startZoneId);

        try {
            healthConnectClient.insertRecords(
                listOf(
                    ExerciseSessionRecord(
                        startTime = zonedDateTime.toInstant(),
                        startZoneOffset = zonedDateTime.offset,
                        endTime = endZonedDateTime.toInstant(),
                        endZoneOffset = endZonedDateTime.offset,
                        exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL,
                        title = "My Swim",
                        segments = listOf(
                            ExerciseSegment(
                                segmentType = ExerciseSegment.EXERCISE_SEGMENT_TYPE_SWIMMING_BACKSTROKE,
                                startTime = zonedDateTime.toInstant(),
                                endTime = endZonedDateTime.toInstant(),
                                repetitions = 8,
                            )
                        ),
                    ),
                ) + buildHeartRateSeries(zonedDateTime, endZonedDateTime)
            )


        } catch (e: Exception) {
            //
        }

       /* try {
            val stepsRecord = StepsRecord(
                count = 120,
                startTime = startTime,
                endTime = endTime,
                startZoneOffset = null,
                endZoneOffset = null,
            )
            healthConnectClient.insertRecords(listOf(stepsRecord))
        } catch (e: Exception) {
            // Run error handling here
        }*/
    }

    private fun buildHeartRateSeries(
        sessionStartTime: ZonedDateTime,
        sessionEndTime: ZonedDateTime,
    ): DistanceRecord {
        return DistanceRecord(
            distance = Length.meters(1100.1),
            startTime = sessionStartTime.toInstant(),
            startZoneOffset = sessionStartTime.offset,
            endTime = sessionEndTime.toInstant(),
            endZoneOffset = sessionEndTime.offset,
        )
    }

    suspend fun getExercise() {
        val endTime = Instant.now()
        val startTime = endTime.minusSeconds( 10 * 24 * 60 * 60) // 10일 전
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                )
            ).records.filter{
                // 74 = 풀 수영
                it.exerciseType.equals(74)
            }

        for (exerciseRecord in response) {
            val heartRateRecords =
                healthConnectClient
                    .readRecords(
                        ReadRecordsRequest(
                            DistanceRecord::class,
                            timeRangeFilter =
                            TimeRangeFilter.between(
                                exerciseRecord.startTime,
                                exerciseRecord.endTime
                            )
                        )
                    )
                    .records
            println(heartRateRecords)
        }

       /* withContext(Dispatchers.Main) {
            RetrofitInstance.sendDataToServer(stepsDataList, context)
        }*/
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