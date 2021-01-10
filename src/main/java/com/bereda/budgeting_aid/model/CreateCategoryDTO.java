package com.bereda.budgeting_aid.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Builder
@Value
public class CreateCategoryDTO {
    String name;
    BigDecimal amount;
}


