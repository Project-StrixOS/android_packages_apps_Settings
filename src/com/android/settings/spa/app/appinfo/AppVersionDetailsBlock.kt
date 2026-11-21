/*
 * Copyright (C) 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.spa.app.appinfo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageInfo
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.android.settings.R
import com.android.settings.Utils
import com.android.settings.applications.AppStoreUtil
import com.android.settingslib.spa.widget.preference.Preference
import com.android.settingslib.spa.widget.preference.PreferenceModel
import com.android.settingslib.spa.widget.ui.Category
import com.android.settingslib.spaprivileged.framework.common.asUser
import com.android.settingslib.spaprivileged.model.app.userHandle
import java.text.DateFormat
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A [Category] block showing the details of the package: package name, version, SDK, install date,
 * update date, install source and user id.
 *
 * Tapping the package name copies it to the clipboard.
 */
@Composable
fun AppVersionDetailsBlock(packageInfo: PackageInfo) {
    val context = LocalContext.current
    val app = packageInfo.applicationInfo ?: return
    val unknown = stringResource(R.string.unknown)
    val dateFormat = remember { DateFormat.getDateInstance(DateFormat.MEDIUM) }

    val installSource by produceState(initialValue = unknown, key1 = app.packageName) {
        value = withContext(Dispatchers.IO) {
            val userContext = context.asUser(app.userHandle)
            val installerPackage =
                AppStoreUtil.getInstallerPackageNameAndInstallSourceInfo(
                    userContext,
                    app.packageName,
                ).first
            if (installerPackage == null) {
                unknown
            } else {
                (Utils.getApplicationLabel(context, installerPackage)
                    ?: installerPackage).toString()
            }
        }
    }

    val versionSummary = stringResource(
        R.string.app_version_summary,
        packageInfo.versionName ?: unknown,
        packageInfo.longVersionCode,
    )
    val sdkSummary = stringResource(
        R.string.app_sdk_summary,
        app.targetSdkVersion,
        app.minSdkVersion,
    )
    val installDate = dateFormat.format(Date(packageInfo.firstInstallTime))
    val updateDate = dateFormat.format(Date(packageInfo.lastUpdateTime))
    val uidSummary = app.uid.toString()

    Category(title = stringResource(R.string.app_package_info_group_title)) {
        Preference(
            object : PreferenceModel {
                override val title = stringResource(R.string.app_package_name_title)
                override val summary = { app.packageName }
                override val onClick = { copyPackageName(context, app.packageName) }
            },
        )
        Preference(
            object : PreferenceModel {
                override val title = stringResource(R.string.app_version_title)
                override val summary = { versionSummary }
            },
        )
        Preference(
            object : PreferenceModel {
                override val title = stringResource(R.string.app_sdk_title)
                override val summary = { sdkSummary }
            },
        )
        Preference(
            object : PreferenceModel {
                override val title = stringResource(R.string.app_install_date_title)
                override val summary = { installDate }
            },
        )
        Preference(
            object : PreferenceModel {
                override val title = stringResource(R.string.app_update_date_title)
                override val summary = { updateDate }
            },
        )
        Preference(
            object : PreferenceModel {
                override val title = stringResource(R.string.app_install_source_title)
                override val summary = { installSource }
            },
        )
        Preference(
            object : PreferenceModel {
                override val title = stringResource(R.string.app_uid_title)
                override val summary = { uidSummary }
            },
        )
    }
}

private fun copyPackageName(context: Context, packageName: String) {
    val clipboard = context.getSystemService(ClipboardManager::class.java) ?: return
    clipboard.setPrimaryClip(ClipData.newPlainText(packageName, packageName))
    Toast.makeText(context, R.string.app_package_name_copied, Toast.LENGTH_SHORT).show()
}
