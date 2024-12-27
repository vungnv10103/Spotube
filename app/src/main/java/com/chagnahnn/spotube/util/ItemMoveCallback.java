//package com.chagnahnn.spotube.util;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.ItemTouchHelper;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.chagnahnn.spotube.ui.adapter.MultiMediaAdapter;
//import com.chagnahnn.spotube.ui.perform.MultiMediaTouchHelper;
//
//public class ItemMoveCallback extends ItemTouchHelper.Callback {
//    private final MultiMediaTouchHelper iMediaItemTouchHelper;
//
//    public ItemMoveCallback(MultiMediaTouchHelper iMediaItemTouchHelper) {
//        this.iMediaItemTouchHelper = iMediaItemTouchHelper;
//    }
//
//    @Override
//    public boolean isItemViewSwipeEnabled() {
//        return false;
//    }
//
//    @Override
//    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//        return makeMovementFlags(dragFlags, 0);
//    }
//
//    @Override
//    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//        iMediaItemTouchHelper.onRowMoved(viewHolder.getBindingAdapterPosition(), target.getBindingAdapterPosition());
//        return true;
//    }
//
//    @Override
//    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//    }
//
//    @Override
//    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
//        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//            if (viewHolder instanceof MultiMediaAdapter.ViewHolder) {
//                MultiMediaAdapter.ViewHolder myViewHolder = (MultiMediaAdapter.ViewHolder) viewHolder;
//                iMediaItemTouchHelper.onRowSelected(myViewHolder);
//            }
//        }
//
//        super.onSelectedChanged(viewHolder, actionState);
//    }
//
//    @Override
//    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//        super.clearView(recyclerView, viewHolder);
//
//        if (viewHolder instanceof MultiMediaAdapter.ViewHolder) {
//            MultiMediaAdapter.ViewHolder myViewHolder =
//                    (MultiMediaAdapter.ViewHolder) viewHolder;
//            iMediaItemTouchHelper.onRowClear(myViewHolder);
//        }
//    }
//}
