package com.pandu.allocap.ui.sandbox

import com.pandu.allocap.data.local.AllocationDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class SandboxViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `adjusting needs rebalances wants and savings proportionally`() = runTest {
        val dao = mock(AllocationDao::class.java)
        `when`(dao.getAllocationSettings()).thenReturn(flowOf(null)) // Use defaults
        
        val viewModel = SandboxViewModel(dao)
        
        // Initial defaults: Needs 0.5, Wants 0.3, Savings 0.2
        // Increase Needs to 0.6 (delta +0.1)
        // Remaining is 0.4. Original others sum was 0.5.
        // Wants ratio was 0.3/0.5 = 0.6
        // Savings ratio was 0.2/0.5 = 0.4
        // New Wants = 0.4 * 0.6 = 0.24
        // New Savings = 0.4 * 0.4 = 0.16
        
        viewModel.updateNeeds(0.6f)
        
        val state = viewModel.state.value
        assertEquals(0.6f, state.needs, 0.001f)
        assertEquals(0.24f, state.wants, 0.001f)
        assertEquals(0.16f, state.savings, 0.001f)
        assertEquals(1.0f, state.needs + state.wants + state.savings, 0.001f)
    }
}
