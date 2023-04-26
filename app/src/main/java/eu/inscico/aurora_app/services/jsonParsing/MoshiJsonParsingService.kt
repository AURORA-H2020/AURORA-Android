package eu.inscico.aurora_app.services.jsonParsing

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import eu.inscico.aurora_app.model.reminder.ReminderTime
import retrofit2.Converter
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


class MoshiJsonParsingService(override val dateFormatPattern: String) : JsonParsingService {

    private val moshi = Moshi.Builder()
        .add(CustomReminderTimeAdapter())
        .add(CustomCalendarAdapter(dateFormatPattern))
        .add(CustomGregorianCalendarAdapter(dateFormatPattern))
        .add(KotlinJsonAdapterFactory())
        .build()

    override val converterFactory: Converter.Factory = MoshiConverterFactory.create(moshi)
    override fun <T : Any> toJson(input: T): String {
        val adapter = moshi.adapter<Any>(Object::class.java)
        return adapter.toJson(input)
    }

    override fun <T : Any> fromJson(jsonString: String?, type: Type): T? {
        if (jsonString == null) return null

        // Build adapter
        val adapter = moshi.adapter<T>(type)

        return try {
            adapter.fromJson(jsonString) as T
        } catch (e: Throwable) {
            null
        }
    }


    // region: Custom Adapters
    // ---------------------------------------------------------------------------------------------

    class CustomCalendarAdapter(dateFormat: String) : JsonAdapter<Calendar?>() {
        private val _dateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())

        @FromJson
        override fun fromJson(reader: JsonReader): Calendar? {
            if (reader.peek() == JsonReader.Token.NULL) {
                return reader.nextNull()
            }

            return try {
                val dateAsString = reader.nextString()
                val date = _dateFormat.parse(dateAsString)
                val cal = GregorianCalendar().apply {
                    time = date
                }
                cal
            } catch (e: Exception) {
                null
            }
        }

        @ToJson
        override fun toJson(writer: JsonWriter, value: Calendar?) {
            if (value != null) {
                writer.value(_dateFormat.format(value.time))
            } else {
                writer.nullValue()
            }
        }
    }

    class CustomGregorianCalendarAdapter(dateFormat: String) : JsonAdapter<GregorianCalendar?>() {
        private val _dateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())

        @FromJson
        override fun fromJson(reader: JsonReader): GregorianCalendar? {
            if (reader.peek() == JsonReader.Token.NULL) {
                return reader.nextNull()
            }

            return try {
                val dateAsString = reader.nextString()
                    val date = _dateFormat.parse(dateAsString)
                val cal = GregorianCalendar().apply {
                    time = date
                }
                cal
            } catch (e: Exception) {
                null
            }
        }

        @ToJson
        override fun toJson(writer: JsonWriter, value: GregorianCalendar?) {
            if (value != null) {
                writer.value(_dateFormat.format(value.time))
            } else {
                writer.nullValue()
            }
        }
    }

    // endregion

    class CustomReminderTimeAdapter : JsonAdapter<ReminderTime?>() {

        val dateFormatPattern = "yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'"


        val mMoshi = Moshi.Builder().add(CustomCalendarAdapter(dateFormatPattern))
            .add(CustomGregorianCalendarAdapter(dateFormatPattern)).add(KotlinJsonAdapterFactory()).build()

        @FromJson
        override fun fromJson(reader: JsonReader): ReminderTime? {
            val cpToCheck = reader.peekJson()
            val cpToCheck2 = reader.peekJson()
            val cpToCheck3 = reader.peekJson()
            val cpToCheck4 = reader.peekJson()

            try {
                val value =
                    mMoshi.adapter(ReminderTime.ReminderTimeYearly::class.java)
                        .fromJson(cpToCheck)

                reader.skipValue()

                return value
            } catch (e: Exception) {
                try {
                    val value =
                        mMoshi.adapter(ReminderTime.ReminderTimeMonthly::class.java)
                            .fromJson(cpToCheck2)

                    reader.skipValue()

                    return value

                } catch (e: java.lang.Exception) {
                    try {
                        val value =
                            mMoshi.adapter(ReminderTime.ReminderTimeWeekly::class.java)
                                .fromJson(cpToCheck3)

                        reader.skipValue()

                        return value

                    } catch (e: java.lang.Exception) {
                        try {
                            val value =
                                mMoshi.adapter(ReminderTime.ReminderTimeDaily::class.java)
                                    .fromJson(cpToCheck4)

                            reader.skipValue()

                            return value
                        } catch (e: java.lang.Exception) {
                            return null
                        }
                    }
                }
            }
        }

        @ToJson
        override fun toJson(writer: JsonWriter, value: ReminderTime?) {
            val jsonString = when(value){
                is ReminderTime.ReminderTimeDaily -> {
                    mMoshi.adapter(ReminderTime.ReminderTimeDaily::class.java).toJson(value)
                }
                is ReminderTime.ReminderTimeMonthly -> {
                    mMoshi.adapter(ReminderTime.ReminderTimeMonthly::class.java).toJson(value)
                }
                is ReminderTime.ReminderTimeWeekly -> {
                    mMoshi.adapter(ReminderTime.ReminderTimeWeekly::class.java).toJson(value)
                }
                is ReminderTime.ReminderTimeYearly -> {
                    mMoshi.adapter(ReminderTime.ReminderTimeYearly::class.java).toJson(value)
                }
                null -> {
                    null
                }
            }
            writer.value(jsonString)
        }
    }

}
