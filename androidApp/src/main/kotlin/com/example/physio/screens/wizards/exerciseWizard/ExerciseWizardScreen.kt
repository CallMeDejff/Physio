import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physio.screens.wizards.AutoComplete
import com.example.physio.screens.wizards.CreatorWizardViewModel
import com.example.physio.screens.sign_in.HeaderView
import com.example.physio.ui.colorPrimary
import com.example.physio.ui.ghost_white
import com.example.physio.ui.typography

@Composable
fun ExerciseWizardScreen(
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreatorWizardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val showProgress by viewModel.showProgress.collectAsState()

    LaunchedEffect(viewModel.wizardMessage) {
        viewModel.wizardMessage.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearLoginMessage()
            }
        }
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (header, wizardForm, exitContinueButtons) = createRefs()

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
            colors = CardDefaults.cardColors(containerColor = ghost_white),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(wizardForm) {
                    top.linkTo(header.bottom)
                    bottom.linkTo(exitContinueButtons.top)
                }
        ) {
            ExerciseWizardForm(
                showProgress,
                navigate,
                popBackStack
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .constrainAs(exitContinueButtons) {
                    bottom.linkTo(parent.bottom)
                },
        ) {
            Button(
                onClick = { viewModel.onExerciseWizardContinueClick(navigate, viewModel) },
                modifier = Modifier
                    .weight(2f),
                //.padding(vertical = 16.dp, horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorPrimary)
            ) {
                Text(
                    text = "Kontynuuj",
                    color = Color.White,
                    style = typography.labelLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            Button(
                onClick = { viewModel.onExitWizardClick(popBackStack) },
                modifier = Modifier
                    .weight(1f),
                //.padding(8.dp),
                shape = RoundedCornerShape(16.dp),

                colors = ButtonDefaults.buttonColors(containerColor = colorPrimary)
            ) {
                Text(
                    text = "Cofnij",
                    color = Color.White,
                    style = typography.labelLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(1.dp))
    }
}


@Composable
fun ExerciseWizardForm(
    showProgress: Boolean,
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    viewModel: CreatorWizardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val selectedEquipment by viewModel.selectedEquipment.collectAsState()
    val equipmentList by viewModel.equipmentList.collectAsState()
    val selectedConditions by viewModel.selectedConditions.collectAsState()
    val conditionsList by viewModel.conditionsList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEquipmentList()
        viewModel.loadConditionList()
    }

    LaunchedEffect(viewModel.wizardMessage) {
        viewModel.wizardMessage.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearLoginMessage()
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
            CircularProgressIndicator()
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Kreator nowego ")

                        withStyle(style = SpanStyle(color = colorPrimary)) {
                            append("ćwiczenia")
                        }
                    },
                    style = typography.bodyLarge,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Podaj tytuł ćwiczenia",
                    style = typography.labelLarge,
                    modifier = Modifier
                )

                TextField(
                    value = viewModel.exerciseTitle.collectAsState().value ?: "",
                    onValueChange = { newTitle -> viewModel.updateExerciseTitle(newTitle) },
                    label = { Text("Tytuł ćwiczenia", style = typography.labelMedium) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions.Default
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Wybór sprzętu",
                    style = typography.labelLarge,
                    modifier = Modifier
                        .padding(top = 8.dp)
                )

                AutoComplete(
                    itemList = equipmentList,
                    selectedItems = selectedEquipment,
                    onToggleItem = { equipmentId ->
                        viewModel.toggleEquipment(equipmentId)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Wybór schorzenia",
                    style = typography.labelLarge,
                    modifier = Modifier
                        .padding(top = 8.dp)
                )

                AutoComplete(
                    itemList = conditionsList,
                    selectedItems = selectedConditions,
                    onToggleItem = { diseaseId ->
                        viewModel.toggleDisease(diseaseId)
                    }
                )
            }
        }
    }
}
