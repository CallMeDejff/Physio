package com.dawidkubica.physio.screens.reminders

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dawidkubica.physio.screens.sign_in.components.HeaderView
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.ghost_white
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun ScheduleReminderScreen(
    popBackStack: () -> Unit,
    viewModel: ReminderViewModel
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

    val selectedDay = remember { mutableStateOf("") }
    val selectedTime = remember { mutableStateOf("") }
    val selectedPackage = remember { mutableStateOf("") }

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = colorPrimary)
        }
    } else {
        ConstraintLayout {
            val (header, remindersForm, navigationButtons) = createRefs()

            HeaderView(
                modifier = Modifier
                    .height(320.dp)
                    .fillMaxWidth()
                    .constrainAs(header) {
                        top.linkTo(parent.top)
                    },
                200, 0.7f
            )

            Card(
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 120.dp)
                    .constrainAs(remindersForm) {
                        top.linkTo(header.bottom)
                        bottom.linkTo(navigationButtons.top)
                    }
            ) {
                RemindersForm(
                    viewModel = viewModel,
                    selectedDay = selectedDay.value,
                    onDaySelected = { selectedDay.value = it },
                    onTimeSelected = { selectedTime.value = it },
                    selectedPackage = selectedPackage.value,
                    onPackageSelected = { selectedPackage.value = it },
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp)
                    .constrainAs(navigationButtons) {
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Button(
                    onClick = {
                        viewModel.scheduleReminder(
                            dayOfWeek = selectedDay.value,
                            time = selectedTime.value,
                            topic = selectedPackage.value
                        )
                    },
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Utwórz",
                        color = MaterialTheme.colorScheme.surface,
                        style = typography.labelLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                Button(
                    onClick = { viewModel.onGoBackClick(popBackStack) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Cofnij",
                        color = MaterialTheme.colorScheme.surface,
                        style = typography.labelLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}
