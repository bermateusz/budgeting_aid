package com.bereda.budgeting_aid.repository;

import com.bereda.budgeting_aid.entity.BudgetCategory;
import org.springframework.data.repository.CrudRepository;

public interface BudgetRepository extends CrudRepository<BudgetCategory, Long> {
}
