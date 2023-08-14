package com.jobik.shkiper.ui.components.cards

import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.billingclient.api.ProductDetails
import com.jobik.shkiper.R
import com.jobik.shkiper.ui.helpers.MultipleEventsCutter
import com.jobik.shkiper.ui.helpers.get
import com.jobik.shkiper.ui.modifiers.bounceClick
import com.jobik.shkiper.ui.theme.CustomAppTheme

data class PurchaseCardContent(
    val product: ProductDetails,
    @DrawableRes
    val image: Int,
    val isBestOffer: Boolean = false
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PurchaseCard(purchaseCardContent: PurchaseCardContent, onClick: () -> Unit) {
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }

    Card(
        modifier = Modifier
            .bounceClick()
            .height(130.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .clickable(onClick = { multipleEventsCutter.processEvent { onClick() } }),
        elevation = 0.dp,
        shape = RoundedCornerShape(15.dp),
        border = if (purchaseCardContent.isBestOffer) BorderStroke(2.dp, CustomAppTheme.colors.active) else null,
        backgroundColor = CustomAppTheme.colors.secondaryBackground,
        contentColor = CustomAppTheme.colors.text,
    ) {
        Column(
            modifier = Modifier.padding(10.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
//            Image(
//                modifier = Modifier.height(60.dp).padding(vertical = 5.dp),
//                painter = painterResource(id = statistic.image),
//                contentDescription = stringResource(R.string.StatisticsImage),
//                contentScale = ContentScale.Fit
//            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.height(75.dp).fillMaxWidth(),
                    painter = painterResource(id = purchaseCardContent.image),
                    contentDescription = stringResource(R.string.StatisticsImage),
                    contentScale = ContentScale.Fit
                )
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = purchaseCardContent.product.name,
                        color = CustomAppTheme.colors.text,
                        style = MaterialTheme.typography.body1.copy(fontSize = 14.sp),
                        textAlign = TextAlign.Center,
                    )
                }
//                Text(
//                    product.description,
//                    color = CustomAppTheme.colors.textSecondary,
//                    style = MaterialTheme.typography.body1.copy(fontSize = 14.sp),
//                )
            }
        }
    }
}