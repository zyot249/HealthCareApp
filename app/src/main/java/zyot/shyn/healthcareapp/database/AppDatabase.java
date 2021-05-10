package zyot.shyn.healthcareapp.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import zyot.shyn.healthcareapp.dao.UserActivityDao;
import zyot.shyn.healthcareapp.dao.UserStepDao;
import zyot.shyn.healthcareapp.entity.UserActivityEntity;
import zyot.shyn.healthcareapp.entity.UserStepEntity;

@Database(entities = {UserActivityEntity.class, UserStepEntity.class},
        version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "health_db")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
//                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return instance;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                }
            };

    public abstract UserActivityDao userActivityDao();

    public abstract UserStepDao userStepDao();
}
