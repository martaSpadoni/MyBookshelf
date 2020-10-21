package it.unibo.sistemiMobile.mybookshelf;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.resources.TextAppearance;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;
import it.unibo.sistemiMobile.mybookshelf.StatisticClasses.StatisticsProducer;
import it.unibo.sistemiMobile.mybookshelf.Utilities.Utility;
import it.unibo.sistemiMobile.mybookshelf.ViewModel.StatisticViewModel;

public class StatisticsFragment extends Fragment implements DialogInterface.OnDismissListener {
    private View view;
    private FragmentActivity activity;
    private StatisticViewModel model;
    private StatisticsProducer producer;
    private int bookReadMonthly;
    private int bookReadAnnual;
    private int monthlyChallenge;
    private int annualChallenge;
    private Map<Integer, Integer> mapBookRead;
    private LinearLayout monthLayout;
    private LinearLayout annualLayout;
    private ImageButton annualMenu;
    private ImageButton monthlyMenu;
    private Button addMonthlyBt;
    private Button addAnnualBt;
    private TextView winMonthText;
    private TextView winAnnualText;
    private PieChart monthlyChart;
    private PieChart annualChart;
    private HorizontalBarChart yearChart;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.statistics_fragment, container, false);
        Utility.setUpBottomNavigation(getActivity());
        Utility.setUpToolbar((AppCompatActivity) getActivity(), getString(R.string.statistics_title));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        if(activity != null){
            producer = new StatisticsProducer();
            checkChallenge();
            initializeView();

            model = new ViewModelProvider(activity).get(StatisticViewModel.class);
            model.getBookRead().observe(activity, new Observer<List<BookRead>>() {
                @Override
                public void onChanged(List<BookRead> bookReads) {
                    producer.updateBooksList(bookReads);
                    bookReadMonthly = producer.getAmountOfBooksReadThisMonth();
                    bookReadAnnual = producer.getAmountOfBookReadThisYear();
                    mapBookRead = producer.getAmountOfBookReadPerMonth();
                    setChartLayout(true, monthlyChart, monthlyChallenge, bookReadMonthly, addMonthlyBt, monthLayout, monthlyMenu, winMonthText);
                    setChartLayout(false, annualChart, annualChallenge, bookReadAnnual, addAnnualBt, annualLayout, annualMenu, winAnnualText);
                    setYearChart();
                    setAverageLayout();
                }
            });


        }

    }

    private void setAverageLayout(){
        ((TextView)view.findViewById(R.id.averagePerMonth)).setText(String.valueOf(producer.monthAverage()));
        ((TextView)view.findViewById(R.id.averagePerYear)).setText(String.valueOf(producer.yearAverage()));
    }

    private void setYearChart() {
        yearChart.setBackgroundColor(Color.WHITE);
        yearChart.setDrawGridBackground(false);
        yearChart.getDescription().setEnabled(false);
        yearChart.animateY(2500);
        yearChart.getLegend().setEnabled(false);
        yearChart.getAxisRight().setEnabled(false);
        yearChart.getAxisLeft().setEnabled(false);
        yearChart.getAxisLeft().setAxisMinimum(0f);
        yearChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        yearChart.getXAxis().setLabelCount(12);

        yearChart.setFitBars(true);
        final List<String> xAxisValues = new ArrayList<>(Arrays.asList("Jan", "Feb", "March", "April", "May", "June",
                "July", "Aug", "Sep", "Oct", "Nov", "Dec"));
        yearChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisValues.get((int) (value / 10));
            }
        });

        float barWidth = 8f;
        float spaceForBar = 10f;
        List<BarEntry> values = new ArrayList<>();
        for(int i = 0; i < 12; i++){
            values.add(new BarEntry(i*spaceForBar, mapBookRead.get(i)));
        }
        BarDataSet set1;

        if (yearChart.getData() != null &&
                yearChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) yearChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            yearChart.getData().notifyDataChanged();
            yearChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new BarDataSet(values, "DataSet 1");
            set1.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return value > 0 ? String.valueOf((int) value): "";
                }
            });
            set1.setDrawIcons(false);
            set1.setColor(Color.parseColor("#7f0000"));
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets
            // create a data object with the data sets
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(barWidth);

            // set data
            yearChart.setData(data);
            yearChart.getData().setHighlightEnabled(false);
            yearChart.invalidate();

        }

    }

    private void initializeView() {
        monthLayout = view.findViewById(R.id.monthLayout);
        annualLayout = view.findViewById(R.id.annualLayout);

        winAnnualText = view.findViewById(R.id.wonTextView2);
        winMonthText = view.findViewById(R.id.wonTextView);

        monthlyChart = view.findViewById(R.id.monthPieChart);
        annualChart = view.findViewById(R.id.annualPieChart);
        yearChart = view.findViewById(R.id.yearChart);

        addMonthlyBt = view.findViewById(R.id.addMonthChallengebutton);
        addAnnualBt = view.findViewById(R.id.addAnnualChallengebutton);

        monthlyMenu = view.findViewById(R.id.monthlyChallengeMenu);
        monthlyMenu.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupMenu menu = new PopupMenu(activity, v);
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.edit_challenge:
                            showDialogToInsertMonthlyChallenge(getString(R.string.edit_monthly_challenge));
                            return true;
                        case R.id.delete_challenge:
                            SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(getString(R.string.monthly_challenge), 0);
                            editor.apply();
                            monthlyChallenge = 0;
                            setChartLayout(true, monthlyChart, monthlyChallenge, bookReadMonthly, addMonthlyBt, monthLayout, monthlyMenu, winMonthText);
                            return true;
                        default:
                            return false;

                    }
                }
            });
                menu.getMenuInflater().inflate(R.menu.challenge_menu, menu.getMenu());
                menu.show();
            }
        });
        annualMenu = view.findViewById(R.id.annualChallengeMenu);
        annualMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(activity, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit_challenge:
                                showDialogToInsertAnnualChallenge(getString(R.string.edit_annual_challenge));
                                return true;
                            case R.id.delete_challenge:
                                SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("annualChallenge", 0);
                                editor.apply();
                                annualChallenge = 0;
                                setChartLayout(false, annualChart, annualChallenge, bookReadAnnual, addAnnualBt, annualLayout, annualMenu, winAnnualText);
                                return true;
                            default:
                                return false;

                        }
                    }
                });
                menu.getMenuInflater().inflate(R.menu.challenge_menu, menu.getMenu());
                menu.show();
            }
        });
    }


    private void setChartLayout(final boolean isMonthly, PieChart chart, int challenge, int bookRead, Button button, LinearLayout layout, ImageButton menu, TextView winTextView) {
        if(challenge == 0){
            chart.setVisibility(View.GONE);
            menu.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
            winTextView.setVisibility(View.GONE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isMonthly) {
                        showDialogToInsertMonthlyChallenge(getString(R.string.add_new_monthly_challenge));
                    }else{
                        showDialogToInsertAnnualChallenge(getString(R.string.add_new_annual_challenge));
                    }
                }
            });
        }else{
            if(button!= null){
                button.setVisibility(View.GONE);
            }
            if(challenge-bookRead > 0) {
                if(chart != null) {
                    menu.setVisibility(View.VISIBLE);
                    winTextView.setVisibility(View.GONE);
                    setPieChart(chart, challenge, bookRead);
                }
            }else{
                if(chart != null){
                    chart.setVisibility(View.GONE);
                }
                winTextView.setVisibility(View.VISIBLE);
            }

        }
    }

    private void setPieChart(PieChart chart, int challenge, int bookRead){
        chart.setVisibility(View.VISIBLE);
        chart.setCenterText(generateCenterSpannableText(bookRead, challenge - bookRead, challenge));
        chart.setDrawCenterText(true);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setHoleRadius(70f);
        chart.animateY(1400, Easing.EaseInOutQuad);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(bookRead, "BOOK READ"));
        entries.add(new PieEntry(challenge - bookRead, ""));
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.parseColor("#7f0000"), Color.LTGRAY);
        dataSet.setValueTextSize(15f);
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        chart.setData(data);
        chart.setDrawEntryLabels(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.invalidate();
    }
    private void showDialogToInsertMonthlyChallenge(String title){
        TextInputLayout inputLayout = new TextInputLayout(activity);
        final TextInputEditText editText = new TextInputEditText(activity);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        inputLayout.addView(editText);
        new AlertDialog.Builder(activity).setTitle(title)
                .setMessage(R.string.how_many_b_month)
                .setView(inputLayout)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(Utility.inputNotEmpty(editText.getText())) {
                            SharedPreferences sharedPref =
                                    getActivity().getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putInt(getString(R.string.monthly_challenge),
                                    Integer.parseInt(editText.getText().toString()));
                            editor.putInt("monthChallenge", Calendar.getInstance().get(Calendar.MONTH));
                            editor.apply();
                        }
                    }
                }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setOnDismissListener(this).show();
    }

    private void showDialogToInsertAnnualChallenge(String title){
        TextInputLayout inputLayout = new TextInputLayout(activity);
        final TextInputEditText editText = new TextInputEditText(activity);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        inputLayout.addView(editText);
        new AlertDialog.Builder(activity).setTitle(title)
                .setMessage(getString(R.string.how_many_b_year))
                .setView(inputLayout)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(Utility.inputNotEmpty(editText.getText())) {
                            SharedPreferences sharedPref =
                                    getActivity().getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putInt("annualChallenge",
                                    Integer.parseInt(editText.getText().toString()));
                            editor.putInt("yearChallenge", Calendar.getInstance().get(Calendar.YEAR));
                            editor.apply();
                        }
                    }
                }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setOnDismissListener(this).show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        monthlyChallenge = sharedPreferences.getInt(getString(R.string.monthly_challenge), 0);
        annualChallenge = sharedPreferences.getInt("annualChallenge", 0);
        setChartLayout(true, monthlyChart, monthlyChallenge, bookReadMonthly, addMonthlyBt, monthLayout, monthlyMenu, winMonthText);
        setChartLayout(false, annualChart, annualChallenge, bookReadAnnual, addAnnualBt, annualLayout, annualMenu, winAnnualText);
    }

    private SpannableString generateCenterSpannableText(int read, int amount, int challenge) {
        try {
            SpannableString s = new SpannableString(read + " / "+ challenge +" \n"+getString(R.string.leggi)+ amount +getString(R.string.more_to_win));
            s.setSpan(new RelativeSizeSpan(3f), 0, 2, 0);
            s.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, 0);
            s.setSpan(new RelativeSizeSpan(1.5f), 3, 7, 0);
            s.setSpan(new StyleSpan(Typeface.NORMAL), 3, 6, 0);
            s.setSpan(new RelativeSizeSpan(1.4f), 7, 19, 0);
            s.setSpan(new StyleSpan(Typeface.NORMAL), 7, 19, 0);
            s.setSpan(new RelativeSizeSpan(1.4f), 19, s.length() , 0);
            s.setSpan(new StyleSpan(Typeface.NORMAL), s.length() - 14, s.length(), 0);
            s.setSpan(new ForegroundColorSpan(Color.DKGRAY), 8, s.length(), 0);
            return s;
        }catch (IllegalStateException e){
            return new SpannableString("");
        }
    }

    private void checkChallenge(){
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        if(producer.isMonthlyChallengeStillActive(sharedPreferences.getInt("monthChallenge",-1))){
            monthlyChallenge = sharedPreferences.getInt(getString(R.string.monthly_challenge), 0);
        }else{
            SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.monthly_challenge), 0);
    }

        if(producer.isYearChallengeStillActive(sharedPreferences.getInt("yearChallenge",-1))){
            annualChallenge = sharedPreferences.getInt("annualChallenge", 0);
        }else{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("annualChallenge", 0);
        }
    }
}
