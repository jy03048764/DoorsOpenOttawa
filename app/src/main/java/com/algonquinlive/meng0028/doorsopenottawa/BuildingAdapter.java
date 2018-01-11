package com.algonquinlive.meng0028.doorsopenottawa;

/**
 * Created by Yanming Meng (meng0028)  on 2018/1/10.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.algonquinlive.meng0028.doorsopenottawa.model.BuildingPOJO;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * BuildingAdapter.
 *
 */
public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.ViewHolder> {

    public static final String BUILDING_KEY = "building_key";
    public static final String JSON_URL = MainActivity.JSON_URL;
    private Context            mContext;
    private List<BuildingPOJO> mBuildings;

    public BuildingAdapter(Context context, List<BuildingPOJO> buildings) {
        this.mContext = context;
        this.mBuildings = buildings;
    }

    public void sortByNameAscending() {
        Collections.sort(mBuildings, new Comparator<BuildingPOJO>() {
            @Override
            public int compare(BuildingPOJO lhs, BuildingPOJO rhs) {
                return lhs.getNameEN().compareTo(rhs.getNameEN());
            }
        });

        notifyDataSetChanged();
    }

    public void sortByNameDescending() {
        Collections.sort(mBuildings, Collections.reverseOrder(new Comparator<BuildingPOJO>() {
            @Override
            public int compare(BuildingPOJO lhs, BuildingPOJO rhs) {
                return lhs.getNameEN().compareTo(rhs.getNameEN());
            }
        }));

        notifyDataSetChanged();
    }
    @Override
    public BuildingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View planetView = inflater.inflate(R.layout.list_building, parent, false);
        ViewHolder viewHolder = new ViewHolder(planetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BuildingAdapter.ViewHolder holder, int position) {
        final BuildingPOJO aBuilding = mBuildings.get(position);

        holder.tvName.setText(aBuilding.getNameEN());
        holder.tvAddress.setText(aBuilding.getAddressEN());


        String url = "https://doors-open-ottawa.mybluemix.net/buildings/" + aBuilding.getBuildingId() + "/image";

        Picasso.with(mContext)
                .load(Uri.parse(url))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.noimagefound)
                .resize(96, 96)
                .into(holder.imageView);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailBuildingActivity.class);
                intent.putExtra(BUILDING_KEY, aBuilding);
                mContext.startActivity(intent);
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(mContext, EditBuildingActivity.class);
                intent.putExtra(BUILDING_KEY, aBuilding);
                mContext.startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBuildings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvAddress;
        public ImageView imageView;
        public View mView;

        public ViewHolder(View buildingView) {
            super(buildingView);

            tvName = (TextView) buildingView.findViewById(R.id.buildingName);
            tvAddress = (TextView) buildingView.findViewById(R.id.buildingAddress);
            imageView = (ImageView) buildingView.findViewById(R.id.imageView);
            mView = buildingView;
        }
    }
}
