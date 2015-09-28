package com.tweener.fag.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.Toast;

import com.tweener.fag.FloatingActionGroup;
import com.tweener.fag.FloatingActionLabelledButton;

public class MainActivity extends Activity implements FloatingActionGroup.OnFloatingActionGroupListener {

    private CoordinatorLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = (CoordinatorLayout) findViewById(R.id.main_layout);

        FloatingActionGroup fag = (FloatingActionGroup) findViewById(R.id.floating_action_group);
        fag.setOnFloatingActionGroupListener(this);

        FloatingActionLabelledButton btn1 = (FloatingActionLabelledButton) findViewById(R.id.floating_action_labelled_button1);
        btn1.setFabClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Toast.makeText(MainActivity.this, "Button 1 clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFloatingActionGroupExpanded() {
        Toast.makeText(MainActivity.this, "FloatingActionGroup expanded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFloatingActionGroupCollapsed() {
        Toast.makeText(MainActivity.this, "FloatingActionGroup collapsed", Toast.LENGTH_SHORT).show();
    }
}
