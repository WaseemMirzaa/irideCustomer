package com.buzzware.iride.screens;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iride.adapters.HistoryAddapter;
import com.buzzware.iride.models.HistoryModel;
import com.buzzware.iride.R;
import com.buzzware.iride.databinding.FragmentUpcommingBinding;

import java.util.ArrayList;
import java.util.List;

public class UpcommingFragment extends Fragment {

    FragmentUpcommingBinding mBinding;
    HistoryAddapter historyAddapter;
    List<HistoryModel> historyModelList;

    public UpcommingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_upcomming, container, false);
        try {

        }catch (Exception e)
        {
            e.printStackTrace();
        }Init();
        return mBinding.getRoot();
    }

    private void Init() {
        historyModelList= new ArrayList<>();
        SetDummyList();
        mBinding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAddapter= new HistoryAddapter(getContext(), historyModelList);
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