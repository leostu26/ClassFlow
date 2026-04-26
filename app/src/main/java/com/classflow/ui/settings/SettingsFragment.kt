package com.classflow.ui.settings

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.classflow.BuildConfig
import com.classflow.R
import com.classflow.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.enableNotifications()
            Snackbar.make(binding.root, "Task reminders enabled.", Snackbar.LENGTH_SHORT).show()
        } else {
            viewModel.disableNotifications()
            Toast.makeText(requireContext(), "Notification permission is required for reminders.", Toast.LENGTH_LONG).show()
        }
    }

    private val createDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) viewModel.writeExportToUri(uri)
    }

    private val pickFileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) viewModel.parseImportFile(uri)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvAppVersion.text = "Version ${BuildConfig.VERSION_NAME}"
        binding.rowGithub.setOnClickListener { openGitHubRepo() }
        binding.rowDevTools.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_devToolsFragment)
        }

        // ── Preferences ───────────────────────────────────────────────────────

        var isLoadingSettings = true

        viewModel.settings.observe(viewLifecycleOwner) { s ->
            isLoadingSettings = true
            binding.tvThemeValue.text = s.themeMode
            binding.tvWeekStartValue.text = s.weekStartDay
            binding.switchShowCompleted.isChecked = s.showCompletedTasks
            binding.tvDefaultTypeValue.text = s.defaultTaskType
            binding.tvDefaultPriorityValue.text = s.defaultPriority
            binding.switchNotifications.isChecked = s.notificationsEnabled
            binding.tvReminderTimingValue.text = s.reminderTiming
            val timingAlpha = if (s.notificationsEnabled) 1f else 0.38f
            binding.rowReminderTiming.alpha = timingAlpha
            binding.rowReminderTiming.isEnabled = s.notificationsEnabled
            isLoadingSettings = false
        }

        binding.rowTheme.setOnClickListener {
            val current = viewModel.settings.value?.themeMode ?: "System"
            showThemeDialog(current)
        }

        binding.rowWeekStart.setOnClickListener {
            val current = viewModel.settings.value?.weekStartDay ?: "Sunday"
            showWeekStartDialog(current)
        }

        binding.rowShowCompleted.setOnClickListener { binding.switchShowCompleted.toggle() }
        binding.switchShowCompleted.setOnCheckedChangeListener { _, isChecked ->
            if (!isLoadingSettings) viewModel.updateShowCompletedTasks(isChecked)
        }

        // ── Task Defaults ─────────────────────────────────────────────────────

        binding.rowDefaultType.setOnClickListener {
            showDefaultTypeDialog(viewModel.settings.value?.defaultTaskType ?: "Assignment")
        }
        binding.rowDefaultPriority.setOnClickListener {
            showDefaultPriorityDialog(viewModel.settings.value?.defaultPriority ?: "Medium")
        }

        // ── Reminders ─────────────────────────────────────────────────────────

        binding.rowNotifications.setOnClickListener { binding.switchNotifications.toggle() }
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (!isLoadingSettings) {
                if (isChecked) {
                    requestNotificationPermissionOrEnable()
                } else {
                    viewModel.disableNotifications()
                    Snackbar.make(binding.root, "Task reminders disabled.", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        binding.rowReminderTiming.setOnClickListener {
            if (viewModel.settings.value?.notificationsEnabled == true) {
                showReminderTimingDialog(viewModel.settings.value?.reminderTiming ?: "1 day before")
            }
        }

        // ── Export ────────────────────────────────────────────────────────────

        binding.rowExport.setOnClickListener { viewModel.exportBackup() }

        viewModel.exportFilename.observe(viewLifecycleOwner) { filename ->
            filename ?: return@observe
            viewModel.consumeExportFilename()
            createDocumentLauncher.launch(filename)
        }

        viewModel.exportSuccess.observe(viewLifecycleOwner) { success ->
            if (success != true) return@observe
            viewModel.consumeExportSuccess()
            Toast.makeText(requireContext(), "Backup file created.", Toast.LENGTH_SHORT).show()
        }

        viewModel.exportError.observe(viewLifecycleOwner) { msg ->
            msg ?: return@observe
            viewModel.consumeExportError()
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
        }

        // ── Import ────────────────────────────────────────────────────────────

        binding.rowImport.setOnClickListener { pickFileLauncher.launch("*/*") }

        viewModel.importConfirmation.observe(viewLifecycleOwner) { preview ->
            preview ?: return@observe
            viewModel.consumeImportConfirmation()
            val msg = buildString {
                append("Backup found:\n")
                append("  • ${preview.courseCount} ${if (preview.courseCount == 1) "class" else "classes"}\n")
                append("  • ${preview.taskCount} ${if (preview.taskCount == 1) "task" else "tasks"}\n")
                append("Exported: ${preview.exportedAt}")
            }
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Import backup")
                .setMessage(msg)
                .setPositiveButton("Merge") { _, _ -> viewModel.confirmImport(false) }
                .setNeutralButton("Replace") { _, _ -> showReplaceConfirmDialog() }
                .setNegativeButton("Cancel") { _, _ -> viewModel.cancelImport() }
                .show()
        }

        viewModel.importSuccess.observe(viewLifecycleOwner) { msg ->
            msg ?: return@observe
            viewModel.consumeImportSuccess()
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        viewModel.importError.observe(viewLifecycleOwner) { msg ->
            msg ?: return@observe
            viewModel.consumeImportError()
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
        }

        // ── Restore Default Settings ──────────────────────────────────────────

        binding.rowRestoreDefaults.setOnClickListener { showRestoreDefaultsDialog() }

        viewModel.restoreSuccess.observe(viewLifecycleOwner) { success ->
            if (success != true) return@observe
            viewModel.consumeRestoreSuccess()
            Toast.makeText(requireContext(), "Default settings restored.", Toast.LENGTH_SHORT).show()
        }

        // ── Delete All Data ───────────────────────────────────────────────────

        binding.btnDeleteAllData.setOnClickListener { showDeleteAllDataDialog() }

        viewModel.deleteAllSuccess.observe(viewLifecycleOwner) { success ->
            if (success != true) return@observe
            viewModel.consumeDeleteAllSuccess()
            Toast.makeText(requireContext(), "All ClassFlow data deleted.", Toast.LENGTH_SHORT).show()
        }

        viewModel.deleteAllError.observe(viewLifecycleOwner) { msg ->
            msg ?: return@observe
            viewModel.consumeDeleteAllError()
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
        }
    }

    // ── Dialogs ───────────────────────────────────────────────────────────────

    private fun showReplaceConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Replace all data?")
            .setMessage("This will delete all existing ClassFlow classes and tasks and replace them with the backup. This cannot be undone.")
            .setPositiveButton("Replace") { _, _ -> viewModel.confirmImport(true) }
            .setNegativeButton("Cancel") { _, _ -> viewModel.cancelImport() }
            .show()
    }

    private fun showRestoreDefaultsDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Restore default settings?")
            .setMessage("This will reset your preferences, task defaults, and reminder settings. Your classes and tasks will not be deleted.")
            .setPositiveButton("Restore") { _, _ -> viewModel.restoreDefaultSettings() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteAllDataDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete all ClassFlow data?")
            .setMessage("This will permanently delete all classes and tasks. This cannot be undone.")
            .setPositiveButton("Continue") { _, _ -> showDeleteConfirmDialog() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmDialog() {
        val dp = resources.displayMetrics.density.toInt()
        val editText = EditText(requireContext()).apply {
            hint = "Type DELETE to confirm"
            setPadding(dp * 24, dp * 8, dp * 24, dp * 8)
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Are you sure?")
            .setMessage("Type DELETE to permanently remove all classes and tasks.")
            .setView(editText)
            .setPositiveButton("Delete All Data", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val deleteBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            deleteBtn.isEnabled = false
            deleteBtn.setTextColor(requireContext().getColor(R.color.cancel_text))

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    deleteBtn.isEnabled = s?.toString() == "DELETE"
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            deleteBtn.setOnClickListener {
                if (editText.text.toString() == "DELETE") {
                    dialog.dismiss()
                    viewModel.deleteAllData()
                }
            }
        }
        dialog.show()
    }

    private fun showThemeDialog(current: String) {
        val options = arrayOf("System", "Light", "Dark")
        val currentIndex = options.indexOf(current).coerceAtLeast(0)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Choose theme")
            .setSingleChoiceItems(options, currentIndex) { dialog, which ->
                val selected = options[which]
                viewModel.updateThemeMode(selected)
                applyTheme(selected)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showWeekStartDialog(current: String) {
        val options = arrayOf("Sunday", "Monday")
        val currentIndex = options.indexOf(current).coerceAtLeast(0)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Week starts on")
            .setSingleChoiceItems(options, currentIndex) { dialog, which ->
                viewModel.updateWeekStartDay(options[which])
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDefaultTypeDialog(current: String) {
        val options = arrayOf("Assignment", "Quiz", "Exam", "Project", "Other")
        val currentIndex = options.indexOf(current).coerceAtLeast(0)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Default task type")
            .setSingleChoiceItems(options, currentIndex) { dialog, which ->
                viewModel.updateDefaultTaskType(options[which])
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDefaultPriorityDialog(current: String) {
        val options = arrayOf("Low", "Medium", "High")
        val currentIndex = options.indexOf(current).coerceAtLeast(0)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Default priority")
            .setSingleChoiceItems(options, currentIndex) { dialog, which ->
                viewModel.updateDefaultPriority(options[which])
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showReminderTimingDialog(current: String) {
        val options = arrayOf("Same day", "1 day before", "3 days before")
        val currentIndex = options.indexOf(current).coerceAtLeast(0)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Reminder timing")
            .setSingleChoiceItems(options, currentIndex) { dialog, which ->
                viewModel.updateReminderTiming(options[which])
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun requestNotificationPermissionOrEnable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
                viewModel.enableNotifications()
                Snackbar.make(binding.root, "Task reminders enabled.", Snackbar.LENGTH_SHORT).show()
            } else {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            viewModel.enableNotifications()
            Snackbar.make(binding.root, "Task reminders enabled.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun openGitHubRepo() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/leostu26/ClassFlow")))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Unable to open link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyTheme(mode: String) {
        val newMode = when (mode) {
            "Light" -> AppCompatDelegate.MODE_NIGHT_NO
            "Dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        if (AppCompatDelegate.getDefaultNightMode() != newMode) {
            AppCompatDelegate.setDefaultNightMode(newMode)
            // TODO: Live theme refresh — currently requires activity recreation for full effect
            requireActivity().recreate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
