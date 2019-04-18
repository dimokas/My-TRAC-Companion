package com.example.mytrac.RecyclerAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.mytrac.Constants;
import com.example.mytrac.ItemClickListeners.OnFavoriteItemClickListener;
import com.example.mytrac.MainActivity;
import com.example.mytrac.R;
import com.example.mytrac.SearchLocationFragment;

import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class FavoritesRecyclerAdapter extends RecyclerView.Adapter<FavoritesRecyclerAdapter.FavoriteViewHolder>{

    private Context context;

    private List<String> favoritesList;
    private OnFavoriteItemClickListener onFavoriteItemClickListener;
    public OnFavoriteItemClickListener getOnFavoriteItemClickListener() {
        return onFavoriteItemClickListener;
    }

    public void setOnFavoriteItemClickListener(OnFavoriteItemClickListener onFavoriteItemClickListener) {
        this.onFavoriteItemClickListener = onFavoriteItemClickListener;
    }

    public FavoritesRecyclerAdapter(Context context, List<String> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_list_item, parent, false);

        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        holder.address.setText(favoritesList.get(position));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavoriteItemClickListener.onItemClick(position);
            }
        };
        SharedPreferences sharedPrefs = MainActivity.mainActivity.getSharedPreferences("mytrac.user.settings", MODE_PRIVATE);
        int userCategory = sharedPrefs.getInt("userCategory", 0);

        if (position == 0) {
            if (favoritesList.get(position).equals(holder.itemView.getContext().getResources().getString(R.string.tap_to_set))) {

                if (userCategory == Constants.LOW_VISION_USER) {
                    holder.icon.setBackgroundResource(R.drawable.ic_dialog_home_off);
                    holder.address.setTextColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.colorDarkGrey, null));
                    holder.address.setContentDescription("Tap to set home location");
                    //holder.address.setHint("Tap to set home location");
                } else if (userCategory == Constants.DEFAULT_USER) {
                    holder.icon.setBackgroundResource(R.drawable.ic_dialog_home_off);
                    holder.address.setTextColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.colorGrey, null));
                }


                if (holder.address.getTypeface() != null) {
                    holder.address.setTypeface(holder.address.getTypeface(), Typeface.ITALIC);
                }

                holder.menu.setVisibility(View.INVISIBLE);
                holder.address.setOnClickListener(listener);
            }
            else {
                if (userCategory == Constants.LOW_VISION_USER) {
                    holder.icon.setBackgroundResource(R.drawable.ic_dialog_home_on_low_vision);
                } else if (userCategory == Constants.DEFAULT_USER) {
                    holder.icon.setBackgroundResource(R.drawable.ic_dialog_home_on);
                    holder.address.setTextColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.colorBlack, null));
                }

                //holder.icon.setBackgroundResource(R.drawable.ic_dialog_home_on);
                holder.address.setTypeface(null, Typeface.NORMAL);
                //holder.address.setTextColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.colorBlack, null));
                holder.menu.setVisibility(View.VISIBLE);
            }
        }
        else if (position == 1) {
            if (favoritesList.get(position).equals(holder.itemView.getContext().getResources().getString(R.string.tap_to_set))) {

                if (userCategory == Constants.LOW_VISION_USER) {
                    holder.icon.setBackgroundResource(R.drawable.ic_dialog_work_off);
                    holder.address.setTextColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.colorDarkGrey, null));
                    holder.address.setContentDescription("Tap to set office location");
                    //holder.address.setHint("Tap to set office location");
                } else if (userCategory == Constants.DEFAULT_USER) {
                    holder.icon.setBackgroundResource(R.drawable.ic_dialog_work_off);
                    holder.address.setTextColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.colorGrey, null));
                }

                if (holder.address.getTypeface() != null) {
                    holder.address.setTypeface(holder.address.getTypeface(), Typeface.ITALIC);
                }

                holder.menu.setVisibility(View.INVISIBLE);
                holder.address.setOnClickListener(listener);
            }
            else {
                if (userCategory == Constants.LOW_VISION_USER) {
                    holder.icon.setBackgroundResource(R.drawable.ic_dialog_work_on_low_vision);
                } else if (userCategory == Constants.DEFAULT_USER) {
                    holder.icon.setBackgroundResource(R.drawable.ic_dialog_work_on);
                    holder.address.setTextColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.colorBlack, null));
                }

                //holder.icon.setBackgroundResource(R.drawable.ic_dialog_work_on);
                holder.address.setTypeface(null, Typeface.NORMAL);
                //holder.address.setTextColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.colorBlack, null));
                holder.menu.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (userCategory == Constants.LOW_VISION_USER) {
                holder.icon.setBackgroundResource(R.drawable.ic_dialog_fav_on_low_vision);
            } else if (userCategory == Constants.DEFAULT_USER) {
                holder.icon.setBackgroundResource(R.drawable.ic_dialog_fav_on);
                holder.address.setTextColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.colorBlack, null));
            }
            //holder.icon.setBackgroundResource(R.drawable.ic_dialog_fav_on);
            holder.address.setTypeface(null, Typeface.NORMAL);
            //holder.address.setTextColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.colorBlack, null));
            holder.menu.setVisibility(View.VISIBLE);
        }

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, holder, position);
            }
        });
    }

    public void add(String item) {
        int position = favoritesList.size();
        favoritesList.add(position, item);
        notifyItemInserted(position);
    }

    public void edit(int position, String newItem) {
        favoritesList.set(position, newItem);
        notifyItemChanged(position);
    }

    public void delete(int position) {
        favoritesList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, favoritesList.size());
    }

    public void removeHomeOrWorkFavorite(int position, FavoriteViewHolder holder) {
        favoritesList.set(position, holder.itemView.getContext().getResources().getString(R.string.tap_to_set));
        holder.address.setText(favoritesList.get(position));
        holder.address.setTextColor(ResourcesCompat.getColor(holder.itemView.getContext().getResources(), R.color.colorGrey, null));

        if (holder.address.getTypeface() != null) {
            holder.address.setTypeface(holder.address.getTypeface(), Typeface.ITALIC);
        }

        holder.menu.setVisibility(View.INVISIBLE);

        if (position == 0) {
            holder.icon.setBackgroundResource(R.drawable.ic_dialog_home_off);
        }
        else {
            holder.icon.setBackgroundResource(R.drawable.ic_dialog_work_off);
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavoriteItemClickListener.onItemClick(position);
            }
        };
        holder.address.setOnClickListener(listener);
    }

    public void showPopup(View v, FavoriteViewHolder holder, int position) {
        PopupMenu popup = new PopupMenu(context, v);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.favorite_edit:
                        Log.d(TAG,"Menu item 1");

                        SearchLocationFragment newFrag = new SearchLocationFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("state", "edit");
                        bundle.putInt("position", position);
                        bundle.putString("location", holder.address.getText().toString());
                        newFrag.setArguments(bundle);

                        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main_screen, newFrag).addToBackStack(null).commit();

                        return true;
                    case R.id.favorite_remove:
                        Log.d(TAG,"Menu item 2");

                        if (position > 1) {
                            delete(holder.getAdapterPosition());
                        }
                        else {
                            removeHomeOrWorkFavorite(position, holder);
                        }

                        return true;
                    default:
                        Log.d(TAG,"Menu item 3");
                        return false;
                }
            }
        });

        popup.inflate(R.menu.favorite_menu);
        popup.show();
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {
        public TextView address;
        public ImageView icon;
        public ImageButton menu;

        public FavoriteViewHolder(View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.favoriteAddress);
            icon = (ImageView) view.findViewById(R.id.favoriteIcon);
            menu = (ImageButton) view.findViewById(R.id.favoriteMenu);
        }
    }
}
