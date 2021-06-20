package com.fwmubarok.mychatapp.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fwmubarok.mychatapp.MainActivity;
import com.fwmubarok.mychatapp.Model.ReadMessageTopic;
import com.fwmubarok.mychatapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MessageAdapter";
    private ArrayList<ReadMessageTopic> listMessage;
    private String token;

    public MessageAdapter(ArrayList<ReadMessageTopic> listMessage, String token) {
        this.listMessage = listMessage;
        this.token = token;
    }

    // Create two class of view for sender and receiver
    class ViewHolderSender extends RecyclerView.ViewHolder {

        TextView tv_text_sender, tv_time_sender;

        public ViewHolderSender(View itemView) {
            super(itemView);

            tv_text_sender = itemView.findViewById(R.id.text_sender);
            tv_time_sender = itemView.findViewById(R.id.time_sender);
        }
    }

    class ViewHolderReceiver extends RecyclerView.ViewHolder {

        TextView tv_text_receiver, tv_time_receiver;

        public ViewHolderReceiver(View itemView) {
            super(itemView);

            tv_text_receiver = itemView.findViewById(R.id.text_receiver);
            tv_time_receiver = itemView.findViewById(R.id.time_receiver);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: 01 = " + listMessage.get(position).getFrom());
        Log.d(TAG, "getItemViewType: 02 = " + token);
        int condition;
        if (listMessage.get(position).getFrom().equals(token)) {
            condition = 0;
        } else {
            condition = 1;
        }
        Log.d(TAG, "getItemViewType: condition = " + condition);
        return condition;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Log.d(TAG, "onCreateViewHolder: viewType = " + viewType);
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sender, parent, false);
                ViewHolderSender viewHolderSender = new ViewHolderSender(view);
                return viewHolderSender;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_receiver, parent, false);
                ViewHolderReceiver viewHolderReceiver = new ViewHolderReceiver(view);
                return viewHolderReceiver;
        }
        return null;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + listMessage.get(position).getMessage());
        Log.d(TAG, "onBindViewHolder: " + holder.getItemViewType());
        Log.d(TAG, "-------------------------------------------------------------");

        switch (holder.getItemViewType()) {
            case 0:
                ViewHolderSender viewHolderSender = (ViewHolderSender)holder;

                // format timestamp to hour and minute
                String lastUpdate = listMessage.get(position).getTimestamp();
                SimpleDateFormat fromResponse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat myFormat = new SimpleDateFormat("HH:mm");
                String reformatDate = null;
                try {
                    reformatDate = myFormat.format(fromResponse.parse(lastUpdate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                viewHolderSender.tv_text_sender.setText(listMessage.get(position).getMessage());
                viewHolderSender.tv_time_sender.setText(reformatDate);

                break;
            case 1:
                ViewHolderReceiver viewHolderReceiver = (ViewHolderReceiver)holder;

                // format timestamp to hour and minute
                String lastUpdate2 = listMessage.get(position).getTimestamp();
                SimpleDateFormat fromResponse2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat myFormat2 = new SimpleDateFormat("HH:mm");
                String reformatDate2 = null;
                try {
                    reformatDate2 = myFormat2.format(fromResponse2.parse(lastUpdate2));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                viewHolderReceiver.tv_text_receiver.setText(listMessage.get(position).getMessage());
                viewHolderReceiver.tv_time_receiver.setText(reformatDate2);

                break;
        }
    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }
}
