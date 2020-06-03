package cn.faker.repaymodel.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RoundGroup extends android.support.v7.widget.AppCompatTextView {
    public RoundGroup(Context context) {
        super(context);
    }

    public RoundGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
         int mWidth;
         int mHeight;
        if (widthSpecMode == MeasureSpec.EXACTLY || heightSpecMode == MeasureSpec.EXACTLY) {
            mWidth = widthSpecSize;//这里的值为实际的值的3倍
            mHeight = heightSpecSize;
        } else {
            float defaultSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getContext().getResources().getDisplayMetrics());
            mHeight = (int) defaultSize;
            mWidth = (int) defaultSize;
        }
        if (mWidth < mHeight) {
            mHeight = mWidth;
        } else {
            mWidth = mHeight;
        }


        setMeasuredDimension(mWidth, mHeight);
    }
}
