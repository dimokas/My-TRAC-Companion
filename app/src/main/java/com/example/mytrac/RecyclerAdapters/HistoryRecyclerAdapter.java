package com.example.mytrac.RecyclerAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mytrac.Constants;
import com.example.mytrac.ItemClickListeners.OnHistoryItemClickListener;
import com.example.mytrac.MainActivity;
import com.example.mytrac.PastDestination;
import com.example.mytrac.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryViewHolder>{

    private Context context;

    private List<PastDestination> historyList;
    private List<String> dates;
    private String currentDate, yesterdayDate;
    private boolean topDate;

    private OnHistoryItemClickListener onHistoryItemClickListener;
    public OnHistoryItemClickListener getOnHistoryItemClickListener() {
        return onHistoryItemClickListener;
    }

    public void setOnHistoryItemClickListener(OnHistoryItemClickListener onHistoryItemClickListener) {
        this.onHistoryItemClickListener = onHistoryItemClickListener;
    }

    public HistoryRecyclerAdapter(Context context, List<PastDestination> historyList) {
        this.context = context;
        this.historyList = historyList;
        dates = new ArrayList<>();
        topDate = true;

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        currentDate = format.format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        yesterdayDate = format.format(cal.getTime());
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false);

        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        holder.address.setText(historyList.get(position).getAddress() + ",");
        holder.area.setText(historyList.get(position).getArea());

        if (!dates.contains(historyList.get(position).getDate())) {
            if (historyList.get(position).getDate().equals(currentDate)) {
                holder.date.setText(context.getResources().getString(R.string.today));
            }
            else if (historyList.get(position).getDate().equals(yesterdayDate)) {
                holder.date.setText(context.getResources().getString(R.string.yesterday));
            }
            else {
                holder.date.setText(historyList.get(position).getDate());
            }
            dates.add(historyList.get(position).getDate());
            holder.date.setVisibility(View.VISIBLE);

            if (topDate) {
                topDate = false;
            }
            else {
                holder.separator.setVisibility(View.VISIBLE);
            }
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHistoryItemClickListener.onItemClick(historyList.get(position));
            }
        };

        SharedPreferences sharedPrefs = MainActivity.mainActivity.getSharedPreferences("mytrac.user.settings", MODE_PRIVATE);
        int userCategory = sharedPrefs.getInt("userCategory", 0);
        if (historyList.get(position).getState() == PastDestination.State.HOME) {
            if (userCategory == Constants.LOW_VISION_USER) {
                holder.icon.setBackgroundResource(R.drawable.ic_dialog_home_on_low_vision);
            } else if (userCategory == Constants.DEFAULT_USER) {
                holder.icon.setBackgroundResource(R.drawable.ic_dialog_home_on);
            }
        }
        else if (historyList.get(position).getState() == PastDestination.State.WORK) {
            if (userCategory == Constants.LOW_VISION_USER) {
                holder.icon.setBackgroundResource(R.drawable.ic_dialog_work_on_low_vision);
            } else if (userCategory == Constants.DEFAULT_USER) {
                holder.icon.setBackgroundResource(R.drawable.ic_dialog_work_on);
            }
        }
        else if (historyList.get(position).getState() == PastDestination.State.FAVORITE) {
            if (userCategory == Constants.LOW_VISION_USER) {
                holder.icon.setBackgroundResource(R.drawable.ic_dialog_fav_on_low_vision);
            } else if (userCategory == Constants.DEFAULT_USER) {
                holder.icon.setBackgroundResource(R.drawable.ic_dialog_fav_on);
            }
        }
        else {
            if (userCategory == Constants.LOW_VISION_USER) {
                holder.icon.setBackgroundResource(R.drawable.ic_dialog_fav_off);
            } else if (userCategory == Constants.DEFAULT_USER) {
                holder.icon.setBackgroundResource(R.drawable.ic_dialog_fav_off);
            }
            holder.itemLayout.setOnClickListener(listener);
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView address, area, date;
        public ImageView icon;
        public ConstraintLayout itemLayout;
        public View separator;

        public HistoryViewHolder(View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.historyAddress);
            area = (TextView) view.findViewById(R.id.historyArea);
            date = (TextView) view.findViewById(R.id.historyDate);
            icon = (ImageView) view.findViewById(R.id.historyIcon);
            itemLayout = (ConstraintLayout) view.findViewById(R.id.historyItem);
            separator = (View) view.findViewById(R.id.historySeparator);
        }
    }
}
