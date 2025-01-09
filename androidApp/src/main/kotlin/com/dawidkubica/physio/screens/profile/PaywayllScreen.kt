package com.dawidkubica.physio.screens.profile

import androidx.compose.runtime.Composable
import com.revenuecat.purchases.ui.revenuecatui.ExperimentalPreviewRevenueCatUIPurchasesAPI
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialog
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialogOptions
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener

@OptIn(ExperimentalPreviewRevenueCatUIPurchasesAPI::class)
@Composable
fun PaywallScreen(popBackStack: () -> Unit) {
    PaywallDialog(
        PaywallDialogOptions.Builder()
            .setRequiredEntitlementIdentifier("entl2b496d26d4")
            .setShouldDisplayDismissButton(true)
            .setDismissRequest { popBackStack() }
            .setListener(
                object : PaywallListener {

                }
            ).build()
    )
}

