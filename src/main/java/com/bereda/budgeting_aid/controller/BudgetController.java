package com.bereda.budgeting_aid.controller;

import com.bereda.budgeting_aid.entity.BudgetCategory;
import com.bereda.budgeting_aid.model.BudgetCategoryResponse;
import com.bereda.budgeting_aid.model.CreateCategory;
import com.bereda.budgeting_aid.model.DepositRequest;
import com.bereda.budgeting_aid.model.TransferRequest;
import com.bereda.budgeting_aid.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/budgeting")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    List<BudgetCategoryResponse> findAll(){
        return budgetService.findAll();
    }

    @PostMapping
    BudgetCategory create(@RequestBody CreateCategory createCategory){
        return budgetService.createCategory(createCategory.getAmount(),createCategory.getName());
    }

    @PostMapping(value = "{id}/transfer")
    void transfer(@PathVariable final Long id, @RequestBody final TransferRequest request){
        budgetService.transferAmount(id, request.getTargetId(), request.getAmount());
    }

    @PostMapping(value = "{id}/deposit")
    void deposit(@PathVariable final Long id, @RequestBody final DepositRequest request){
        budgetService.depositAmount(id, request.getAmount());
    }

    @DeleteMapping(value = "{id}")
    void delete(@PathVariable final Long id){
        budgetService.deleteCategory(id);
    }
}
