package com.bereda.budgeting_aid.service;

import com.bereda.budgeting_aid.entity.BudgetCategory;
import com.bereda.budgeting_aid.exceptions.BudgetCategoryNotFoundException;
import com.bereda.budgeting_aid.exceptions.TransferException;
import com.bereda.budgeting_aid.exceptions.ValidationException;
import com.bereda.budgeting_aid.model.BudgetCategoryDTO;
import com.bereda.budgeting_aid.model.CreateCategoryDTO;
import com.bereda.budgeting_aid.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public BudgetCategory transferAmount(final Long fromCategory, final Long toCategory, final BigDecimal amount) {
        if (fromCategory.equals(toCategory)) {
            throw new ValidationException("Cannot transfer amount between the same category");
        }

        final BudgetCategory sourceCategory = findById(fromCategory);

        if (sourceCategory.getAmount().compareTo(amount) < 0) {
            throw new TransferException("Not enough amount in category: " + fromCategory);
        }

        final BudgetCategory targetCategory = findById(toCategory);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferException("Amount cannot be less or equal to zero");
        } else {


            sourceCategory.depositAmount(amount.negate());
            targetCategory.depositAmount(amount);
        }
        return targetCategory;
    }

    @Transactional
    public BudgetCategory depositAmount(final Long id, final BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            final BudgetCategory budgetCategory = findById(id);
            budgetCategory.depositAmount(amount);
            return budgetCategory;
        } else {
            throw new TransferException("Amount cannot be less or equal to zero");
        }
    }

    public BudgetCategory createCategory(final CreateCategoryDTO createCategoryDTO) {
        BudgetCategory newCategory = BudgetCategory.builder()
                .amount(createCategoryDTO.getAmount())
                .name(createCategoryDTO.getName())
                .build();
        budgetRepository.save(newCategory);
        return newCategory;
    }

    public List<BudgetCategoryDTO> findAll() {
        return stream(budgetRepository.findAll().spliterator(), false)
                .map(category -> BudgetCategoryDTO.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .amount(category.getAmount())
                        .build())
                .collect(toList());
    }

    private BudgetCategory findById(final Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetCategoryNotFoundException("Budget category with id: " + id + " not found"));
    }

}
