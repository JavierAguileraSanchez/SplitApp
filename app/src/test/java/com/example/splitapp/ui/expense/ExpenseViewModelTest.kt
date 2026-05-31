package com.example.splitapp.ui.expense

import com.example.splitapp.data.model.Expense
import com.example.splitapp.domain.usecase.expense.AddExpenseUseCase
import com.example.splitapp.domain.usecase.expense.GetExpensesUseCase
import com.example.splitapp.domain.usecase.expense.SettleDebtUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.math.round

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    lateinit var addExpenseUseCase: AddExpenseUseCase

    @MockK
    lateinit var getExpensesUseCase: GetExpensesUseCase

    @MockK
    lateinit var settleDebtUseCase: SettleDebtUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `equitable split with rounding produces 0_01 rounding impact`() = runTest {
        // Arrange
        val addExpense = mockk<AddExpenseUseCase>()
        val getExpenses = mockk<GetExpensesUseCase>()
        val settleDebt = mockk<SettleDebtUseCase>()

        every { getExpenses.invoke(any()) } returns flowOf(emptyList())
        val slotExpense = slot<Expense>()
        coEvery { addExpense.invoke(any(), capture(slotExpense)) } returns Result.success(Unit)

        val vm = ExpenseViewModel(addExpense, getExpenses, settleDebt, groupId = "g1")

        // Act: add expense 10.0 split equally among 3 participants
        val participants = listOf("u1", "u2", "u3")
        vm.addExpense(title = "Test", amount = 10.0, paidBy = "u1", participantUids = participants, customDistribution = null)

        // Advance coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val expense = slotExpense.captured
        val distribucion = expense.distribucion
        assertEquals(3, distribucion.size)

        // Each raw share
        val shares = distribucion.values.toList()
        // Rounded to cents
        val roundedSum = shares.sumOf { (Math.round(it * 100.0) / 100.0) }
        val roundedEach = shares.map { (Math.round(it * 100.0) / 100.0) }

        // Expect each rounded to 3.33 and total 9.99 with difference 0.01
        assertEquals(3, roundedEach.size)
        assertEquals(3.33, roundedEach[0], 0.001)
        assertEquals(3.33, roundedEach[1], 0.001)
        assertEquals(3.33, roundedEach[2], 0.001)

        val diff = (10.0 - roundedSum)
        // Due to rounding down, expect difference of 0.01
        assertEquals(0.01, (Math.round(diff * 100.0) / 100.0), 0.0)
    }

    @Test
    fun `custom amounts invalid when sum does not equal total`() = runTest {
        val addExpense = mockk<AddExpenseUseCase>()
        val getExpenses = mockk<GetExpensesUseCase>()
        val settleDebt = mockk<SettleDebtUseCase>()

        every { getExpenses.invoke(any()) } returns flowOf(emptyList())
        val slotExpense = slot<Expense>()
        coEvery { addExpense.invoke(any(), capture(slotExpense)) } returns Result.success(Unit)

        val vm = ExpenseViewModel(addExpense, getExpenses, settleDebt, groupId = "g1")

        val participants = listOf("u1", "u2", "u3")
        // Provide custom amounts that sum to 8.0 while total is 10.0
        val custom = mapOf("u1" to 4.0, "u2" to 3.0, "u3" to 1.0)

        vm.addExpense(title = "CustomInvalid", amount = 10.0, paidBy = "u1", participantUids = participants, customDistribution = custom)
        testDispatcher.scheduler.advanceUntilIdle()

        val expense = slotExpense.captured
        val distribucion = expense.distribucion
        val sum = distribucion.values.sum()

        // The distribution sum must not equal the total amount: invalid
        assertTrue(sum != 10.0)
    }

    @Test
    fun `percentages summing 100 produce valid euro equivalents`() = runTest {
        val addExpense = mockk<AddExpenseUseCase>()
        val getExpenses = mockk<GetExpensesUseCase>()
        val settleDebt = mockk<SettleDebtUseCase>()

        every { getExpenses.invoke(any()) } returns flowOf(emptyList())
        val slotExpense = slot<Expense>()
        coEvery { addExpense.invoke(any(), capture(slotExpense)) } returns Result.success(Unit)

        val vm = ExpenseViewModel(addExpense, getExpenses, settleDebt, groupId = "g1")

        val participants = listOf("u1", "u2", "u3")
        val total = 100.0
        // Percentages that sum to 100%
        val percentages = mapOf("u1" to 50.0, "u2" to 30.0, "u3" to 20.0)
        // Convert to euros as UI would
        val custom = percentages.mapValues { (it.value / 100.0) * total }

        vm.addExpense(title = "PercentValid", amount = total, paidBy = "u1", participantUids = participants, customDistribution = custom)
        testDispatcher.scheduler.advanceUntilIdle()

        val expense = slotExpense.captured
        val distribucion = expense.distribucion
        val sumRounded = distribucion.values.sumOf { Math.round(it * 100.0) / 100.0 }

        // Sum of euro equivalents rounded to cents should equal total
        assertEquals(total, sumRounded, 0.01)

        // Verify individual mappings
        assertEquals(50.0, (Math.round((distribucion["u1"]!! * 100.0)) / 100.0), 0.01)
        assertEquals(30.0, (Math.round((distribucion["u2"]!! * 100.0)) / 100.0), 0.01)
        assertEquals(20.0, (Math.round((distribucion["u3"]!! * 100.0)) / 100.0), 0.01)
    }
}
