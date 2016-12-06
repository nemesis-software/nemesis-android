package io.nemesis.ninder.listener;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    private StaggeredGridLayoutManager LayoutManager;
    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;

    public EndlessRecyclerOnScrollListener(StaggeredGridLayoutManager linearLayoutManager) {
        this.LayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        visibleItemCount = LayoutManager.getChildCount();
        totalItemCount = LayoutManager.getItemCount();
        int[] firstVisibleItems = null;
        firstVisibleItems = LayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
        if(firstVisibleItems != null && firstVisibleItems.length > 0) {
            pastVisibleItems = firstVisibleItems[0];
        }

        if (loading) {
            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                loading = false;
                onLoadMore(0);
                Log.d("tag", "LOAD NEXT ITEM");
            }
        }
    }

    public abstract void onLoadMore(int current_page);
}