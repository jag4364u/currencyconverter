package com.dj.challenge.currenyconverter.network

import com.dj.challenge.currenyconverter.api.Quotes
import com.google.gson.*
import java.lang.reflect.Type

class CurrencyRateDeserializer : JsonDeserializer<Quotes>,
    JsonSerializer<Quotes> {

    override fun serialize(
        src: Quotes,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val jsonObject = JsonObject()
        for ((key, value) in src.rates) {
            jsonObject.add(key, JsonPrimitive(value))
        }
        return jsonObject
    }

    override fun deserialize(
        jsonElement: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Quotes {
        // parse automatically converts utc date to local time zone
        val list = LinkedHashMap<String, Double>()
        val json = jsonElement.asJsonObject
        if (json.isJsonNull || jsonElement.isJsonObject.not()) return Quotes(list)
        for (entry in jsonElement.asJsonObject.entrySet()) {
            list[entry.key] = entry.value.asDouble
        }
        return Quotes(list)
    }
}