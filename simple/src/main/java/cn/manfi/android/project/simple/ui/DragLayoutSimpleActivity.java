package cn.manfi.android.project.simple.ui;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.ScrollView;

import cn.manfi.android.project.base.common.view.DragLinearLayout;
import cn.manfi.android.project.base.ui.base.BaseActivity;
import cn.manfi.android.project.simple.R;

public class DragLayoutSimpleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draglayout_simple);
    }

    @Override
    protected void initView() {
        NestedScrollView sv = findViewById(R.id.sv);

        DragLinearLayout dragLinearLayout = findViewById(R.id.dragLinearLayout);
        for (int i = 0; i < dragLinearLayout.getChildCount(); i++) {
            View child = dragLinearLayout.getChildAt(i);
            View drag = child.findViewWithTag("btn_Drag");
            dragLinearLayout.setViewDraggable(child, drag);
        }
        dragLinearLayout.setContainerScrollView(sv);
    }
}
