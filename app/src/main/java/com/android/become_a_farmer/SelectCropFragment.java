package com.android.become_a_farmer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.become_a_farmer.databinding.FragmentSelectCropBinding;
import com.google.android.material.button.MaterialButton;

public class SelectCropFragment extends Fragment {
    private FragmentSelectCropBinding binding;
    private View selectedView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSelectCropBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //글자 색 변경
        String content = binding.selectCropTitle.getText().toString();
        SpannableString spannableString = new SpannableString(content);
        String word = "작물";

        int start = content.indexOf(word);
        int end = start + word.length();

        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.lightGreen)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.selectCropTitle.setText(spannableString);

        String[] crops = {"마늘", "쌀", "사과", "양파"};
        for(int i = 0; i<crops.length; i++) {
            MaterialButton btn = (MaterialButton) binding.selectCropGrid.getChildAt(i);
            btn.setText(crops[i]);
            btn.setTag(crops[i]);
            btn.setOnClickListener(this::selectView);
        }
        binding.selectCropNextBtn.setOnClickListener(view1 -> {
            if(selectedView == null){
                Toast.makeText(getActivity(), "작물을 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
            String crop = selectedView.getTag().toString();
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("selectedCrop", crop);
            editor.commit();

            PlannerActivity activity = (PlannerActivity) getActivity();
            activity.moveNextStep();
        });
        return view;
    }

    private void selectView(View view){
        if (selectedView != null) {
            selectedView.setSelected(false);
        }
        view.setSelected(true);
        selectedView = view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}