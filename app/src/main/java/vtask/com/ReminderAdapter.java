package vtask.com;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by akhil on 9/5/2016.
 */

public class ReminderAdapter  extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder> {

    private List<ReminderItem> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.row_title);
            genre = (TextView) view.findViewById(R.id.row_date);
            year = (TextView) view.findViewById(R.id.row_time);
        }
    }


    public ReminderAdapter(List<ReminderItem> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ReminderItem movie = moviesList.get(position);
        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getDate());
        holder.year.setText(movie.getTime());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
