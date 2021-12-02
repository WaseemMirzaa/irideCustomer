package com.buzzware.iride.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iride.adapters.HistoryAddapter;
import com.buzzware.iride.databinding.FragmentHIstoryBinding;
import com.buzzware.iride.models.HistoryModel;
import com.buzzware.iride.R;
import com.buzzware.iride.screens.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class History extends BaseActivity {

    FragmentHIstoryBinding mBinding;
    HistoryAddapter historyAddapter;
    List<HistoryModel> historyModelList;

    public History() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = FragmentHIstoryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        Init();
    }

    private void Init() {
        historyModelList= new ArrayList<>();
        SetDummyList();
        mBinding.rvHistory.setLayoutManager(new LinearLayoutManager(History.this));
        historyAddapter= new HistoryAddapter(History.this, historyModelList);
        mBinding.rvHistory.setAdapter(historyAddapter);
        historyAddapter.notifyDataSetChanged();
    }

    private void SetDummyList() {
        historyModelList.add(new HistoryModel());
        historyModelList.add(new HistoryModel());
        historyModelList.add(new HistoryModel());
        historyModelList.add(new HistoryModel());
        historyModelList.add(new HistoryModel());
        historyModelList.add(new HistoryModel());
        historyModelList.add(new HistoryModel());
        historyModelList.add(new HistoryModel());
        historyModelList.add(new HistoryModel());
        historyModelList.add(new HistoryModel());
    }
}