package com.dawidkubica.physio.screens.wizards

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dawidkubica.physio.screens.sign_in.components.HeaderView
import com.dawidkubica.physio.screens.wizards.viewmodels.CreatorWizardViewModel
import com.dawidkubica.physio.ui.theme.PhysioTheme
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.ghost_white
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun CreatorWizardScreen(
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreatorWizardViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (header, wizardForm, exitButton) = createRefs()

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
                .constrainAs(wizardForm) {
                    top.linkTo(header.bottom)
                    bottom.linkTo(exitButton.top)
                }
        ) {
            CreatorWizardForm(
                navigate,
                popBackStack
            )
        }

        Button(
            onClick = { viewModel.onExitWizardClick(navigate) },
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxWidth()
                .constrainAs(exitButton) {
                    bottom.linkTo(parent.bottom)
                },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Wyjdź z kreatora",
                color = MaterialTheme.colorScheme.surface,
                style = typography.labelLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(1.dp))
    }
}
