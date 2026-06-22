package com.pandu.allocap.ui.welcome

import com.pandu.allocap.data.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class WelcomeViewModelTest {

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
    fun `userName flow correctly reflects repository data`() = runTest {
        val repository = mock(UserPreferencesRepository::class.java)
        `when`(repository.userNameFlow).thenReturn(flowOf("Pandu"))
        
        val viewModel = WelcomeViewModel(repository)
        
        // Use a background job to collect the StateFlow to keep it active
        val collectJob = launch(testDispatcher) {
            viewModel.userName.collect {}
        }
        
        assertEquals("Pandu", viewModel.userName.value)
        
        collectJob.cancel()
    }
}
