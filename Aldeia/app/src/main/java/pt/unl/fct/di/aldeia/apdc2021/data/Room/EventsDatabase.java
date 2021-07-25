package pt.unl.fct.di.aldeia.apdc2021.data.Room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {EventEntity.class, RouteEntity.class, EventRouteCrossReference.class}, version = 3)
public abstract class EventsDatabase extends RoomDatabase {
    private static volatile EventsDatabase INSTANCE;
    public abstract EventDao eventDao();
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static EventsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EventsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EventsDatabase.class, "events_database").fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                EventDao dao = INSTANCE.eventDao();
                dao.nukeTable();

            });
        }
    };
}
