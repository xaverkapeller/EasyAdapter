package com.github.easyadapter.app.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.easyadapter.EasyAdapter;
import com.github.easyadapter.api.ViewModel;
import com.github.easyadapter.app.models.ExampleListener;
import com.github.easyadapter.app.models.ExampleModelOne;
import com.github.easyadapter.app.R;
import com.github.easyadapter.app.models.ExampleModelTwo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 01/02/15
 */
public class MainFragment extends Fragment implements ExampleListener {

    private RecyclerView recyclerView;
    private EasyAdapter<ViewModel> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final List<ViewModel> models = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if(i % 2 == 0) {
                models.add(new ExampleModelOne(String.valueOf(i), R.drawable.ic_launcher));
            } else {
                models.add(new ExampleModelTwo(R.string.checkbox_text));
            }
        }

        this.adapter = new EasyAdapter<>(getActivity(), models, this);
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onClick(ExampleModelOne model) {
        int i = this.adapter.models().indexOf(model);
        this.adapter.models().remove(model);
        this.adapter.notifyItemRemoved(i);
        Toast.makeText(getActivity(), "Clicked on " + model.getText(), Toast.LENGTH_SHORT).show();
    }
}
