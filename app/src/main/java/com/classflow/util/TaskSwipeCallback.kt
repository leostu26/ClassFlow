package com.classflow.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class TaskSwipeCallback(
    private val onSwipeLeft: (position: Int) -> Unit,
    private val onSwipeRight: (position: Int) -> Unit,
    private val isSwipeableAt: ((position: Int) -> Boolean)? = null
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        rv: RecyclerView,
        vh: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun getSwipeDirs(rv: RecyclerView, vh: RecyclerView.ViewHolder): Int {
        val pos = vh.adapterPosition
        if (pos == RecyclerView.NO_POSITION) return 0
        if (isSwipeableAt?.invoke(pos) == false) return 0
        return super.getSwipeDirs(rv, vh)
    }

    override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
        val pos = vh.adapterPosition
        if (pos == RecyclerView.NO_POSITION) return
        if (direction == ItemTouchHelper.LEFT) {
            onSwipeLeft(pos)
        } else {
            onSwipeRight(pos)
            (vh.itemView.parent as? RecyclerView)?.adapter?.notifyItemChanged(pos)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        rv: RecyclerView,
        vh: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
            super.onChildDraw(c, rv, vh, dX, dY, actionState, isCurrentlyActive)
            return
        }

        val iv = vh.itemView
        val density = iv.context.resources.displayMetrics.density
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 14f * density
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        val textY = iv.top + iv.height / 2f + textPaint.textSize / 2f - 2f * density

        if (dX < 0) {
            bgPaint.color = Color.parseColor("#10B981")
            c.drawRect(
                iv.right + dX, iv.top.toFloat(),
                iv.right.toFloat(), iv.bottom.toFloat(), bgPaint
            )
            val revealed = -dX
            if (revealed > 64f * density) {
                c.drawText("✓  Complete", iv.right - revealed / 2f, textY, textPaint)
            }
        } else if (dX > 0) {
            bgPaint.color = Color.parseColor("#3D5AFE")
            c.drawRect(
                iv.left.toFloat(), iv.top.toFloat(),
                iv.left + dX, iv.bottom.toFloat(), bgPaint
            )
            val revealed = dX
            if (revealed > 64f * density) {
                c.drawText("Details  →", iv.left + revealed / 2f, textY, textPaint)
            }
        }

        super.onChildDraw(c, rv, vh, dX, dY, actionState, isCurrentlyActive)
    }
}
