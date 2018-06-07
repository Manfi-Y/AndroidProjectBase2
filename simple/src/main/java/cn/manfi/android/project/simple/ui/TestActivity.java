package cn.manfi.android.project.simple.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;

import cn.manfi.android.project.base.common.RxDisposedManager;
import cn.manfi.android.project.base.ui.base.BaseActivity;
import cn.manfi.android.project.simple.R;
import cn.manfi.android.project.simple.databinding.ActivityTestBinding;

public class TestActivity extends BaseActivity {

    private ActivityTestBinding binding;

    private Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_test);
    }

    @Override
    protected void initView() {
        RxDisposedManager.addDisposed(activity, RxView.clicks(binding.btn1)
                .subscribe(o -> {
                    List<String> strList = new ArrayList<>();
                    strList.add("a");
                    strList.add("b");
                    strList.add("c");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    adapter.setData(strList);
                }));
        RxDisposedManager.addDisposed(activity, RxView.clicks(binding.btn2)
                .subscribe(o -> {
                    List<String> strList = new ArrayList<>();
                    strList.add("a");
                    strList.add("b");
                    strList.add("c");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    strList.add("d");
                    adapter.setData(strList);
                }));

        binding.rv.setLayoutManager(new LinearLayoutManager(activity));
        binding.rv.setAdapter(adapter = new Adapter(activity));
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private Context context;
        private List<String> list = new ArrayList<>();

        public Adapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_test, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tv.setText(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void setData(List<String> strList) {
            int size = list.size();
            if (size > 0) {
                list.clear();
                notifyItemRangeRemoved(0, size );
            }
            list.addAll(strList);
            notifyItemRangeInserted(0, list.size());
        }

        public void addData(String str) {
            list.add(str);
            notifyItemInserted(list.size() - 1);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv;

            public ViewHolder(View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tv);
            }
        }
    }
}
