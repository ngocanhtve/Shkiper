package com.jobik.shkiper.screens.about

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jobik.shkiper.BuildConfig
import com.jobik.shkiper.R
import com.jobik.shkiper.helpers.IntentHelper
import com.jobik.shkiper.services.statistics.StatisticsService
import com.jobik.shkiper.ui.components.cards.LinkCard
import com.jobik.shkiper.ui.components.cards.UserCard
import com.jobik.shkiper.ui.components.cards.UserCardLink
import com.jobik.shkiper.ui.components.layouts.ScreenWrapper
import com.jobik.shkiper.ui.helpers.allWindowInsetsPadding
import com.jobik.shkiper.ui.modifiers.bounceClick
import com.jobik.shkiper.ui.theme.AppTheme

@SuppressLint("Range")
@Composable
fun AboutNotepadScreen() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val statisticsService = StatisticsService(context)

        statisticsService.appStatistics.apply {
            truthSeeker.increment()
        }
        statisticsService.saveStatistics()
    }

    ScreenWrapper(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .allWindowInsetsPadding()
            .padding(top = 85.dp, bottom = 30.dp)
            .padding(horizontal = 20.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .fillMaxWidth(.65f)
                    .clip(AppTheme.shapes.medium)
                    .background(AppTheme.colors.container)
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    color = AppTheme.colors.primary,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Text(
                    text = stringResource(R.string.AboutAppDescription),
                    color = AppTheme.colors.textSecondary,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .fillMaxWidth(1.33f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(.65f)
                        .fillMaxWidth()
                        .clip(AppTheme.shapes.medium)
                        .background(AppTheme.colors.container)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier.widthIn(max = 100.dp),
                        painter = painterResource(id = R.drawable.ic_app),
                        contentDescription = stringResource(R.string.Image),
                        contentScale = ContentScale.Fit
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight(1.35f)
                        .fillMaxWidth()
                        .clip(AppTheme.shapes.medium)
                        .background(AppTheme.colors.container)
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.basicMarquee().padding(horizontal = 20.dp),
                        text = BuildConfig.VERSION_NAME,
                        color = AppTheme.colors.primary,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        val shkiperLink = stringResource(id = R.string.shkiper_github_link)
        val onLinkCopiedText = stringResource(R.string.LinkCopied)
        val linkTextLabel = stringResource(R.string.Link)
        LinkCard(link = shkiperLink, linkTextLabel = linkTextLabel, onLinkCopiedText = onLinkCopiedText)

        Spacer(modifier = Modifier.height(16.dp))
        Column (modifier = Modifier.fillMaxWidth()){
            Text(
                text = stringResource(R.string.OtherMyApps),
                color = AppTheme.colors.textSecondary,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Column(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                val link = stringResource(R.string.game_of_life_link)
                Card(
                    modifier = Modifier
                        .height(160.dp)
                        .bounceClick()
                        .clickable { IntentHelper().openBrowserIntent(context = context, link = link) },
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.game_of_life_banner),
                        contentDescription = null,
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            Text(
                text = stringResource(R.string.Contact),
                color = AppTheme.colors.textSecondary,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            val email = stringResource(R.string.jobik_link)
            val emailHeader = stringResource(R.string.DevMailHeader)
            UserCard(
                photo = R.drawable.photo_my_favorite_cat_2,
                name = stringResource(R.string.Efim),
                description = stringResource(R.string.EfimDescription),
                onClick = { IntentHelper().sendMailIntent(context, listOf(email), emailHeader) },
                links = listOf(
                    UserCardLink(
                        image = R.drawable.ic_gmail,
                        description = stringResource(R.string.Image),
                    ) { IntentHelper().sendMailIntent(context, listOf(email), emailHeader) }
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.Icons),
                color = AppTheme.colors.textSecondary,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                val rakibHassanRahimLink = stringResource(R.string.RakibHassanRahimLink)
                UserCard(
                    photo = R.drawable.rakib_hassan_rahim,
                    onClick = { IntentHelper().openBrowserIntent(context, rakibHassanRahimLink) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                val freepikLink = stringResource(R.string.FreepikLink)
                UserCard(
                    photo = R.drawable.freepik,
                    onClick = { IntentHelper().openBrowserIntent(context, freepikLink) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                val stickersLink = stringResource(R.string.StickersLink)
                UserCard(
                    photo = R.drawable.stickers_pack,
                    onClick = { IntentHelper().openBrowserIntent(context, stickersLink) }
                )
            }
        }
    }
}