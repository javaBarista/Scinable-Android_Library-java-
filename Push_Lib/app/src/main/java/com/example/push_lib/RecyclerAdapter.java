package com.example.push_lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ArrayList<RecyclerItem> mData;
    private int curPos = -1;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    RecyclerAdapter(ArrayList<RecyclerItem> list) {
        mData = list;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        @SuppressLint("ResourceType") View view = inflater.inflate(R.layout.recycler_item, parent, false);
        RecyclerAdapter.ViewHolder vh = new RecyclerAdapter.ViewHolder(view);

        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(final RecyclerAdapter.ViewHolder holder, final int position) {
        RecyclerItem item = mData.get(position);

        holder.title.setText(item.getTitle());
        if(item.getBody().length() > 12) {
            holder.body.setText(item.getBody().substring(0,9) + "......");
        }
        else {
            holder.body.setText(item.getBody());
        }
        holder.date.setText(item.getDate());
        if(item.getChkread().equals("1")){
            holder.chkRead.setChecked(true);
            holder.chkRead.setEnabled(false);
        }

        holder.cl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                curPos=position;
                if(curPos==position){
                    holder.cl.setBackgroundColor(Color.parseColor("#FFD3D3D3"));
                    holder.chkRead.setChecked(true);
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            holder.cl.setBackgroundColor(Color.parseColor("#FFF8F8FF"));
                            holder.chkRead.setEnabled(false);
                        }
                    }, 300);
                }
            }
        });
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView body;
        TextView date;
        CheckBox chkRead;
        ConstraintLayout cl;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        RecyclerItem item = mData.get(pos);
                    }
                }
            });

            // 뷰 객체에 대한 참조. (hold strong reference)
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            date = itemView.findViewById(R.id.receivedDate);
            chkRead = itemView.findViewById(R.id.chkRead);
            cl = itemView.findViewById(R.id.layoutColor);
        }
    }

}