package com.bereda.budgeting_aid.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "budget_category")
public class BudgetCategory {

    @Id
    Long id;
    String name;
    BigDecimal amount;

    public void depositAmount(final BigDecimal value) {
        amount = amount.add(value);
    }
}