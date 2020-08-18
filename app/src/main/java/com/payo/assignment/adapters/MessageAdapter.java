package com.payo.assignment.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.payo.assignment.R;
import com.payo.assignment.data.Message;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> dataset = new ArrayList<>();

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageAdapter.MessageViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = dataset.get(position);
        holder.amount.setText(String.format("Rs %s", message.getAmount()));
        holder.bank.setText(message.getSenderId());
        holder.message.setText(message.getBody());
        @ColorInt int color = 0;
        switch (message.getType()) {
            case DEBIT:
                color = holder.amount.getContext().getResources().getColor(R.color.debit_color);
                break;
            case CREDIT:
                color = holder.amount.getContext().getResources().getColor(R.color.credit_color);
                break;
            case UNDEFINED:
                color = holder.amount.getContext().getResources().getColor(R.color.undefined_color);
        }
        holder.amount.setBackgroundColor(color);
    }

    public void setDataset(List<Message> messages) {
        dataset = messages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_amount)
        TextView amount;
        @BindView(R.id.tv_bank_id)
        TextView bank;
        @BindView(R.id.tv_message)
        TextView message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
