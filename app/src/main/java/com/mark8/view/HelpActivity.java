package com.mark8.view;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mark8.R;
import com.mark8.adapter.HelpAdapter;
import com.mark8.databinding.ActivityHelpBinding;
import com.mark8.model.Help;

import java.util.ArrayList;

import static com.mark8.storage.LanguageUtils.loadLocale;

public class HelpActivity extends AppCompatActivity {

    private ActivityHelpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_help);
//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle(getResources().getString(R.string.help_center));

        setUpRecyclerView();

    }

    private void setUpRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.helpList.setLayoutManager(layoutManager);
        binding.helpList.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.helpList.addItemDecoration(dividerItemDecoration);

        HelpAdapter helpAdapter = new HelpAdapter(getHelpList());
        binding.helpList.setAdapter(helpAdapter);
    }

    private ArrayList<Help> getHelpList() {
        final ArrayList<Help> helpList = new ArrayList<>();
        helpList.add(new Help(getString(R.string.inquiryOne), getString(R.string.answerOne)));
        helpList.add(new Help(getString(R.string.inquiryTwo), getString(R.string.answerTwo)));
        helpList.add(new Help(getString(R.string.inquiryThree), getString(R.string.answerThree)));
        helpList.add(new Help(getString(R.string.inquiryFour), getString(R.string.answerFour)));
        helpList.add(new Help(getString(R.string.inquiryFive), getString(R.string.answerFive)));
        return helpList;
    }
}
