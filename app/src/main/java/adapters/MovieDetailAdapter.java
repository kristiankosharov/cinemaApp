package adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import database.SQLite.ActorsDataSource;
import database.SQLite.AllDaysDataSource;
import database.SQLite.AllGenresDataSource;
import database.SQLite.GenresDataSource;
import database.SQLite.MoviesCinemasDataSource;
import database.SQLite.MoviesGenresDataSource;
import helpers.CustomHorizontalScrollView;
import helpers.SessionManager;
import models.AddMovies;
import models.Cinema;
import models.Genre;
import models.Movie;
import mycinemaapp.com.mycinemaapp.BaseActivity;
import mycinemaapp.com.mycinemaapp.MovieTrailerLandscape;
import mycinemaapp.com.mycinemaapp.R;
import mycinemaapp.com.mycinemaapp.RateActivity;
import mycinemaapp.com.mycinemaapp.WebViewActivity;

/**
 * Created by kristian on 15-3-5.
 */
public class MovieDetailAdapter extends PagerAdapter {

    private Activity context;
    private ArrayList<Movie> list = new ArrayList<>();
    private ArrayList<Movie> listFromMain = new ArrayList<>();
    private String day;
    private String place;

    private float density;
    private int viewWidth;
    private SessionManager sm;
    private boolean isList, isRated, isBought;
    private AllDaysDataSource daysDataSource;
    private AllGenresDataSource allGenresDataSource;
    private ActorsDataSource actorsDataSource;
    private GenresDataSource genresDataSource;
    private MoviesGenresDataSource moviesGenresDataSource;
    private MoviesCinemasDataSource moviesCinemasDataSource;


    public MovieDetailAdapter(Activity context, ArrayList<Movie> list, ArrayList<Movie> staticList, boolean isList, boolean isRated, boolean isBought) {
        this.context = context;
        this.list = list;
        this.isList = isList;
        this.isRated = isRated;
        this.isBought = isBought;
        listFromMain = staticList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        density = ((BaseActivity) context).getDensity();
        viewWidth = ((BaseActivity) context).getViewWidth();
        sm = new SessionManager(context);
        daysDataSource = new AllDaysDataSource(context);
        daysDataSource.open();
        allGenresDataSource = new AllGenresDataSource(context);
        allGenresDataSource.open();
        actorsDataSource = new ActorsDataSource(context);
        actorsDataSource.open();
        genresDataSource = new GenresDataSource(context);
        genresDataSource.open();
        moviesGenresDataSource = new MoviesGenresDataSource(context);
        moviesGenresDataSource.open();
        moviesCinemasDataSource = new MoviesCinemasDataSource(context);
        moviesCinemasDataSource.open();

        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.movie_detail_item, null, false);

        final PagerHolder viewHolder = new PagerHolder();
        viewHolder.ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        viewHolder.rateBtn = (Button) view.findViewById(R.id.rate_button);
        viewHolder.movieImage = (ImageView) view.findViewById(R.id.image);
        viewHolder.title = (TextView) view.findViewById(R.id.title);
        viewHolder.genre = (TextView) view.findViewById(R.id.genre);
        viewHolder.director = (TextView) view.findViewById(R.id.names_director);
        viewHolder.actors = (TextView) view.findViewById(R.id.names_actors);
        viewHolder.duration = (TextView) view.findViewById(R.id.duration);
        viewHolder.addButton = (ImageView) view.findViewById(R.id.add_button);
        viewHolder.userRatingIcon = (TextView) view.findViewById(R.id.rating);

        viewHolder.titleDescription = (TextView) view.findViewById(R.id.title_details);
        viewHolder.imdb = (Button) view.findViewById(R.id.imdb);
        viewHolder.fullDescription = (TextView) view.findViewById(R.id.full_desription);
        viewHolder.movieTrailer = (VideoView) view.findViewById(R.id.movie_trailer);
        viewHolder.playTrailer = (ImageButton) view.findViewById(R.id.play_trailer);
        RelativeLayout l = (RelativeLayout) view.findViewById(R.id.rating_layout);
        l.requestFocus();

        view.setTag(viewHolder);

        //PagerHolder holder = (PagerHolder) view.getTag();

        final Movie item = list.get(position);

        viewHolder.ratingBar.setMax(5);
        viewHolder.ratingBar.setStepSize(0.5f);
        viewHolder.ratingBar.setRating(item.getMovieProgress());
        //viewHolder.movieImage.setImageUrl("", null);
        Picasso.with(context)
                .load(item.getImageUrl())
                .noPlaceholder()
                .into(viewHolder.movieImage);
        viewHolder.title.setText(item.getMovieTitle());

        String genresString = "";
        ArrayList<Genre> genresArray = moviesGenresDataSource.getAllGenreFromId(item.getId());
        Log.d("ARRAY LIST SIZE", genresArray.size() + "");
        for (int i = 0; i < genresArray.size(); i++) {
            genresString = genresString + genresArray.get(i).getTitle() + ".";
        }
        viewHolder.genre.setText(genresString);


        String directorsString = "";
//        ArrayList<String> directorsArray = item.getMovieDirectors();
//        if (item.getMovieDirectors() != null) {
//            for (int i = 0; i < item.getCountDeirectors(); i++) {
//                if (directorsArray.get(i) == null) {
//                    directorsArray.remove(i);
//                    directorsArray.add(i, "");
//                }
//                directorsString = directorsString + directorsArray.get(i) + "\n";
//            }
//        }
        viewHolder.director.setText(item.getMovieDirectors());

        String actorsString = "";
        if (listFromMain.get(position).getMovieActors() != null) {
            for (int i = 0; i < listFromMain.get(position).getMovieActors().size(); i++) {
                actorsString = actorsString + listFromMain.get(position).getMovieActors().get(i) + "\n";
            }
        }

        viewHolder.actors.setText(actorsString);
        if (item.getDuration() != 0) {
            viewHolder.duration.setText(item.getDuration() + " min " + item.getReleaseDate());
        }
        if (item.getMovieTitle() != null) {
            viewHolder.titleDescription.setText(item.getMovieTitle());
        }
        if (item.getFullDescription() != null) {
            viewHolder.fullDescription.setText(item.getFullDescription());
        }
        viewHolder.playTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieTrailerLandscape.class);
                intent.putExtra("url", item.getMovieTrailerUrl());
                intent.putExtra("POSITION", position);
                context.startActivityForResult(intent, 1);
            }
        });

        viewHolder.movieTrailer.clearFocus();
        viewHolder.imdb.setOnClickListener(imdbListener(item));


        RelativeLayout daysLayout = (RelativeLayout) view.findViewById(R.id.days_horizontal_scroll_view);
        RelativeLayout mallLayout = (RelativeLayout) view.findViewById(R.id.mall_horizontal_scroll_view);
        RelativeLayout projectionLayout = (RelativeLayout) view.findViewById(R.id.projection_horizontal_scroll_view);

        //New Thread maybe
        if (item.getCinemaIds() != null || item.getDate() != null) {
            createDaysScroll(daysLayout, item, projectionLayout, position);
            createPlaceScroll(mallLayout, item, projectionLayout, position);
        }
        viewHolder.rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RateActivity.class);
                intent.putExtra("ISRATED", isRated);
                intent.putExtra("ISLIST", isList);
                intent.putExtra("POSITION", position);
                context.startActivityForResult(intent, 2);
            }
        });

        if (item.isAdd()) {
            viewHolder.addButton.setImageResource(R.drawable.check_icon);
            viewHolder.addButton.setPadding(10, 10, 10, 10);
            viewHolder.addButton.setOnClickListener(null);
        }
        if (item.getUserRating() != 0) {
            viewHolder.addButton.setVisibility(View.GONE);
            viewHolder.userRatingIcon.setText(String.valueOf(item.getUserRating()));
            viewHolder.userRatingIcon.setTextColor(Color.WHITE);
            viewHolder.userRatingIcon.setVisibility(View.VISIBLE);
        }
        if (!item.isAdd()) {
            viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sm.getRemember()) {
                        item.setAdd(true);
                        item.setPosition(position);
                        AddMovies.addMovie.add(item);

                        viewHolder.addButton.setImageResource(R.drawable.check_icon);
                        viewHolder.addButton.setPadding(10, 10, 10, 10);
                        viewHolder.addButton.setOnClickListener(null);
                        Toast.makeText(context, "Add movie to list.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "First you must log in!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        ((ViewPager) container).addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    static class PagerHolder {
        RatingBar ratingBar;
        Button rateBtn, imdb;
        ImageView movieImage;
        TextView title, genre, director, actors, duration, titleDescription, fullDescription, userRatingIcon;
        VideoView movieTrailer;
        ImageButton playTrailer;
        ImageView addButton;
    }

    public void createDaysScroll(RelativeLayout containerLayout, Movie item, RelativeLayout
            containerForProjects, int position) {
        boolean fromAdapter = true;
        ArrayList<String> nameOfDays = new ArrayList<>();
        ArrayList<String> date = new ArrayList<>();
        ArrayList<Cinema> places = new ArrayList<>();
        try {
            if (listFromMain.get(position).getDate() != null) {
                date = listFromMain.get(position).getDate();
            }
            places = moviesCinemasDataSource.getAllCinemasFromId(item.getId());
            if (listFromMain.get(position).getNameDayOfMonth() != null) {
                nameOfDays = listFromMain.get(position).getNameDayOfMonth();
            }

        } catch (Exception e) {
            Log.d("EXEPTION", "ADAPTER MOVIE CINEMAS");
            e.printStackTrace();
        }
        Log.d("PLACES", places.toString());
//        Log.d("PLACES DATE SIZE", date.size() + ", PLACES: " + places.size());
//        for (Filters s : date) {
//            nameOfDays.add(s.getDayNameFilter());
//        }

//        final ArrayList<String> nameOfPlace = item.getNameOfPlace();

        TextView dayView;
        TextView dateView;
        LinearLayout layout;

        LinearLayout masterLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                viewWidth,
                (int) (50 * density));

        LinearLayout.LayoutParams textViewParam = new LinearLayout.LayoutParams(
                viewWidth,
                (int) (50 * density), Gravity.CENTER_HORIZONTAL);
        textViewParam.weight = 1;
        int halfViewWidth = viewWidth / 2;

        final CustomHorizontalScrollView scrollView = new CustomHorizontalScrollView(context, date.size(), viewWidth, viewWidth, containerForProjects);

        if (listFromMain.get(position).getAllProjections() != null) {
            scrollView.setDayAndPlace(places, date, listFromMain.get(position).getAllProjections());
        }
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.fromScroll(true, false, false);

        for (int i = -1; i < date.size() + 1; i++) {

            dayView = new TextView(context);
            dateView = new TextView(context);
            if (!places.isEmpty() && !date.isEmpty()) {
                scrollView.createProjectionScroll(places.get(0).getTitle(), date.get(0), fromAdapter);
            }
            layout = new LinearLayout(context);

            if (i == -1 || i == date.size()) {
                layout.setLayoutParams(layoutParams);
            } else {
                layout.setLayoutParams(layoutParams);
                layout.setGravity(Gravity.CENTER_HORIZONTAL);
                layout.setOrientation(LinearLayout.VERTICAL);

                dayView.setWidth(viewWidth);
                dayView.setLayoutParams(textViewParam);
                dayView.setGravity(Gravity.CENTER_HORIZONTAL);
                if (nameOfDays.size() > 0) {
                    dayView.setText(nameOfDays.get(i));
                }
                dayView.setTag(i);

                dateView.setWidth(viewWidth);
                dateView.setLayoutParams(textViewParam);
                dateView.setGravity(Gravity.CENTER_HORIZONTAL);
                if (date.size() > 0) {
                    dateView.setText(date.get(i));
                }

                layout.addView(dayView);
                layout.addView(dateView);

                layout.setLayoutParams(layoutParams);
            }
            if (!date.isEmpty()) {
                day = date.get(0);
            }

            layout.setBackground(context.getResources().getDrawable(R.drawable.scalloped_rectangle));
            masterLayout.addView(layout);
        }
        scrollView.addView(masterLayout);
        containerLayout.addView(scrollView);

    }

    public void createPlaceScroll(RelativeLayout containerLayout, Movie item, RelativeLayout
            containerForProjects, int position) {

        final ArrayList<Cinema> nameOfPlace = moviesCinemasDataSource.getAllCinemasFromId(item.getId());
        ArrayList<String> date = new ArrayList<>();

        if (listFromMain.get(position).getDate() != null) {
            date = listFromMain.get(position).getDate();
        }
        final CustomHorizontalScrollView scrollView = new CustomHorizontalScrollView(context, nameOfPlace.size() - 1, viewWidth, viewWidth, containerForProjects);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.fromScroll(false, true, false);

        if (listFromMain.get(position).getAllProjections() != null) {
            scrollView.setDayAndPlace(nameOfPlace, date, listFromMain.get(position).getAllProjections());
        }
        if (!nameOfPlace.isEmpty() && !date.isEmpty()) {
            scrollView.createProjectionScroll(nameOfPlace.get(0).getTitle(), date.get(0), true);
        }

        LinearLayout masterLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                viewWidth,
                (int) (50 * density));

        LinearLayout.LayoutParams textViewParam = new LinearLayout.LayoutParams(
                viewWidth,
                (int) (50 * density), Gravity.CENTER_HORIZONTAL);
        textViewParam.weight = 1;
        int halfViewWidth = viewWidth / 2;

        LinearLayout.LayoutParams emptyViewParam = new LinearLayout.LayoutParams(
                viewWidth + halfViewWidth,
                (int) (50 * density), Gravity.CENTER_HORIZONTAL);


        TextView placeView;
        LinearLayout layout;
        LinearLayout emptyLayout;

        for (int i = -1; i < nameOfPlace.size() + 1; i++) {

            placeView = new TextView(context);
            layout = new LinearLayout(context);
            if (i == -1 || i == nameOfPlace.size()) {
                layout.setLayoutParams(layoutParams);
            } else {

                layout.setLayoutParams(layoutParams);
                layout.setGravity(Gravity.CENTER_HORIZONTAL);
                layout.setOrientation(LinearLayout.VERTICAL);

                placeView.setWidth(viewWidth);
                placeView.setLayoutParams(textViewParam);
                placeView.setGravity(Gravity.CENTER);
                placeView.setText(nameOfPlace.get(i).getTitle());
                placeView.setTag(i);

                layout.addView(placeView);
                layout.setLayoutParams(layoutParams);
            }
            layout.setBackground(context.getResources().getDrawable(R.drawable.scalloped_rectangle));
            masterLayout.addView(layout);
        }
        scrollView.addView(masterLayout);
        containerLayout.addView(scrollView);
    }

    private View.OnClickListener imdbListener(final Movie item) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", item.getImdbUrl());
                context.startActivity(intent);
            }
        };

        return listener;
    }
}
