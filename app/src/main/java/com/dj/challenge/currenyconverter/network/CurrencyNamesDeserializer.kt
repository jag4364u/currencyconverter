package com.dj.challenge.currenyconverter.network

import com.dj.challenge.currenyconverter.api.Currencies
import com.google.gson.*
import java.lang.reflect.Type

class CurrencyNamesDeserializer : JsonDeserializer<Currencies>,
    JsonSerializer<Currencies> {

    override fun serialize(
        src: Currencies,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val jsonObject = JsonObject()
        for ((key, value) in src.names) {
            jsonObject.add(key, JsonPrimitive(value))
        }
        return jsonObject
    }

    override fun deserialize(
        jsonElement: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Currencies {
        // parse automatically converts utc date to local time zone
        val list = LinkedHashMap<String, String>()
        val json = jsonElement.asJsonObject
        if (json.isJsonNull || jsonElement.isJsonObject.not()) return Currencies(list)
        for (entry in jsonElement.asJsonObject.entrySet()) {
            list[entry.key] = entry.value.asString
        }
        return Currencies(list)
    }
}