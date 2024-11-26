package com.dawidkubica.physio.screens.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.screens.reminders.components.DropdownMenu
import com.dawidkubica.physio.screens.reminders.components.TimePicker
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun RemindersForm(
    viewModel: ReminderViewModel,
    selectedDay: String,
    onDaySelected: (String) -> Unit,
    onTimeSelected: (String) -> Unit,
    selectedPackage: String,
    onPackageSelected: (String) -> Unit,
) {
    val listedPackages by viewModel.listedPackages.collectAsState()
    val reminders by viewModel.reminders.observeAsState(emptyList())
    val daysOfWeek =
        listOf("Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela")


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp)
            .wrapContentHeight(Alignment.Top)
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ustal swój harmonogram ćwiczeń dla każdego pakietu",
            style = typography.labelLarge,
            modifier = Modifier.padding(top = 10.dp),
            textAlign = TextAlign.Center
        )

        DropdownMenu(
            defaultTitle = "Wybierz dzień tygodnia",
            items = daysOfWeek,
            selectedItem = selectedDay,
            onItemSelected = onDaySelected
        )

        TimePicker(onTimeSelected = onTimeSelected)

        Text(
            text = "Wybierz pakiet ćwiczeń",
            style = typography.labelLarge,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .heightIn(max = 300.dp)
                .wrapContentHeight(align = Alignment.Top)
                .fillMaxWidth()
        )

        DropdownMenu(
            defaultTitle = "Wybierz pakiet ćwiczeń",
            items = listedPackages.toList(),
            selectedItem = selectedPackage,
            onItemSelected = onPackageSelected
        )

        Spacer(modifier = Modifier.height(120.dp))
    }
}

