package com.example.aexpress.adapters;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperAdapter {
    void onItemSwiped(int position, int direction);
    void onItemMoved(int fromPosition, int toPosition);
    void onItemSelected(RecyclerView.ViewHolder viewHolder);
    void onItemClear(RecyclerView.ViewHolder viewHolder);
}
