package diordna.support.nestedscrollviewclone;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

        LimitLine ll1 = new LimitLine(1500f, "Max");
        ll1.setLineWidth(2f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(500f, "Min");
        ll2.setLineWidth(2f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll2.setTextSize(10f);

        LimitLine ll3 = new LimitLine(2000f, "Over");
        ll3.setLineWidth(2f);
        ll3.enableDashedLine(10f, 10f, 0f);
        ll3.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll3.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.addLimitLine(ll3);
        leftAxis.setAxisMaximum(2100f);
        leftAxis.setAxisMinimum(0f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setAxisMaximum(5000f);
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
        }

        LineDataSet vcset = (LineDataSet) data.getDataSetByIndex(2);
        if (vcset == null && timeVc >= 0) {
            vcset = new LineDataSet(null, "VC");
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
        if (timeVc >= 0) {
            data.addEntry(new Entry(timeVc, vc), 2);
        }

        data.notifyDataChanged();
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    private void reset() {
        LineData data = mChart.getData();
        data.clearValues();
        mChart.invalidate();
    }

    private void startAnimation(final OverScroller overScroller, float startL, float startV, final boolean vc) {
        final long startTime = AnimationUtils.currentAnimationTimeMillis();
        addEntry(0, startL, startV, -1, -1);

        Runnable runnable = new Runnable() {
            double lastTime = startTime;
            double lastL = 0;
            @Override
            public void run() {
                if (overScroller.computeScrollOffset()) {
                    long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
                    double time = currentAnimationTimeMillis - startTime;
                    double dt = currentAnimationTimeMillis - lastTime;
                    double currL = overScroller.getCurrY();
                    double dl = currL - lastL;
                    double timeVC = dt / 2 + lastTime - startTime;
                    if (!vc) {
                        timeVC = -1;
                    }

                    lastTime = currentAnimationTimeMillis;
                    lastL = currL;

                    addEntry((float) time, overScroller.getCurrY(), overScroller.getCurrVelocity(), (float) timeVC, (float) (dl / dt * 1000));
                    mChart.postOnAnimation(this);
                }
            }
        };

        mChart.postOnAnimation(runnable);
    }

    // in => in
    public void test1() {
        reset();
        final OverScroller overScroller = new OverScroller(this);
        int startL = 600;
        int startV = 3000;
        int minL = 500;
        int maxL = 1500;
        int overL = 500;
        overScroller.fling(0, startL, 0, startV, 0, 0, minL, maxL, 0, overL);

        startAnimation(overScroller, startL, startV, false);
    }

    // in => over
    public void test2() {
        reset();
        final OverScroller overScroller = new OverScroller(this);
        int startL = 1000;
        int startV = 4000;
        int minL = 500;
        int maxL = 1500;
        int overL = 500;
        overScroller.fling(0, startL, 0, startV, 0, 0, minL, maxL, 0, overL);

        startAnimation(overScroller, startL, startV, false);
    }

    // over => same over
    public void test3() {
        reset();
        final OverScroller overScroller = new OverScroller(this);
        int startL = 0;
        int startV = 10;
        int minL = 500;
        int maxL = 1500;
        int overL = 500;
        overScroller.fling(0, startL, 0, startV, 0, 0, minL, maxL, 0, overL);

        startAnimation(overScroller, startL, startV, false);
    }

    // over => in
    public void test4() {
        reset();
        final OverScroller overScroller = new OverScroller(this);
        int startL = 0;
        int startV = 4000;
        int minL = 500;
        int maxL = 1500;
        int overL = 500;
        overScroller.fling(0, startL, 0, startV, 0, 0, minL, maxL, 0, overL);

        startAnimation(overScroller, startL, startV, false);
    }

    // over => other over
    public void test5() {
        reset();
        final OverScroller overScroller = new OverScroller(this);
        int startL = 100;
        int startV = 6000;
        int minL = 500;
        int maxL = 1500;
        int overL = 500;
        overScroller.fling(0, startL, 0, startV, 0, 0, minL, maxL, 0, overL);

        startAnimation(overScroller, startL, startV, false);
    }

    public void test6() {
    }

    public void test7() {
        reset();
        final OverScroller overScroller = new OverScroller(this);

        int startL = 500;
        float startV;
        int minL = 0;
        int maxL = 1000;
        int overL = 500;
        overScroller.springBack(0, startL, 0, 0, minL, maxL);
        startV = overScroller.getCurrVelocity();

        startAnimation(overScroller, startL, startV, false);
    }

    public void test8() {
        reset();
        final OverScroller overScroller = new OverScroller(this);

        int startL = 0;
        int dl = 1000;
        int minL = 0;
        int maxL = 1000;
        int overL = 500;
        int duration = 3000;
        overScroller.startScroll(0, startL, 0, dl, duration);
        float startV = overScroller.getCurrVelocity();

        startAnimation(overScroller, startL, startV, true);
    }

    public void test9() {
        reset();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.test1:
            test1();
            return true;
        case R.id.test2:
            test2();
            return true;
        case R.id.test3:
            test3();
            return true;
        case R.id.test4:
            test4();
            return true;
        case R.id.test5:
            test5();
            return true;
        case R.id.test6:
            test6();
            return true;
        case R.id.test7:
            test7();
            return true;
        case R.id.test8:
            test8();
            return true;
        case R.id.test9:
            test9();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
