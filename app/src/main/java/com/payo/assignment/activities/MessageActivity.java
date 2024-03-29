package com.payo.assignment.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.payo.assignment.R;
import com.payo.assignment.adapters.MessageAdapter;
import com.payo.assignment.data.TransactionType;
import com.payo.assignment.viewmodels.MessagesViewModel;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageActivity extends AppCompatActivity {

    private final String TAG = MessageActivity.class.getName();
    private MessagesViewModel messagesViewModel;

    private double spentMoney = 0;
    private double earnedMoney = 0;
    private double unknownMoney = 0;

    @BindView(R.id.rv_messages)
    RecyclerView messagesList;
    @BindView(R.id.pie_chart)
    PieChart pieChart;
    @BindView(R.id.rg_filter)
    RadioGroup filter;
    @BindView(R.id.tv_total_credit)
    TextView totalCredit;
    @BindView(R.id.tv_total_debit)
    TextView totalDebit;


    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);

        messagesViewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);

        MessageAdapter messageAdapter = new MessageAdapter();
        messagesList.setLayoutManager(new LinearLayoutManager(this));
        messagesList.setAdapter(messageAdapter);

        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(0f);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawCenterText(false);

        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.animateY(1400, Easing.EaseInOutQuad);

        filter.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.rb_all:
                    messagesViewModel.getMessages(getApplicationContext()).observe(this,
                            messageAdapter::setDataset);
                    break;
                case R.id.rb_credit:
                    messagesViewModel.filterMessages(getApplicationContext(), TransactionType.CREDIT.name())
                            .observe(this, messageAdapter::setDataset);
                    break;
                case R.id.rb_debit:
                    messagesViewModel.filterMessages(getApplicationContext(), TransactionType.DEBIT.name())
                            .observe(this, messageAdapter::setDataset);
                    break;
            }
        });

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_SMS)
                .subscribe(granted -> {
                    if (granted) {
                        messagesViewModel.getMessages(getApplicationContext()).observe(this,
                                messageAdapter::setDataset);
                    }
                }, throwable -> Log.e(TAG, throwable.toString()));

        messagesViewModel.getAmountCredited(this).observe(this, credited -> {
            earnedMoney = credited;
            totalCredit.setText(String.format("Total Credit - Rs %s", credited));
            updateChart();
        });
        messagesViewModel.getAmountDebited(this).observe(this, debited -> {
            spentMoney = debited;
            totalDebit.setText(String.format("Total Debit - Rs %s", debited));
            updateChart();
        });
        messagesViewModel.getAmountUndefined(this).observe(this, undefined -> {
            unknownMoney = undefined;
            updateChart();
        });
    }

    private void updateChart() {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.debit_color));
        colors.add(getResources().getColor(R.color.credit_color));
        colors.add(getResources().getColor(R.color.undefined_color));

        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry((float) spentMoney, "Debit"));
        pieEntries.add(new PieEntry((float) earnedMoney, "Credit"));
        pieEntries.add(new PieEntry((float) unknownMoney, "Unknown"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);

        PieData data = new PieData(pieDataSet);
        data.setDrawValues(false);

        pieChart.highlightValues(null);
        pieChart.setData(data);
        pieChart.invalidate();
    }
}