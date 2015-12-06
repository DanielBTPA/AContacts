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
    private boolean isAnimEnable = false;
    private Bitmap iconLabel;
    private int colorLabelId;
    private int duration;

    private Paint paint;

    private View.OnClickListener onClick;
    private int width, height;
    private Rect rect;

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
                    iconLabel = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(type, 0));
                    break;
                case R.styleable.ChoosePictureButton_actionColor:
                    this.colorLabelId = typedArray.getColor(type, Color.BLACK);
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

        paint = new Paint();
        paint.setColor(colorLabelId);
        paint.setAntiAlias(false);

        rect = new Rect(0, 0, width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width = w;
        this.height = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /* Desenho de um retangulo com um icone e mensagem do botão onde quando selecionado o botão,
       dispara uma thread que trata dos efeitos de animação do retangulo.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setAlpha(alpha);
        rect.set(0, 0, width, height);
        canvas.drawRect(rect, paint);

        // Centraliza o icone no centro.
        if (iconLabel != null) {
            int posIconX = (width - iconLabel.getWidth()) >> 1;
            int posIconY = (height - iconLabel.getHeight()) >> 1;
            canvas.drawBitmap(iconLabel, posIconX, posIconY, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /*
       Se caso o botão for selecionado na tela, é acionado um TouchListener que é tratado, obtendo se
       'isAnimEnable' for true e ao mesmo tempo se a ação for igual a ação de seleção, dispara o metodo 'onClick'
       para o dev tratar esse listener pelo 'setOnCLickListener.
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (isAnimEnable && event.getAction() == MotionEvent.ACTION_UP) {
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

                postInvalidate();
                SystemClock.sleep(0001);

                if (alpha >= VALUE_DEFAULT_ALPHA || alpha <= 0) {
                    break;
                }
            }
        }
    }

    private void animateImageView() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                isAnimEnable = true;

                startAnim(+1);

                SystemClock.sleep(duration == 0 ? 2000 : 3500);

                startAnim(-1);

                isAnimEnable = false;
            }
        }.start();

    }

    @Override
    public void onClick(View v) {
        if (!isAnimEnable) {
            animateImageView();
        }
    }

    // Metodos para uso publico
    @Override
    public void setOnClickListener(OnClickListener listener) {
        this.onClick = listener;
    }

    public void setActionIcon(int iconId) {
        iconLabel = BitmapFactory.decodeResource(getResources(), iconId);
    }

    public void setActionColor(int colorId) {
        this.colorLabelId = colorId;
    }

    public void setActionDuration(int sec) {
        this.duration = sec;
    }

}
