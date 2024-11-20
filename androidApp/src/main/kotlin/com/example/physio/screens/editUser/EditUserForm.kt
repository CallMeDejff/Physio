package com.example.physio.screens.editUser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.physio.R
import com.example.physio.screens.sign_in.components.LabeledTextField
import com.example.physio.ui.theme.colorPrimary

@Composable
fun EditUserForm(
    nameState: MutableState<TextFieldValue>,
    lastnameState: MutableState<TextFieldValue>,
    licenseNumberState: MutableState<TextFieldValue>,
    userType: Int,
) {
    val isLicenseChecked = remember { mutableStateOf(false) }

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 20.dp),
            text = "Edytuj swoje dane.",
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
        )

        LabeledTextField(
            label = "Imię",
            valueState = nameState,
            placeholder = "Imię",
            leadingIcon = Icons.Outlined.AccountBox,
            keyboardType = KeyboardType.Text,
            isPassword = false
        )

        LabeledTextField(
            label = "Nazwisko",
            valueState = lastnameState,
            placeholder = "Nazwisko",
            leadingIcon = Icons.Outlined.Person,
            keyboardType = KeyboardType.Text,
            isPassword = false
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = userType == 1 || isLicenseChecked.value,
                onCheckedChange = { isChecked ->
                    isLicenseChecked.value = isChecked
                },
                colors = CheckboxDefaults.colors(colorPrimary)
            )

            Text(
                text = "Jestem fizjoterapeutą zarejestrowanym w KIF",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.helvetica_neue_regular))
                )
            )
        }

        if (userType == 1 || isLicenseChecked.value) {
            LabeledTextField(
                label = "Numer licencji",
                valueState = licenseNumberState,
                placeholder = "Numer licencji",
                leadingIcon = Icons.Outlined.Badge,
                keyboardType = KeyboardType.Number,
                isPassword = false
            )
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}