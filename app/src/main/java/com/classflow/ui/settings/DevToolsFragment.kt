package com.classflow.ui.settings

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.classflow.BuildConfig
import com.classflow.databinding.FragmentDevToolsBinding
import com.classflow.notification.NotificationHelper
import com.classflow.notification.ReminderScheduler
import com.classflow.notification.TestReminderWorker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class DevToolsFragment : Fragment() {

    private var _binding: FragmentDevToolsBinding? = null
    private val binding get() = _binding!!

    private val devToolsViewModel: DevToolsViewModel by viewModels()

    // ── Notification permission ───────────────────────────────────────────────

    private var pendingTestAction: (() -> Unit)? = null
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pendingTestAction?.invoke()
        } else {
            Toast.makeText(
                requireContext(),
                "Notifications are disabled. Enable them in app settings.",
                Toast.LENGTH_LONG
            ).show()
        }
        pendingTestAction = null
        updateDebugInfo()
    }

    // ── Export launcher ───────────────────────────────────────────────────────

    private val createDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) devToolsViewModel.writeExportToUri(uri)
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDevToolsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ── Notification test buttons ─────────────────────────────────────────

        binding.rowSendTestNotification.setOnClickListener {
            runWithNotificationPermission { sendTestNotificationImmediate() }
        }

        binding.rowTestReminder10s.setOnClickListener {
            runWithNotificationPermission { scheduleTestReminderIn10Seconds() }
        }

        binding.rowRescheduleAllReminders.setOnClickListener {
            lifecycleScope.launch {
                ReminderScheduler.scheduleAllEligibleReminders(requireContext())
                Toast.makeText(requireContext(), "All reminders rescheduled.", Toast.LENGTH_SHORT).show()
            }
        }

        // ── Backup test buttons ───────────────────────────────────────────────

        binding.rowExportTestBackup.setOnClickListener {
            devToolsViewModel.exportTestBackup()
        }

        binding.rowImportSampleBackup.setOnClickListener {
            devToolsViewModel.parseSampleBackup()
        }

        binding.rowDevClearAllData.setOnClickListener {
            showClearAllDataDialog()
        }

        // ── Version ───────────────────────────────────────────────────────────

        binding.tvDevVersion.text = BuildConfig.VERSION_NAME

        // ── Observers ─────────────────────────────────────────────────────────

        devToolsViewModel.exportFilename.observe(viewLifecycleOwner) { filename ->
            filename ?: return@observe
            devToolsViewModel.consumeExportFilename()
            createDocumentLauncher.launch(filename)
        }

        devToolsViewModel.exportSuccess.observe(viewLifecycleOwner) { success ->
            if (success != true) return@observe
            devToolsViewModel.consumeExportSuccess()
            Toast.makeText(requireContext(), "Backup file created.", Toast.LENGTH_SHORT).show()
        }

        devToolsViewModel.exportError.observe(viewLifecycleOwner) { msg ->
            msg ?: return@observe
            devToolsViewModel.consumeExportError()
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
        }

        devToolsViewModel.sampleImportPreview.observe(viewLifecycleOwner) { preview ->
            preview ?: return@observe
            devToolsViewModel.consumeSampleImportPreview()
            showSampleImportDialog(preview)
        }

        devToolsViewModel.sampleImportSuccess.observe(viewLifecycleOwner) { msg ->
            msg ?: return@observe
            devToolsViewModel.consumeSampleImportSuccess()
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        devToolsViewModel.sampleImportError.observe(viewLifecycleOwner) { msg ->
            msg ?: return@observe
            devToolsViewModel.consumeSampleImportError()
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
        }

        devToolsViewModel.clearAllSuccess.observe(viewLifecycleOwner) { success ->
            if (success != true) return@observe
            devToolsViewModel.consumeClearAllSuccess()
            Toast.makeText(requireContext(), "All ClassFlow data deleted.", Toast.LENGTH_SHORT).show()
        }

        devToolsViewModel.clearAllError.observe(viewLifecycleOwner) { msg ->
            msg ?: return@observe
            devToolsViewModel.consumeClearAllError()
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        updateDebugInfo()
    }

    // ── Dialogs ───────────────────────────────────────────────────────────────

    private fun showSampleImportDialog(preview: ImportPreview) {
        val msg = buildString {
            append("Sample backup:\n")
            append("  • ${preview.courseCount} ${if (preview.courseCount == 1) "class" else "classes"}\n")
            append("  • ${preview.taskCount} ${if (preview.taskCount == 1) "task" else "tasks"}")
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Import sample backup?")
            .setMessage(msg)
            .setPositiveButton("Merge") { _, _ -> devToolsViewModel.confirmSampleImport(false) }
            .setNeutralButton("Replace") { _, _ -> showSampleReplaceConfirmDialog() }
            .setNegativeButton("Cancel") { _, _ -> devToolsViewModel.cancelSampleImport() }
            .show()
    }

    private fun showSampleReplaceConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Replace all data?")
            .setMessage("This will delete all existing ClassFlow classes and tasks and replace them with the sample backup. This cannot be undone.")
            .setPositiveButton("Replace") { _, _ -> devToolsViewModel.confirmSampleImport(true) }
            .setNegativeButton("Cancel") { _, _ -> devToolsViewModel.cancelSampleImport() }
            .show()
    }

    private fun showClearAllDataDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Clear all ClassFlow data?")
            .setMessage("This will permanently delete all classes and tasks. This cannot be undone.")
            .setPositiveButton("Delete All") { _, _ -> devToolsViewModel.clearAllData() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ── Notification helpers ──────────────────────────────────────────────────

    private fun updateDebugInfo() {
        val ctx = requireContext()

        val permissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        binding.tvPermissionStatus.text = if (permissionGranted) "Granted" else "Denied"

        val channelExists = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.getNotificationChannel(NotificationHelper.CHANNEL_ID) != null
        } else {
            true
        }
        binding.tvChannelStatus.text = if (channelExists) "Created" else "Missing"
    }

    private fun runWithNotificationPermission(action: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
                action()
            } else {
                pendingTestAction = action
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            action()
        }
    }

    private fun sendTestNotificationImmediate() {
        NotificationHelper.showTestNotification(requireContext(), "This is a test reminder.")
    }

    private fun scheduleTestReminderIn10Seconds() {
        val request = OneTimeWorkRequestBuilder<TestReminderWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(requireContext()).enqueue(request)
        Toast.makeText(requireContext(), "Test reminder scheduled in 10 seconds.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
