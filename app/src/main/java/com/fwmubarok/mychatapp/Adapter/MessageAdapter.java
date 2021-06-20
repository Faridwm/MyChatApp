package com.fwmubarok.mychatapp.Adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fwmubarok.mychatapp.Model.ReadMessageTopic;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ReadMessageTopic> listMessage;

    public MessageAdapter(ArrayList<ReadMessageTopic> listMessage) {
        this.listMessage = listMessage;
    }

    // Create two class of view for sender and receiver
    class ViewHolderSender extends RecyclerView.ViewHolder {

        public ViewHolderSender(View itemView) {
            super(itemView);
        }
    }

    class ViewHolderReceiver extends RecyclerView.ViewHolder {

        public ViewHolderReceiver(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
