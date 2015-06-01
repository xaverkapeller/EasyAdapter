package com.github.easyadapter.app.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.github.easyadapter.EasyAdapter;
import com.github.easyadapter.annotations.Inject;
import com.github.easyadapter.app.R;
import com.github.easyadapter.app.models.ExampleListener;
import com.github.easyadapter.app.models.ExampleModel;
import com.github.easyadapter.impl.AbsViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 01/02/15
 */
public class MainFragment extends Fragment implements ExampleListener {

    private static final String[] MOVIES = new String[]{
            "The Woman in Black: Angel of Death",
            "20 Once Again",
            "Taken 3",
            "Tevar",
            "I",
            "Blackhat",
            "Spare Parts",
            "The Wedding Ringer",
            "Ex Machina",
            "Mortdecai",
            "Strange Magic",
            "The Boy Next Door",
            "The SpongeBob Movie: Sponge Out of Water",
            "Kingsman: The Secret Service",
            "Boonie Bears: Mystical Winter",
            "Project Almanac",
            "Running Man",
            "Wild Card",
            "It Follows",
            "C'est si bon",
            "Yennai Arindhaal",
            "Shaun the Sheep Movie",
            "Jupiter Ascending",
            "Old Fashioned",
            "Somewhere Only We Know",
            "Fifty Shades of Grey",
            "Dragon Blade",
            "Zhong Kui: Snow Girl and the Dark Crystal",
            "Badlapur",
            "Hot Tub Time Machine 2",
            "McFarland, USA",
            "The Duff",
            "The Second Best Exotic Marigold Hotel",
            "A la mala",
            "Focus",
            "The Lazarus Effect",
            "Chappie",
            "Faults",
            "Road Hard",
            "Unfinished Business",
            "Cinderella",
            "NH10",
            "Run All Night",
            "X+Y",
            "Furious 7",
            "Danny Collins",
            "Do You Believe?",
            "Jalaibee",
            "The Divergent Series: Insurgent",
            "The Gunman",
            "Get Hard",
            "Home"
    };

    private RecyclerView mRecyclerView;
    private EasyAdapter<ExampleModel> mAdapter;
    private final List<ExampleModel> mModels = new ArrayList<>();

    private final Comparator<ExampleModel> alphabeticalComparator = new Comparator<ExampleModel>() {
        @Override
        public int compare(ExampleModel lhs, ExampleModel rhs) {
            return lhs.getText().compareTo(rhs.getText());
        }
    };

    private final Comparator<ExampleModel> reverseAlphabeticalComparator = new Comparator<ExampleModel>() {
        @Override
        public int compare(ExampleModel lhs, ExampleModel rhs) {
            return rhs.getText().compareTo(lhs.getText());
        }
    };

    private boolean mShouldFilter = false;
    private boolean mAlphabeticalOrder = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        for (String movie : MOVIES) {
            mModels.add(new ExampleModel(movie));
        }

        mAdapter = new EasyAdapter<>(getActivity(), mModels);
        mAdapter.inject(this);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onExampleModelClick(AbsViewHolder<ExampleModel> viewHolder) {
        final ExampleModel model = viewHolder.itemModel;
        Toast.makeText(getActivity(), "Clicked on " + model.getText(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onExampleModelCheckedChange(@Inject AbsViewHolder<ExampleModel> viewHolder) {
        if (mShouldFilter && !viewHolder.itemModel.isChecked()) {
            final int index = viewHolder.getAdapterPosition();
            mAdapter.removeItem(index);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        setupOrderSwitch(menu);
        setupFilterSwitch(menu);
    }

    private void setupOrderSwitch(Menu menu) {
        final MenuItem item = menu.findItem(R.id.action_order_switch);
        final View actionView = item.getActionView();
        final SwitchCompat orderSwitch = (SwitchCompat) actionView.findViewById(R.id.scSwitch);
        orderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAlphabeticalOrder = isChecked;
                updateModels();

                final int toastMessageId = mAlphabeticalOrder
                        ? R.string.fragment_main_toast_ordered_alphabetical
                        : R.string.fragment_main_toast_ordered_reverse_alphabetical;
                Toast.makeText(getActivity(), toastMessageId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFilterSwitch(Menu menu) {
        final MenuItem item = menu.findItem(R.id.action_filter_switch);
        final View actionView = item.getActionView();
        final SwitchCompat orderSwitch = (SwitchCompat) actionView.findViewById(R.id.scSwitch);
        orderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mShouldFilter = isChecked;
                updateModels();

                final int toastMessageId = mShouldFilter
                        ? R.string.fragment_main_toast_applied_filter
                        : R.string.fragment_main_toast_removed_filter;
                Toast.makeText(getActivity(), toastMessageId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateModels() {
        final List<ExampleModel> filteredModels = mShouldFilter ? filterModels(mModels) : new ArrayList<>(mModels);
        final List<ExampleModel> orderedModels = orderModels(filteredModels);
        mAdapter.animateTo(orderedModels);
        mRecyclerView.scrollToPosition(0);
    }

    private List<ExampleModel> filterModels(List<ExampleModel> models) {
        final List<ExampleModel> filteredModels = new ArrayList<>();
        for (ExampleModel exampleModel : models) {
            if (exampleModel.isChecked()) {
                filteredModels.add(exampleModel);
            }
        }
        return filteredModels;
    }

    private List<ExampleModel> orderModels(List<ExampleModel> models) {
        final Comparator<ExampleModel> comparator = mAlphabeticalOrder
                ? alphabeticalComparator
                : reverseAlphabeticalComparator;

        return createOrderedList(models, comparator);
    }

    private List<ExampleModel> createOrderedList(List<ExampleModel> models, Comparator<ExampleModel> comparator) {
        final List<ExampleModel> orderedList = new ArrayList<>(models);
        Collections.sort(orderedList, comparator);
        return orderedList;
    }
}
