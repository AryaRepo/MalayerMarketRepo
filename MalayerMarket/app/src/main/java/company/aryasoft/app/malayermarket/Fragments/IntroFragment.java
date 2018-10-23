package company.aryasoft.app.malayermarket.Fragments;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import company.aryasoft.app.malayermarket.R;
import company.aryasoft.app.malayermarket.UtilityAndHelper.TypeWriter;

public class IntroFragment extends Fragment
{
    int[] colors = new int[8];
    private TypeWriter tw;
    public static IntroFragment newInstance(String str, int pos, boolean isEnd)
    {
        IntroFragment IntroFragmentInstance = new IntroFragment();
        Bundle args = new Bundle();
        args.putString("str", str);
        args.putInt("pos", pos);
        IntroFragmentInstance.setArguments(args);
        return IntroFragmentInstance;
    }
    private void initColor()
    {
        colors[0] = Color.parseColor("#2196F3");
        colors[1] = Color.parseColor("#673AB7");
        colors[2] = Color.parseColor("#82B1FF");
        colors[3] = Color.parseColor("#81D4FA");
        colors[4] = Color.parseColor("#03A9F4");
        colors[5] = Color.parseColor("#FFA726");
        colors[6] = Color.parseColor("#FF9800");
        colors[7] = Color.parseColor("#FF8A65");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        initColor();
        View view = inflater.inflate(R.layout.fragment_intro, container, false);
        RelativeLayout rel = view.findViewById(R.id.rel_intro);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getActivity().getWindow().setNavigationBarColor(colors[getArguments().getInt("pos")]);
        }
        rel.setBackgroundColor(colors[getArguments().getInt("pos")]);
        // Inflate the layout for this fragment
        tw = view.findViewById(R.id.tv);
        tw.setText("");
        tw.setCharacterDelay(50);
        tw.animateText(getArguments().getString("str"));
        return view;
    }

}
