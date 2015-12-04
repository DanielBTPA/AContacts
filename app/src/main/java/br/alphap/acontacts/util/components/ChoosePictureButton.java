package br.alphap.acontacts.util.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import br.alphap.acontacts.R;

/**
 * Created by danielbt on 03/12/15.
 */
public class ChoosePictureButton extends ImageView implements View.OnClickListener, View.OnTouchListener {

    private static final int VALUE_DEFAULT_ALPHA = 180;

    private int alpha = 0;
    private boolean isClicked = false;
    private int iconLabelId;
    private int colorLabelId;
    private int duration;

    private View.OnClickListener onClick;

    public ChoosePictureButton(Context context) {
        super(context);
        init();
    }

    public ChoosePictureButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChoosePictureButton);

        for (int pos = 0; pos < typedArray.getIndexCount(); pos++) {
            int type = typedArray.getIndex(pos);
            switch (type) {
                case R.styleable.ChoosePictureButton_actionIcon:
                    this.iconLabelId = typedArray.getResourceId(type, 0);
                    break;
                case R.styleable.ChoosePictureButton_actionColor:
                    this.colorLabelId = typedArray.getColor(type, 0);
                    break;
                case R.styleable.ChoosePictureButton_actionDuration:
                    this.duration = typedArray.getInteger(type, 0);
                    break;
            }
        }

        init();
    }

    // Inicia as operações dessa classe.
    private void init() {
        super.setClickable(true);
        super.setOnClickListener(this);
        this.setOnTouchListener(this);
    }

    /* Desenho de um retangulo com um icone e mensagem do botão onde quando selecionado o botão,
       dispara uma thread que trata dos efeitos de animação do retangulo.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(colorLabelId == 0 ? Color.BLACK : colorLabelId);
        paint.setAlpha(alpha);
        paint.setAntiAlias(true);


        Rect rect = new Rect(getWidth(), getHeight(), 0, (getHeight() - 100));
        canvas.drawRect(rect, paint);

        if (iconLabelId != 0) {
            Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), iconLabelId);
            canvas.drawBitmap(icon, (getWidth() / 2) - (icon.getWidth() / 2), (rect.bottom + icon.getHeight() / 2), paint);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getY() > (getHeight() - 100) && isClicked &&
                event.getAction() == MotionEvent.ACTION_UP) {
            if (onClick != null) {
                onClick.onClick(this);
            }
        }
        return false;

    }

    private void startAnim(int value) {
        synchronized (this) {
            while (true) {
                alpha += value;

                post(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });

                if (alpha >= VALUE_DEFAULT_ALPHA || alpha <= 0) {
                    break;
                }

                SystemClock.sleep(0001);
            }
        }
    }

    private void animateImageView() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                isClicked = true;

                startAnim(+1);

                SystemClock.sleep(duration == 0 ? 1000 : 2000);

                startAnim(-1);

                isClicked = false;
            }
        }.start();

    }

    @Override
    public void onClick(View v) {
        if (!isClicked) {
            animateImageView();
        }
    }

    // Metodos para uso publico
    @Override
    public void setOnClickListener(OnClickListener listener) {
        this.onClick = listener;
    }

    public void setActionIcon(int iconId) {
        this.iconLabelId = iconId;
    }

    public void setActionColor(int colorId) {
        this.colorLabelId = colorId;
    }

    public void setActionDuration(int sec) {
        this.duration = sec;
    }

}
