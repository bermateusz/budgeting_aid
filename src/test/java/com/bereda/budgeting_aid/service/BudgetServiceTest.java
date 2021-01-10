package com.bereda.budgeting_aid.service;

import com.bereda.budgeting_aid.entity.BudgetCategory;
import com.bereda.budgeting_aid.exceptions.BudgetCategoryNotFoundException;
import com.bereda.budgeting_aid.exceptions.TransferException;
import com.bereda.budgeting_aid.exceptions.ValidationException;
import com.bereda.budgeting_aid.model.BudgetCategoryResponse;
import com.bereda.budgeting_aid.model.CreateCategory;
import com.bereda.budgeting_aid.repository.BudgetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    private final Fixtures fixtures = new Fixtures();

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetService budgetService;


    @Test
    void shouldReturnListOfAllCategories() {
        //given
        when(budgetRepository.findAll()).thenReturn(List.of(fixtures.walletCategory, fixtures.savingsCategory));

        //when
        List<BudgetCategoryResponse> listOfCategories = budgetService.findAll();

        //then
        assertThat(listOfCategories).containsOnly(
                BudgetCategoryResponse.builder()
                        .id(fixtures.walletCategoryId)
                        .name(fixtures.walletCategoryName)
                        .amount(fixtures.walletCategoryBalance)
                        .build(),
                BudgetCategoryResponse.builder()
                        .id(fixtures.savingsCategoryId)
                        .name(fixtures.savingsCategoryName)
                        .amount(fixtures.savingsCategoryBalance)
                        .build());
    }

    @Test
    void shouldCreateNewCategory() {
        //given
        when(budgetRepository.save(Mockito.any(BudgetCategory.class))).thenReturn(fixtures.walletCategory);

        //when
        BudgetCategory savedCategory = budgetService.createCategory(fixtures.walletCategory.getAmount(), fixtures.walletCategory.getName());

        //then
        assertThat(savedCategory.getName()).isSameAs(fixtures.walletCategory.getName());
        assertThat(savedCategory.getAmount()).isSameAs(fixtures.walletCategory.getAmount());
    }

    @Test
    void shouldCorrectlyDeposit() {
        //given
        when(budgetRepository.findById(fixtures.walletCategoryId)).thenReturn(Optional.of(fixtures.walletCategory));

        //when
        BudgetCategory budgetCategoryWithAdditionalAmount
                = budgetService.depositAmount(fixtures.walletCategoryId, fixtures.depositAmount);

        //then
        assertThat(budgetCategoryWithAdditionalAmount.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1150));
    }

    @Test
    void shouldNotDepositWhenAmountIsLowerThanZero() {
        //when
        Throwable throwable
                = catchThrowable(() -> budgetService.depositAmount(fixtures.walletCategoryId, BigDecimal.valueOf(-150)));

        //then
        assertThat(throwable).isInstanceOf(TransferException.class);
        assertThat(fixtures.walletCategory.getAmount()).isEqualByComparingTo(fixtures.walletCategoryBalance);
    }

    @Test
    void shouldNotDepositWhenAmountIsEqualToZero() {
        //when
        Throwable throwable
                = catchThrowable(() -> budgetService.depositAmount(fixtures.walletCategoryId, BigDecimal.ZERO));

        //then
        assertThat(throwable).isInstanceOf(TransferException.class);
        assertThat(fixtures.walletCategory.getAmount()).isEqualByComparingTo(fixtures.walletCategoryBalance);
    }

    @Test
    void shouldNotTransferAmountBetweenTheSameCategories() {
        //when
        Throwable throwable
                = catchThrowable(() -> budgetService.transferAmount(fixtures.savingsCategoryId,
                fixtures.savingsCategoryId, fixtures.transferAmount));

        //then
        assertThat(throwable).isInstanceOf(ValidationException.class);
        assertThat(fixtures.savingsCategory.getAmount()).isEqualByComparingTo(fixtures.savingsCategoryBalance);
    }

    @Test
    void shouldNotTransferAmountBetweenCategoriesWhenAmountIsLessThanZero() {
        //when
        Throwable throwable
                = catchThrowable(() -> budgetService.transferAmount(fixtures.walletCategoryId,
                fixtures.savingsCategoryId, BigDecimal.valueOf(-100)));

        //then
        assertThat(throwable).isInstanceOf(TransferException.class);
        assertThat(fixtures.walletCategory.getAmount()).isEqualByComparingTo(fixtures.walletCategoryBalance);
        assertThat(fixtures.savingsCategory.getAmount()).isEqualByComparingTo(fixtures.savingsCategoryBalance);

    }

    @Test
    void shouldNotTransferAmountBetweenCategoriesWhenAmountIsEqualZero() {
        //when
        Throwable throwable
                = catchThrowable(() -> budgetService.transferAmount(fixtures.walletCategoryId,
                fixtures.savingsCategoryId, BigDecimal.ZERO));

        //then
        assertThat(throwable).isInstanceOf(TransferException.class);
        assertThat(fixtures.walletCategory.getAmount()).isEqualByComparingTo(fixtures.walletCategoryBalance);
        assertThat(fixtures.savingsCategory.getAmount()).isEqualByComparingTo(fixtures.savingsCategoryBalance);

    }

    @Test
    void shouldNotTransferAmountWhenEntityNotFound() {
        //given
        when(budgetRepository.findById(fixtures.wrongCategoryId)).thenReturn(Optional.empty());

        //when
        Throwable throwable
                = catchThrowable(() -> budgetService.transferAmount(fixtures.wrongCategoryId,
                fixtures.savingsCategoryId, fixtures.transferAmount));

        //then
        assertThat(throwable).isInstanceOf(BudgetCategoryNotFoundException.class);
        assertThat(fixtures.savingsCategory.getAmount()).isEqualByComparingTo(fixtures.savingsCategoryBalance);
    }

    @Test
    void shouldTransferAmount() {
        //given
        when(budgetRepository.findById(fixtures.walletCategoryId)).thenReturn(Optional.of(fixtures.walletCategory));
        when(budgetRepository.findById(fixtures.savingsCategoryId)).thenReturn(Optional.of(fixtures.savingsCategory));

        //when
        budgetService.transferAmount(fixtures.walletCategoryId, fixtures.savingsCategoryId, fixtures.transferAmount);

        //then
        assertThat(fixtures.savingsCategory.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(600));
        assertThat(fixtures.walletCategory.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(900));
    }



    private static class Fixtures {
        Long wrongCategoryId = 3L;
        Long walletCategoryId = 1L;
        String walletCategoryName = "Wallet";
        BigDecimal walletCategoryBalance = BigDecimal.valueOf(1000);
        Long savingsCategoryId = 2L;
        String savingsCategoryName = "Savings";
        BigDecimal savingsCategoryBalance = BigDecimal.valueOf(500);
        BigDecimal transferAmount = BigDecimal.valueOf(100);
        BigDecimal depositAmount = BigDecimal.valueOf(150);
        BudgetCategory walletCategory = BudgetCategory.builder()
                .id(walletCategoryId)
                .name(walletCategoryName)
                .amount(walletCategoryBalance)
                .build();
        BudgetCategory savingsCategory = BudgetCategory.builder()
                .id(savingsCategoryId)
                .name(savingsCategoryName)
                .amount(savingsCategoryBalance)
                .build();

        CreateCategory walletCategoryDTO = CreateCategory.builder()
                .name(walletCategoryName)
                .amount(walletCategoryBalance)
                .build();

        public Long getWrongCategoryId() {
            return wrongCategoryId;
        }

        public void setWrongCategoryId(Long wrongCategoryId) {
            this.wrongCategoryId = wrongCategoryId;
        }

        public Long getWalletCategoryId() {
            return walletCategoryId;
        }

        public void setWalletCategoryId(Long walletCategoryId) {
            this.walletCategoryId = walletCategoryId;
        }

        public String getWalletCategoryName() {
            return walletCategoryName;
        }

        public void setWalletCategoryName(String walletCategoryName) {
            this.walletCategoryName = walletCategoryName;
        }

        public BigDecimal getWalletCategoryBalance() {
            return walletCategoryBalance;
        }

        public void setWalletCategoryBalance(BigDecimal walletCategoryBalance) {
            this.walletCategoryBalance = walletCategoryBalance;
        }

        public Long getSavingsCategoryId() {
            return savingsCategoryId;
        }

        public void setSavingsCategoryId(Long savingsCategoryId) {
            this.savingsCategoryId = savingsCategoryId;
        }

        public String getSavingsCategoryName() {
            return savingsCategoryName;
        }

        public void setSavingsCategoryName(String savingsCategoryName) {
            this.savingsCategoryName = savingsCategoryName;
        }

        public BigDecimal getSavingsCategoryBalance() {
            return savingsCategoryBalance;
        }

        public void setSavingsCategoryBalance(BigDecimal savingsCategoryBalance) {
            this.savingsCategoryBalance = savingsCategoryBalance;
        }

        public BigDecimal getTransferAmount() {
            return transferAmount;
        }

        public void setTransferAmount(BigDecimal transferAmount) {
            this.transferAmount = transferAmount;
        }

        public BigDecimal getDepositAmount() {
            return depositAmount;
        }

        public void setDepositAmount(BigDecimal depositAmount) {
            this.depositAmount = depositAmount;
        }

        public BudgetCategory getWalletCategory() {
            return walletCategory;
        }

        public void setWalletCategory(BudgetCategory walletCategory) {
            this.walletCategory = walletCategory;
        }

        public BudgetCategory getSavingsCategory() {
            return savingsCategory;
        }

        public void setSavingsCategory(BudgetCategory savingsCategory) {
            this.savingsCategory = savingsCategory;
        }

        public CreateCategory getWalletCategoryDTO() {
            return walletCategoryDTO;
        }

        public void setWalletCategoryDTO(CreateCategory walletCategoryDTO) {
            this.walletCategoryDTO = walletCategoryDTO;
        }
    }

}
