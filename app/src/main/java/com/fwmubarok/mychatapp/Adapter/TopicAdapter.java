package com.fwmubarok.mychatapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fwmubarok.mychatapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ListViewHolder> {

    private ArrayList<ArrayList<String>> listTopic;
    private OnTopicListener mOnTopicListener;

    public TopicAdapter(ArrayList<ArrayList<String>> listTopic, OnTopicListener mOnTopicListener) {
        this.listTopic = listTopic;
        this.mOnTopicListener = mOnTopicListener;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_topic, parent, false);
        ListViewHolder listViewHolder = new ListViewHolder(view, mOnTopicListener);
        return listViewHolder;
    }

    @Override
    public void onBindViewHolder(TopicAdapter.ListViewHolder holder, int position) {
        holder.tv_topic_name.setText(listTopic.get(position).get(0));
        holder.tv_topic_lastMessage.setText(listTopic.get(position).get(1));

        // format timestamp to hour and minute
        String lastUpdate = listTopic.get(position).get(2);
        SimpleDateFormat fromResponse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat myFormat = new SimpleDateFormat("HH:mm");
        String reformatDate = null;
        try {
            reformatDate = myFormat.format(fromResponse.parse(lastUpdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tv_topic_lastMessageTime.setText(reformatDate);
    }

    @Override
    public int getItemCount() {
        return listTopic.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_topic_name, tv_topic_lastMessage, tv_topic_lastMessageTime;
        OnTopicListener onTopicListener;

        public ListViewHolder(View itemView, OnTopicListener onTopicListener) {
            super(itemView);

            tv_topic_name = itemView.findViewById(R.id.topic_name);
            tv_topic_lastMessage = itemView.findViewById(R.id.topic_last_message);
            tv_topic_lastMessageTime = itemView.findViewById(R.id.topic_last_message_time);
            this.onTopicListener = onTopicListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTopicListener.onTopicClick(getBindingAdapterPosition());
        }
    }

    public interface OnTopicListener {
        void onTopicClick(int position);
    }
}
