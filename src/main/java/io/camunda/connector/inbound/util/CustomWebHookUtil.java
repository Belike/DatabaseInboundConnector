package io.camunda.connector.inbound.util;

import com.google.common.net.HttpHeaders;
import io.camunda.connector.api.json.ConnectorsObjectMapperSupplier;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import com.google.common.net.MediaType;

public class CustomWebHookUtil {
        public static String extractContentType(Map<String, String> headers) {
        var caseInsensitiveMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        caseInsensitiveMap.putAll(headers);
        return caseInsensitiveMap.getOrDefault(HttpHeaders.CONTENT_TYPE, "").toString();
    }
    public static Object transformRawBodyToObject(byte[] rawBody, String contentTypeHeader) {
        if (rawBody == null) {
            return Collections.emptyMap();
        }
        if (MediaType.FORM_DATA.toString().equalsIgnoreCase(contentTypeHeader)) {
            String bodyAsString =
                    URLDecoder.decode(new String(rawBody, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            return Arrays.stream(bodyAsString.split("&"))
                    .filter(Objects::nonNull)
                    .map(param -> param.split("="))
                    .collect(Collectors.toMap(param -> param[0], param -> param.length == 1 ? "" : param[1]));
        } else {
            // Do our best to parse to JSON (throws exception otherwise)
            try {
                return ConnectorsObjectMapperSupplier.getCopy().readValue(rawBody, Object.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
