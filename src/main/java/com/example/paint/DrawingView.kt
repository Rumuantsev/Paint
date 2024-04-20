package com.example.paint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // Переменная для траектории движения маркера
    private lateinit var mDrawPath: CustomPath

    // Переменные для работы с рисованием
    private var mCanvasBitmap: Bitmap? = null
    private lateinit var mDrawPaint: Paint
    private lateinit var mCanvasPaint: Paint
    private var mBrushSize: Float = 0f
    private var color: Int = Color.BLACK
    private lateinit var canvas: Canvas

    private var touchX: Float = 0f // Координата X касания
    private var touchY: Float = 0f // Координата Y касания

    init {
        setUpDrawing()
    }

    // Функция настройки рисования
    private fun setUpDrawing() {
        // Инициализация характеристик рисунка
        mDrawPaint = Paint()
        mDrawPaint.color = color
        mDrawPaint.style = Paint.Style.STROKE
        mDrawPaint.strokeJoin = Paint.Join.ROUND
        mDrawPaint.strokeCap = Paint.Cap.ROUND
        mDrawPaint.isAntiAlias = true
        mBrushSize = 20f // Устанавливаем размер маркера

        // Инициализация характеристик области рисования
        mCanvasPaint = Paint(Paint.DITHER_FLAG)

        // Инициализация траектории движения маркера

        mDrawPath.color = color
        mDrawPath.brushThickness = mBrushSize
    }

    // Внутренний класс CustomPath
    internal inner class CustomPath(color: Int, brushThickness: Float) : Path() {
        var color: Int = Color.BLACK
        var brushThickness: Float = 5f
    }

    // Наследуем функцию onSizeChanged от класса View
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        // Инициализация mCanvasBitmap с конфигурацией ARGB_8888
        mCanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        // Инициализация canvas с mCanvasBitmap
        canvas = Canvas(mCanvasBitmap!!)
    }

    // Наследуем функцию onDraw от класса View
    override fun onDraw(canvas: Canvas) {


        // Проверяем, если mDrawPath пустой
        if (mDrawPath.isEmpty) {
            // Устанавливаем толщину маркера и цвет для mDrawPaint
            mDrawPaint.strokeWidth = mDrawPath.brushThickness
            mDrawPaint.color = mDrawPath.color
        }

        // Инициализируем отрисовщик canvas с использованием параметров mDrawPath и mDrawPaint
        canvas?.drawPath(mDrawPath, mDrawPaint)
    }

    // Наследуем функцию onTouchEvent от класса View
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Получаем координаты x и y при касании экрана
        val x = event?.x ?: 0f
        val y = event?.y ?: 0f

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                // Определяем цвет и толщину маркера, затем сбрасываем маркер
                mDrawPath.color = color
                mDrawPath.brushThickness = mBrushSize
                mDrawPath.reset()
                // Смещаем начало маркера на координаты x и y
                mDrawPath.moveTo(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                // Переводим линию рисунка на координаты x и y
                mDrawPath.lineTo(x, y)
            }
            MotionEvent.ACTION_UP -> {
                // В момент поднятия пальца с экрана, устанавливаем параметры цвета и толщины маркера по умолчанию
                mDrawPath.color = color
                mDrawPath.brushThickness = mBrushSize
            }
            else -> return false
        }

        // Перерисовываем вид
        invalidate()
        return true
    }
}

