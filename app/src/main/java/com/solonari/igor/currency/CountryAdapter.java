package com.solonari.igor.currency;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.Collections;
import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

    private List<Country> mValues;
    private String TAG = "COUTRY_ADAPTER";
    private MainActivity mainActivity;

    public CountryAdapter(List<Country> items, MainActivity mainActivity) {
        mValues = items;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public CountryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.country_view, parent, false);

        return new CountryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CountryAdapter.ViewHolder holder, final int position) {

        holder.country = mValues.get(position);

        holder.name.setText(mValues.get(position).name);
        holder.value.setText(String.format("%.2f", mValues.get(position).value));
        holder.description.setText(Utils.dictionary.get(mValues.get(position).name));

        holder.mView.setTag(mValues.get(position).name);

        String imageName = "flag_".concat(mValues.get(position).name.toLowerCase());
        int flagId = mainActivity.getResources().getIdentifier(imageName, "drawable", mainActivity.getPackageName());
        holder.flag.setImageResource(flagId);

        holder.value.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (holder.country.name.equals(mainActivity.baseCountry.name) && s.length() > 0) {
                    mainActivity.baseCountry.value = Double.valueOf(s.toString());
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                mainActivity.baseCountry = mValues.get(position);
                mainActivity.countryRecyclerView.scrollToPosition(0);
                Collections.swap(mValues, position, 0);
                mainActivity.countryRecyclerViewAdapter.notifyItemMoved(position, 0);
                holder.value.requestFocus();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public Country country;
        public final View mView;
        public final TextView name;
        public final EditText value;
        public final ImageView flag;
        public final TextView description;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = view.findViewById(R.id.name);
            value = view.findViewById(R.id.value);
            flag = view.findViewById(R.id.flag);
            description = view.findViewById(R.id.description);
        }
    }
}
