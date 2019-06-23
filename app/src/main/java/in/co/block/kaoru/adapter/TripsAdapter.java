package in.co.block.kaoru.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.co.block.kaoru.R;
import in.co.block.kaoru.models.Trip;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {

    private final List<Trip> lTrips;
    private TripsListInterationListner mListener;

    public TripsAdapter(List<Trip> items, TripsListInterationListner listener) {
        lTrips = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.trip = lTrips.get(position);
        holder.tvStartStation.setText(lTrips.get(position).getStationFrom());
        holder.tvEndStation.setText(lTrips.get(position).getStationTo());
       holder.tvCharges.setText(lTrips.get(position).getCharges()+"");


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListInteraction(holder.trip);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lTrips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvStartStation,tvEndStation,tvCharges;
        public Trip trip;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvStartStation = (TextView) view.findViewById(R.id.from_station);
            tvEndStation = (TextView) view.findViewById(R.id.to_station);
            tvCharges = (TextView) view.findViewById(R.id.charges);

        }


    }

    public interface TripsListInterationListner {

        void onListInteraction(Trip item);
    }
}
