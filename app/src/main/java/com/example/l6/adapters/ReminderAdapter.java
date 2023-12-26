package com.example.l6.adapters;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.l6.R;
import com.example.l6.ReminderActivity;
import com.example.l6.classes.AlarmReceiver;
import com.example.l6.classes.DatabaseHelper;
import com.example.l6.models.Reminder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    Context context;
    List<Reminder> reminderList;
    SQLiteDatabase db;
    DatabaseHelper databaseHelper;
    AlarmManager alarmManager;

    public ReminderAdapter(Context context, List<Reminder> reminderList, DatabaseHelper databaseHelper) {
        this.context = context;
        this.reminderList = reminderList;
        this.databaseHelper = databaseHelper;
    }

    //Какой дизайн
    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View reminderItem = LayoutInflater.from(context).inflate(R.layout.activity_reminder_item, parent, false);

        return new ReminderViewHolder(reminderItem);
    }

    //Взаимодействие с объектом и его элементами
    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //Подставляем данные
        holder.reminderDateTime.setText(reminderList.get(position).getDateTime());
        holder.reminderTitle.setText(reminderList.get(position).getTitle());
        holder.reminderDescription.setText(reminderList.get(position).getDescription());

        //Переход на активити уведомления
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Создаём намерение
                Intent intent = new Intent(context, ReminderActivity.class);

                //Передаём данные
                intent.putExtra("reminderId", reminderList.get(position).getId());
                intent.putExtra("reminderDateTime", reminderList.get(position).getDateTime());
                intent.putExtra("reminderTitle", reminderList.get(position).getTitle());
                intent.putExtra("reminderDescription", reminderList.get(position).getDescription());

                //Переходим к активити
                context.startActivity(intent);
            }
        });

        //Удаление напоминания
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Открываем соединение с БД
                db = databaseHelper.getReadableDatabase();

                //Удаляем указанное уведомление
                db.delete(DatabaseHelper.TABLE, "_id = ?", new String[]{String.valueOf(reminderList.get(position).getId())});

                //Закрываем соединение с БД
                db.close();

                //Создаем менеджер оповещений, который был создан при первоначальном создании напоминания
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                //Создаём намерение, которое было создано при первоначальном создании напоминания
                Intent intent = new Intent(context, AlarmReceiver.class);

                //Создаём ожидаемое намерение, которое было создано при первоначальном создании напоминания
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        reminderList.get(position).getId(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                //Удаляем оповещение
                alarmManager.cancel(pendingIntent);

                //Удаляем напоминание
                reminderList.remove(position);

                Toast.makeText(context, "Напоминание удалено!", Toast.LENGTH_SHORT).show();

                //Обновляем список
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    //С какими элементами в дизайне работать
    public static final class ReminderViewHolder extends RecyclerView.ViewHolder{
        ImageButton btnDelete;
        AppCompatButton btnUpdate;
        TextView reminderDateTime, reminderTitle, reminderDescription;
        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);

            reminderDateTime = itemView.findViewById(R.id.reminderDateTime);
            reminderTitle = itemView.findViewById(R.id.reminderTitle);
            reminderDescription = itemView.findViewById(R.id.reminderDescription);

            btnDelete = itemView.findViewById(R.id.btnDeleteReminder);
        }
    }
}
