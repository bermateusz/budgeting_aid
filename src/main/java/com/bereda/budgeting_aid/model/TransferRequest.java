package com.bereda.budgeting_aid.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

import static com.bereda.budgeting_aid.model.TransferRequest.*;

@Value
@Builder
@JsonDeserialize(builder = TransferRequestBuilder.class)
public class TransferRequest {
    @NonNull
    Long targetRegisterId;
    @NonNull
    BigDecimal amount;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class TransferRequestBuilder {
    }
}