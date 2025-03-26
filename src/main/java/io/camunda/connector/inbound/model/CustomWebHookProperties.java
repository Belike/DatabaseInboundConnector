package io.camunda.connector.inbound.model;

import io.camunda.connector.api.inbound.webhook.WebhookHttpResponse;
import io.camunda.connector.api.inbound.webhook.WebhookResultContext;
import io.camunda.connector.generator.java.annotation.TemplateProperty;
import io.camunda.connector.generator.java.annotation.TemplateProperty.DropdownPropertyChoice;
import io.camunda.connector.generator.java.annotation.TemplateProperty.PropertyType;
import io.camunda.connector.generator.dsl.Property.FeelMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Map;
import java.util.function.Function;

public record CustomWebHookProperties(
        @TemplateProperty(
                id = "method",
                label = "Webhook method",
                group = "endpoint",
                description = "Select HTTP method",
                type = PropertyType.Dropdown,
                choices = {
                       @DropdownPropertyChoice(label ="GET", value = "get"),
                       @DropdownPropertyChoice(label = "POST", value = "post")
                },
                defaultValue = "get")
        String method,

        @TemplateProperty(
                id = "context",
                label = "Webhook ID",
                group = "endpoint",
                description = "The webhook ID is a part of the URL",
                feel = FeelMode.disabled)
        @NotBlank
        @Pattern(
                regexp = "^[a-zA-Z0-9]+([-_][a-zA-Z0-9]+)*$",
                message =
                        "can only contain letters, numbers, or single underscores/hyphens and cannot begin or end with an underscore/hyphen")
        String context,
        @TemplateProperty(
                id = "responseExpression",
                label = "Response expression",
                type = PropertyType.Text,
                group = "webhookResponse",
                description = "Expression used to generate the HTTP response",
                feel = FeelMode.required,
                optional = true
        )
        Function<WebhookResultContext, WebhookHttpResponse> responseExpression,
        @TemplateProperty(ignore = true) Function<WebhookResultContext, Object> responseBodyExpression,
        @TemplateProperty(
                id = "verificationExpression",
                label = "One time verification response expression",
                description =
                        "Specify condition and response. Learn more in the <a href='https://docs.camunda.io/docs/components/connectors/protocol/http-webhook/#verification-expression' target='_blank'>documentation</a>",
                type = PropertyType.Text,
                group = "webhookResponse",
                feel = FeelMode.required,
                optional = true)
        Function<Map<String, Object>, WebhookHttpResponse> verificationExpression) {
    public CustomWebHookProperties(CustomWebHookPropertiesWrapper wrapper){
        this(
                wrapper.inbound.method,
                wrapper.inbound.context,
                wrapper.inbound.responseExpression,
                wrapper.inbound.responseBodyExpression,
                wrapper.inbound.verificationExpression);
    }
    public record CustomWebHookPropertiesWrapper(CustomWebHookProperties inbound) {}

    private static <T> T getOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    @Override
    public String toString() {
        return "CustomWebHookProperties{" +
                "method='" + method + '\'' +
                ", context='" + context + '\'' +
                ", responseExpression=" + responseExpression +
                ", responseBodyExpression=" + responseBodyExpression +
                ", verificationExpression=" + verificationExpression +
                '}';
    }
}
