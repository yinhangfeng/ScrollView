package diordna.support.nestedscrollviewclone;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import diordna.widget.OverScroller;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class ScrollerChartActivity extends AppCompatActivity {
    private static final String TAG = "ScrollerChartActivity";

    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroller_chart);

        mChart = (LineChart) findViewById(R.id.line_chart);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line

        LimitLine ll1 = new LimitLine(1000f, "Edge");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(500f, "Edge");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll2.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(1600f);
        leftAxis.setAxisMinimum(0f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setAxisMaximum(5000);
        rightAxis.setAxisMinimum(0);

        initLineData();
    }

    private void initLineData() {
        LineData data = new LineData();
        // add empty data
        mChart.setData(data);
    }

    private void addEntry(float time, float l, float v, float timeVc, float vc) {
        Log.d(TAG, "addEntry() called with " + "time = " + time + ", l = " + l + ", v = " + v + ", timeVc = " + timeVc + ", vc = " + vc + "");

        LineData data = mChart.getData();

        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            LineDataSet lset = new LineDataSet(null, "L");
            lset.setAxisDependency(YAxis.AxisDependency.LEFT);
            lset.setColor(Color.BLUE);
            lset.setCircleColor(Color.BLUE);
            lset.setLineWidth(2f);
            lset.setCircleRadius(3f);
            lset.setHighLightColor(Color.rgb(244, 117, 117));
//            lset.setValueTextColor(Color.WHITE);
            lset.setValueTextSize(9f);
            lset.setDrawValues(false);
            data.addDataSet(lset);

            LineDataSet vset = new LineDataSet(null, "V");
            vset.setAxisDependency(YAxis.AxisDependency.RIGHT);
            vset.setColor(Color.GREEN);
            vset.setCircleColor(Color.GREEN);
            vset.setLineWidth(2f);
            vset.setCircleRadius(3f);
            vset.setHighLightColor(Color.rgb(244, 117, 117));
//            vset.setValueTextColor(Color.WHITE);
            vset.setValueTextSize(9f);
            vset.setDrawValues(false);
            data.addDataSet(vset);

            LineDataSet vcset = new LineDataSet(null, "VC");
            vcset.setAxisDependency(YAxis.AxisDependency.RIGHT);
            vcset.setColor(Color.YELLOW);
            vcset.setCircleColor(Color.YELLOW);
            vcset.setLineWidth(2f);
            vcset.setCircleRadius(3f);
            vcset.setHighLightColor(Color.rgb(244, 117, 117));
            //            vset.setValueTextColor(Color.WHITE);
            vcset.setValueTextSize(9f);
            vcset.setDrawValues(false);
            data.addDataSet(vcset);
        }

        data.addEntry(new Entry(time, l), 0);
        data.addEntry(new Entry(time, v), 1);
        data.addEntry(new Entry(timeVc, vc), 2);

        data.notifyDataChanged();
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    private void reset() {
        LineData data = mChart.getData();
        data.clearValues();
        mChart.invalidate();
    }

    public void test1(View v) {
        reset();
        final OverScroller overScroller = new OverScroller(this);
        int vinit = 4000;
        overScroller.fling(0, 0, 0, vinit, 0, 0, 0, 1000, 0, 500);

        final long startTime = AnimationUtils.currentAnimationTimeMillis();
        addEntry(0, 0, vinit, 0, vinit);

        Runnable runnable = new Runnable() {
            double lastTime = startTime;
            double lastL = 0;
            @Override
            public void run() {
                if (overScroller.computeScrollOffset()) {
                    long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
                    double time = currentAnimationTimeMillis - startTime;
                    double dt = currentAnimationTimeMillis - lastTime;
                    double dl = overScroller.getCurrY() - lastL;
                    double timeVC = (dt) / 2 + lastTime - startTime;
                    Log.i(TAG, "run: " + currentAnimationTimeMillis + " " + time + " " + dt + " " + dl + " " + startTime);
                    lastTime = currentAnimationTimeMillis;
                    lastL = overScroller.getCurrY();
                    addEntry((float) time, overScroller.getCurrY(), overScroller.getCurrVelocity(), (float) timeVC, (float) (dl / dt * 1000));
                    mChart.postOnAnimation(this);
                }
            }
        };

        mChart.postOnAnimation(runnable);
    }

    public void test2(View v) {
        reset();
        final OverScroller overScroller = new OverScroller(this);
        int vinit = 4000;
        overScroller.startScroll(0, 0, 0, 1000, 2000);

        final long startTime = AnimationUtils.currentAnimationTimeMillis();
        addEntry(0, 0, vinit, 0, vinit);

        Runnable runnable = new Runnable() {
            double lastTime = startTime;
            double lastL = 0;
            @Override
            public void run() {
                if (overScroller.computeScrollOffset()) {
                    long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
                    double time = currentAnimationTimeMillis - startTime;
                    double dt = currentAnimationTimeMillis - lastTime;
                    double dl = overScroller.getCurrY() - lastL;
                    double timeVC = (dt) / 2 + lastTime - startTime;
                    Log.i(TAG, "run: " + currentAnimationTimeMillis + " " + time + " " + dt + " " + dl + " " + startTime);
                    lastTime = currentAnimationTimeMillis;
                    lastL = overScroller.getCurrY();
                    addEntry((float) time, overScroller.getCurrY(), overScroller.getCurrVelocity(), (float) timeVC, (float) (dl / dt * 1000));
                    mChart.postOnAnimation(this);
                }
            }
        };

        mChart.postOnAnimation(runnable);
    }

    public void test3(View v) {
        reset();
        final OverScroller overScroller = new OverScroller(this);
        int vinit = 5000;
        int startY = 500;
        overScroller.fling(0, startY, 0, vinit, 0, 0, 0, 1000, 0, 500);

        final long startTime = AnimationUtils.currentAnimationTimeMillis();
        addEntry(0, startY, vinit, 0, vinit);

        Runnable runnable = new Runnable() {
            double lastTime = startTime;
            double lastL = 0;
            @Override
            public void run() {
                if (overScroller.computeScrollOffset()) {
                    long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
                    double time = currentAnimationTimeMillis - startTime;
                    double dt = currentAnimationTimeMillis - lastTime;
                    double dl = overScroller.getCurrY() - lastL;
                    double timeVC = (dt) / 2 + lastTime - startTime;
                    Log.i(TAG, "run: " + currentAnimationTimeMillis + " " + time + " " + dt + " " + dl + " " + startTime);
                    lastTime = currentAnimationTimeMillis;
                    lastL = overScroller.getCurrY();
                    addEntry((float) time, overScroller.getCurrY(), overScroller.getCurrVelocity(), (float) timeVC, (float) (dl / dt * 1000));
                    mChart.postOnAnimation(this);
                }
            }
        };

        mChart.postOnAnimation(runnable);
    }

    public void test4(View v) {
        reset();
        final OverScroller overScroller = new OverScroller(this);
        int vinit = 5000;
        overScroller.springBack(0, 0, 0, 0, 500, 1000);

        final long startTime = AnimationUtils.currentAnimationTimeMillis();
        addEntry(0, 0, vinit, 0, vinit);

        Runnable runnable = new Runnable() {
            double lastTime = startTime;
            double lastL = 0;
            @Override
            public void run() {
                if (overScroller.computeScrollOffset()) {
                    long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
                    double time = currentAnimationTimeMillis - startTime;
                    double dt = currentAnimationTimeMillis - lastTime;
                    double dl = overScroller.getCurrY() - lastL;
                    double timeVC = (dt) / 2 + lastTime - startTime;
                    Log.i(TAG, "run: " + currentAnimationTimeMillis + " " + time + " " + dt + " " + dl + " " + startTime);
                    lastTime = currentAnimationTimeMillis;
                    lastL = overScroller.getCurrY();
                    addEntry((float) time, overScroller.getCurrY(), overScroller.getCurrVelocity(), (float) timeVC, (float) (dl / dt * 1000));
                    mChart.postOnAnimation(this);
                }
            }
        };

        mChart.postOnAnimation(runnable);
    }

    public void test5(View v) {
        reset();
    }
}
