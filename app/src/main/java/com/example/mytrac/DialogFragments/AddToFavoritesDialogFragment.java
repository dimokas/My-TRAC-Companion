package com.example.mytrac.DialogFragments;


import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mytrac.Constants;
import com.example.mytrac.MainActivity;
import com.example.mytrac.R;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddToFavoritesDialogFragment extends DialogFragment {

    private TextView title;
    private ImageView favStateImg, dismissImg;
    private Button addBtn;

    public AddToFavoritesDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_to_favorites_dialog, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        title = (TextView) view.findViewById(R.id.addFavoriteTv);
        title.setText("Aigeialias 52, Marousi"); // display address to be added to favorites

        favStateImg = (ImageView) view.findViewById(R.id.addFavoriteImg);

        addBtn = (Button) view.findViewById(R.id.addFavoriteBtn);
        addBtn.setOnClickListener(new View.OnClickListener() { // onclick add favorite to back-end and change display of dialog to indicate success
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPrefs = MainActivity.mainActivity.getSharedPreferences("mytrac.user.settings", MODE_PRIVATE);
                int userCategory = sharedPrefs.getInt("userCategory", 0);

                if (userCategory == Constants.LOW_VISION_USER) {
                    favStateImg.setBackgroundResource(R.drawable.ic_dialog_fav_state2_low_vision);
                } else if (userCategory == Constants.DEFAULT_USER) {
                    favStateImg.setBackgroundResource(R.drawable.ic_dialog_fav_state2);
                }

                addBtn.setBackgroundResource(R.drawable.btn_grey_wide);
                addBtn.setText(getResources().getString(R.string.location_added_to_favorites));
                addBtn.setContentDescription(getResources().getString(R.string.location_added_to_favorites));
            }
        });

        dismissImg = (ImageView) view.findViewById(R.id.addFavoriteDismiss);
        dismissImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {

            Window window = getDialog().getWindow();
            Point size = new Point();

            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);

            int width = size.x;
            int height = size.y;

            // set percentage of width and height screen the dialog is supposed to take
            window.setLayout((int) (width * 0.95), (int) (height * 0.55));
            window.setGravity(Gravity.CENTER);

        }
    }

}
