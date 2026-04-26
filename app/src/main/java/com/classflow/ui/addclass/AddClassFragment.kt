package com.classflow.ui.addclass

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.classflow.R
import com.classflow.databinding.FragmentAddClassBinding
import kotlin.math.sqrt
import kotlin.random.Random

class AddClassFragment : Fragment() {

    private var _binding: FragmentAddClassBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddClassViewModel by viewModels()

    private val colorPalette = listOf(
        "#4A90E2", "#E74C3C", "#2ECC71", "#F39C12",
        "#9B59B6", "#1ABC9C", "#E67E22", "#34495E",
        "#E91E63", "#3F51B5", "#00BCD4", "#FFC107"
    )
    private var selectedColor = colorPalette[0]
    private var existingColorsList: List<String> = emptyList()
    private val swatchViews = mutableListOf<View>()

    private var generatedColor: String? = null
    private var generatedSwatchView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddClassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupColorPicker()
        setupPreviewWatchers()
        setupClassMode()
        setupPlatformDropdown()

        viewModel.existingColors.observe(viewLifecycleOwner) { colors ->
            existingColorsList = colors
            selectColor(pickSmartDefault(colors))
        }
        viewModel.loadExistingColors()

        binding.btnSaveClass.setOnClickListener { saveClass() }
        binding.btnCancel.setOnClickListener { findNavController().navigateUp() }
    }

    // ── Class mode ────────────────────────────────────────────────────────────

    private fun setupClassMode() {
        binding.cgClassMode.setOnCheckedStateChangeListener { _, _ ->
            updateConditionalFields()
            updatePreview()
        }
        updateConditionalFields()
    }

    private fun updateConditionalFields() {
        val checkedId = binding.cgClassMode.checkedChipId
        val showRoom = checkedId == R.id.chip_in_person || checkedId == R.id.chip_hybrid
        val showOnline = checkedId == R.id.chip_online || checkedId == R.id.chip_hybrid
        binding.tilRoom.visibility = if (showRoom) View.VISIBLE else View.GONE
        binding.tilMeetingLink.visibility = if (showOnline) View.VISIBLE else View.GONE
        binding.tilPlatform.visibility = if (showOnline) View.VISIBLE else View.GONE
    }

    private fun selectedMode(): String = when (binding.cgClassMode.checkedChipId) {
        R.id.chip_online -> "Online"
        R.id.chip_hybrid -> "Hybrid"
        else -> "In Person"
    }

    private fun setupPlatformDropdown() {
        val platforms = listOf("Zoom", "Teams", "Google Meet", "Canvas", "Other")
        binding.actPlatform.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, platforms)
        )
    }

    // ── Color picker ──────────────────────────────────────────────────────────

    private fun setupColorPicker() {
        val density = resources.displayMetrics.density
        val sizePx = (40 * density).toInt()
        val gapPx = (6 * density).toInt()
        val perRow = 6

        var currentRow: LinearLayout? = null
        var rowCount = 0

        colorPalette.forEachIndexed { index, hex ->
            if (index % perRow == 0) {
                currentRow = LinearLayout(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { if (rowCount > 0) topMargin = gapPx }
                    orientation = LinearLayout.HORIZONTAL
                }
                binding.llColorSwatches.addView(currentRow)
                rowCount++
            }
            val v = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(sizePx, sizePx).apply {
                    if (index % perRow != 0) marginStart = gapPx
                }
                contentDescription = "Color $hex"
                setOnClickListener { onSwatchTapped(hex) }
            }
            swatchViews.add(v)
            currentRow?.addView(v)
        }

        binding.btnRandomColor.setOnClickListener {
            selectColor(pickRandomDistinct(existingColorsList))
        }

        updateSwatchStates()
    }

    private fun onSwatchTapped(hex: String) {
        selectedColor = hex
        if (hex in colorPalette) {
            removeGeneratedSwatch()
            generatedColor = null
        }
        updateSwatchStates()
        updatePreview()
        val tooSimilar = existingColorsList.any { colorDistance(hex, it) < COLOR_DISTANCE_THRESHOLD }
        binding.tvColorWarning.visibility = if (tooSimilar) View.VISIBLE else View.GONE
    }

    private fun selectColor(hex: String) {
        selectedColor = hex
        updateSwatchStates()
        updatePreview()
        binding.tvColorWarning.visibility = View.GONE
    }

    private fun updateSwatchStates() {
        colorPalette.forEachIndexed { idx, hex ->
            val v = swatchViews.getOrNull(idx) ?: return@forEachIndexed
            v.background = if (hex == selectedColor) selectedSwatchDrawable(Color.parseColor(hex))
                           else unselectedSwatchDrawable(Color.parseColor(hex))
        }
        updateGeneratedSwatchState()
    }

    private fun selectedSwatchDrawable(colorInt: Int): Drawable {
        val d = resources.displayMetrics.density
        val borderPx = (1 * d).toInt()
        val ringPx = (3 * d).toInt()
        // Three layers: subtle dark outer border → white ring → color circle
        // Dark border ensures ring is visible on both light and dark backgrounds
        val darkBorder = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.TRANSPARENT)
            setStroke(borderPx, 0x33000000.toInt())
        }
        val whiteRing = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.WHITE)
        }
        val inner = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(colorInt)
        }
        return LayerDrawable(arrayOf(darkBorder, whiteRing, inner)).also { ld ->
            ld.setLayerInset(1, borderPx, borderPx, borderPx, borderPx)
            ld.setLayerInset(
                2,
                borderPx + ringPx, borderPx + ringPx,
                borderPx + ringPx, borderPx + ringPx
            )
        }
    }

    private fun unselectedSwatchDrawable(colorInt: Int): Drawable =
        GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(colorInt)
        }

    // ── Generated swatch ──────────────────────────────────────────────────────

    private fun addOrUpdateGeneratedSwatch(hex: String) {
        val density = resources.displayMetrics.density
        val sizePx = (40 * density).toInt()

        if (generatedSwatchView == null) {
            generatedSwatchView = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(sizePx, sizePx)
                contentDescription = "Generated color"
            }
            binding.llGeneratedSwatchRow.addView(generatedSwatchView)
        }
        generatedSwatchView?.setOnClickListener { onSwatchTapped(hex) }
        updateGeneratedSwatchState()
        binding.llGeneratedSection.visibility = View.VISIBLE
    }

    private fun removeGeneratedSwatch() {
        generatedSwatchView?.let { binding.llGeneratedSwatchRow.removeView(it) }
        generatedSwatchView = null
        binding.llGeneratedSection.visibility = View.GONE
    }

    private fun updateGeneratedSwatchState() {
        val gv = generatedSwatchView ?: return
        val gc = generatedColor ?: return
        val colorInt = try { Color.parseColor(gc) } catch (_: Exception) { return }
        gv.background = if (gc == selectedColor) selectedSwatchDrawable(colorInt)
                        else generatedUnselectedDrawable(colorInt)
    }

    private fun generatedUnselectedDrawable(colorInt: Int): Drawable {
        val strokePx = (2 * resources.displayMetrics.density).toInt()
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(colorInt)
            setStroke(strokePx, 0x80FFFFFF.toInt())
        }
    }

    // ── Preview ───────────────────────────────────────────────────────────────

    private fun setupPreviewWatchers() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = updatePreview()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.etCourseName.addTextChangedListener(watcher)
        binding.etCourseCode.addTextChangedListener(watcher)
    }

    private fun updatePreview() {
        binding.tvPreviewName.text =
            binding.etCourseName.text.toString().ifEmpty { "Course Name" }
        binding.tvPreviewCode.text =
            binding.etCourseCode.text.toString().ifEmpty { "Code" }
        binding.tvPreviewMode.text = selectedMode()
        binding.cvColorPreview.setStrokeColor(
            ColorStateList.valueOf(Color.parseColor(selectedColor))
        )
    }

    // ── Color intelligence ────────────────────────────────────────────────────

    private fun pickSmartDefault(existing: List<String>): String =
        colorPalette.firstOrNull { candidate ->
            existing.none { colorDistance(candidate, it) < COLOR_DISTANCE_THRESHOLD }
        } ?: colorPalette.first()

    // Random always generates via HSV; snaps to a palette swatch only if very close
    private fun pickRandomDistinct(existing: List<String>): String {
        val hex = generateDistinctHsv(existing)
        val paletteMatch = colorPalette.firstOrNull { colorDistance(hex, it) < PALETTE_SNAP_THRESHOLD }
        return if (paletteMatch != null) {
            removeGeneratedSwatch()
            generatedColor = null
            paletteMatch
        } else {
            generatedColor = hex
            addOrUpdateGeneratedSwatch(hex)
            hex
        }
    }

    private fun generateDistinctHsv(existing: List<String>): String {
        var bestHex = colorPalette.random()
        var bestMinDist = -1f

        repeat(50) {
            val h = Random.nextFloat() * 360f
            val s = 0.55f + Random.nextFloat() * 0.30f   // 0.55–0.85 — vivid but not neon
            val v = 0.65f + Random.nextFloat() * 0.25f   // 0.65–0.90 — bright but not blinding
            val colorInt = Color.HSVToColor(floatArrayOf(h, s, v))
            val hex = "#%06X".format(colorInt and 0xFFFFFF)
            val minDist = if (existing.isEmpty()) Float.MAX_VALUE
                          else existing.minOf { colorDistance(hex, it) }
            if (minDist >= COLOR_DISTANCE_THRESHOLD) return hex
            if (minDist > bestMinDist) { bestMinDist = minDist; bestHex = hex }
        }
        return bestHex  // best-effort: most distinct candidate found
    }

    private fun colorDistance(hex1: String, hex2: String): Float = try {
        val c1 = Color.parseColor(hex1)
        val c2 = Color.parseColor(hex2)
        val dr = (Color.red(c1) - Color.red(c2)).toFloat()
        val dg = (Color.green(c1) - Color.green(c2)).toFloat()
        val db = (Color.blue(c1) - Color.blue(c2)).toFloat()
        sqrt(dr * dr + dg * dg + db * db)
    } catch (_: Exception) { Float.MAX_VALUE }

    // ── Save ──────────────────────────────────────────────────────────────────

    private fun saveClass() {
        val name = binding.etCourseName.text.toString().trim()
        val code = binding.etCourseCode.text.toString().trim()

        if (name.isEmpty()) {
            binding.etCourseName.error = "Course name is required"
            return
        }
        if (code.isEmpty()) {
            binding.etCourseCode.error = "Course code is required"
            return
        }

        val mode = selectedMode()
        val checkedId = binding.cgClassMode.checkedChipId
        val showOnline = checkedId == R.id.chip_online || checkedId == R.id.chip_hybrid

        viewModel.saveCourse(
            name, code,
            binding.etInstructor.text.toString().trim(),
            binding.etSchedule.text.toString().trim(),
            binding.etRoom.text.toString().trim(),
            selectedColor,
            mode,
            if (showOnline) binding.etMeetingLink.text.toString().trim() else "",
            if (showOnline) binding.actPlatform.text.toString().trim() else ""
        )
        Toast.makeText(requireContext(), "$name added!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val COLOR_DISTANCE_THRESHOLD = 90f  // reject/warn if RGB distance < this
        private const val PALETTE_SNAP_THRESHOLD = 30f    // snap generated color to palette if within this
    }
}
