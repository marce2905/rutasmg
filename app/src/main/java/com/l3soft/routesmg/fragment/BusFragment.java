package com.l3soft.routesmg.fragment;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.l3soft.routesmg.MainActivity;
import com.l3soft.routesmg.R;
import com.l3soft.routesmg.adapter.BusAdapter;
import com.l3soft.routesmg.data.BusData;
import com.l3soft.routesmg.entity.Bus;

import java.util.List;

public class BusFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView recyclerView;
    private List<Bus> buses;
    private View view;
    private SwipeRefreshLayout swipeContainer;
    private BusData dataBus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bus, container, false);

        swipeContainer = view.findViewById(R.id.swipe_refresh);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(
                R.color.purple,
                R.color.orange,
                R.color.green);
        setHasOptionsMenu(true);

        init(view);
        return view;
    }

    private void init(final View view) {
        recyclerView = view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        MainActivity.loadStatusConn(null,null,R.drawable.offline,getString(R.string.status_offline));
        BusData dataBus = new BusData(getContext());
        this.dataBus= dataBus;

        buses = dataBus.loadDB();

        if(!buses.isEmpty()){
            //load information for local database
            BusAdapter busAdapter = new BusAdapter(buses, getContext());
            recyclerView.setAdapter(busAdapter);
        }
        swipeContainer.setRefreshing(true);
        dataBus.getbuses(recyclerView,swipeContainer);

    }

    @Override
    public void onRefresh() {
        dataBus.getbuses(recyclerView,swipeContainer);
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
