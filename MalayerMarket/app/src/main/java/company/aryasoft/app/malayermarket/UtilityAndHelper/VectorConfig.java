package company.aryasoft.app.malayermarket.UtilityAndHelper;

import android.support.graphics.drawable.VectorDrawableCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;


public class VectorConfig
{
    private static android.content.res.Resources _Resourses;
    private static ArrayList<VectorsModel> _List;

    public enum MyDirType
    {
        start,
        top,
        end,
        bottom
    }
    public static void SetVectors(android.content.res.Resources res, ArrayList<VectorsModel> list)
    {

        _Resourses = res;
        _List = list;

        VectorDrawableCompat vdc = null;
        for (int i = 0; i < _List.size(); i++)
        {
            vdc = VectorDrawableCompat.create(_Resourses, _List.get(i).draws, _List.get(i).view.getContext().getTheme());
            if (_List.get(i).view instanceof TextView)
            {
                TextView txt = (TextView)_List.get(i).view;

                switch (_List.get(i).dirs)
                {
                    case start:
                        txt.setCompoundDrawablesRelativeWithIntrinsicBounds(vdc, null, null, null);
                        break;
                    case top:
                        txt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, vdc, null, null);
                        break;
                    case end:
                        txt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, vdc, null);
                        break;
                    case bottom:
                        txt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, vdc);
                        break;
                }

            }
            else if (_List.get(i).view instanceof Button)
            {
                Button btn = (Button)_List.get(i).view;
                switch (_List.get(i).dirs)
                {
                    case start:
                        btn.setCompoundDrawablesRelativeWithIntrinsicBounds(vdc, null, null, null);
                        break;
                    case top:
                        btn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, vdc, null, null);
                        break;
                    case end:
                        btn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, vdc, null);
                        break;
                    case bottom:
                        btn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, vdc);
                        break;
                }
            }
            else if (_List.get(i).view instanceof EditText)
            {
                EditText edt = (EditText)_List.get(i).view;
                switch (_List.get(i).dirs)
                {
                    case start:
                        edt.setCompoundDrawablesRelativeWithIntrinsicBounds(vdc, null, null, null);
                        break;
                    case top:
                        edt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, vdc, null, null);
                        break;
                    case end:
                        edt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, vdc, null);
                        break;
                    case bottom:
                        edt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, vdc);
                        break;
                }
            }

        }
    }
}

