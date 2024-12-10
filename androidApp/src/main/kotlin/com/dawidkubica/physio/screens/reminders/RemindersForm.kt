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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.R
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
    val daysOfWeek = stringArrayResource(id = R.array.days_of_week_full).toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp, top = 10.dp)
            .wrapContentHeight(Alignment.Top),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.reminders_form_title),
            style = typography.labelLarge,
            modifier = Modifier.padding(top = 10.dp),
            textAlign = TextAlign.Center
        )

        DropdownMenu(
            defaultTitle = stringResource(id = R.string.reminders_form_select_day),
            items = daysOfWeek,
            selectedItem = selectedDay,
            onItemSelected = onDaySelected,
        )

        TimePicker(onTimeSelected = onTimeSelected)

        Text(
            text = stringResource(id = R.string.reminders_form_select_package),
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
            defaultTitle = stringResource(id = R.string.reminders_form_select_package),
            items = listedPackages.toList(),
            selectedItem = selectedPackage,
            onItemSelected = onPackageSelected
        )

        Spacer(modifier = Modifier.height(120.dp))
    }
}

